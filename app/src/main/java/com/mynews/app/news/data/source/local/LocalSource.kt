package com.mynews.app.news.data.source.local

import android.os.Looper
import com.mynews.app.news.BuildConfig
import com.mynews.app.news.R
import com.mynews.app.news.bean.*
import com.mynews.app.news.bean.base.BaseNewsBean
import com.mynews.app.news.bean.list.ListChannel
import com.mynews.app.news.bean.list.ListNews
import com.mynews.app.news.bean.list.ListRegion
import com.mynews.app.news.data.DataDictionary
import com.mynews.app.news.data.source.local.db.DBAPI
import com.mynews.app.news.data.source.local.file.FileAPI
import com.mynews.app.news.data.source.local.preference.PreferenceAPI
import com.mynews.app.news.data.source.local.raw.RawAPI
import com.mynews.app.news.data.source.remote.http.url.HttpBaseUrls
import com.mynews.app.news.widget.NewsVideoView
import com.mynews.common.core.util.ResUtils
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

open class LocalSource {

    fun getRawChannelList(): ListChannel {
        val listChannel = RawAPI.getChannels() ?: ListChannel()
        val articleChannels = listChannel.articleChannels
        val articleRecommendChannel = Channel(DataDictionary.CHANNEL_ID_RECOMMEND_ARTICLE, ResUtils.getString(R.string.Channel_ForYou), index = 0)
        articleChannels.add(0, articleRecommendChannel)
        for (i in articleChannels.indices) {
            articleChannels[i].index = i
        }
        val videoChannels = listChannel.videoChannels
        val videoRecommendChannel = Channel(DataDictionary.CHANNEL_ID_RECOMMEND_VIDEO, ResUtils.getString(R.string.Channel_ForYou), index = 0)
        videoChannels.add(0, videoRecommendChannel)
        for (i in videoChannels.indices) {
            videoChannels[i].index = i
        }
        return listChannel
    }

    fun getFileChannelList(): ListChannel? {
        return FileAPI.getChannels()
    }

    fun getChannelList(): Observable<List<Channel>> {
        return DBAPI.ARTICLE_DB.selectFromChannel()
                .orderByIndexAsc()
                .executeAsObservable()
                .toList()
                .toObservable()
    }

    fun saveFileChannelList(listChannel: ListChannel) {
        FileAPI.saveChannels(listChannel)
    }

    fun saveChannelList(channels: List<Channel>) {
        DBAPI.ARTICLE_DB.deleteFromChannel().execute()
        DBAPI.ARTICLE_DB.relationOfChannel().inserter().executeAll(channels)
    }

    fun getRegionList(): Observable<ListRegion> {
        return Observable.fromCallable {
            FileAPI.getRegions() ?: RawAPI.getRegions() ?: ListRegion()
        }
    }

    fun saveRegionList(listRegion: ListRegion) {
        return FileAPI.saveRegions(listRegion)
    }

    fun getPushArrival(): PushArrival {
        return FileAPI.getPushArrival() ?: PushArrival()
    }

    fun savePushArrival(arrival: PushArrival) {
        return FileAPI.savePushArrival(arrival)
    }

    fun getNewsFeed(chid: String, minEmitTime: Long?, maxEmitTime: Long?): Observable<ListNews> {
        //指定频道下按发布时间倒序取15条
        return DBAPI.ARTICLE_DB.selectFromNewsListOrder()
                .chidEq(chid)
                .orderByEmitTimeDesc()
                .limit(15)
                .executeAsObservable()
                .map { it: NewsListOrder ->
                    when (it.type) {
                        DataDictionary.NewsType.ARTICLE.value -> {
                            DBAPI.ARTICLE_DB.selectFromArticle().aidEq(it.aid).getOrNull(0)
                        }
                        DataDictionary.NewsType.VIDEO.value -> {
                            DBAPI.ARTICLE_DB.selectFromVideo().aidEq(it.aid).getOrNull(0)
                        }
                        else -> {
                            Unit
                        }
                    }
                }
                .filter { it is BaseNewsBean }
                .map { it as BaseNewsBean }
                .toList()
                .map { it -> ListNews(news = ArrayList(it)) }
                .toObservable()
    }

    fun getFontSize(): DataDictionary.FontSize {
        return PreferenceAPI.getFontSize()
    }

    fun saveFontSize(size: DataDictionary.FontSize) {
        PreferenceAPI.saveFontSize(size)
    }

    fun getUniqueDeviceId(): String {
        return PreferenceAPI.getUniqueDeviceId()
    }

    fun saveUniqueDeviceId(id: String) {
        return PreferenceAPI.saveUniqueDeviceId(id)
    }

    fun getUserLastSelectedRegion(): Region {
        return PreferenceAPI.getRegion()
    }

    fun saveUserLastSelectedRegion(region: Region) {
        return PreferenceAPI.saveRegion(region)
    }

    fun getPushTokenSubmit(): Boolean {
        return PreferenceAPI.getPushTokenSubmit()
    }

    fun savePushTokenSubmit(submit: Boolean) {
        return PreferenceAPI.savePushTokenSubmit(submit)
    }

    fun getAppLogConfig(): AppLogConfig {
        return PreferenceAPI.getAppLogConfig()
    }

    fun saveAppLogConfig(config: AppLogConfig) {
        PreferenceAPI.saveAppLogConfig(config)
    }

    fun saveLastKnownLatitude(latitude: Float) {
        PreferenceAPI.saveLastKnownLatitude(latitude)
    }

    fun getLastKnownLatitude(): Float {
        return PreferenceAPI.getLastKnownLatitude()
    }

    fun saveLastKnownLongitude(longitude: Float) {
        PreferenceAPI.saveLastKnownLongitude(longitude)
    }

    fun getLastKnownLongitude(): Float {
        return PreferenceAPI.getLastKnownLatitude()
    }

    fun saveNewsFeed(chid: String, newsFeed: List<BaseNewsBean>): Completable {
        return DBAPI.ARTICLE_DB.transactionAsCompletable {
            newsFeed.forEach {
                when (it) {
                    is Article -> {
                        DBAPI.ARTICLE_DB.relationOfArticle().upsert(it)
                    }
                    is Video -> {
                        DBAPI.ARTICLE_DB.relationOfVideo().upsert(it)
                    }
                }
                val order = NewsListOrder(
                        orderID = "${chid}_${it.aid}",
                        chid = chid,
                        aid = it.aid,
                        type = it.type,
                        style = it.style,
                        emitTime = it.emitTime)
                DBAPI.ARTICLE_DB.relationOfNewsListOrder().upsert(order)
            }
        }
    }

//    @Deprecated("用Cache进行替换")
//    fun saveArticleFeedContent(article: Article): Single<Int> {
//        return DBAPI.ARTICLE_DB.relationOfArticle()
//                .updater()
//                .aidEq(article.aid)
//                .content(article.content)
//                .title(article.title)
//                .updateTime(article.updateTime)
//                .executeAsSingle()
//    }

    fun saveArticleFeedTitleAndUpdateTime(article: Article): Single<Int> {
        return DBAPI.ARTICLE_DB.relationOfArticle()
                .updater()
                .aidEq(article.aid)
                .title(article.title)
                .updateTime(article.updateTime)
                .executeAsSingle()
    }

    fun getAppConfig(): AppConfig? {
        return PreferenceAPI.getAppConfig()
    }

    fun saveAppConfig(config: AppConfig) {
        PreferenceAPI.saveAppConfig(config)
    }

    fun deleteArticleListOrder(news: BaseNewsBean, channel: Channel): Single<Int> {
        return DBAPI.ARTICLE_DB.relationOfNewsListOrder().deleter()
                .orderIDEq("${channel.chid}_${news.aid}")
                .executeAsSingle()
    }

    /**
     * 清理数据库缓存，定时调用
     */
    fun cleanDataBase(): Completable {
        return DBAPI.ARTICLE_DB.transactionAsCompletable {
            //需要保留的文章id列表
            val saveArticleIds = hashSetOf<String>()
            //需要保留的视频id列表
            val saveVideoIds = hashSetOf<String>()
            //选出保存的所有频道
            val channels = DBAPI.ARTICLE_DB.selectFromChannel().toList()
            channels.add(0, Channel(chid = DataDictionary.CHANNEL_ID_RECOMMEND_ARTICLE))
            //遍历频道
            for (channel in channels) {
                //当前频道下按时间倒序取20个结果用来保存
                val saveOrders = DBAPI.ARTICLE_DB.selectFromNewsListOrder()
                        .chidEq(channel.chid)
                        .orderByEmitTimeDesc()
                        .limit(20)
                        .toList()
                //没有结果时返回
                if (saveOrders.isEmpty()) {
                    continue
                }
                //提取id
                val saveOrderID = saveOrders.map {
                    it.orderID
                }
                //删除不在保留id列表中的结果
                DBAPI.ARTICLE_DB.deleteFromNewsListOrder()
                        .chidEq(channel.chid)
                        .orderIDNotIn(saveOrderID)
                        .execute()
                //将保留的文章id汇总
                saveOrders.map {
                    when (it.type) {
                        DataDictionary.NewsType.ARTICLE.value -> {
                            saveArticleIds.add(it.aid)
                        }
                        DataDictionary.NewsType.VIDEO.value -> {
                            saveVideoIds.add(it.aid)
                        }
                        else -> {
                        }
                    }
                }
            }
            //删除文章数据
            if (saveArticleIds.isNotEmpty()) {
                DBAPI.ARTICLE_DB.deleteFromArticle()
                        .aidNotIn(saveArticleIds)
                        .execute()
            }
            //删除视频数据
            if (saveVideoIds.isNotEmpty()) {
                DBAPI.ARTICLE_DB.deleteFromArticle()
                        .aidNotIn(saveVideoIds)
                        .execute()
            }
        }
    }

    fun cleanDBForce() {
        Observable.create(ObservableOnSubscribe<Any?> {
            DBAPI.ARTICLE_DB.deleteAll()
            DBAPI.APP_LOG_DB.deleteAll()
        })
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    fun getLastCleanDataBaseTime(): Long {
        return PreferenceAPI.getLastCleanDataBaseTime()
    }

    fun saveLastCleanDataBaseTime(timeInMillis: Long) {
        PreferenceAPI.saveLastCleanDataBaseTime(timeInMillis)
    }

    fun getBaseUrl(): String {
        return if (BuildConfig.BUILD_TYPE == "debug") {
            PreferenceAPI.getBaseUrl()
        } else {
            HttpBaseUrls.getReleaseUrl()
        }
    }

    fun saveBaseUrl(url: String) {
        PreferenceAPI.saveBaseUrl(url)
    }

    fun saveVideoClarity(clarity: NewsVideoView.Clarity) {
        PreferenceAPI.saveVideoClarity(clarity)
    }

    fun getVideoClarity(): NewsVideoView.Clarity {
        return PreferenceAPI.getVideoClarity()
    }

    fun getArticleTransCodeCss(): String {
        var css = FileAPI.getArticleTransCodeCss()
        if (css.isBlank()) {
            css = RawAPI.getArticleTransCodeCss() ?: ""
        }
        return css
    }

    fun saveArticleTransCodeCss(css: String) {
        FileAPI.saveArticleTransCodeCss(css)
    }

    fun getArticleTransCodeJs(): String {
        var js = FileAPI.getArticleTransCodeJs()
        if (js.isBlank()) {
            js = RawAPI.getArticleTransCodeJS() ?: ""
        }
        return js
    }

    fun saveArticleTransCodeJs(js: String) {
        FileAPI.saveArticleTransCodeJs(js)
    }

    fun getArticleTransCodeJsLang(): String {
        var jsLang = FileAPI.getArticleTransCodeJSLang()
        if (jsLang.isBlank()) {
            jsLang = RawAPI.getArticleTransCodeJSLang() ?: ""
        }
        return jsLang
    }

    fun saveArticleTransCodeJsLang(js: String) {
        FileAPI.saveArticleTransCodeJsLang(js)
    }

    fun getArticleTransCodeCssInfo(): HtmlResource {
        return PreferenceAPI.getArticleTransCodeCssInfo()
    }

    fun saveArticleTransCodeCssInfo(css: HtmlResource) {
        PreferenceAPI.saveArticleTransCodeCssInfo(css)
    }

    fun getArticleTransCodeJsInfo(): HtmlResource {
        return PreferenceAPI.getArticleTransCodeJsInfo()
    }

    fun saveArticleTransCodeJsInfo(js: HtmlResource) {
        PreferenceAPI.saveArticleTransCodeJsInfo(js)
    }

    fun getArticleTransCodeJsLangInfo(): HtmlResource {
        return PreferenceAPI.getArticleTransCodeJsLangInfo()
    }

    fun saveArticleTransCodeJsLangInfo(jsLang: HtmlResource) {
        PreferenceAPI.saveArticleTransCodeJsLangInfo(jsLang)
    }

    fun saveAccountIsLogin(isLogin: Boolean) {
        PreferenceAPI.saveAccountIsLogin(isLogin)
    }

    fun getAccountIsLogin(): Boolean {
        return PreferenceAPI.getAccountIsLogin()
    }

    fun saveAccountPlatform(platform: Int) {
        PreferenceAPI.saveAccountPlatform(platform)
    }

    fun getAccountPlatform(): Int {
        return PreferenceAPI.getAccountPlatform()
    }

    fun saveAccountInfo(account: Account?) {
        PreferenceAPI.saveAccountInfo(account)
    }

    fun getAccountInfo(): Account? {
        return PreferenceAPI.getAccountInfo()
    }

    fun savePushUseSound(useSound: Boolean) {
        PreferenceAPI.savePushUseSound(useSound)
    }

    fun getPushUseSound(): Boolean {
        return PreferenceAPI.getPushUseSound()
    }

    fun saveDeferredDeepLinkOpened(opened: Boolean) {
        PreferenceAPI.saveDeferredDeepLinkOpened(opened)
    }

    fun getDeferredDeepLinkOpened(): Boolean {
        return PreferenceAPI.getDeferredDeepLinkOpened()
    }

    fun saveDeferredDeepLinkRan(ran: Boolean) {
        PreferenceAPI.saveDeferredDeepLinkRan(ran)
    }

    fun getDeferredDeepLinkRan(): Boolean {
        return PreferenceAPI.getDeferredDeepLinkRan()
    }

    fun saveDebugTranslateContent(isTranslate: Boolean) {
        PreferenceAPI.saveDebugTranslateContent(isTranslate)
    }

    fun getDebugTranslateContent(): Boolean {
        return PreferenceAPI.getDebugTranslateContent()
    }

    fun saveDebugShowRefer(show: Boolean) {
        PreferenceAPI.saveDebugShowRefer(show)
    }

    fun getDebugShowRefer(): Boolean {
        return PreferenceAPI.getDebugShowRefer()
    }

    fun saveDebugAdTestDevice(show: Boolean) {
        PreferenceAPI.saveDebugAdTestDevice(show)
    }

    fun getDebugAdTestDevice(): Boolean {
        return PreferenceAPI.getDebugAdTestDevice()
    }

    fun saveDebugAdShowId(show: Boolean) {
        PreferenceAPI.saveDebugAdShowId(show)
    }

    fun getDebugAdShowId(): Boolean {
        return PreferenceAPI.getDebugAdShowId()
    }

    fun saveDebugListAdTest(test: Boolean) {
        PreferenceAPI.saveDebugListAdTest(test)
    }

    fun getDebugListAdTest(): Boolean {
        return PreferenceAPI.getDebugListAdTest()
    }

    fun saveLastUploadDeviceAppInfoTimeFast(time: Long) {
        PreferenceAPI.saveLastUploadDeviceAppInfoTimeFast(time)
    }

    fun getLastUploadDeviceAppInfoTimeFast(): Long {
        return PreferenceAPI.getLastUploadDeviceAppInfoTimeFast()
    }

    fun saveLastUploadDeviceAppInfoTimeAll(time: Long) {
        PreferenceAPI.saveLastUploadDeviceAppInfoTimeAll(time)
    }

    fun getLastUploadDeviceAppInfoTimeAll(): Long {
        return PreferenceAPI.getLastUploadDeviceAppInfoTimeAll()
    }

    fun saveAdvertisingId(id: String) {
        PreferenceAPI.saveAdvertisingId(id)
    }

    fun getAdvertisingId(): String {
        return PreferenceAPI.getAdvertisingId()
    }

    fun saveShowPushDialogWhenLock(show: Boolean) {
        PreferenceAPI.saveShowPushDialogWhenLock(show)
    }

    fun getShowPushDialogWhenLock(): Boolean {
        return PreferenceAPI.getShowPushDialogWhenLock()
    }

    fun saveRedDot(redDotListString: String) {
        PreferenceAPI.saveRedDot(redDotListString)
    }

    fun getRedDot(): String = PreferenceAPI.getRedDot()
}