package com.box.app.news.page.mvp.layer.main.list.comment

import android.os.Bundle
import com.box.app.news.R
import com.box.app.news.analytics.AnalyticsKey
import com.box.app.news.bean.*
import com.box.app.news.bean.base.BaseNewsBean
import com.box.app.news.bean.list.ListComment
import com.box.app.news.data.DataAction
import com.box.app.news.data.DataDictionary
import com.box.app.news.data.DataManager
import com.box.app.news.data.adapter.bundle.AppLogReferConverter
import com.box.app.news.event.EventManager
import com.box.app.news.event.change.CommentListChangeEvent
import com.box.app.news.event.change.FontSizeChangeEvent
import com.box.app.news.item.CommentItem
import com.box.app.news.item.factory.ItemFactory
import com.box.app.news.item.payload.CommentPayload
import com.box.app.news.page.mvp.layer.main.dialog.comment.InputCommentDialogFragment
import com.box.app.news.page.mvp.layer.main.dialog.comment.InputCommentDialogPresenterAutoBundle
import com.box.app.news.page.mvp.layer.main.dialog.more.comment.CommentMoreDialogFragment
import com.box.app.news.page.mvp.layer.main.dialog.more.comment.CommentMoreDialogPresenterAutoBundle
import com.box.app.news.proto.AppLog
import com.box.app.news.util.ToastUtils
import com.box.common.core.analytics.AnalyticsManager
import com.box.common.core.rx.schedulers.ioToMain
import com.box.common.core.util.ResUtils
import com.box.common.extension.app.mvp.bindToLifecycle
import com.box.common.extension.app.mvp.loading.list.MVPListPresent
import com.box.common.extension.widget.recycler.item.BaseItem
import com.box.common.extension.widget.recycler.util.convertBeansToItems
import com.yatatsu.autobundle.AutoBundleField
import io.reactivex.rxkotlin.subscribeBy
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class CommentPresenter : MVPListPresent<CommentContract.View>(),
        CommentContract.Presenter<CommentContract.View> {

    @AutoBundleField
    var mNews: BaseNewsBean = Article()
    @AutoBundleField(converter = AppLogReferConverter::class)
    var mRefer: AppLog.Refer = AppLog.Refer.getDefaultInstance()
    @AutoBundleField(required = false)
    var mParentChannel: Channel = Channel()
    @AutoBundleField(required = false)
    var mListComment: ListComment = ListComment()
    @AutoBundleField(required = false)
    var mShowForwardBoard: Boolean = false
    @AutoBundleField
    lateinit var mAnalyticsEventKey: String

    //自定义字段只是于这个界面的生命周期内，避免修改mBean里的字段引起不可知错误
    private var isLiked = false

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        loadData(0)
        EventManager.register(this)
        isLiked = mNews.isDigged
    }

    override fun onDestroy() {
        super.onDestroy()
        EventManager.unregister(this)
    }

    override fun loadData(pageNum: Int, isRefresh: Boolean) {
        DataManager.Remote.loadComment(DataDictionary.TargetType.ARTICLE.value, mNews.aid)
                .map {
                    mListComment = it
                    it.comments
                }
                .convertBeansToItems(ItemFactory.COMMENT)
                .ioToMain()
                .bindToLifecycle(this)
                .subscribeBy(
                        onNext = {
                            mView?.onLoadMoreComplete(it)
                            mView?.setLoadMoreEnable(mListComment.hasMore)
                        },
                        onError = {
                            loadDataFail()
                        }
                )
    }

    fun loadMoreData(pageNum: Int) {
        DataManager.Remote.loadCommentMore(targetType = DataDictionary.TargetType.ARTICLE.value,
                targetId = mNews.aid,
                pageNo = pageNum,
                lastCommentId = mListComment.comments.last().id)
                .map {
                    mListComment.hasMore = it.hasMore
                    mListComment.comments.addAll(it.comments)
                    it.comments
                }
                .convertBeansToItems(ItemFactory.COMMENT)
                .ioToMain()
                .bindToLifecycle(this)
                .subscribeBy(
                        onNext = {
                            mView?.onLoadMoreComplete(it)
                            mView?.setLoadMoreEnable(mListComment.hasMore)
                        },
                        onError = {
                            loadDataFail()
                        }
                )
    }

    override fun onLoadMore(lastPosition: Int, currentPage: Int) {
        mIsRefreshing = false
        loadMoreData(currentPage)
    }

    override fun onItemClick(position: Int, item: BaseItem<*, *>): Boolean {
        AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_COMMENT_SHOW_BUTTON)
        val comment = item.getModel(Comment::class.java) ?: return false
        goCommentMore(comment)
        return false
    }

    override fun onItemChildClick(position: Int, item: BaseItem<*, *>, id: Int): Boolean {
        val comment = item.getModel(Comment::class.java) ?: return false
        when (id) {
            R.id.dig_btn -> {
                isLiked = !isLiked
                if (isLiked) {
                    AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.COMMENT_LIKE)
                } else {
                    AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.COMMENT_LIKE_CANCEL)
                }
                DataManager.Remote.toggleDigComment(comment, mNews)
            }
            R.id.more_btn -> {
                goCommentMore(comment)
            }
        }
        return super.onItemChildClick(position, item, id)
    }

    private fun goCommentMore(comment: Comment) {
        val targetType = when (mNews) {
            is Article -> DataDictionary.TargetType.ARTICLE
            is Video -> DataDictionary.TargetType.VIDEO
            is Image -> DataDictionary.TargetType.IMAGE
            is GIF -> DataDictionary.TargetType.GIF
            is Essay -> DataDictionary.TargetType.ESSAY
            else -> return
        }
        mView?.goFromRoot(CommentMoreDialogFragment::class.java, CommentMoreDialogPresenterAutoBundle
                .builder(comment, mNews, targetType, mNews.aid, mAnalyticsEventKey)
                .mShowForwardBoard(mShowForwardBoard)
                .bundle(), hideSelf = false)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCommentListChangeEvent(event: CommentListChangeEvent) {
        val news = event.targetBean as? BaseNewsBean ?: return
        if (news.aid != this.mNews.aid) {
            return
        }
        val comment = event.comment
        val position = getAdapter()?.currentItems?.indexOfFirst {
            it.getModel(Comment::class.java)?.id == comment.id
        } ?: return
        val item = getAdapter()?.getItem(position)
        val index = mListComment.comments.indexOf(comment)
        when (event.action) {
            DataAction.INSERT -> {
                mListComment.comments.add(0, comment)
                getAdapter()?.addItem(0, CommentItem(comment))
                getAdapter()?.scrollToPosition(0)
            }
            DataAction.DELETE -> {
                if (index < 0 || index >= mListComment.comments.size) {
                    return
                }
                mListComment.comments.removeAt(index)
                getAdapter()?.removeItem(position)
            }
            DataAction.UPDATE -> {
                if (index < 0 || index >= mListComment.comments.size) {
                    return
                }

                when (event.extra) {
                    CommentListChangeEvent.EXTRA.NORMAL -> {
                        mListComment.comments[index] = comment
                        if (item is CommentItem) {
                            item.setModel(comment)
                        }
                        getAdapter()?.notifyItemChanged(position)
                    }
                    CommentListChangeEvent.EXTRA.UPDATE_INFORMATION -> {
                        mListComment.comments[index].updateInfo(comment)
                        if (item is CommentItem) {
                            item.setModel(comment)
                        }
                        getAdapter()?.notifyItemChanged(position, CommentPayload.DIG)
                    }
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onFontSizeChangeEvent(event: FontSizeChangeEvent) {
        getAdapter()?.notifyDataSetChanged()
    }

    override fun onLoadingLayoutRetryClicked(id: Int) {
        when (id) {
            R.layout.layout_comment_empty -> {
                if (mNews.commentType == DataDictionary.CommentType.ENABLE.value) {
                    val targetType = when (mNews) {
                        is Article -> DataDictionary.TargetType.ARTICLE
                        is Video -> DataDictionary.TargetType.VIDEO
                        is Image -> DataDictionary.TargetType.IMAGE
                        is GIF -> DataDictionary.TargetType.GIF
                        is Essay -> DataDictionary.TargetType.ESSAY
                        else -> return
                    }
                    mView?.goFromRoot(InputCommentDialogFragment::class.java,
                            InputCommentDialogPresenterAutoBundle
                                    .builder(mNews, targetType, mNews.aid, mAnalyticsEventKey)
                                    .mShowForwardBoard(mShowForwardBoard)
                                    .bundle(), hideSelf = false)
                } else {
                    ToastUtils.showToast(ResUtils.getString(R.string.Tip_NewsCommentUnsupported))
                }
            }
            R.layout.layout_comment_fail -> {
                super.onLoadingLayoutRetryClicked(id)
            }
        }
    }

    override fun onUpdateEmptyView(size: Int) {
        if (size == 0) {
            if (mNews.commentType == DataDictionary.CommentType.ENABLE.value) {
                mView?.showEmpty(ResUtils.getString(R.string.Comment_NoCommentClickPost), R.drawable.comment_loading_empty_ic)
            } else {
                mView?.showEmpty(ResUtils.getString(R.string.Tip_NewsCommentUnsupported), R.drawable.comment_loading_no_comment_ic)
            }
        } else {
            mView?.showContent()
        }
    }

    override fun onUpdateShowForwardBoard(showForwardBoard: Boolean) {
        this.mShowForwardBoard = showForwardBoard
    }

}

