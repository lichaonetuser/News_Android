package com.box.app.news.page.mvp.layer.main.article

import android.os.Bundle
import com.box.app.news.analytics.AnalyticsKey
import com.box.app.news.applog.AppLogKey
import com.box.app.news.applog.AppLogManager
import com.box.app.news.bean.Channel
import com.box.app.news.bean.list.ListChannel
import com.box.app.news.data.DataManager
import com.box.app.news.data.task.initialization.InitTaskKey
import com.box.app.news.event.EventManager
import com.box.app.news.event.change.HideOrShowNewChannelTipEvent
import com.box.app.news.event.refresh.ChannelListChangeEvent
import com.box.app.news.event.refresh.NewsListRefreshEvent
import com.box.app.news.event.refresh.SwitchSearchWordEvent
import com.box.app.news.event.refresh.UpdateArticleVideoChannelEvent
import com.box.app.news.page.mvp.layer.main.article.search.SearchFragment
import com.box.app.news.page.mvp.layer.main.article.search.SearchPresenterAutoBundle
import com.box.app.news.page.mvp.layer.main.channel.ChannelEditFragment
import com.box.app.news.proto.AppLog
import com.box.common.core.analytics.AnalyticsManager
import com.box.common.core.rx.schedulers.ioToMain
import com.box.common.extension.app.mvp.base.MVPBasePresenter
import com.crashlytics.android.Crashlytics
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer
import com.yatatsu.autobundle.AutoBundleField
import io.reactivex.rxkotlin.subscribeBy
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

open class ArticlePresenter<V : ArticleContract.View> : MVPBasePresenter<V>(),
        ArticleContract.Presenter<V> {

    @AutoBundleField(required = false)
    var mListChannel = ListChannel()
    protected open val mAnalyticsKey = AnalyticsKey.Event.NEWS
    @AutoBundleField(required = false)
    var mIsFirstVisible = true
    @AutoBundleField(required = false)
    var mDispatchBack = false

    private var mSearchHotWordsIndex: Int = 0
    private var mSearchHotWords = arrayListOf<String>()

    private var mHeadlineCount: Int = 0

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        mView?.setDispatchBack(mDispatchBack)
        if (isNotRestore()) {
            initListChannel()
            val list = getListChannels()
            initCurrentClickedChannel(list[0])
            mView?.setChannels(list)
        } else {
            mView?.setChannels(getListChannels())
        }

        EventManager.register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventManager.unregister(this)
    }

    open fun initCurrentClickedChannel(channel: Channel) {
        DataManager.Memory.putCurrentClickedChannel(channel)

        //记录文章/视频tab当前的频道
        DataManager.Memory.putCurrentArticleChannel(channel)
    }

    open fun getListChannels(listChannel: ListChannel = mListChannel): List<Channel> {
        return listChannel.articleChannels.distinct()
    }

    override fun onClickTab(currentIndex: Int, newIndex: Int) {
        val channel = getListChannels()[newIndex]
        DataManager.Memory.putCurrentClickedChannel(channel)
        if (currentIndex == newIndex) {
            AnalyticsManager.logEvent(mAnalyticsKey, AnalyticsKey.Parameter.CLICK_CHANNEL_TO_REFRESH)
            EventManager.post(NewsListRefreshEvent(channel))
        }
        GSYVideoManager.releaseAllVideos()

        //记录文章/视频tab当前的频道
        when (mAnalyticsKey) {
            AnalyticsKey.Event.NEWS -> { DataManager.Memory.putCurrentArticleChannel(channel) }
            AnalyticsKey.Event.VIDEO -> { DataManager.Memory.putCurrentVideoChannel(channel) }
        }
    }

    override fun onPageSelected(position: Int) {
        AnalyticsManager.logEvent(mAnalyticsKey, AnalyticsKey.Parameter.SWITCH_CHANNEL)
        val channel = getListChannels()[position]
        DataManager.Memory.putCurrentClickedChannel(channel)
        AppLogManager.logEvent(
                name = AppLog.EventName.EVENT_ARTICLE_LIST,
                label = AppLogKey.Label.CLICK_ENTER,
                body = AppLog.EventBody.newBuilder()
                        .setItemId(channel.chid)
                        .setEnterTime(System.currentTimeMillis())
                        .build())

        //记录文章/视频tab当前的频道
        when (mAnalyticsKey) {
            AnalyticsKey.Event.NEWS -> { DataManager.Memory.putCurrentArticleChannel(channel) }
            AnalyticsKey.Event.VIDEO -> { DataManager.Memory.putCurrentVideoChannel(channel) }
        }
    }

    override fun onViewVisible() {
        super.onViewVisible()
        if (mIsFirstVisible) {
            mIsFirstVisible = false
            AppLogManager.logEvent(
                    name = AppLog.EventName.EVENT_ARTICLE_LIST,
                    label = AppLogKey.Label.LAUNCH_ENTER,
                    body = AppLog.EventBody.newBuilder()
                            .setItemId(getListChannels().getOrNull(0)?.chid ?: "")
                            .setEnterTime(System.currentTimeMillis())
                            .build())
        }
        val position = mView?.getCurrentPagePosition() ?: return
        if (getListChannels().isNotEmpty()) {
            val channel = getListChannels()[position]
            DataManager.Memory.putCurrentClickedChannel(channel)

            //记录文章/视频tab当前的频道
            when (mAnalyticsKey) {
                AnalyticsKey.Event.NEWS -> { DataManager.Memory.putCurrentArticleChannel(channel) }
                AnalyticsKey.Event.VIDEO -> { DataManager.Memory.putCurrentVideoChannel(channel) }
            }
        }
    }

    override fun getSearchHotwords(type: Int) {
        DataManager.Remote.getSearchHotwords(type)
                .ioToMain()
                .subscribeBy(onNext = {
                    mSearchHotWordsIndex = 0
                    mSearchHotWords.clear()
                    mSearchHotWords.addAll(it.keywords)
                    switchSearchHotwords()
                })
    }

    // 搜索热门词汇轮流展示
    override fun switchSearchHotwords() {
        if (mSearchHotWords.isEmpty()) {
            mView?.setSearchData("")
            return
        }
        mSearchHotWordsIndex = when (mSearchHotWordsIndex) {
            in mSearchHotWords.indices -> mSearchHotWordsIndex
            else -> {
                0
            }
        }
        mView?.setSearchData(mSearchHotWords[mSearchHotWordsIndex])
        mSearchHotWordsIndex++
    }

    override fun onSearchClick() {
        AnalyticsManager.logEvent(AnalyticsKey.Event.NEWS, AnalyticsKey.Parameter.CLICK_SEARCH_BOX)
        mView?.goFromRoot(
                clazz = SearchFragment::class.java,
                bundle = SearchPresenterAutoBundle
                        .builder()
                        .mSearchHotwords(mSearchHotWords)
                        .mSearchHotwordsIndex(getCurrentIndex())
                        .bundle())
    }

    override fun onChannelEditClick() {
        AnalyticsManager.logEvent(AnalyticsKey.Event.NEWS, AnalyticsKey.Parameter.CLICK_CHANNEL_EDIT)
        mView?.goFromRoot(
                clazz = ChannelEditFragment::class.java)
    }

    private fun getCurrentIndex() = if(mSearchHotWordsIndex == 0) mSearchHotWordsIndex else mSearchHotWordsIndex - 1

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSwitchSearchWordEvent(event: SwitchSearchWordEvent) {
        switchSearchHotwords()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onChannelListChangeEvent(event: ChannelListChangeEvent) {
        if (javaClass != ArticlePresenter::class.java) {
            return
        }
        mListChannel.articleChannels = arrayListOf()
        mListChannel.articleChannels.addAll(event.channels)
        val channels = event.channels
        val beforeChid = DataManager.Memory.getCurrentArticleChannel()?.chid
        val pos = channels.indices.lastOrNull { beforeChid == channels[it].chid } ?: -1
        if (pos == -1) {
            mView?.updateChannels(channels, 0,true)
        } else {
            mView?.updateChannels(channels, pos, false)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onHideOrShowNewChannelTipEvent(event: HideOrShowNewChannelTipEvent) {
        mView?.hideOrShowChannelEditTip(event.show)
    }

    //注意：子类父类完全不一样，修改前三思（因为mview对象和其他对象在子类父类里不一致，所以要分开写）
    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onUpdateArticleVideoChannelEvent(event: UpdateArticleVideoChannelEvent) {
        if (javaClass != ArticlePresenter::class.java) {
            return
        }
        //更新mlist里的数据
        mListChannel.articleChannels = arrayListOf()
        mListChannel.articleChannels.addAll(event.recommendChannel.selectedChannels.articleChannels)

        val channels = event.recommendChannel.selectedChannels.articleChannels
        val beforeChid = DataManager.Memory.getCurrentArticleChannel()?.chid
        val pos = channels.indices.lastOrNull { beforeChid == channels[it].chid } ?: -1
        if (pos == -1) {
            mView?.updateChannels(channels, 0,true)
        } else {
            mView?.updateChannels(channels, pos, false)
        }
    }

    //初始化频道列表
    private fun initListChannel() {
        mListChannel = DataManager.Memory.getChannelList()
        /**
         * 防止memory里channel为空导致崩溃，手动从本地获取一次
         */
        val articleEmpty = mListChannel.articleChannels.isEmpty()
        val videoEmpty = mListChannel.videoChannels.isEmpty()
        if (articleEmpty || videoEmpty) {
            Crashlytics.logException(Exception("ArticlePresenter onCreate() MemorySource getChannelList() EmptyList.->articleEmpty : $articleEmpty ->videoEmpty : $videoEmpty"))
            DataManager.Init.start(InitTaskKey.INIT_NEWS_CHANNEL_LIST_LOCAL) //从本地重新初始化任务放入Memory
            mListChannel = DataManager.Memory.getChannelList() //从Memory重新获取
        }
    }

}
