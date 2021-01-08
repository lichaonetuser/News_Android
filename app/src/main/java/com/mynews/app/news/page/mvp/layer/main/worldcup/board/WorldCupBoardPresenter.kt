package com.mynews.app.news.page.mvp.layer.main.worldcup.board

import android.os.Bundle
import com.mynews.app.news.R
import com.mynews.app.news.analytics.AnalyticsKey
import com.mynews.app.news.applog.AppLogKey
import com.mynews.app.news.bean.*
import com.mynews.app.news.bean.base.BaseBean
import com.mynews.app.news.data.DataAction
import com.mynews.app.news.data.DataDictionary
import com.mynews.app.news.data.DataManager
import com.mynews.app.news.event.EventManager
import com.mynews.app.news.event.change.CommentListChangeEvent
import com.mynews.app.news.event.refresh.NewsListRefreshEvent
import com.mynews.app.news.item.factory.ItemFactory
import com.mynews.app.news.item.payload.CommentPayload
import com.mynews.app.news.item.world.BaseWorldCupBoardItem
import com.mynews.app.news.item.world.WorldCupBoardCommentItem
import com.mynews.app.news.item.world.WorldCupBoardHeaderItem
import com.mynews.app.news.openurl.OpenUrlManager
import com.mynews.app.news.openurl.OpenUrlSchema
import com.mynews.app.news.page.mvp.layer.main.article.detail.ArticleDetailFragment
import com.mynews.app.news.page.mvp.layer.main.article.detail.ArticleDetailPresenterAutoBundle
import com.mynews.app.news.page.mvp.layer.main.dialog.comment.InputCommentDialogFragment
import com.mynews.app.news.page.mvp.layer.main.dialog.comment.InputCommentDialogPresenterAutoBundle
import com.mynews.app.news.page.mvp.layer.main.dialog.more.comment.CommentMoreDialogFragment
import com.mynews.app.news.page.mvp.layer.main.dialog.more.comment.CommentMoreDialogPresenterAutoBundle
import com.mynews.app.news.page.mvp.layer.main.image.detail.ImageDetailFragment
import com.mynews.app.news.page.mvp.layer.main.image.detail.ImageDetailPresenterAutoBundle
import com.mynews.app.news.page.mvp.layer.main.video.detail.VideoDetailFragment
import com.mynews.app.news.page.mvp.layer.main.video.detail.VideoDetailPresenterAutoBundle
import com.mynews.app.news.proto.AppLog
import com.mynews.app.news.util.checkHasMore
import com.mynews.common.core.analytics.AnalyticsManager
import com.mynews.common.core.rx.schedulers.ioToMain
import com.mynews.common.core.util.ResUtils
import com.mynews.common.extension.app.mvp.bindToLifecycle
import com.mynews.common.extension.app.mvp.loading.list.MVPListPresent
import com.mynews.common.extension.widget.recycler.item.BaseItem
import com.mynews.common.extension.widget.recycler.util.convertBeanToItems
import com.yatatsu.autobundle.AutoBundleField
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import me.yokeyword.fragmentation.ISupportFragment
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.regex.Pattern

class WorldCupBoardPresenter : MVPListPresent<WorldCupBoardContract.View>(),
        WorldCupBoardContract.Presenter<WorldCupBoardContract.View> {

    @AutoBundleField
    var mTargetType = DataDictionary.TargetType.WORLD_CUP
    @AutoBundleField
    var mTargetId = ""
    @AutoBundleField
    lateinit var mChannel: Channel
    @AutoBundleField
    var mAnalyticsEventKey: String = AnalyticsKey.Event.WORLD_CUP
    /**
     * 是否优先展示返回结果中的Content(新闻，图片，球队等)
     * false的话不显示content，展示Reply内容(直接构建WorldCupBoardCommentItem)！！只对最新(recent)和最热(hot)列表应用此逻辑
     * true的话如果有content，显示content(构建对应样式Item)， 否则显示replay(构建WorldCupBoardCommentItem)
     */
    @AutoBundleField(required = false)
    var mContentPriority = false
    /**
     * type 1. 热门的列表页，其他board不传
     */
    @AutoBundleField(required = false)
    var mRequestType: Int? = null
    @AutoBundleField(required = false)
    var mWorldCupBoard = WorldCupBoard()

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        if (isRestore()) {
            restore()
        } else {
            loadData(0)
        }
        EventManager.register(this)
    }

    private fun restore() {
        mView?.setLoadMoreEnable(mWorldCupBoard.hasMore)
        loadDataComplete(Observable.just(mWorldCupBoard)
                .convertBeanToItems(ItemFactory.WORLD_CUP_BOARD)
                .blockingSingle())
    }

    override fun onDestroy() {
        super.onDestroy()
        EventManager.unregister(this)
    }

    override fun loadData(pageNum: Int, isRefresh: Boolean) {
        DataManager.Remote.getSportsBoard(
                pageNo = pageNum,
                chid = mChannel.chid,
                lastCommentId = if (isRefresh) null else getLastCommentId(),
                type = mRequestType)
                .checkHasMore(getAdapter())
                .map {
                    if (isRefresh) {
                        mWorldCupBoard.hot.clear()
                        mWorldCupBoard.recent.clear()
                        mWorldCupBoard.aggregated.clear()
                        mWorldCupBoard.hot.addAll(it.hot.distinct())
                        mWorldCupBoard.recent.addAll(it.recent.distinct())
                        mWorldCupBoard.aggregated.addAll(it.aggregated.distinct())
                    } else {
                        mWorldCupBoard.hot = getLoadMoreReturnComments(mWorldCupBoard.hot, it.hot)
                        mWorldCupBoard.recent = getLoadMoreReturnComments(mWorldCupBoard.recent, it.recent)
                        mWorldCupBoard.aggregated = getLoadMoreReturnComments(mWorldCupBoard.aggregated, it.aggregated)
                    }
                    mWorldCupBoard = it.copy(
                            hot = mWorldCupBoard.hot,
                            recent = mWorldCupBoard.recent,
                            aggregated = mWorldCupBoard.aggregated
                    )
                    mWorldCupBoard.hasMore = it.hasMore
                    mWorldCupBoard
                }
                .convertBeanToItems(
                        if (mContentPriority) {
                            ItemFactory.WORLD_CUP_BOARD_CONTENT_PRIORITY
                        } else {
                            ItemFactory.WORLD_CUP_BOARD
                        })
                .ioToMain()
                .bindToLifecycle(this)
                .subscribeBy(
                        onNext = {
                            loadDataComplete(it, true, 0, 0)
                        },
                        onError = {
                            loadDataFail()
                        })
    }

    private fun getLoadMoreReturnComments(localList: List<Comment>, responseList: List<Comment>): ArrayList<Comment> {
        val filterList = localList.filterNot { responseList.contains(it) }
        val returnList = ArrayList(filterList)
        returnList.addAll(responseList.distinct())
        return returnList
    }

    private fun getLastCommentId(): String? {
        return when {
            mWorldCupBoard.aggregated.isNotEmpty() -> return mWorldCupBoard.aggregated.last().id
            mWorldCupBoard.recent.isNotEmpty() -> return mWorldCupBoard.recent.last().id
            mWorldCupBoard.hot.isNotEmpty() -> return mWorldCupBoard.hot.last().id
            else -> null
        }
    }

    override fun loadDataComplete(items: List<BaseItem<*, *>>, isUpdate: Boolean, position: Int, scrollToPosition: Int) {
        if (mIsRefreshing) {
            mView?.onRefreshComplete(items, isUpdate, position, scrollToPosition)
        } else {
            mView?.onLoadMoreComplete(items)
        }
    }

    override fun onItemClick(position: Int, item: BaseItem<*, *>): Boolean {
        AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_COMMENT_SHOW_BUTTON)
        val comment = item.getModel(Comment::class.java) ?: return false

        val httpPattern = Pattern.compile("((http|https)://)(([a-zA-Z0-9._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9&%_./-~-]*)?", Pattern.CASE_INSENSITIVE)
        val httpMatch = httpPattern.matcher(comment.content)

//        OpenUrl支持，正则有点问题
//        val openUrlPattern = Pattern.compile("((${OpenUrlSchema.NEWSJET}|${OpenUrlSchema.LOCAL})://)*", Pattern.CASE_INSENSITIVE)
//        val openUrlMatch = openUrlPattern.matcher(comment.content)

        when {
            httpMatch.find() -> {
                val url = httpMatch.group()
                mView?.showGoLinkDialog(url)
            }
//            openUrlMatch.find() -> {
//                val url = openUrlMatch.group()
//                mView?.showGoLinkDialog(url)
//            }
            else -> goCommentMore(comment)
        }

        return false
    }

    override fun onItemChildClick(position: Int, item: BaseItem<*, *>, id: Int): Boolean {
        val comment = item.getModel(Comment::class.java) ?: return false
        val targetBean: BaseBean? = ((comment.article ?: comment.video) as? BaseBean?)
                ?: comment.image ?: comment.player ?: comment.team ?: comment.match
        when (id) {
            R.id.dig_btn -> {
                if (comment.isDigged) {
                    AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.COMMENT_LIKE_CANCEL)
                } else {
                    AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.COMMENT_LIKE)
                }
                DataManager.Remote.toggleDigComment(comment, mChannel)
            }
            R.id.content_layout -> {
                when (targetBean) {
                    is Article -> {
                        AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CHAT_BOARD_CLICK_TO_ARTICLE)
                        mView?.goFromRoot(ArticleDetailFragment::class.java, ArticleDetailPresenterAutoBundle
                                .builder(targetBean, false, AppLog.Refer.newBuilder()
                                        .setName(AppLogKey.Refer.MY_COMMENT)
                                        .build())
                                .bundle(), ISupportFragment.STANDARD)
                    }
                    is Video -> {
                        AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CHAT_BOARD_CLICK_TO_VIDEO)
                        mView?.goFromRoot(VideoDetailFragment::class.java, VideoDetailPresenterAutoBundle
                                .builder(targetBean, false, AppLog.Refer.newBuilder()
                                        .setName(AppLogKey.Refer.MY_COMMENT)
                                        .build())
                                .bundle(), ISupportFragment.STANDARD)

                    }
                    is Image -> {
                        AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CHAT_BOARD_CLICK_TO_IMAGE)
                        mView?.goFromRoot(ImageDetailFragment::class.java, ImageDetailPresenterAutoBundle
                                .builder(targetBean, false, AppLog.Refer.newBuilder()
                                        .setName(AppLogKey.Refer.MY_COMMENT)
                                        .build())
                                .bundle(), ISupportFragment.STANDARD)
                    }
                }
            }
        }
        return super.onItemChildClick(position, item, id)
    }

    override fun onClickPostComment() {
        AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_BAR_COMMENT_POST)
//        if (!AccountManager.isLogin()) {
//            mView?.goFromRoot(LoginDialogFragment::class.java, hideSelf = false)
//            return
//        }
        mView?.goFromRoot(InputCommentDialogFragment::class.java,
                InputCommentDialogPresenterAutoBundle.builder(mChannel, mTargetType, mTargetId, mAnalyticsEventKey)
                        .bundle(), hideSelf = false)
    }

    override fun onClickGoLinkDialog(url: String, isYes: Boolean) {
        if (!isYes) {
            return
        }

        if (url.startsWith(OpenUrlSchema.NEWSJET) || url.startsWith(OpenUrlSchema.LOCAL)) {
            OpenUrlManager.checkOpenUrl(url)
        } else {
            mView?.startBrowserIntent(url)
        }
    }

    fun goCommentMore(comment: Comment) {
//        if (!AccountManager.isLogin()) {
//            mView?.goFromRoot(LoginDialogFragment::class.java, hideSelf = false)
//            return
//        }
        mView?.goFromRoot(CommentMoreDialogFragment::class.java, CommentMoreDialogPresenterAutoBundle
                .builder(comment, mChannel, mTargetType, mTargetId, mAnalyticsEventKey)
                .mShowForwardBoard(false)
                .bundle())
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCommentListChangeEvent(event: CommentListChangeEvent) {
        val channel = event.targetBean as? Channel ?: return
        if (channel.chid != mChannel.chid) {
            return
        }
        /**
         * 时间紧迫，原谅我不经大脑写这么low的代码
         * 如果之后有用就重构，没用放着吧
         */
        val comment = event.comment
        val hotIndex = mWorldCupBoard.hot.indexOf(comment)
        val recentIndex = mWorldCupBoard.recent.indexOf(comment)
        val aggregatedIndex = mWorldCupBoard.aggregated.indexOf(comment)
        when (event.action) {
            DataAction.INSERT -> {
                mWorldCupBoard.recent.add(0, comment)
                mWorldCupBoard.recentCount = (mWorldCupBoard.recentCount ?: 0) + 1
                val headerTitle = ResUtils.getString(R.string.WorldCup2018_Comment_Newest)
                val count = mWorldCupBoard.recentCount ?: mWorldCupBoard.recent.size
                val headerItem = WorldCupBoardHeaderItem(headerTitle, count)
                val newItem = WorldCupBoardCommentItem(comment)
                newItem.header = headerItem
                val recentHeaderPosition = getAdapter()?.getGlobalPositionOf(headerItem) ?: 0
                getAdapter()?.updateItem(headerItem, true)
                getAdapter()?.addItemToSection(newItem, headerItem, 0)
                mView?.scrollToPositionTop(recentHeaderPosition)
            }
            DataAction.DELETE -> {
                if (hotIndex >= 0 && hotIndex < mWorldCupBoard.hot.size) {
                    mWorldCupBoard.hot.removeAt(hotIndex)
                    val headerTitle = ResUtils.getString(R.string.WorldCup2018_Comment_Best)
                    val count = (mWorldCupBoard.hotCount ?: mWorldCupBoard.hot.size) - 1
                    val headerItem = WorldCupBoardHeaderItem(headerTitle, count)
                    val position = getAdapter()?.getSectionItems(headerItem)?.indexOfFirst {
                        if (it is BaseWorldCupBoardItem) {
                            it.getModel(Comment::class.java)?.id == comment.id
                        } else {
                            false
                        }
                    } ?: return
                    getAdapter()?.updateItem(headerItem, true)
                    val item =
                            getAdapter()?.getSectionItems(headerItem)?.get(position)
                    val removePosition = getAdapter()?.getGlobalPositionOf(item) ?: -1
                    if (removePosition > -1) {
                        getAdapter()?.removeItem(removePosition)
                    }
                }
                if (recentIndex >= 0 && recentIndex < mWorldCupBoard.recent.size) {
                    mWorldCupBoard.recent.removeAt(recentIndex)
                    val headerTitle = ResUtils.getString(R.string.WorldCup2018_Comment_Newest)
                    val count = (mWorldCupBoard.recentCount ?: mWorldCupBoard.recent.size) - 1
                    val headerItem = WorldCupBoardHeaderItem(headerTitle, count)
                    val position = getAdapter()?.getSectionItems(headerItem)?.indexOfFirst {
                        if (it is BaseWorldCupBoardItem) {
                            it.getModel(Comment::class.java)?.id == comment.id
                        } else {
                            false
                        }
                    } ?: return
                    getAdapter()?.updateItem(headerItem, true)
                    val item =
                            getAdapter()?.getSectionItems(headerItem)?.get(position)
                    val removePosition = getAdapter()?.getGlobalPositionOf(item) ?: -1
                    if (removePosition > -1) {
                        getAdapter()?.removeItem(removePosition)
                    }
                }
                if (aggregatedIndex >= 0 && aggregatedIndex < mWorldCupBoard.aggregated.size) {
                    mWorldCupBoard.aggregated.removeAt(aggregatedIndex)
                    val headerTitle = ResUtils.getString(R.string.WorldCup2018_Comment_Other)
                    val count = (mWorldCupBoard.aggregatedCount
                            ?: mWorldCupBoard.aggregated.size) - 1
                    val headerItem = WorldCupBoardHeaderItem(headerTitle, count)
                    val position = getAdapter()?.getSectionItems(headerItem)?.indexOfFirst {
                        if (it is BaseWorldCupBoardItem) {
                            it.getModel(Comment::class.java)?.id == comment.id
                        } else {
                            false
                        }
                    } ?: return
                    getAdapter()?.updateItem(headerItem, true)
                    val item =
                            getAdapter()?.getSectionItems(headerItem)?.get(position)
                    val removePosition = getAdapter()?.getGlobalPositionOf(item) ?: -1
                    if (removePosition > -1) {
                        getAdapter()?.removeItem(removePosition)
                    }
                }
            }
            DataAction.UPDATE -> {
                if (hotIndex >= 0 && hotIndex < mWorldCupBoard.hot.size) {
                    when (event.extra) {
                        CommentListChangeEvent.EXTRA.NORMAL -> {
                            mWorldCupBoard.hot[hotIndex] = comment
                        }
                        CommentListChangeEvent.EXTRA.UPDATE_INFORMATION -> {
                            mWorldCupBoard.hot[hotIndex].updateInfo(comment)
                        }
                    }
                    val headerTitle = ResUtils.getString(R.string.WorldCup2018_Comment_Best)
                    val count = (mWorldCupBoard.hotCount ?: mWorldCupBoard.hot.size)
                    val headerItem = WorldCupBoardHeaderItem(headerTitle, count)
                    val sectionItems = getAdapter()?.getSectionItems(headerItem)
                    val position = sectionItems?.indexOfFirst {
                        if (it is BaseWorldCupBoardItem) {
                            it.getModel(Comment::class.java)?.id == comment.id
                        } else {
                            false
                        }
                    } ?: -1
                    if (position > -1) {
                        val item =
                                getAdapter()?.getSectionItems(headerItem)?.get(position)
                        if (item is BaseWorldCupBoardItem) {
                            item.setModel(comment)
                            getAdapter()?.updateItem(item, CommentPayload.DIG)
                        }
                    }
                }
                if (recentIndex >= 0 && recentIndex < mWorldCupBoard.recent.size) {
                    when (event.extra) {
                        CommentListChangeEvent.EXTRA.NORMAL -> {
                            mWorldCupBoard.recent[recentIndex] = comment
                        }
                        CommentListChangeEvent.EXTRA.UPDATE_INFORMATION -> {
                            mWorldCupBoard.recent[recentIndex].updateInfo(comment)
                        }
                    }
                    val headerTitle = ResUtils.getString(R.string.WorldCup2018_Comment_Newest)
                    val count = (mWorldCupBoard.recentCount ?: mWorldCupBoard.recent.size)
                    val headerItem = WorldCupBoardHeaderItem(headerTitle, count)
                    val sectionItems = getAdapter()?.getSectionItems(headerItem)
                    val position = sectionItems?.indexOfFirst {
                        if (it is BaseWorldCupBoardItem) {
                            it.getModel(Comment::class.java)?.id == comment.id
                        } else {
                            false
                        }
                    } ?: -1
                    if (position > -1) {
                        val item =
                                getAdapter()?.getSectionItems(headerItem)?.get(position)
                        if (item is BaseWorldCupBoardItem) {
                            item.setModel(comment)
                            getAdapter()?.updateItem(item, CommentPayload.DIG)
                        }
                    }
                }
                if (aggregatedIndex >= 0 && aggregatedIndex < mWorldCupBoard.aggregated.size) {
                    when (event.extra) {
                        CommentListChangeEvent.EXTRA.NORMAL -> {
                            mWorldCupBoard.aggregated[aggregatedIndex] = comment
                        }
                        CommentListChangeEvent.EXTRA.UPDATE_INFORMATION -> {
                            mWorldCupBoard.aggregated[aggregatedIndex].updateInfo(comment)
                        }
                    }
                    val headerTitle = ResUtils.getString(R.string.WorldCup2018_Comment_Other)
                    val count = (mWorldCupBoard.aggregatedCount
                            ?: mWorldCupBoard.aggregated.size)
                    val headerItem = WorldCupBoardHeaderItem(headerTitle, count)
                    val sectionItems = getAdapter()?.getSectionItems(headerItem)
                    val position = sectionItems?.indexOfFirst {
                        if (it is BaseWorldCupBoardItem) {
                            it.getModel(Comment::class.java)?.id == comment.id
                        } else {
                            false
                        }
                    } ?: -1
                    if (position > -1) {
                        val item =
                                getAdapter()?.getSectionItems(headerItem)?.get(position)
                        if (item is BaseWorldCupBoardItem) {
                            item.setModel(comment)
                            getAdapter()?.updateItem(item, CommentPayload.DIG)
                        }
                    }
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Suppress("unused")
    fun onNewsListRefreshEvent(event: NewsListRefreshEvent) {
        if (event.channel != mChannel) {
            return
        }
        if (getAdapter()?.isAllEmpty() == true) {
            mView?.showLoading()
            loadData()
        } else {
            getAdapter()?.smoothScrollToPosition(0)
            mView?.autoRefresh()
        }
    }

}

