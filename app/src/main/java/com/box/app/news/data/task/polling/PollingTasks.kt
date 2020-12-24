package com.box.app.news.data.task.polling

import com.box.app.news.applog.AppLogManager
import com.box.app.news.data.DataManager
import com.box.app.news.data.task.polling.PollingTaskKey.*
import com.box.app.news.util.LocationUtils
import com.box.common.core.log.Logger
import com.box.common.core.net.http.HttpManager
import com.box.common.core.rx.repeatWithDelay
import com.box.common.core.rx.schedulers.computation
import com.box.common.core.rx.schedulers.io
import com.box.common.extension.location.RxLocation
import com.google.firebase.perf.metrics.AddTrace
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import java.util.concurrent.TimeUnit

open class PollingTasks {

    companion object {
        /**
         * Logcat标签
         */
        const val LOGGER_TAG = "POLLING_TASKS"
    }

    val mTasks = HashMap<PollingTaskKey, Disposable>()

    fun start(key: PollingTaskKey) {
        if (mTasks[key] != null && !mTasks[key]!!.isDisposed) {
            Logger.w("PollingTasks ${key.name} is already run.")
            return
        }
        when (key) {
            WEATHER_SIMPLE_INFO -> {
                val region = DataManager.Local.getUserLastSelectedRegion()
                if (!region.isEmptyRegion()) {
                    startUpdateWeatherSimpleInfoCache(region.cityCode)
                }
            }
            APP_LOG_CONFIG -> {
                startUpdateAppLogConfig()
            }
            SEND_APP_LOG -> {
                startSendAppLog()
            }
            UPDATE_GEO -> {
                startUpdateGeo()
            }
        }
    }

    @AddTrace(name = "InitDataManager_PollingTasks", enabled = true)
    fun startDefault() {
//        start(PollingTaskKey.APP_LOG_CONFIG)
        start(PollingTaskKey.SEND_APP_LOG)
        start(PollingTaskKey.UPDATE_GEO)
    }

    fun startAll() {
        PollingTaskKey.values().forEach { start(it) }
    }

    fun interrupt(key: PollingTaskKey) {
        if (mTasks[key]?.isDisposed == false) {
            mTasks[key]?.dispose()
        }
    }

    fun interruptAll() {
        mTasks.values
                .filterNot { it.isDisposed }
                .forEach { it.dispose() }
    }

    fun startUpdateWeatherSimpleInfoCache(cityCode: String) {
//        mTasks[WEATHER_SIMPLE_INFO] = DataManager.Remote.getWeatherSimpleInfo(cityCode)
//                .repeatWithDelay(1, TimeUnit.HOURS)
//                .io()
//                .subscribeBy(onNext = { info ->
//                    DataManager.Memory.putWeatherSimpleInfo(info.weather)
//                })
    }

    fun startUpdateAppLogConfig() {
        Logger.tag(LOGGER_TAG).d("Run startUpdateAppLogConfig")
        mTasks[APP_LOG_CONFIG] = DataManager.Remote.getAppLogConfig()
                .repeatWhen { completedObservable ->
                    val delaySeconds = AppLogManager.config.fetchSettingInterval.toLong()
                    Logger.tag(LOGGER_TAG).d("Repeat startUpdateAppLogConfig delaySeconds : $delaySeconds")
                    completedObservable.delay(delaySeconds, TimeUnit.SECONDS)
                }
                .retryWhen { completedObservable ->
                    val delaySeconds = AppLogManager.config.fetchSettingInterval.toLong()
                    Logger.tag(LOGGER_TAG).d("Retry startUpdateAppLogConfig delaySeconds : $delaySeconds")
                    completedObservable.delay(delaySeconds, TimeUnit.SECONDS)
                }
                .io()
                .subscribeBy(
                        onNext = { config ->
                            try {
                                Logger.tag(LOGGER_TAG).d("Run startUpdateAppLogConfig success!")
                                AppLogManager.config = config
                                DataManager.Local.saveAppLogConfig(config)
                            } catch (error: Exception) {
                                Logger.tag(LOGGER_TAG).d("Run startUpdateAppLogConfig error\n!error :\n$error")
                            }
                        },
                        onComplete = {
                            Logger.tag(LOGGER_TAG).d("Run startUpdateAppLogConfig complete!")
                        },
                        onError = { error ->
                            Logger.tag(LOGGER_TAG).d("Run startUpdateAppLogConfig error\n!error :\n$error")
                        })
    }

    fun startSendAppLog() {
        Logger.tag(LOGGER_TAG).d("Run startSendAppLog")
        mTasks[SEND_APP_LOG] = Observable.create<Unit> { emmit ->
            emmit.onNext(AppLogManager.sendAppLog())
            emmit.onComplete()
        }.repeatWhen { completedObservable ->
            val delaySeconds = AppLogManager.config.pollingInterval.toLong()
            Logger.tag(LOGGER_TAG).d("Repeat startSendAppLog delaySeconds : $delaySeconds")
            completedObservable.delay(delaySeconds, TimeUnit.SECONDS)
        }.retryWhen { completedObservable ->
            val delaySeconds = AppLogManager.config.pollingInterval.toLong()
            Logger.tag(LOGGER_TAG).d("Retry startSendAppLog delaySeconds : $delaySeconds")
            completedObservable.delay(delaySeconds, TimeUnit.SECONDS)
        }.io().subscribeBy(
                onNext = {
                    Logger.tag(LOGGER_TAG).d("Run startSendAppLog success!")
                },
                onComplete = {
                    Logger.tag(LOGGER_TAG).d("Run startSendAppLog complete!")
                },
                onError = { error ->
                    Logger.tag(LOGGER_TAG).d("Run startSendAppLog error\n!error :\n$error")
                })
    }

    fun startUpdateGeo() {
        mTasks[UPDATE_GEO] =
                RxLocation.haveLocationPermission()
                        .flatMap { havePermission ->
                            if (havePermission) {
                                RxLocation.getLastKnownLocation()
                            } else {
                                null
                            }
                        }
                        .repeatWhen { completedObservable -> completedObservable.delay(1, TimeUnit.MINUTES) }
                        .retryWhen { completedObservable -> completedObservable.delay(1, TimeUnit.MINUTES) }
                        .computation()
                        .subscribeBy(
                                onNext = { location ->
                                    try {
                                        if (location == null || location.latitude < 0 || location.longitude < 0) {
                                            return@subscribeBy
                                        }
                                        val latitude = location.latitude.toFloat()
                                        val longitude = location.longitude.toFloat()
                                        HttpManager.putCommonParams(*LocationUtils.getLocationPairs(latitude, longitude))
                                    } catch (error: Exception) {
                                        Logger.tag(LOGGER_TAG).d("Run startUpdateGeo error\n!error :\n$error")
                                    }
                                },
                                onComplete = {
                                    Logger.tag(LOGGER_TAG).d("Run startUpdateGeo complete!")
                                },
                                onError = { error ->
                                    Logger.tag(LOGGER_TAG).d("Run startUpdateGeo error\n!error :\n$error")
                                })

    }

}