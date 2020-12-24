package com.box.app.news.util

import com.box.app.news.ab.ABManager
import com.box.app.news.bean.User
import com.box.app.news.data.DataManager
import com.box.app.news.data.source.local.LocalKeys
import com.box.common.core.analytics.AnalyticsManager
import com.box.common.core.net.http.HttpManager
import com.box.common.core.net.http.bean.APIResponse
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

object UDIDUtils {

    /**
     * Http请求的通用参数中使用的KEY
     */
    private const val HTTP_PARAMS_KEY_UNIQUE_DEVICE_ID = "unique_device_id"

    /**
     * 加载UniqueDeviceId，如果本地有，返回本地
     * 否侧去远程获取
     * @param timeoutInMillis 超时时间，从远端或者本地获取UniqueDeviceId超过此时间后算做获取失败
     */
    fun loadUniqueDeviceId(timeoutInMillis: Long = 1000): Observable<String> {

        val uniqueDeviceId = DataManager.Local.getUniqueDeviceId()
        val source = if (uniqueDeviceId.isBlank()) {
            DataManager.Remote
                    .getUniqueDeviceId()
                    .map { t: User ->
                        DataManager.Local.saveUniqueDeviceId(t.uniqueDeviceId)
                        //额外需求，如果这里返回了e_flag，更新e_flag
                        ABManager.updateEFlag(t.eFlag)
                        val gson = Gson()
                        val interestData = gson.toJson(t.interest)
                        val notFirstRun = Prefs.getBoolean(LocalKeys.NOT_FIRST_RUN, false)
                        when {
                            !notFirstRun && t.interest != null && t.interest?.items != null -> Prefs.putString(LocalKeys.MY_INTEREST, interestData)
                            !notFirstRun && t.gender != null -> Prefs.putBoolean(LocalKeys.IS_SELECT_GENDER_AGE, true)
                            else -> {
                                DataManager.Init.initNewsChannelListFromRemote()
                                AnalyticsManager.logEvent(LocalKeys.INTEREST_EVENT, "no_data", onFirebase = false)
                            }
                        }
                        t.uniqueDeviceId

                    }
                    .doOnNext { id ->
                        //保存到本地
                        DataManager.Local.saveUniqueDeviceId(id)
                    }
        } else {
            Observable.just(uniqueDeviceId)
        }
        return source
                .timeout(timeoutInMillis, TimeUnit.MILLISECONDS)
                .doOnNext { id ->
                    //加为通用参数
                    HttpManager.putCommonParams(HTTP_PARAMS_KEY_UNIQUE_DEVICE_ID to id)
                }
    }

    /**
     * 获取通用参数用的Pair
     */
    fun getUniqueDeviceIdPair(): Pair<String, String> {
        return HTTP_PARAMS_KEY_UNIQUE_DEVICE_ID to DataManager.Local.getUniqueDeviceId()
    }

    /**
     * 目前等价于从本地获取
     */
    fun getUniqueDeviceId() = DataManager.Local.getUniqueDeviceId()
}

/**
 * 如果一个请求依赖于UDID(即必须本地获取UDID成功后再调用)
 * 那么可以在响应式调用链上追加此方法
 * @param timeoutInMillis 从远端或本地请求UDID的超时时间，超过则进入onError流程
 */
fun <T> Observable<APIResponse<T>>.checkUDID(timeoutInMillis: Long = 5000): Observable<APIResponse<T>> = UDIDUtils
        .loadUniqueDeviceId(timeoutInMillis)
        .flatMap { this }