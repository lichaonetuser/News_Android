package com.box.app.news.page.mvp.layer.main.list.news

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.box.app.news.R
import com.box.app.news.analytics.AnalyticsHelper
import com.box.app.news.analytics.AnalyticsKey
import com.box.app.news.applog.AppLogKey
import com.box.app.news.applog.AppLogManager
import com.box.app.news.bean.*
import com.box.app.news.bean.base.BaseBean
import com.box.app.news.bean.base.BaseNewsBean
import com.box.app.news.bean.list.ListNews
import com.box.app.news.data.DataAction
import com.box.app.news.data.DataDictionary
import com.box.app.news.data.DataManager
import com.box.app.news.data.adapter.bundle.AppLogReferConverter
import com.box.app.news.debug.DebugTool
import com.box.app.news.event.EventManager
import com.box.app.news.event.change.FontSizeChangeEvent
import com.box.app.news.event.change.MainTabChangeEvent
import com.box.app.news.event.change.NewsListChangeEvent
import com.box.app.news.event.change.WeatherRegionChangeEvent
import com.box.app.news.event.refresh.NewsListRefreshEvent
import com.box.app.news.event.refresh.SwitchSearchWordEvent
import com.box.app.news.item.*
import com.box.app.news.item.base.*
import com.box.app.news.item.factory.ItemFactory
import com.box.app.news.item.payload.NewsPayload
import com.box.app.news.page.mvp.layer.main.MainTabEnum
import com.box.app.news.page.mvp.layer.main.article.detail.ArticleDetailFragment
import com.box.app.news.page.mvp.layer.main.article.detail.ArticleDetailPresenterAutoBundle
import com.box.app.news.page.mvp.layer.main.article.headline.HeadlineFragment
import com.box.app.news.page.mvp.layer.main.dialog.more.MoreDialogFragment
import com.box.app.news.page.mvp.layer.main.dialog.more.MoreDialogPresenterAutoBundle
import com.box.app.news.page.mvp.layer.main.essay.EssayDetailFragment
import com.box.app.news.page.mvp.layer.main.essay.EssayDetailPresenterAutoBundle
import com.box.app.news.page.mvp.layer.main.gif.GifDetailFragment
import com.box.app.news.page.mvp.layer.main.gif.GifDetailPresenterAutoBundle
import com.box.app.news.page.mvp.layer.main.image.browser.ImageBrowserFragment
import com.box.app.news.page.mvp.layer.main.image.browser.ImageBrowserPresenterAutoBundle
import com.box.app.news.page.mvp.layer.main.image.detail.ImageDetailFragment
import com.box.app.news.page.mvp.layer.main.image.detail.ImageDetailPresenterAutoBundle
import com.box.app.news.page.mvp.layer.main.video.detail.VideoDetailFragment
import com.box.app.news.page.mvp.layer.main.video.detail.VideoDetailPresenterAutoBundle
import com.box.app.news.proto.AppLog
import com.box.app.news.util.GSYVideoTransferUtils
import com.box.common.core.CoreApp
import com.box.common.core.analytics.AnalyticsManager
import com.box.common.core.debug.DebugManager
import com.box.common.core.log.Logger
import com.box.common.core.rx.schedulers.io
import com.box.common.core.rx.schedulers.ioToMain
import com.box.common.core.util.ResUtils
import com.box.common.extension.app.mvp.bindToLifecycle
import com.box.common.extension.app.mvp.loading.list.MVPListPresent
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.box.common.extension.widget.recycler.decoration.CommonItemDecoration
import com.box.common.extension.widget.recycler.item.BaseItem
import com.box.common.extension.widget.recycler.util.convertBeansToItems
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer
import com.yatatsu.autobundle.AutoBundleField
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import me.yokeyword.fragmentation.anim.FragmentAnimator
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.dip
import java.lang.IllegalArgumentException
import java.util.*
import java.util.concurrent.TimeUnit

open class NewsListPresenter<V : NewsListContract.View> : MVPListPresent<V>(),
        NewsListContract.Presenter<V> {

    override var mRefreshFailMessage = ResUtils.getString(R.string.Tip_ServerError)

    @AutoBundleField
    lateinit var mChannel: Channel
    @AutoBundleField
    lateinit var path: String
    @AutoBundleField
    lateinit var mAnalyticsEventKey: String
    @AutoBundleField(required = false)
    var mListNews = ListNews()
//    @AutoBundleField(required = false)
//    var mWeather = Weather()
    @AutoBundleField(required = false)
    var mIsFirstRefresh = true // 第一次下拉刷新
    @AutoBundleField(required = false)
    var mLastRefreshTime: Long = System.currentTimeMillis()
    @AutoBundleField(required = false)
    var mLastReadPosition = -1
    @AutoBundleField(required = false, converter = AppLogReferConverter::class)
    var mRefer: AppLog.Refer = AppLog.Refer.getDefaultInstance()
    @AutoBundleField(required = false, converter = AppLogReferConverter::class)
    var mItemRefer: AppLog.Refer = AppLog.Refer.getDefaultInstance()
    @AutoBundleField(required = false, converter = AppLogReferConverter::class)
    var mPositionRefer: AppLog.Refer = AppLog.Refer.getDefaultInstance()
    @AutoBundleField(required = false, converter = AppLogReferConverter::class)
    var mPositionSourceRefer: AppLog.Refer = AppLog.Refer.getDefaultInstance()
    @AutoBundleField(required = false)
    var mSwitchSearchHint = false //指示刷新操作是否应该滚动热词

    open val mShowTimeHeader = false
    open val mLoadCache = true

    private var mRefreshCount = 0
    private var mLoadMoreCount = 0

    private var mFeedDisposable: Disposable? = null
    private val mRequestingMap = HashMap<String, Disposable>()

//    private val mWeatherBannerItem = WeatherBannerItem(mWeather)
    private val mTipItem = TipBannerItem(Tip())
    private val mLastItem = LastReadItem(Channel())

    private val mLoadMoreDuplicateNews = arrayListOf<BaseNewsBean>()

    //用来去除上次阅读到条目的分割线
    private val decoration = CommonItemDecoration().withDivider(R.drawable.divider_common).withDrawDividerOnLastItem(true)

    private var isLoading = false

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        //如果外界不传递，则替换默认Refer如下
        if (mItemRefer == AppLog.Refer.getDefaultInstance()) {
            mItemRefer = AppLog.Refer
                    .newBuilder()
                    .setItemId(mChannel.chid)
                    .setName(AppLogKey.Refer.ARTICLE_LIST)
                    .build()
        }
        if (mPositionRefer == AppLog.Refer.getDefaultInstance()) {
            mPositionRefer = AppLog.Refer
                    .newBuilder()
                    .setItemId(mChannel.chid)
                    .setName(AppLogKey.Refer.ARTICLE_LIST)
                    .build()
        }
        if (mPositionSourceRefer == AppLog.Refer.getDefaultInstance()) {
            mPositionSourceRefer = AppLog.Refer
                    .newBuilder()
                    .setName(AppLogKey.Label.LAUNCH)
                    .build()
        }
        //更新最后阅读到所对应的频道
        mLastItem.setModel(mChannel)
        when {
            //首次创建,加载本地数据的数据,如果本地数据库没有数据，忽略加载
            isNotRestore() && mLoadCache -> {
                //同步查询本地保存的数据
                val items = DataManager.Local.getNewsFeed(mChannel.chid, 0, 0)
                        .onErrorReturn { ListNews() }
                        .map { t: ListNews ->
                            mListNews.news.addAll(t.news.filterNotNull())
                            return@map mListNews.news.filterNotNull()
                        }
                        .convertBeansToItems(ItemFactory.NEWS)
                        .map(AnalyticsHelper.getNewsItemAnalyticsMap(
                                analyticsKey = mAnalyticsEventKey,
                                refer = mItemRefer,
                                positionRefer = mPositionRefer,
                                positionSourceRefer = mPositionSourceRefer))
                        .blockingSingle()
                //如果本地数据库有数据，加载
                if (items.isNotEmpty()) {
                    loadDataComplete(items)
                }
            }
            //从销毁状态中恢复，且销毁前数据不为空，加载销毁前的数据
            isRestore() && mListNews.news.filterNotNull().isNotEmpty() -> {
                //设置是否可以继续加载更多
                if (mIsRefreshing) {
                    mView?.setLoadMoreEnable(true)
                } else {
                    mView?.setLoadMoreEnable(mListNews.hasMore)
                }
                //同步查询并加载销毁前的数据
                val items = Observable.just(mListNews.news.filterNotNull())
                        .convertBeansToItems(ItemFactory.NEWS)
                        .map(AnalyticsHelper.getNewsItemAnalyticsMap(
                                analyticsKey = mAnalyticsEventKey,
                                refer = mItemRefer,
                                positionRefer = mPositionRefer,
                                positionSourceRefer = mPositionSourceRefer))
                        .blockingSingle()
                //恢复上次阅读到的位置
                if (mLastReadPosition > 0 && mLastReadPosition < items.size) {
                    items.add(mLastReadPosition, mLastItem)
                    val delta = if (mChannel.chid == DataDictionary.CHANNEL_ID_RECOMMEND_ARTICLE) 1 else -1
                    decoration.setNoDrawIndexList(listOf(mLastReadPosition, mLastReadPosition + delta))

                    getAdapter()?.postion0 = mLastReadPosition
                    getAdapter()?.postion1 = mLastReadPosition + delta
                }
                //加载销毁前的数据
                loadDataComplete(items)
            }
        }
        //如果是推荐分类
        if (mChannel.chid == DataDictionary.CHANNEL_ID_RECOMMEND_ARTICLE) {
            val haveWeatherBanner = getAdapter()?.isScrollableHeaderOrFooter(0)
            if (haveWeatherBanner == false) {
                //增加天气Banner
//                mWeatherBannerItem.updateWeather(mWeather)
//                getAdapter()?.addScrollableHeader(mWeatherBannerItem)
            }
            //如果是从销毁中恢复
            //用户修改过地区
//            if (isRestore() && mWeather.cityName != DataManager.Local.getUserLastSelectedRegion().name) {
//                updateWeatherBannerItem()
//            }
        }
        //如果是文章分类
        if (mChannel.channelType == DataDictionary.ChannelType.ARTICLE.value) {
            //使用默认无间距分割线
            mView?.setCommonItemDecoration(decoration)
        }
        //注册事件
        EventManager.register(this)

        //DEBUG
        if (DebugManager.enable) {
            setViewShowRefer("Refer\n" +
                    "name : ${mRefer.name}\n" +
                    "itemId : ${mRefer.itemId}\n\n" +
                    "ItemRefer\n" +
                    "name : ${mItemRefer.name}\n" +
                    "itemId : ${mItemRefer.itemId}\n\n" +
                    "PositionRefer\n" +
                    "name : ${mPositionRefer.name}\n" +
                    "itemId : ${mPositionRefer.itemId}\n\n" +
                    "PositionSourceRefer\n" +
                    "name : ${mPositionSourceRefer.name}\n" +
                    "itemId : ${mPositionSourceRefer.itemId}\n")
        }

        getAdapter()?.channel = mChannel
        getAdapter()?.refer = mItemRefer
    }

    override fun onLazyCreate(savedState: Bundle?) {
        super.onLazyCreate(savedState)
        //页面第一次对用户可见
        when {
            //从销毁状态中恢复，并且页面没有数据的情况下，尝试远程获取数据
            isRestore() && mListNews.news.filterNotNull().isEmpty() -> {
                if (mChannel.chid != DataDictionary.CHANNEL_ID_HEADLINE) {
                    AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.AUTO_REFRESH)
                } else {
                    AnalyticsManager.logEvent(AnalyticsKey.Event.NEWS, AnalyticsKey.Parameter.AUTO_REFRESH_HEADLINE)
                }
                loadData(0)
            }
            //首次创建并没有本地数据，尝试远程获取数据
            isNotRestore() && mListNews.news.filterNotNull().isEmpty() -> {
                if (mChannel.chid != DataDictionary.CHANNEL_ID_HEADLINE) {
                    AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.AUTO_REFRESH)
                } else {
                    AnalyticsManager.logEvent(AnalyticsKey.Event.NEWS, AnalyticsKey.Parameter.AUTO_REFRESH_HEADLINE)
                }
                loadData(0)
            }
            //首次创建且包含本地数据，转下拉刷新触发获取数据
            isNotRestore() && mListNews.news.filterNotNull().isNotEmpty() -> {
                if (mChannel.chid != DataDictionary.CHANNEL_ID_HEADLINE) {
                    AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.AUTO_REFRESH)
                } else {
                    AnalyticsManager.logEvent(AnalyticsKey.Event.NEWS, AnalyticsKey.Parameter.AUTO_REFRESH_HEADLINE)
                }
                mView?.autoRefresh()
            }
        }

        getAdapter()?.channel = mChannel
        getAdapter()?.refer = mItemRefer
    }

    open fun getChannel(): Channel {
        return mChannel
    }

    override fun onPause() {
        mRefreshCount = 0
        mLoadMoreCount = 0
        AppLogManager.sendAppLog()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            mRequestingMap.forEach { it.value.dispose() }
            mRequestingMap.clear()
        } catch (e: ConcurrentModificationException) {
            mRequestingMap.clear()
        }

        EventManager.unregister(this)
    }

    override fun onRefresh(isAutoRefresh: Boolean) {
        if (!isAutoRefresh) {
            if (mChannel.chid != DataDictionary.CHANNEL_ID_HEADLINE) {
                AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.SLIDE_DOWN_TO_REFRESH)
            } else {
                AnalyticsManager.logEvent(AnalyticsKey.Event.NEWS, AnalyticsKey.Parameter.SLIDE_DOWN_TO_REFRESH_HEADLINE)
            }
        }
        super.onRefresh(isAutoRefresh)
    }

    override fun onLoadMore(lastPosition: Int, currentPage: Int) {
        if (mChannel.chid != DataDictionary.CHANNEL_ID_HEADLINE) {
            AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.LOAD_MORE)
        } else {
            AnalyticsManager.logEvent(AnalyticsKey.Event.NEWS, AnalyticsKey.Parameter.LOAD_MORE_HEADLINE)
        }
        super.onLoadMore(lastPosition, currentPage)
    }

    open fun checkDataTypeValid(type: Int): Boolean {
        return type == DataDictionary.NewsType.ARTICLE.value
                || type == DataDictionary.NewsType.VIDEO.value
                || type == DataDictionary.NewsType.IMAGE.value
//            || type == DataDictionary.NewsType.WORLDCUPVIDEO.value
//            || type == DataDictionary.NewsType.WORLDCUPBANNER.value
//            || type == DataDictionary.NewsType.WORLDCUPMATCH.value
                || type == DataDictionary.NewsType.HEADLINE.value
                || type == DataDictionary.NewsType.MULTIPLEIMAGE.value
                || type == DataDictionary.NewsType.GIF.value
                || type == DataDictionary.NewsType.ESSAY.value
                || type == DataDictionary.NewsType.CARD_TWITTER.value
                || type == DataDictionary.NewsType.NATIVE_AD.value
    }

    override fun loadData(pageNum: Int, isRefresh: Boolean) {
        if (isLoading) {
            return
        }
        isLoading = true
        //isRefresh ：是否是刷新操作    刷新操作&&该频道应该滚动热词
        if (isRefresh && mSwitchSearchHint) {
            EventManager.post(SwitchSearchWordEvent())
        }

        if (mFeedDisposable?.isDisposed == false) {
            mFeedDisposable?.dispose()
            if (isRefresh) {
                mView?.onLoadMoreComplete(arrayListOf())
            } else {
                mView?.onRefreshComplete(arrayListOf())
            }
        }

        var minEmitTime: Long? = null
        var maxEmitTime: Long? = null
        if (isRefresh) {
            //刷新时使用上次返回的minEmitTime，除此外取0
            minEmitTime = mListNews.minEmitTime
            //rc计数+1
//            mRefreshCount++
        } else {
            //加载时使用上次返回的maxEmitTime，如果上次结果是0，取列表最后一项的emitTime
            maxEmitTime = if (mListNews.maxEmitTime != 0L) {
                mListNews.maxEmitTime
            } else {
                mListNews.news.lastOrNull()?.emitTime
            }
            //lc计数+1
//            mLoadMoreCount++
        }
        Logger.tag("loadNews").d("request minEmitTime = $minEmitTime,maxEmitTime=$maxEmitTime")
        if (isRefresh) {
            AnalyticsManager.logEvent(event = AnalyticsKey.Event.LOAD_NEWS_REQUEST,
                    onAppsFlyer = true, onUmeng = true, onFirebase = true)
        }
        mFeedDisposable = DataManager.Remote.getFeed(
                pub = path,
                chid = mChannel.chid,
                minEmitTime = minEmitTime,
                maxEmitTime = maxEmitTime,
                channelType = mChannel.channelType,
                refreshCount = mRefreshCount,
                loadMoreCount = mLoadMoreCount)
                .map { newListNews ->
                    Logger.tag("LoadNewsFeed").d("response:" +
                            "isRefreshing = $isRefresh," +
                            "minEmitTime = ${newListNews.minEmitTime}," +
                            "maxEmitTime = ${newListNews.maxEmitTime}," +
                            "hasMore = ${newListNews.hasMore}," +
                            "hasMoreToRefresh = ${newListNews.hasMoreToRefresh}")
                    /**
                     * WARNING!!!!!!
                     * WARNING!!!!!!
                     * WARNING!!!!!!
                     * 警告！！重要警告！！
                     * 所有Feed列表的主要数据处理逻辑，修改前要慎重确认!!
                     */
                    isLoading = false
                    if (isRefresh) {
                        //rc计数+1
                        mRefreshCount++
                    } else {
                        //lc计数+1
                        mLoadMoreCount++
                    }
                    //------------------------
                    //刷新时发送到AppsFlyer的事件
                    //------------------------
                    if (isRefresh) {
                        AnalyticsManager.logEvent(event = AnalyticsKey.Event.LOAD_NEWS_SUCCESS,
                                onAppsFlyer = true, onUmeng = true, onFirebase = true)
                    }

                    //------------------------
                    //关于min_emit_time和max_emit_time的处理
                    //------------------------
                    val newMinEmitTime: Long
                    val newMaxEmitTime: Long
                    if (isRefresh) {
                        //下拉刷新时的场景
                        //记录刷新时间
                        mLastRefreshTime = System.currentTimeMillis()
                        //无条件更新minEmitTime（服务器给什么用什么，但这里也许应该有个非法值检测更好？）
                        newMinEmitTime = newListNews.minEmitTime
                        //下拉刷新时hasMore为true的时候更新max，否则继续使用之前的数值
                        newMaxEmitTime = if (newListNews.hasMore) {
                            newListNews.maxEmitTime
                        } else {
                            mListNews.maxEmitTime
                        }
                    } else {
                        //加载更多时的场景
                        //保留之前minEmitTime
                        newMinEmitTime = mListNews.minEmitTime
                        //无条件更新maxEmitTime
                        newMaxEmitTime = newListNews.maxEmitTime
                    }

                    //------------------------
                    //加载更多（LoadMore）的相关处理
                    //------------------------
                    if (isRefresh) {
                        //下拉刷新时的场景
                        //当下拉刷新hasMore为True，一定可以加载更多，为false时不影响本来的逻辑
                        if (newListNews.hasMore) {
                            //hasMore为true时设置为可以加载更多
                            mView?.setLoadMoreEnable(true)
                        } else {
                            //hasMore为false时不进行处理
                        }
                    } else {
                        //加载更多时的场景
                        //无条件按服务器返回结果设置是否可以加载更多
                        mView?.setLoadMoreEnable(newListNews.hasMore)
                    }

                    //------------------------
                    //列表数据的处理
                    //------------------------
                    //当前的新闻列表
                    var currentNews = mListNews.news
                            .filterNotNull() //过滤null，如果有的话，比如Gson写进来
                            .filter { checkDataTypeValid(it.type) } //过滤掉不支持的类型
                            .distinct() //去重
                            .toMutableList() //转为长度可变列表，便于修改，可删
                    //新返回的新闻列表
                    val newLoadNews = newListNews.news
                            .filterNotNull()
                            .filter { checkDataTypeValid(it.type) }
                            .distinct()
                            .toMutableList()

                    val temp = newLoadNews.filter { it is ArticleHeadline }.toMutableList()
                    for (new in temp) {
                        if (new is ArticleHeadline) {
                            for (art in new.news) {
                                art.headline = true
                            }
                        }
                    }

                    //当前和新返回数据合并后的新闻列表
                    val allNews = arrayListOf<BaseNewsBean>()
                    if (isRefresh) {
                        //下拉刷新时的场景
                        //当hasMore返回true时需要清除之前的数据
                        if (newListNews.hasMore) {
                            //当hasMore返回true时
                            //关闭当前正在进行的预加载缓存
                            mRequestingMap.forEach { it.value.dispose() }
                            mRequestingMap.clear()
                            //清除数据
                            currentNews.clear()
                            mListNews.news.clear()
                            //移除"上次阅读到"Item的位置记录
                            mLastReadPosition = -1
                        } else {
                            //当hasMore返回false时
                            //从当前数据中移除新返回数据中包含的条目
                            currentNews = currentNews.filterNot { newLoadNews.contains(it) }.toMutableList()
                            //记录"上次阅读到"Item的位置，位置为新数据的末尾
                            mLastReadPosition = newLoadNews.size
                        }
                        //新返回的刷新数据放在队首
                        allNews.addAll(newLoadNews)
                        //旧数据放在新数据后面
                        allNews.addAll(currentNews)

                        if (mShowTimeHeader) {
                            createTimeTitle(allNews, null)
                        }
                    } else {
                        //加载更多时的场景
                        //从当前数据中移除新返回数据中包含的条目
                        mLoadMoreDuplicateNews.clear()
                        currentNews = currentNews.filterNot {
                            val duplicate = newLoadNews.contains(it)
                            if (duplicate) {
                                mLoadMoreDuplicateNews.add(it)
                            }
                            duplicate
                        }.toMutableList()

                        if (mShowTimeHeader) {
                            createTimeTitle(newLoadNews, currentNews.last())
                        }

                        //旧数据放在队首
                        allNews.addAll(currentNews)
                        //新返回的刷新数据放在旧数据的后面
                        allNews.addAll(newLoadNews)
                    }
                    Logger.d("mLoadMoreDuplicateNews : ${mLoadMoreDuplicateNews.size}")

                    //如果返回的数据不为空
                    if (newLoadNews.isNotEmpty()) {
                        //保存增量数据至本地，如果增量数据中有老数据已有的条目，则更新
                        DataManager.Local.saveNewsFeed(mChannel.chid, newLoadNews).onErrorComplete().blockingGet()
                    }

                    //------------------------
                    //对处理结果进行保存
                    //------------------------
                    mListNews.news = allNews.toCollection(arrayListOf())
                    mListNews.minEmitTime = newMinEmitTime
                    mListNews.maxEmitTime = newMaxEmitTime
                    mListNews.tip = newListNews.tip
                    mListNews.hasMore = newListNews.hasMore
                    mListNews.hasMoreToRefresh = newListNews.hasMoreToRefresh
                    //------------------------
                    //返回数据继续处理
                    //------------------------
                    if (isRefresh) {
                        //如果下拉刷新返回的内容什么都没有，并且之前包含数据，抛出错误
                        if (newLoadNews.isEmpty() && mListNews.news.isNotEmpty()) {
                            throw IllegalArgumentException("Return items is Empty")
                        }
                    } else {
                        //如果加载更多返回的内容什么都没有，但是服务端返回有，抛出错误
                        if (newLoadNews.isEmpty() && newListNews.hasMore) {
                            throw IllegalArgumentException("Return items is Empty")
                        }

                    }

                    //下拉刷新时返回全量，加载更多的时候返回增量
                    return@map if (isRefresh) {
                        //下拉刷新时返回全量数据
                        allNews
                    } else {
                        //加载更多时返回增量数据
                        newLoadNews
                    }
                }
                .convertBeansToItems(ItemFactory.NEWS)
                .map(AnalyticsHelper.getNewsItemAnalyticsMap(
                        analyticsKey = mAnalyticsEventKey,
                        refer = mItemRefer,
                        positionRefer = mPositionRefer,
                        positionSourceRefer = mPositionSourceRefer))
                .ioToMain()
                .bindToLifecycle(this)
                .subscribeBy(
                        onNext = { items ->
                            /**
                             * WARNING!!!!!!
                             * WARNING!!!!!!
                             * WARNING!!!!!!
                             * 警告！！重要警告！！
                             * 所有Feed列表的主要UI处理逻辑，修改前要慎重确认!!
                             */
                            //------------------------
                            //Impression相关
                            //------------------------
                            //更新Item的可见性
                            //强制当前显示的Item为不可见状态，并记录
                            if (isRefresh) {
                                checkOnItemVisibleTo(false)
                            }
                            isLoading = false
                            //------------------------
                            //Item刷新相关
                            //------------------------
                            if (isRefresh) {
                                //下拉刷新时的场景
                                //移除之前的上次阅读到（如果有的话）
                                val index = getAdapter()?.getGlobalPositionOf(mLastItem) ?: -1
                                if (index > -1) {
                                    getAdapter()?.removeItem(index)
                                }
                                //如果hasMore为false,并且上次阅读到Item有记录位置，增加上次阅读到Item
                                if (!mListNews.hasMore && mLastReadPosition > 0) {
                                    items.add(mLastReadPosition, mLastItem)
                                    val delta = if (mChannel.chid == DataDictionary.CHANNEL_ID_RECOMMEND_ARTICLE) 1 else -1
                                    decoration.setNoDrawIndexList(listOf(mLastReadPosition, mLastReadPosition + delta))

                                    getAdapter()?.postion0 = mLastReadPosition
                                    getAdapter()?.postion1 = mLastReadPosition + delta
                                }
                                //如果hasMore返回true时需要清除之前的数据
                                if (mListNews.hasMore) {
                                    getAdapter()?.clear()
                                    //文章推荐文磊需要重新加回天气banner
                                    if (mChannel.chid == DataDictionary.CHANNEL_ID_RECOMMEND_ARTICLE) {
//                                        getAdapter()?.addScrollableHeader(mWeatherBannerItem)
                                    }
                                }
                            } else {
                                //加载更多时的场景
                                //如果有和之前重复的数据，移除之前重复的数据
                                if (mLoadMoreDuplicateNews.isNotEmpty()) {
                                    mLoadMoreDuplicateNews.forEach { duplicateBean ->
                                        val position = getAdapter()?.currentItems?.indexOfFirst {
                                            it.getModel(BaseNewsBean::class.java)?.aid == duplicateBean.aid
                                        } ?: -1
                                        if (position >= 0 && position < getAdapter()?.itemCount ?: 0) {
                                            getAdapter()?.removeItem(position)
                                        }
                                    }
                                    mLoadMoreDuplicateNews.clear()
                                }
                            }
                            //更新列表，起始位置为移除滑动头的偏移量<--目前为全量刷新，已弃用
                            //val addOffset = getAdapter()?.scrollableHeaders?.size ?: 0
                            loadDataComplete(items, true, 0, 0)
                            //更新刷新后更新了多少条提示Item的内容
                            mTipItem.updateTip(mListNews.tip)
                            //没有数据的时候展示空页面 --- 有可能和业务冲突
                            if (mListNews.news.isEmpty()) {
                                mView?.showEmpty()
                            }

                            //------------------------
                            //Impression相关
                            //------------------------
                            //更新Item的可见性
                            //强制当前显示的Item为可见状态，并记录
//                            if (isRefresh) {
//                                checkOnItemVisibleTo(true)
//                            }

                            //------------------------
                            //统计相关
                            //------------------------
                            //当刷新完全完成时统计，这段代码一定要在末尾调用
                            if (isRefresh) {
                                AnalyticsManager.logEvent(event = AnalyticsKey.Event.LOAD_NEWS_DONE,
                                        onAppsFlyer = true, onUmeng = true, onFirebase = true)
                            }
                        },
                        onError = { throwable ->
                            isLoading = false
                            if (isRefresh) {
                                AnalyticsManager.logEvent(event = AnalyticsKey.Event.LOAD_NEWS_FAIL,
                                        onAppsFlyer = true, onUmeng = true, onFirebase = true)
                            }
                            Logger.i(throwable.toString())
                            loadDataFail()
                        })
        updateWeatherBannerItem()
    }

    private fun updateWeatherBannerItem() {
//        if (mChannel.chid != DataDictionary.CHANNEL_ID_RECOMMEND_ARTICLE) {
//            return
//        }
//
//        val region = DataManager.Local.getUserLastSelectedRegion()
//        if (mIsRefreshing) {
//            DataManager.Remote.getWeatherSimpleInfo(region.cityCode)
//                    .ioToMain()
//                    .bindToLifecycle(this)
//                    .subscribeBy(onNext = { info ->
//                        val adapter = getAdapter()
//                        if (adapter != null) {
//                            mWeather = info.weather
//                            mWeatherBannerItem.updateWeather(mWeather)
//                            adapter.updateItem(mWeatherBannerItem, null)
//                        }
//                    }, onError = {
//                    })
//        }
    }

    /**
     * 当绑定列表物件时预加载内容
     */
    override fun onBindItem(position: Int, item: BaseItem<*, *>, payloads: MutableList<Any?>?) {
        if (payloads?.contains(NewsPayload.UPDATE_INFORMATION) == true) {
            //更新顶踩不触发
            return
        }

        val appConfig = DataManager.Memory.getAppConfig()
        if (appConfig.disableDetailPreload) {
            return
        }

        if (item !is BaseArticleItem) {
            return
        }

        val article = item.getModel()
        //如果已经有内容并且上次内容更新的时间到现在的间隔小于配置的间隔时长，不再重新获取
        val updateInterval: Long = (System.currentTimeMillis() - article.updateTime) / 1000L
        val isEffect = updateInterval < appConfig.detailMinRefreshInterval

        val content = DataManager.Cache.getArticleContent(article.aid)
        if (content != null && content.isNotEmpty() && isEffect) {
            return
        }

        //如果已经再请求，等待请求结果
        val disposable = mRequestingMap[article.aid]
        if (disposable != null && !disposable.isDisposed) {
            return
        }

        //创建请求
        mRequestingMap[article.aid] = DataManager.Remote.getArticleDetail(article.aid)
                .filter { articleContentWrapper -> article.aid == articleContentWrapper.aid }
                .io()
                .bindToLifecycle(this)
                .subscribeBy(
                        onNext = {
                            if (it.news !is Article || it.news?.aid != article.aid) {
                                return@subscribeBy
                            }

                            val newArticle = it.news as? Article ?: return@subscribeBy

                            article.title = newArticle.title
                            // article.content = newArticle.content 内存有限，容不得随便放
                            article.updateTime = System.currentTimeMillis()

                            //检索在数据列表中的位置
                            val index = mListNews.news.indexOfFirst { it?.aid == article.aid }
                            if (index > -1) {
                                mListNews.news[index] = article
                            }
                            item.setModel(article)

                            val newContent = newArticle.content
                            //用LRU缓存池替换DB存储
                            DataManager.Cache.putArticleContent(article.aid, newContent)
                            //但是标题和时间还是要更新的
                            DataManager.Local.saveArticleFeedTitleAndUpdateTime(article).blockingGet()
                            mRequestingMap.remove(article.aid)
                        },
                        onError = { throwable ->
                            Logger.e(throwable)
                        }
                )
    }

    override fun onItemVisibility(position: Int, item: BaseItem<*, *>, visible: Boolean) {
        val news = item.getModel(BaseNewsBean::class.java) ?: return
        val isVisible = visible && this.isVisible()

        val type = when (news) {
            is Article -> AppLog.ImpressionCellType.IMPRESSION_CELL_ARTICLE
            is Video -> AppLog.ImpressionCellType.IMPRESSION_CELL_VIDEO
            is Image -> AppLog.ImpressionCellType.IMPRESSION_CELL_IMAGE
            is GIF -> AppLog.ImpressionCellType.IMPRESSION_CELL_GIF
            is Essay -> AppLog.ImpressionCellType.IMPRESSION_CELL_ESSAY
            is ArticleHeadline -> {
                setArticleHeadlineLog(news, isVisible)
                return
            }
            else -> return
        }

        Logger.tag("TempCheck2").d("onItemVisibility position : $position | isVisible : $isVisible | visible : $visible | this.isVisible : ${this.isVisible()}")
        track(isVisible, type, news)
    }

    fun track(isVisible: Boolean, type: AppLog.ImpressionCellType, news: BaseNewsBean) {
        if (isVisible) {
            AppLogManager.startTrackImpressionCell(news.title, mChannel.chid,
                    AppLog.ImpressionType.IMPRESSION_ARTICLE_LIST,
                    news.aid, type)
        } else {
            AppLogManager.endTrackImpressionCell(news.title, mChannel.chid,
                    AppLog.ImpressionType.IMPRESSION_ARTICLE_LIST,
                    news.aid, type)
        }
    }

    private fun trackArray(isVisible: Boolean, type: AppLog.ImpressionCellType, news: List<BaseNewsBean>) {
        var aid = ""
        var title = ""
        for (i in news.indices) {
            aid += news[i].aid + "|"
            title += news[i].title + "|"
        }
        aid = aid.substring(0, aid.length - 1)
        title = title.substring(0, title.length - 1)

        if (isVisible) {
            AppLogManager.startTrackImpressionCell(title, mChannel.chid,
                    AppLog.ImpressionType.IMPRESSION_ARTICLE_LIST,
                    aid, type)
        } else {
            AppLogManager.endTrackImpressionCell(title, mChannel.chid,
                    AppLog.ImpressionType.IMPRESSION_ARTICLE_LIST,
                    aid, type)
        }
    }

    private fun checkOnItemVisibleTo(visible: Boolean) {
        val layoutManager = getAdapter()?.flexibleLayoutManager
        val firstPosition = layoutManager?.findFirstVisibleItemPosition()
                ?: RecyclerView.NO_POSITION
        val lastPosition = layoutManager?.findLastVisibleItemPosition() ?: RecyclerView.NO_POSITION
        Logger.tag("TempCheck").d("checkOnItemVisibleTo : $visible | firstPosition : $firstPosition | lastPosition : $lastPosition")
        if (firstPosition == RecyclerView.NO_POSITION || lastPosition == RecyclerView.NO_POSITION) {
            return
        }
        for (position in firstPosition..lastPosition) {
            val item = getAdapter()?.getItem(position) ?: continue
            onItemVisibility(position, item, visible)
        }
    }

    private fun setArticleHeadlineLog(bean: ArticleHeadline, isVisible: Boolean) {
        val news = bean.news
        val type = AppLog.ImpressionCellType.IMPRESSION_CELL_HEADLINE
        trackArray(isVisible, type, news)
    }

    //注意headline频道的完全重写了这个方法，修改这个方法寄的修改速报headline里的方法
    open fun onArticleItemClick(item: BaseItem<*, *>) {
        if (item is BaseArticleItem) {
            GSYVideoManager.releaseAllVideos()
            AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_ARTICLE)
            val article = item.getModel()
            mView?.goFromRoot(ArticleDetailFragment::class.java, ArticleDetailPresenterAutoBundle
                    .builder(article, false, mItemRefer)
                    .mChannel(mChannel)
                    .bundle())
        }
    }

    override fun onItemClick(position: Int, item: BaseItem<*, *>): Boolean {

//        mView?.goFromRoot(WorldcupPlayerFragment::class.java, WorldcupPlayerPresenterAutoBundle
//                .builder("y6", AnalyticsKey.Event.WORLD_CUP)
//                .bundle())


//        mView?.goFromRoot(WorldcupTeamFragment::class.java, WorldcupTeamPresenterAutoBundle
//                .builder("y6", AnalyticsKey.Event.WORLD_CUP)
//                .bundle())

//        mView?.goFromRoot(WorldcupMatchFragment::class.java, WorldcupMatchPresenterAutoBundle
//        .builder("5aed6312674a791eaec7248e", AnalyticsKey.Event.WORLD_CUP)
//                .bundle())


        AnalyticsManager.logEvent(AnalyticsKey.Event.CLICK_NEWS, onAppsFlyer = true, onUmeng = false, onFirebase = false)
        if (getAdapter()?.getItem(position) is BaseNewsItem && getAdapter()?.isSelected(position) == false) {
            getAdapter()?.toggleSelection(position)
        }
        val bean = item.getModel(BaseNewsBean::class.java)
        if (bean is ArticleHeadline) {
            return true
        }
        if (bean != null) {
            bean.isRead = true
            getAdapter()?.notifyItemChanged(position, NewsPayload.UPDATE_READ)
        }

        when (item) {
            is BaseArticleItem -> {
                onArticleItemClick(item)
            }
            is BaseVideoItem -> {
                GSYVideoTransferUtils.prepareTransfer()
                GSYVideoManager.releaseAllVideos()
                AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_VIDEO)
                val video = item.getModel()
                mView?.goFromRoot(VideoDetailFragment::class.java, VideoDetailPresenterAutoBundle
                        .builder(video, false, mItemRefer)
                        .mChannel(mChannel)
                        .bundle())
            }
            is BasePictureItem -> {
                GSYVideoManager.releaseAllVideos()
                AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_IMAGE)
                val image = item.getModel()
                mView?.goFromRoot(ImageDetailFragment::class.java, ImageDetailPresenterAutoBundle
                        .builder(image, false, mItemRefer)
                        .mChannel(mChannel)
                        .bundle())
            }
            is BaseGifItem -> {
                GSYVideoManager.releaseAllVideos()
                AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_GIF)
                val gif = item.getModel()
                mView?.goFromRoot(GifDetailFragment::class.java, GifDetailPresenterAutoBundle
                        .builder(gif, false, mItemRefer)
                        .mChannel(mChannel)
                        .bundle())
            }
            is BaseEssayItem -> {
                GSYVideoManager.releaseAllVideos()
                AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_ESSAY)
                val essay = item.getModel()
                mView?.goFromRoot(EssayDetailFragment::class.java, EssayDetailPresenterAutoBundle
                        .builder(essay, false, mItemRefer)
                        .mChannel(mChannel)
                        .bundle())
            }
            else -> {
                GSYVideoManager.releaseAllVideos()
            }
        }
        return true
    }

    override fun onItemChildItemClick(childPosition: Int, item: BaseItem<*, *>, bean: BaseBean): Boolean {
        AnalyticsManager.logEvent(AnalyticsKey.Event.NEWS, AnalyticsKey.Parameter.CLICK_HEADLINE_NEWS)
        onArticleItemClick(item)
        return true
    }

    override fun onItemChildClick(position: Int, item: BaseItem<*, *>, id: Int): Boolean {
        val bean = item.getModel() as? BaseNewsBean ?: return true
        when (item) {
            is BaseArticleItem -> {
                when (id) {
                    R.id.remove_btn -> {
                        AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_ARTICLE_NOT_INTERESTED)
                        mView?.showRemoveDialog(bean)
                    }
                }
            }
            is BaseVideoItem -> {
                when (id) {
                    R.id.remove_btn -> {
                        AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_VIDEO_NOT_INTERESTED)
                        mView?.showRemoveDialog(bean)
                    }
                    R.id.dig_btn -> {
                        if (bean.isDigged) {
                            AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_CANCEL_VIDEO_DIG)
                        } else {
                            AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_VIDEO_DIG)
                        }
                        DataManager.Remote.toggleDigNews(bean, mChannel)
                    }
                    R.id.bury_btn -> {
                        if (bean.isBuried) {
                            AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_CANCEL_VIDEO_BURY)
                        } else {
                            AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_VIDEO_BURY)
                        }
                        DataManager.Remote.toggleBuryNews(bean, mChannel)
                    }
                    R.id.more_btn -> {
                        mView?.goFromRoot(MoreDialogFragment::class.java, MoreDialogPresenterAutoBundle
                                .builder(bean, mChannel, mAnalyticsEventKey, "video_ellipsis_")
                                .mReferName(AppLogKey.Refer.ARTICLE_LIST)
                                .mReferId(mChannel.chid)
                                .bundle())
                    }
                }
            }
            is BasePictureItem -> {
                when (id) {
                    R.id.remove_btn -> {
                        mView?.showRemoveDialog(bean)
                    }
                    R.id.dig_btn -> {
                        if (bean.isDigged) {
                            AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_CANCEL_IMAGE_DIG)
                        } else {
                            AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_IMAGE_DIG)
                        }
                        DataManager.Remote.toggleDigNews(bean, mChannel)
                    }
                    R.id.bury_btn -> {
                        if (bean.isBuried) {
                            AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_CANCEL_IMAGE_BURY)
                        } else {
                            AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_IMAGE_BURY)
                        }
                        DataManager.Remote.toggleBuryNews(bean, mChannel)
                    }
                    R.id.more_btn -> {
                        mView?.goFromRoot(MoreDialogFragment::class.java, MoreDialogPresenterAutoBundle
                                .builder(bean, mChannel, mAnalyticsEventKey, "image_ellipsis_")
                                .mReferName(AppLogKey.Refer.ARTICLE_LIST)
                                .mReferId(mChannel.chid)
                                .bundle())
                    }
                    R.id.news_img -> {
                        val image = bean as? Image ?: return true
                        AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_IMAGE_TO_VIEWER)
                        mView?.goFromRoot(
                                clazz = ImageBrowserFragment::class.java,
                                bundle = ImageBrowserPresenterAutoBundle
                                        .builder(ArrayList(image.info.urls), 0)
                                        .mNews(image)
                                        .mAnalyticsEventKey(mAnalyticsEventKey)
                                        .mRefer(mItemRefer)
                                        .bundle(),
                                fragmentAnimator = FragmentAnimator(
                                        R.anim.core_fade_in,
                                        R.anim.core_fade_out,
                                        R.anim.no_anim,
                                        R.anim.core_fade_out))
                    }
                    R.id.mutiple_news_img0,
                    R.id.mutiple_news_img1,
                    R.id.mutiple_news_img2,
                    R.id.mutiple_news_img3 -> {
                        val index = when (id) {
                            R.id.mutiple_news_img0 -> 0
                            R.id.mutiple_news_img1 -> 1
                            R.id.mutiple_news_img2 -> 2
                            else -> 3
                        }
                        val image = bean as? Image ?: return true
                        val images = if (image.images.isEmpty()) image.info.urls else {
                            image.images.map { it.urls.firstOrNull()!! }
                        }
                        AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_IMAGE_TO_VIEWER)
                        mView?.goFromRoot(
                                clazz = ImageBrowserFragment::class.java,
                                bundle = ImageBrowserPresenterAutoBundle
                                        .builder(ArrayList(images), index)
                                        .mNews(image)
                                        .mAnalyticsEventKey(mAnalyticsEventKey)
                                        .mRefer(mItemRefer)
                                        .bundle(),
                                fragmentAnimator = FragmentAnimator(
                                        R.anim.core_fade_in,
                                        R.anim.core_fade_out,
                                        R.anim.no_anim,
                                        R.anim.core_fade_out))
                    }
                }
            }

            is ArticleHeadlineItem -> {
                when (id) {
                    R.id.headline_more_layout -> {
                        AnalyticsManager.logEvent(AnalyticsKey.Event.NEWS, AnalyticsKey.Parameter.CLICK_HEADLINE_MORE)
                        val channel = Channel()
                        channel.chid = DataDictionary.CHANNEL_ID_HEADLINE
                        channel.channelType = DataDictionary.ChannelType.ARTICLE.value
                        val path = "public_feed"
                        mView?.goFromRoot(
                                clazz = HeadlineFragment::class.java,
                                bundle = NewsListPresenterAutoBundle
                                        .builder(channel, path, AnalyticsKey.Event.HEADLINE)
                                        .mRefer(AppLog.Refer
                                                .newBuilder()
                                                .setItemId(channel.chid)
                                                .setName(AppLogKey.Refer.ARTICLE_LIST)
                                                .build())
                                        .mItemRefer(AppLog.Refer
                                                .newBuilder()
                                                .setItemId(channel.chid)
                                                .setName(AppLogKey.Refer.HEADLINE)
                                                .build())
                                        .mPositionRefer(AppLog.Refer
                                                .newBuilder()
                                                .setItemId(channel.chid)
                                                .setName(AppLogKey.Refer.HEADLINE)
                                                .build())
                                        .mPositionSourceRefer(mPositionRefer)
                                        .bundle())
                    }

                    R.id.headline_cancel_icon_img -> {
                        mListNews.news.removeAt(position)
                        getAdapter()?.removeItem(position)
                    }
                }
            }
            is BaseGifItem -> {
                when (id) {
                    R.id.remove_btn -> {
                        mView?.showRemoveDialog(bean)
                    }
                    R.id.dig_btn -> {
                        if (bean.isDigged) {
                            AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_CANCEL_GIF_DIG)
                        } else {
                            AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_GIF_DIG)
                        }
                        DataManager.Remote.toggleDigNews(bean, mChannel)
                    }
                    R.id.bury_btn -> {
                        if (bean.isBuried) {
                            AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_CANCEL_GIF_BURY)
                        } else {
                            AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_GIF_BURY)
                        }
                        DataManager.Remote.toggleBuryNews(bean, mChannel)
                    }
                    R.id.more_btn -> {
                        mView?.goFromRoot(MoreDialogFragment::class.java, MoreDialogPresenterAutoBundle
                                .builder(bean, mChannel, mAnalyticsEventKey, "gif_ellipsis_")
                                .mReferName(AppLogKey.Refer.ARTICLE_LIST)
                                .mReferId(mChannel.chid)
                                .bundle())
                    }
                }
            }
            is BaseEssayItem -> {
                when (id) {
                    R.id.remove_btn -> {
                        mView?.showRemoveDialog(bean)
                    }
                    R.id.dig_btn -> {
                        if (bean.isDigged) {
                            AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_CANCEL_ESSAY_DIG)
                        } else {
                            AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_ESSAY_DIG)
                        }
                        DataManager.Remote.toggleDigNews(bean, mChannel)
                    }
                    R.id.bury_btn -> {
                        if (bean.isBuried) {
                            AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_CANCEL_ESSAY_BURY)
                        } else {
                            AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_ESSAY_BURY)
                        }
                        DataManager.Remote.toggleBuryNews(bean, mChannel)
                    }
                    R.id.more_btn -> {
                        mView?.goFromRoot(MoreDialogFragment::class.java, MoreDialogPresenterAutoBundle
                                .builder(bean, mChannel, mAnalyticsEventKey, "essay_ellipsis_")
                                .mReferName(AppLogKey.Refer.ARTICLE_LIST)
                                .mReferId(mChannel.chid)
                                .bundle())
                    }
                }
            }
        }
        return super.onItemChildClick(position, item, id)
    }

    override fun onClickRemoveNews(news: BaseNewsBean, isYes: Boolean) {
        if (isYes) {
            if (news is Video) {
                AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_VIDEO_NOT_INTERESTED_CONFIRM)
                GSYVideoManager.releaseAllVideos()
            }

            if (news is Article) {
                AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_ARTICLE_NOT_INTERESTED_CONFIRM)
            }

            DataManager.deleteArticleAndPostNotInterest(news, mChannel)
                    .onErrorReturn { -1 }
                    .io()
                    .subscribeBy(onError = {})
        } else {
            when (news) {
                is Article -> AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_ARTICLE_NOT_INTERESTED_CANCEL)
                is Video -> AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_VIDEO_NOT_INTERESTED_CANCEL)
            }
        }
    }

    override fun onRefreshScrollFinish(success: Boolean) {
        if (success) {
            getAdapter()?.removeAllScrollableHeaders()
            if (mTipItem.isValid()) {
                getAdapter()?.addScrollableHeader(mTipItem)
                if (mChannel.chid == DataDictionary.CHANNEL_ID_RECOMMEND_ARTICLE) {
//                    getAdapter()?.addScrollableHeader(mWeatherBannerItem)
                }
                getAdapter()?.removeScrollableHeaderWithDelay(mTipItem, 1300L)
            }

            if (mChannel.chid == DataDictionary.CHANNEL_ID_RECOMMEND_ARTICLE) {
                mView?.scrollCommonRVBy(CoreApp.getInstance().dip(11))
            } else {
                mView?.scrollCommonRVBy(CoreApp.getInstance().dip(-45))
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Suppress("unused")
    fun onNewsListRefreshEvent(event: NewsListRefreshEvent) {
        if (event.channel != mChannel) {
            return
        }
        if (mListNews.news.filterNotNull().isEmpty()) {
            mView?.showLoading()
            loadData()
        } else {
            mView?.smoothScrollCommonRVTo(0)
            mView?.autoRefresh()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Suppress("unused", "UNUSED_PARAMETER")
    fun onWeatherRegionChangeEvent(event: WeatherRegionChangeEvent) {
        updateWeatherBannerItem()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Suppress("unused")
    fun onNewsListChangeEvent(event: NewsListChangeEvent) {
        if (event.checkChannel && event.channel != mChannel) {
            return
        }
        val news = event.news

        // 删除事件先检查是否是要闻里的新闻，是的话直接处理，不是的话由后面处理
        if (event.action == DataAction.DELETE) {
            // 尝试找出要闻的数据bean
            val headline = mListNews.news.filter {
                it?.type == DataDictionary.NewsType.HEADLINE.value
            }.toMutableList()
            // 一般只有一条要闻数据
            for (i in headline.indices) {
                if (headline[i] is ArticleHeadline) {
                    val articleHeadline = headline[i] as ArticleHeadline
                    if (articleHeadline.news.contains(news)) {
                        articleHeadline.news.remove(news)
                        // 删除了一条数据以后，有可能要闻中的内容已经空了。这时候应该删除整个数据bean
                        if (articleHeadline.news.isEmpty()) {
                            val dataPosition = mListNews.news.indexOf(articleHeadline)
                            val itemPosition = getAdapter()?.currentItems?.indexOfFirst {
                                it.getModel(BaseNewsBean::class.java)?.aid == articleHeadline.aid
                            }
                            if (itemPosition != null) {
                                mListNews.news.removeAt(dataPosition)
                                getAdapter()?.removeItem(itemPosition)
                                return
                            }
                        }
                        getAdapter()?.notifyDataSetChanged()
                        return
                    }
                }
            }
        }

        val position = getAdapter()?.currentItems?.indexOfFirst {
            it.getModel(BaseNewsBean::class.java)?.aid == news.aid
        } ?: return
        val item = getAdapter()?.getItem(position)
        val index = mListNews.news.indexOf(news)
        if (index < 0 || index >= mListNews.news.size) {
            return
        }
        when (event.action) {
            DataAction.DELETE -> {
                mListNews.news.removeAt(index)
                getAdapter()?.removeItem(position)
            }
            DataAction.UPDATE -> {
                when (event.extra) {
                    NewsListChangeEvent.EXTRA.NORMAL -> {
                        keepNewsData(mListNews.news[index], news)
                        mListNews.news[index] = news
                        if (item is BaseArticleItem && news is Article) {
                            item.setModel(news)
                        }
                        if (item is BaseVideoItem && news is Video) {
                            item.setModel(news)
                        }
                        if (item is BasePictureItem && news is Image) {
                            item.setModel(news)
                        }
                        if (item is BaseGifItem && news is GIF) {
                            item.setModel(news)
                        }
                        if (item is BaseEssayItem && news is Essay) {
                            item.setModel(news)
                        }
                        getAdapter()?.notifyItemChanged(position)
                    }
                    NewsListChangeEvent.EXTRA.UPDATE_INFORMATION -> {
                        keepNewsData(mListNews.news[index], news)
                        mListNews.news[index]?.updateInformation(news)
                        getAdapter()?.notifyItemChanged(position, NewsPayload.UPDATE_INFORMATION)
                    }
                }
            }
            else -> {

            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onFontSizeChangeEvent(event: FontSizeChangeEvent) {
        getAdapter()?.notifyDataSetChanged()
    }

    private fun keepNewsData(src: BaseNewsBean?, dest: BaseNewsBean?) {
        val emitTime = src?.emitTime
        if (emitTime != null) {
            dest?.emitTime = emitTime
        }
        val showTime = src?.showTimerTitle
        if (showTime != null) {
            dest?.showTimerTitle = showTime
        }

        dest?.isRead = src?.isRead ?: false
    }

    override fun onViewVisible() {
        super.onViewVisible()
        checkOnItemVisibleTo(true)
        //当列表对用户可见时，距离上次刷新成功时间大于4小时时进行刷新
        if (System.currentTimeMillis() - mLastRefreshTime > 4 * 60 * 60 * 1 * 1000) {
            EventManager.post(NewsListRefreshEvent(mChannel))
        }
        if (mChannel.channelType == DataDictionary.ChannelType.GIF.value) {
            getAdapter()?.state = CommonRecyclerAdapter.FragmentState.VISIBLE
        }
    }

    override fun onViewInvisible() {
        super.onViewInvisible()
        GSYVideoManager.releaseAllVideos()
        checkOnItemVisibleTo(false)
        val bodyBuilder = AppLog.EventBody.newBuilder()
                .setItemId(mChannel.chid)
                .setEnterTime(mVisibleTime)
                .setDuration(System.currentTimeMillis() - mVisibleTime)
        if (mRefer != AppLog.Refer.getDefaultInstance()) {
            @Suppress("UsePropertyAccessSyntax")
            bodyBuilder.setRefer(mRefer)
        }
        AppLogManager.logEvent(
                name = AppLog.EventName.EVENT_ARTICLE_LIST,
                label = AppLogKey.Label.STAY_PAGE,
                body = bodyBuilder.build())
        if (mChannel.channelType == DataDictionary.ChannelType.GIF.value) {
            getAdapter()?.state = CommonRecyclerAdapter.FragmentState.HIDDEN
            getAdapter()?.notifyDataSetChanged()
        }
    }

    override fun onAnalyticsPage(pageName: String): String {
        return pageName + mChannel.chid
    }

    open fun createTimeTitle(list: MutableList<BaseNewsBean>, lastData: BaseNewsBean?) {

    }

    // DEBUG
    private fun setViewShowRefer(text: String) {
        if (!DebugManager.enable || !DebugTool.mShowRefer) {
            return
        }
        mView?.setShowRefer(text)
    }

}

