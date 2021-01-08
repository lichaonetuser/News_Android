package com.mynews.app.news.applog

import com.mynews.app.news.bean.*
import com.mynews.app.news.data.DataManager
import com.mynews.app.news.data.source.local.db.DBAPI
import com.mynews.app.news.proto.AppLog
import com.mynews.app.news.proto.Base
import com.mynews.common.core.environment.*
import com.mynews.common.core.json.gson.util.CoreGsonUtils
import com.mynews.common.core.log.Logger
import com.mynews.common.core.util.NetworkUtils
import com.mynews.common.core.util.TimeUtils
import io.reactivex.Completable
import io.reactivex.internal.schedulers.RxThreadFactory
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.rxkotlin.subscribeBy
import java.util.*
import kotlin.collections.ArrayList

object AppLogManager {

    /**
     * Logcat标签
     */
    const val LOGGER_TAG = "APP_LOG_MANAGER"
    const val LOGGER_TAG_TRACKING = "APP_LOG_MANAGER_TRACKING"

    /**
     * 服务端配置
     */
    var config = DataManager.Local.getAppLogConfig() ?: AppLogConfig()

    /**
     * 重要，所有操作运行在一个单任务线程池上
     */
    private val mSingleScheduler = RxJavaPlugins.createSingleScheduler(RxThreadFactory("AppLogScheduler", Thread.NORM_PRIORITY, true))

    fun init() {
        DBAPI.APP_LOG_DB.selectFromAppLogEvent()
                .executeAsObservable()
                .toList()
                .subscribeOn(mSingleScheduler)
                .subscribeBy(
                        onSuccess = { events ->
                            Logger.tag(LOGGER_TAG).d("Init db log success!")
                            mEventList.addAll(events)
                        },
                        onError = { error ->
                            Logger.tag(LOGGER_TAG).d("Init db log error!\nerror :\n$error")
                            DBAPI.APP_LOG_DB.deleteFromAppLogEvent().execute()
                        }
                )
    }

    /*-------*/
    /* Event */
    /*-------*/

    private val mEventList = ArrayList<AppLogEvent>()

    fun logEvent(name: AppLog.EventName, label: String, body: AppLog.EventBody) {
        Completable.fromCallable {
            Logger.tag(LOGGER_TAG).d("Run logEvent. name : $name | label : $label | body : $body")
            val id = UUID.randomUUID().toString()
            val event = AppLogEvent(
                    id = id,
                    failCount = 0,
                    event = AppLog.Event.newBuilder()
                            .setLogId(id)
                            .setName(name)
                            .setLabel(label)
                            .setBody(body)
                            .setTimestamp(System.currentTimeMillis())
                            .build())
            logEventInternal(event)
        }.subscribeOn(mSingleScheduler).subscribeBy(
                onError = { e ->
                    Logger.tag(LOGGER_TAG).d(e)
                }
        )
    }

    //https://stackoverflow.com/questions/16558709/gson-issue-with-string/16558757
    private val otherInfoGson = CoreGsonUtils.getNewInstanceBuildFromDefault()
            .disableHtmlEscaping()
            .create()

    fun logEvent(info: ExtraInfo) {
        Completable.fromCallable {
            Logger.tag(LOGGER_TAG).d("Run logEvent_custom. info : $info")
            val id = UUID.randomUUID().toString()
            //https://stackoverflow.com/questions/16558709/gson-issue-with-string/16558757
            val otherInfo = otherInfoGson.toJson(info)
            otherInfo.replace("\\\\", "\\")
            val event = AppLogEvent(
                    id = id,
                    failCount = 0,
                    event = AppLog.Event.newBuilder()
                            .setName(AppLog.EventName.EVENT_OTHER)
                            .setLogId(id)
                            .setTimestamp(System.currentTimeMillis())
                            .setOtherInfo(otherInfo)
                            .build())
            logEventInternal(event)
        }.subscribeOn(mSingleScheduler).subscribeBy(
                onError = { e ->
                    Logger.tag(LOGGER_TAG).d(e)
                }
        )
    }

    private fun logEventInternal(event: AppLogEvent) {
        mEventList.add(event)
        DBAPI.APP_LOG_DB.insertIntoAppLogEvent(event)
        //如果超出一次最多发送的数量，发送
        if (mEventList.size >= config.onceMaxCount) {
            val sendEvents = mEventList.subList(0, config.onceMaxCount - 1)
            sendAppLogInternal(sendEvents)
        }
    }

    /*------------*/
    /* Impression */
    /*------------*/

    private val mImpressionCellTrackMap = linkedMapOf<AppLogImpressionCellTrack, LinkedHashMap<String, AppLogImpressionCell>>()

    fun startTrackImpressionCell(testTitle: String, listId: String, listType: AppLog.ImpressionType, cellId: String, cellType: AppLog.ImpressionCellType) {
        val trackingStartTime = System.currentTimeMillis()
        Completable.fromCallable {
            val track = AppLogImpressionCellTrack(id = listId, type = listType)
            val cellMap = mImpressionCellTrackMap[track] ?: linkedMapOf()

            //不允许重复追踪cell，需要先结束指定cell的追踪
            val cell = cellMap[cellId]
                    ?: AppLogImpressionCell(id = cellId, enterTime = trackingStartTime, type = cellType)
            if (cell.isTracking) {
                Logger.tag(LOGGER_TAG_TRACKING).d("cell is already Tracking at : ${cell.trackingStartTime}")
                return@fromCallable Unit
            }
            cell.isTracking = true
            cell.trackingStartTime = trackingStartTime

            Logger.tag(LOGGER_TAG_TRACKING).d("cell start Tracking at : ${cell.trackingStartTime}  | testTitle : $testTitle")

            cellMap[cellId] = cell
            mImpressionCellTrackMap[track] = cellMap
            return@fromCallable Unit
        }.subscribeOn(mSingleScheduler).subscribeBy(
                onError = { e ->
                    Logger.tag(LOGGER_TAG_TRACKING).d(e)
                }
        )
    }

    fun endTrackImpressionCell(testTitle: String, listId: String, listType: AppLog.ImpressionType, cellId: String, cellType: AppLog.ImpressionCellType) {
        val trackingEndTime = System.currentTimeMillis()
        Logger.tag(LOGGER_TAG_TRACKING).d("call endTrackImpressionCell")
        Completable.fromCallable {
            Logger.tag(LOGGER_TAG_TRACKING).d("run endTrackImpressionCell")
            val track = AppLogImpressionCellTrack(id = listId, type = listType)
            val cellMap = mImpressionCellTrackMap[track] ?: return@fromCallable Unit
            val cell = cellMap[cellId]
            if (cell == null || !cell.isTracking) {
                Logger.tag(LOGGER_TAG_TRACKING).d("call end Tracking but cell is not valid.isTracking : ${cell?.isTracking}")
                return@fromCallable Unit
            }

            val duration = trackingEndTime - cell.trackingStartTime
            Logger.tag(LOGGER_TAG_TRACKING).d("cell end Tracking at : $trackingEndTime | duration : $duration | testTitle : $testTitle")

            if (duration < 300) {
                cellMap.remove(cellId)
                mImpressionCellTrackMap[track] = cellMap
                if (mImpressionCellTrackMap[track]?.isEmpty() == true) {
                    mImpressionCellTrackMap.remove(track)
                }
                return@fromCallable Unit
            }
            if (duration > cell.maxDuration) {
                cell.maxDuration = duration
            }
            cell.duration += duration

            cell.isTracking = false
            cellMap[cellId] = cell
            mImpressionCellTrackMap[track] = cellMap

            Logger.tag(LOGGER_TAG_TRACKING).d("Change impressionCell $cell")
            return@fromCallable Unit
        }.subscribeOn(mSingleScheduler).subscribeBy(
                onError = { e ->
                    Logger.tag(LOGGER_TAG_TRACKING).d(e)
                }
        )
    }

    private fun getUnSendImpressionsMap(): LinkedHashMap<AppLogImpressionCellTrack, LinkedHashMap<String, AppLogImpressionCell>> {
        val unSendImpressionsMap = linkedMapOf<AppLogImpressionCellTrack, LinkedHashMap<String, AppLogImpressionCell>>()
        mImpressionCellTrackMap.forEach {
            val cells = it.value.filter { it.value.isTracking }
            if (cells.isNotEmpty()) {
                if (cells is LinkedHashMap) {
                    unSendImpressionsMap[it.key] = cells
                } else {
                    val map = linkedMapOf<String, AppLogImpressionCell>()
                    map.putAll(cells)
                    unSendImpressionsMap[it.key] = map
                }
            }
        }
        return unSendImpressionsMap
    }

    private fun getSendAppLogImpressions(): List<AppLog.Impression> {
        return mImpressionCellTrackMap.map {
            val cells = it.value.filterValues { !it.isTracking }.map {
                val cell = it.value
                AppLog.ImpressionCell.newBuilder()
                        .setItemId(cell.id)
                        .setDuration(cell.duration)
                        .setMaxDuration(cell.maxDuration)
                        .setType(cell.type)
                        .build()
            }
            val impression = it.key
            if (cells.isNotEmpty()) {
                AppLog.Impression.newBuilder()
                        .setItemId(impression.id)
                        .setType(impression.type)
                        .addAllImpressionCell(cells)
                        .setTimestamp(System.currentTimeMillis())
                        .build()
            } else {
                null
            }
        }.filter { it != null }.requireNoNulls()
    }

    /*----------*/
    /* Send Log */
    /*----------*/

    fun sendAppLog() {
        Completable.create {
            Logger.tag(LOGGER_TAG).d("Run sendAppLog.")

            var impressionCellsEmpty = true
            for (entry in mImpressionCellTrackMap) {
                for (cell in entry.value.values) {
                    if (!cell.isTracking) {
                        impressionCellsEmpty = false
                        break
                    }
                }
                if (!impressionCellsEmpty) {
                    break
                }
            }

            if (mEventList.isEmpty() && impressionCellsEmpty) {
                Logger.tag(LOGGER_TAG).d("No AppLog need Send.")
                return@create
            }
            //如果超出一次最多发送的数量，发送指定数目，否则全部发送
            val sendEvents = if (mEventList.size >= config.onceMaxCount) {
                mEventList.subList(0, config.onceMaxCount - 1)
            } else {
                mEventList
            }
            sendAppLogInternal(sendEvents)
        }.subscribeOn(mSingleScheduler).subscribeBy(
                onError = { e ->
                    Logger.tag(LOGGER_TAG).d(e)
                }
        )
    }

    private fun sendAppLogInternal(events: Collection<AppLogEvent>) {
        try {
            Logger.tag(LOGGER_TAG).d("Run sendAppLogInternal.")
            //Header
            //修改每次请求header中的可变字段
            val access = when (NetworkUtils.getNetworkType()) {
                NetworkUtils.NetWorkType.Net3G -> "3G"
                NetworkUtils.NetWorkType.Net4G -> "4G"
                NetworkUtils.NetWorkType.Wifi -> "WIFI"
                else -> "other"
            }

            val header = Base.Header.newBuilder()
                    .setAppId(EnvPackage.PACKAGE_NAME)
                    .setAppName(EnvPackage.APP_NAME)
                    .setCarrier(EnvTelephony.NETWORK_OPERATOR_NAME)
                    .setChannel(EnvPackage.UMENG_CHANNEL)
                    .setLang(EnvLocale.LANGUAGE)
                    .setChannelLang(EnvLocale.LANGUAGE)
                    .setUniqueDeviceId(DataManager.Local.getUniqueDeviceId())
                    .setAccess(access)
                    .setDeviceType(EnvBuild.MODEL)
                    .setJailBreak(EnvOther.IS_ROOT)
                    .setModel(EnvBuild.CPU_ABI)
                    .setOsn(EnvBuild.PRODUCT)
                    .setOsv(EnvBuild.RELEASE_VERSION)
                    .setPhoneType(EnvIOSApp.phonetype)
                    .setVersion(EnvPackage.VERSION_NAME)
                    .setVersionCode(EnvPackage.VERSION_CODE.toLong())
                    .setResolution(EnvDisplayMetrics.RESOLUTION)
                    .setTimezone(TimeUtils.getGMTOffset().toLong())
                    .setClientId(EnvUnique.CLIENT_ID)
                    .setInstallId(EnvUnique.UTDID)
                    .setOsApi(EnvBuild.SDK_INT.toLong())
                    .setDpi(EnvDisplayMetrics.DENSITY_DPI.toLong())
                    .setCountry(EnvLocale.COUNTRY)
                    .setCarrierCode(EnvTelephony.NETWORK_OPERATOR)
                    .setRom(EnvBuild.INCREMENTAL)
                    .build()

            //events
            val sendEvents = events.map { it.event }
            //impressions
            val sendImpressions = getSendAppLogImpressions()

            if (sendEvents.isEmpty() && sendImpressions.isEmpty()) {
                Logger.tag(LOGGER_TAG).d("Run sendAppLogInternal but sendEvents and sendImpressions is empty.")
                return
            }

            //Log
            val log = AppLog.NewsAppLog.newBuilder()
                    .setHeader(header)
                    .addAllEvent(sendEvents)
                    .addAllImpression(sendImpressions)
                    .build()

            Logger.tag(LOGGER_TAG).d("Start sendAppLog\nEvents :\n$log")
            DataManager.Remote.postAppLog(log).blockingFirst()

            Logger.tag(LOGGER_TAG).d("End sendAppLog Success！")
            //从数据库删除发送的数据
            DBAPI.APP_LOG_DB.deleteFromAppLogEvent()
                    .idIn(events.map { it.id })
                    .execute()
            //从缓存删除发送的数据
            mEventList.removeAll(events)
            //清理Impression
            val unSendImpressionsMap = getUnSendImpressionsMap()
            mImpressionCellTrackMap.forEach { it.value.clear() }
            mImpressionCellTrackMap.clear()
            mImpressionCellTrackMap.putAll(unSendImpressionsMap)
        } catch (error: Exception) {
            try {
                Logger.tag(LOGGER_TAG).d("End sendAppLog Error！\nError :\n$error")
                //移除重试指定次数后依然失败的事件
                val overFailEvents = arrayListOf<AppLogEvent>()
                events.forEach {
                    it.failCount++
                    if (it.failCount >= config.maxRetryCount) {
                        overFailEvents.add(it)
                    }
                }
                if (overFailEvents.isNotEmpty()) {
                    mEventList.removeAll(overFailEvents)
                    DBAPI.APP_LOG_DB.deleteFromAppLogEvent()
                            .idIn(overFailEvents.map { it.id })
                            .execute()
                }
            } catch (error: Exception) {
                Logger.tag(LOGGER_TAG).d("End sendAppLog Error Exception！\nError :\n$error")
            }
        }
    }

}