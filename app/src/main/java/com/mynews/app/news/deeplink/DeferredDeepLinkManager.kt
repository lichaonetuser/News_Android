package com.mynews.app.news.deeplink

import androidx.annotation.UiThread
import androidx.appcompat.app.AlertDialog
import com.mynews.app.news.R
import com.mynews.app.news.analytics.AnalyticsKey
import com.mynews.app.news.bean.PLinkRequest
import com.mynews.app.news.bean.PLinkResponse
import com.mynews.app.news.data.DataDictionary
import com.mynews.app.news.data.DataManager
import com.mynews.app.news.openurl.OpenUrlManager
import com.mynews.common.core.CoreApp
import com.mynews.common.core.analytics.AnalyticsManager
import com.mynews.common.core.log.Logger
import com.mynews.common.core.rx.schedulers.ioToMain
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
//import com.crashlytics.android.Crashlytics
import com.facebook.applinks.AppLinkData
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import java.util.concurrent.TimeUnit

object DeferredDeepLinkManager {

    const val LOGGER_TAG = "DEEP_LINK_MANAGER"
    const val MIN_INTENT_TIME = 5 * 1000

    const val SOURCE_ID_UNKNOW = "unknow"
    const val SOURCE_ID_OWN = "own"
    const val SOURCE_ID_FACEBOOK = "facebook"

    private val mInitializerTime = System.currentTimeMillis()

    private fun isDetected() = DataManager.Local.getDeferredDeepLinkRan()
    private fun markDetected() = DataManager.Local.saveDeferredDeepLinkRan(true)
    private fun isOpened() = DataManager.Local.getDeferredDeepLinkOpened()
    private fun markOpened() = DataManager.Local.saveDeferredDeepLinkOpened(true)

    fun detectDeepLink() {
        //侦测行为一次安装只运行一次
        if (isDetected()) {
            return
        }
        markDetected()
        //开始侦测
        loadPLinkFromFaceBook()
        loadPLinkFromAppsFlyer()
        reportPLink(sourceID = SOURCE_ID_OWN)
    }

    private fun loadPLinkFromAppsFlyer() {
        AppsFlyerLib.getInstance().registerConversionListener(CoreApp.getInstance(), object : AppsFlyerConversionListener {
            override fun onInstallConversionDataLoaded(conversionData: Map<String, String>) {
                Logger.tag(LOGGER_TAG).d("AF conversionData : $conversionData")
                AppsFlyerLib.getInstance().unregisterConversionListener()

                val afStatus = conversionData.getOrDefault("af_status", "organic")
                var sourceID = conversionData.getOrDefault("media_source", SOURCE_ID_UNKNOW)
                val analyticsDurationStr = generateAnalyticsDurationStr()

                if (afStatus == "Non-organic") {
                    if (sourceID != SOURCE_ID_UNKNOW) {
                        AnalyticsManager.logEvent(AnalyticsKey.Event.DEFERRED_DEEP_LINK,
                                "non_organic_install_${sourceID}_$analyticsDurationStr")
                    } else {
                        AnalyticsManager.logEvent(AnalyticsKey.Event.DEFERRED_DEEP_LINK,
                                "non_organic_install_$analyticsDurationStr")
                    }
                } else {
                    sourceID = "organic"
                    AnalyticsManager.logEvent(AnalyticsKey.Event.DEFERRED_DEEP_LINK,
                            "organic_install_$analyticsDurationStr")
                }

                try {
                    val jsonMap = hashMapOf<String, String?>()
                    jsonMap.putAll(conversionData)
                    reportPLink(type = DataDictionary.DeepLinkReportType.REPORT_AF_JSON.value,
                            jsonMap = jsonMap,
                            sourceID = sourceID)
                } catch (e: Exception) {
//                    Crashlytics.logException(e)
                }
            }

            override fun onInstallConversionFailure(error: String?) {
                AppsFlyerLib.getInstance().unregisterConversionListener()
            }

            override fun onAppOpenAttribution(conversionData: Map<String, String>) {

            }

            override fun onAttributionFailure(error: String) {

            }
        })
    }

    private fun loadPLinkFromFaceBook() {
        AppLinkData.fetchDeferredAppLinkData(CoreApp.getInstance(), { it: AppLinkData? ->
            Logger.d("loadPLinkFromFaceBook AppLinkData : $it")
            Logger.d("loadPLinkFromFaceBook AppLinkData targetUri : ${it?.targetUri}")

            val url = if (it?.targetUri != null) it.targetUri?.toString() else SOURCE_ID_UNKNOW
            reportPLink(type = DataDictionary.DeepLinkReportType.REPORT_FB_URL.value,
                    url = url,
                    sourceID = SOURCE_ID_FACEBOOK)

            if (it != null && !url.isNullOrBlank() && url != SOURCE_ID_UNKNOW) {
                //友盟统计
                val analyticsDurationStr = generateAnalyticsDurationStr()
                AnalyticsManager.logEvent(AnalyticsKey.Event.DEFERRED_DEEP_LINK,
                        "fb_sdk_callback_$analyticsDurationStr")
            }
        })
    }

    private fun reportPLink(type: Int = DataDictionary.DeepLinkReportType.LAUNCH_FETCH.value,
                            jsonMap: Map<String, String?>? = null,
                            url: String? = null,
                            sourceID: String) {
        DataManager.Remote.pLinkReport(
                PLinkRequest(type = type, jsonMap = jsonMap, url = url))
                .flatMapSingle { Single.just(it).delay(it.delaySeconds.toLong(), TimeUnit.SECONDS) }
                .ioToMain()
                .subscribeBy(
                        onNext = {
                            Logger.tag(LOGGER_TAG).d("reportPLink response : $it")
                            if (it.openUrl.isNullOrBlank()) {
                                return@subscribeBy
                            }
                            open(it, sourceID)
                        },
                        onError = {
                            Logger.e(it)
                        }
                )
    }

    /**
     * 打开PLink返回的结果.
     */
    @UiThread
    private fun open(pLinkResponse: PLinkResponse, sourceID: String) {
        try {
            Logger.tag(LOGGER_TAG).d("run open")
            //只生效一次，之后不再生效
            if (isOpened()) {
                return
            }
            markOpened()
            //计算初始化到当前经过时间，5s以内直接跳转，5s以外做通知处理
            val elapsedTime = System.currentTimeMillis() - mInitializerTime
            Logger.tag(LOGGER_TAG).d("run open elapsedTime : $elapsedTime")
            //生产友盟统计用的时间字段
            val analyticsDurationStr = generateAnalyticsDurationStr(elapsedTime)
            if (elapsedTime <= MIN_INTENT_TIME) {
                //时间范围内，直接做跳转处理
                OpenUrlManager.checkOpenUrl(pLinkResponse.openUrl ?: return)
                //友盟统计
                AnalyticsManager.logEvent(AnalyticsKey.Event.DEFERRED_DEEP_LINK,
                        "open_${sourceID}_$analyticsDurationStr")
            } else {
                //时间范围外，如果有title，用通知样式展示
                if (!pLinkResponse.title.isNullOrBlank()) {
                    //超时有title的场景
                    showIntentDialog(pLinkResponse)
                    AnalyticsManager.logEvent(AnalyticsKey.Event.DEFERRED_DEEP_LINK,
                            "open_with_title_${sourceID}_$analyticsDurationStr")
                } else {
                    //超时没有title的场景
                    AnalyticsManager.logEvent(AnalyticsKey.Event.DEFERRED_DEEP_LINK,
                            "no_open_without_title_${sourceID}_$analyticsDurationStr")
                }
            }
        } catch (e: Exception) {
            Logger.e(e)
//            Crashlytics.logException(e)
        }
    }

    /**
     * 展示跳转提示窗
     */
    @UiThread
    private fun showIntentDialog(pLinkResponse: PLinkResponse) {
        try {
            Logger.tag(LOGGER_TAG).d("run showIntentDialog.pLinkResponse : $pLinkResponse")
            val context = CoreApp.getLastBaseActivityInstance() ?: return
            val openUrl = pLinkResponse.openUrl ?: return
            val title = pLinkResponse.title ?: return
            if (title.isBlank()) {
                return
            }
            AlertDialog.Builder(context)
                    .setMessage(title)
                    .setPositiveButton(R.string.WorldCup2018_PopupToWorldCupTab, { dialog, _ ->
                        OpenUrlManager.checkOpenUrl(openUrl)
                        dialog.dismiss()
                    })
                    .setNegativeButton(R.string.Common_Cancel, { dialog, _ ->
                        dialog.dismiss()
                    })
                    .setCancelable(false)
                    .show()
        } catch (e: Exception) {
            Logger.e(e)
//            Crashlytics.logException(e)
        }
    }

    private fun generateAnalyticsDurationStr(
            elapsedTimeMilliS: Long = System.currentTimeMillis() - mInitializerTime
    ): String {
        val analyticsDuration = (elapsedTimeMilliS / 1000).toInt()
        return if (analyticsDuration > 30) "30+" else analyticsDuration.toString()
    }
}

