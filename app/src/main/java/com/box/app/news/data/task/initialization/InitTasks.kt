package com.box.app.news.data.task.initialization

import com.box.app.news.R
import com.box.app.news.ab.ABManager
import com.box.app.news.applog.AppLogManager
import com.box.app.news.bean.AppConfig
import com.box.app.news.bean.Channel
import com.box.app.news.bean.list.ListChannel
import com.box.app.news.bean.list.ListRegion
import com.box.app.news.data.DataDictionary
import com.box.app.news.data.DataManager
import com.box.app.news.data.source.local.db.DBAPI
import com.box.app.news.data.source.local.preference.PreferenceAPI
import com.box.app.news.event.EventManager
import com.box.app.news.event.change.HideOrShowNewChannelTipEvent
import com.box.app.news.event.refresh.UpdateArticleVideoChannelEvent
import com.box.app.news.util.LaunchUtils
import com.box.app.news.util.ReddotUtils
import com.box.common.core.CoreApp
import com.box.common.core.log.Logger
import com.box.common.core.rx.schedulers.io
import com.box.common.core.util.ResUtils
import com.crashlytics.android.Crashlytics
import com.google.firebase.perf.metrics.AddTrace
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy

open class InitTasks {

    companion object {
        /**
         * Logcat标签
         */
        const val LOGGER_TAG = "INIT_TASKS"
    }

    fun start(key: InitTaskKey) {
        when (key) {
            InitTaskKey.INIT_DB -> initDB()
            InitTaskKey.INIT_CACHE -> initCache()
            InitTaskKey.INIT_APP_LOG_DATA -> initAppLogData()
            InitTaskKey.INIT_WEATHER_REGION_LIST -> initWeatherRegionList()
            InitTaskKey.INIT_NEWS_CHANNEL_LIST_LOCAL -> initNewsChannelListFromLocal()
            //7.1 这个初始化不在启动任务里做
//            InitTaskKey.INIT_NEWS_CHANNEL_LIST_REMOTE -> initNewsChannelListFromRemote()
            InitTaskKey.APP_CONFIG -> initAppConfigWithAB()
            InitTaskKey.APP_LOG_CONFIG -> initAppLogConfig()
            InitTaskKey.CLEAN_DB -> cleanDB()
        }
    }

    @AddTrace(name = "InitDataManager_InitTasks", enabled = true)
    fun startDefault() {
        start(InitTaskKey.INIT_DB)
        start(InitTaskKey.INIT_CACHE)
        start(InitTaskKey.INIT_APP_LOG_DATA)
        start(InitTaskKey.INIT_NEWS_CHANNEL_LIST_LOCAL)
        //7.1 这个初始化不在启动任务里做
//        start(InitTaskKey.INIT_NEWS_CHANNEL_LIST_REMOTE)
        start(InitTaskKey.CLEAN_DB)
    }

    fun startAll() {
        InitTaskKey.values().forEach { start(it) }
    }

    @AddTrace(name = "InitDataManager_InitTasks_InitDB", enabled = true)
    fun initDB() {
        DBAPI.connectAllDB().io().subscribeBy(
                onError = { e ->
                    Logger.e(e, "InitDB fail")
                }
        )
    }

    fun initAppLogData() {
        AppLogManager.init()
    }

    @AddTrace(name = "InitDataManager_InitTasks_CleanDB", enabled = true)
    fun cleanDB() {
        if (LaunchUtils.isVersionFirstLaunch) {
            DataManager.Local.cleanDBForce()
            return
        }
        val lastCleanTime = DataManager.Local.getLastCleanDataBaseTime()
        if (lastCleanTime < 0) {
            DataManager.Local.saveLastCleanDataBaseTime(System.currentTimeMillis())
            return
        }
        val appConfig = DataManager.Memory.getAppConfig()
        val curTime = System.currentTimeMillis()
        if (curTime - lastCleanTime > (appConfig.autoCleanCacheMinInterval * 1000)) {
            DataManager.Local.cleanDataBase()
            DataManager.Local.saveLastCleanDataBaseTime(curTime)
        }
    }

    @AddTrace(name = "InitDataManager_InitTasks_InitCache", enabled = true)
    fun initCache() {
        Completable.fromCallable {
            DataManager.Cache.transferArticleContentDiskToCache()
        }.io().subscribeBy(
                onComplete = {
                    Logger.d("InitCache Success")
                },
                onError = {
                    Logger.d("InitCache Fail")
                }
        )
    }

    /**
     * 先从本地加载天气城市列表进内存
     * 之后从远程获取，更新本地和内存
     */
    @AddTrace(name = "InitDataManager_InitTasks_InitWeatherRegionList", enabled = true)
    fun initWeatherRegionList() {
        //先从本次放进内存缓存
        val local = DataManager.Local.getRegionList()
                .onErrorReturn { ListRegion() }
                .doOnNext { data -> DataManager.Memory.putRegionList(data) }//更新内存
        //再从远端更新
        val remote = DataManager.Remote.getWeatherCityList()
                .onErrorResumeNext(Observable.empty()) //发生错误时用空发射序列替代
                .doOnNext { data ->
                    if (data.regions.isNotEmpty()) { //有效的远程结果
                        DataManager.Local.saveRegionList(data) //更新本地内容
                        DataManager.Memory.putRegionList(data) //更新内存
                    }
                }
        //依次处理
        Observable.concat(local, remote).io().subscribeBy(onError = {})
    }

    /**
     * 初始化新闻频道列表
     * 只加载本地保存的内容到内存
     */
    @AddTrace(name = "InitDataManager_InitTasks_InitNewsChannelListFromLocal", enabled = true)
    fun initNewsChannelListFromLocal() {
        //推荐频道由客户端自行添加
        val articleRecommendChannel = Channel(DataDictionary.CHANNEL_ID_RECOMMEND_ARTICLE, ResUtils.getString(R.string.Channel_ForYou), index = 0)
        val videoRecommendChannel = Channel(DataDictionary.CHANNEL_ID_RECOMMEND_VIDEO, ResUtils.getString(R.string.Channel_ForYou), index = 0)
//        val worldcupRecommendChannel = Channel(DataDictionary.CHANNEL_ID_RECOMMEND_WORLDCUP, ResUtils.getString(R.string.WorldCup2018_Home), index = 0)

        //从文件中读取列表
        val fastLoadListChannel = DataManager.Local.getFileChannelList() ?: ListChannel()

        val articleEmpty = fastLoadListChannel.articleChannels.isEmpty()
        val videoEmpty = fastLoadListChannel.videoChannels.isEmpty()
//        val worldCupEmpty = fastLoadListChannel.worldcupChannels.isEmpty()

        if (articleEmpty || videoEmpty) {
            //从预置的Raw文件中读取列表
            val rawListChannel = DataManager.Local.getRawChannelList()
            if (articleEmpty) {
                fastLoadListChannel.articleChannels.addAll(rawListChannel.articleChannels)
            }
            if (videoEmpty) {
                fastLoadListChannel.videoChannels.addAll(rawListChannel.videoChannels)
            }
        }

        //过滤article
        val articleFastLoadChannels = fastLoadListChannel.articleChannels.filter {
            it.channelType == DataDictionary.ChannelType.ARTICLE.value ||
                    it.channelType == DataDictionary.ChannelType.VIDEO.value ||
                    it.channelType == DataDictionary.ChannelType.IMAGE.value ||
                    it.channelType == DataDictionary.ChannelType.GIF.value ||
                    it.channelType == DataDictionary.ChannelType.PUBLIC_FEED.value ||
                    it.channelType == DataDictionary.ChannelType.TWITTER_VIDEO.value
        }.toMutableList()
        articleFastLoadChannels.removeAll { it.chid == DataDictionary.CHANNEL_ID_RECOMMEND_ARTICLE }
        articleFastLoadChannels.add(0, articleRecommendChannel)
        //过滤video
        val videoFastLoadChannels = fastLoadListChannel.videoChannels.filter {
            it.channelType == DataDictionary.ChannelType.ARTICLE.value ||
                    it.channelType == DataDictionary.ChannelType.VIDEO.value ||
                    it.channelType == DataDictionary.ChannelType.IMAGE.value ||
                    it.channelType == DataDictionary.ChannelType.GIF.value ||
                    it.channelType == DataDictionary.ChannelType.TWITTER_VIDEO.value
        }.toMutableList()
        videoFastLoadChannels.removeAll { it.chid == DataDictionary.CHANNEL_ID_RECOMMEND_VIDEO }
        videoFastLoadChannels.add(0, videoRecommendChannel)

//        val worldcupFastLoadChannels = fastLoadListChannel.worldcupChannels.filter {
//            it.channelType == DataDictionary.ChannelType.ARTICLE.value ||
//                    it.channelType == DataDictionary.ChannelType.VIDEO.value ||
//                    it.channelType == DataDictionary.ChannelType.IMAGE.value ||
//                    it.channelType == DataDictionary.ChannelType.TWITTER_VIDEO.value ||
//                    it.channelType == DataDictionary.ChannelType.WEB.value ||
//                    it.channelType == DataDictionary.ChannelType.PUBLIC_FEED.value ||
//                    it.channelType == DataDictionary.ChannelType.BOARD.value
//        }.toMutableList()
//        worldcupFastLoadChannels.removeAll { it.chid == DataDictionary.CHANNEL_ID_RECOMMEND_WORLDCUP }
//        worldcupFastLoadChannels.add(0, worldcupRecommendChannel)

        //构建结果
        Logger.d("Init Channel fastLoadListChannel : $fastLoadListChannel")
        fastLoadListChannel.articleChannels = ArrayList(articleFastLoadChannels)
        fastLoadListChannel.videoChannels = ArrayList(videoFastLoadChannels)
//        fastLoadListChannel.worldcupChannels = ArrayList(worldcupFastLoadChannels)

        //放入内存缓存
        Logger.d("cache2:${fastLoadListChannel.articleChannels}")
        DataManager.Memory.putChannelList(fastLoadListChannel)
    }

    /**
     * 初始化新闻频道列表
     * 从远程获取，更新本地和内存
     */
    @AddTrace(name = "InitDataManager_InitTasks_InitNewsChannelListFromRemote", enabled = true)
    fun initNewsChannelListFromRemote() {
        DataManager.Remote.getChannelList()
                .onErrorResumeNext(Observable.empty())
                .doOnNext { data ->

                    //推荐频道由客户端自行添加
                    val articleRecommendChannel = Channel(DataDictionary.CHANNEL_ID_RECOMMEND_ARTICLE, ResUtils.getString(R.string.Channel_ForYou), index = 0)
                    val videoRecommendChannel = Channel(DataDictionary.CHANNEL_ID_RECOMMEND_VIDEO, ResUtils.getString(R.string.Channel_ForYou), index = 0)
//                    val worldcupRecommendChannel = Channel(DataDictionary.CHANNEL_ID_RECOMMEND_WORLDCUP, ResUtils.getString(R.string.WorldCup2018_Home), index = 0)

                    if (data.selectedChannels.articleChannels.isEmpty() && data.selectedChannels.videoChannels.isEmpty()/* && data.worldcupChannels.isEmpty()*/) {
                        return@doOnNext
                    }

                    val articleRemoteChannels = data.selectedChannels.articleChannels
                    val articleFilterRemoteChannels = articleRemoteChannels.filter {
                        it.channelType == DataDictionary.ChannelType.ARTICLE.value ||
                                it.channelType == DataDictionary.ChannelType.VIDEO.value ||
                                it.channelType == DataDictionary.ChannelType.IMAGE.value ||
                                it.channelType == DataDictionary.ChannelType.GIF.value ||
                                it.channelType == DataDictionary.ChannelType.PUBLIC_FEED.value ||
                                it.channelType == DataDictionary.ChannelType.TWITTER_VIDEO.value
                    }.toMutableList()
                    articleFilterRemoteChannels.removeAll { it.chid == DataDictionary.CHANNEL_ID_RECOMMEND_ARTICLE }
                    articleFilterRemoteChannels.add(0, articleRecommendChannel)
                    for (i in articleFilterRemoteChannels.indices) {
                        articleFilterRemoteChannels[i].index = i
                    }
                    data.selectedChannels.articleChannels = ArrayList(articleFilterRemoteChannels)

                    val videoRemoteChannels = data.selectedChannels.videoChannels
                    val videoFilterRemoteChannels = videoRemoteChannels.filter {
                        it.channelType == DataDictionary.ChannelType.ARTICLE.value ||
                                it.channelType == DataDictionary.ChannelType.VIDEO.value ||
                                it.channelType == DataDictionary.ChannelType.IMAGE.value ||
                                it.channelType == DataDictionary.ChannelType.GIF.value ||
                                it.channelType == DataDictionary.ChannelType.TWITTER_VIDEO.value
                    }.toMutableList()
                    videoFilterRemoteChannels.removeAll { it.chid == DataDictionary.CHANNEL_ID_RECOMMEND_VIDEO }
                    videoFilterRemoteChannels.add(0, videoRecommendChannel)
                    for (i in videoFilterRemoteChannels.indices) {
                        videoFilterRemoteChannels[i].index = i
                    }
                    data.selectedChannels.videoChannels = ArrayList(videoFilterRemoteChannels)

//                    val worldRemoteChannels = data.worldcupChannels
//                    val worldcupFilterRemoteChannels = worldRemoteChannels.filter {
//                        it.channelType == DataDictionary.ChannelType.ARTICLE.value ||
//                        it.channelType == DataDictionary.ChannelType.VIDEO.value ||
//                        it.channelType == DataDictionary.ChannelType.IMAGE.value ||
//                        it.channelType == DataDictionary.ChannelType.GIF.value ||
//                        it.channelType == DataDictionary.ChannelType.TWITTER_VIDEO.value ||
//                        it.channelType == DataDictionary.ChannelType.WEB.value ||
//                        it.channelType == DataDictionary.ChannelType.PUBLIC_FEED.value ||
//                        it.channelType == DataDictionary.ChannelType.BOARD.value
//                    }.toMutableList()
//                    worldcupFilterRemoteChannels.removeAll { it.chid == DataDictionary.CHANNEL_ID_RECOMMEND_VIDEO }
//                    worldcupFilterRemoteChannels.add(0, worldcupRecommendChannel)
//                    for (i in worldcupFilterRemoteChannels.indices) {
//                        videoFilterRemoteChannels[i].index = i
//                    }
//                    data.worldcupChannels = ArrayList(worldcupFilterRemoteChannels)

                    Logger.d("Init Channel remoteLoadListChannel : $data")
                    //更新内存
                    DataManager.Memory.putRecommendChannelList(data.recommendChannels)
                    DataManager.Memory.putChannelList(data.selectedChannels)
                    //更新文件
                    DataManager.Local.saveFileChannelList(data.selectedChannels)
                    //更新数据库
                    val saveChannels = arrayListOf<Channel>()
                    saveChannels.addAll(data.selectedChannels.articleChannels)
                    saveChannels.addAll(data.selectedChannels.videoChannels)
//                    saveChannels.addAll(data.worldcupChannels)
                    Logger.d("Init Channel remoteSaveChannels : $saveChannels")
                    DataManager.Local.saveChannelList(saveChannels)

                    //发送广播是否展示文章首页频道编辑按钮右上角的新频道提醒
                    //如果推荐频道里有没有存在本地的reddot，则发送广播显示提醒
                    var show = false
                    data.recommendChannels.articleChannels.forEach {
                        if (!ReddotUtils.containRedDot(it.redDot.toString())) {
                            show = true
                        }
                    }
                    if (show) {
                        EventManager.post(HideOrShowNewChannelTipEvent(true))
                    }

                    //更新文章/视频channel
                    EventManager.post(UpdateArticleVideoChannelEvent(data))

                }.io().subscribeBy(onError = {})


        //启动的时候如果数据库没有Channel表的数据，把缓存的结果放进去
        Observable.fromCallable { DataManager.Local.getChannelList().blockingFirst() }
                .onErrorResumeNext(Observable.empty())
                .io()
                .subscribeBy(
                        onNext = { channels: List<Channel> ->
                            if (channels.isEmpty()) {
                                val listChannel = DataManager.Memory.getChannelList()
                                val saveChannels = arrayListOf<Channel>()
                                saveChannels.addAll(listChannel.articleChannels)
                                saveChannels.addAll(listChannel.videoChannels)
//                                saveChannels.addAll(listChannel.worldcupChannels)
                                DataManager.Local.saveChannelList(saveChannels)
                            }
                        },
                        onError = {}
                )
    }

    @AddTrace(name = "InitDataManager_InitTasks_InitAppConfig", enabled = true)
    fun initAppConfigWithAB() {
        //从本地加载到内存中
        val config = DataManager.Local.getAppConfig() ?: AppConfig()
        DataManager.Memory.putAppConfig(config)
        ABManager.updateABInfo(config.abInfo)
        //从远程获取更新
        DataManager.Remote.getAppConfig()
                .io()
                .subscribeBy(
                        onNext = {
                            DataManager.Local.saveAppConfig(it) //更新本地
                            ABManager.updateABInfo(it.abInfo)
                        },
                        onError = {
                            Logger.e(it)
                            Crashlytics.logException(it)
                        }
                )
    }

    fun initAppLogConfig() {
        DataManager.Remote.getAppLogConfig()
                .io()
                .subscribeBy(
                        onNext = { config ->
                            try {
                                AppLogManager.config = config
                                DataManager.Local.saveAppLogConfig(config)
                            } catch (error: Exception) {
                            }
                        },
                        onComplete = {
                            Logger.tag(LOGGER_TAG).d("Run startUpdateAppLogConfig complete!")
                        },
                        onError = { error ->
                            Logger.tag(LOGGER_TAG).d("Run startUpdateAppLogConfig error\n!error :\n$error")
                        })
    }

}