package com.mynews.app.news.page.mvp.layer.main.list.comment.my

import android.os.Bundle
import com.mynews.app.news.R
import com.mynews.app.news.analytics.AnalyticsKey
import com.mynews.app.news.applog.AppLogKey
import com.mynews.app.news.bean.*
import com.mynews.app.news.bean.base.BaseNewsBean
import com.mynews.app.news.bean.list.ListComment
import com.mynews.app.news.data.DataAction
import com.mynews.app.news.data.DataDictionary
import com.mynews.app.news.data.DataManager
import com.mynews.app.news.event.EventManager
import com.mynews.app.news.event.change.CommentListChangeEvent
import com.mynews.app.news.item.CommentItem
import com.mynews.app.news.item.CommentMoreItem
import com.mynews.app.news.item.factory.ItemFactory
import com.mynews.app.news.item.payload.CommentPayload
import com.mynews.app.news.page.mvp.layer.main.article.detail.ArticleDetailFragment
import com.mynews.app.news.page.mvp.layer.main.article.detail.ArticleDetailPresenterAutoBundle
import com.mynews.app.news.page.mvp.layer.main.dialog.more.comment.CommentMoreDialogFragment
import com.mynews.app.news.page.mvp.layer.main.dialog.more.comment.CommentMoreDialogPresenterAutoBundle
import com.mynews.app.news.page.mvp.layer.main.essay.EssayDetailFragment
import com.mynews.app.news.page.mvp.layer.main.essay.EssayDetailPresenterAutoBundle
import com.mynews.app.news.page.mvp.layer.main.gif.GifDetailFragment
import com.mynews.app.news.page.mvp.layer.main.gif.GifDetailPresenterAutoBundle
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
import com.mynews.common.extension.widget.recycler.item.DefaultNoMoreItem
import com.mynews.common.extension.widget.recycler.util.convertBeansToItems
import com.yatatsu.autobundle.AutoBundleField
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MyCommentPresenter : MVPListPresent<MyCommentContract.View>(),
        MyCommentContract.Presenter<MyCommentContract.View> {

    @AutoBundleField(required = false)
    var mListComments = ListComment()

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        AnalyticsManager.logEvent(AnalyticsKey.Event.MY_COMMENT_PAGE, AnalyticsKey.Parameter.ENTER)
        EventManager.register(this)
    }

    override fun onEnterEnd() {
        super.onEnterEnd()
        //页面创建，不关心用户是否可见
        when {
        //首次创建,
            isNotRestore() -> {
                loadData(1)
            }
        //从销毁状态中恢复，且销毁前数据不为空，加载销毁前的数据
            isRestore() && mListComments.comments.isNotEmpty() -> {
                //恢复是否可以加载更多
                mView?.setLoadMoreEnable(mListComments.hasMore)
                //同步查询并加载销毁前的数据
                loadDataComplete(Observable.just(mListComments.comments)
                        .convertBeansToItems(ItemFactory.COMMENT)
                        .blockingSingle())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventManager.unregister(this)
    }

    override fun loadData(pageNum: Int, isRefresh: Boolean) {
        DataManager.Remote.getMyCommentlist(pageNo = pageNum,
                lastCommentId = mListComments.comments.lastOrNull()?.id ?: "")
                .checkHasMore(getAdapter())
                .map { it: ListComment ->
                    if (mListComments.comments.isEmpty()) {
                        mListComments = it
                    } else {
                        mListComments.hasMore = it.hasMore
                        mListComments.comments.addAll(it.comments)
                    }
                    it.comments
                }
                .convertBeansToItems(ItemFactory.MYCOMMENT)
                .ioToMain()
                .bindToLifecycle(this)
                .subscribeBy(
                        onNext = { items ->
                            mView?.setLoadMoreEnable(mListComments.hasMore)
                            mView?.onLoadMoreComplete(items)
                            if (!mListComments.hasMore && items.isNotEmpty()) {
                                getAdapter()?.addItem(DefaultNoMoreItem())
                            }
                        },
                        onError = { throwable ->
                            loadDataFail()
                        })
    }

    override fun onItemClick(position: Int, item: BaseItem<*, *>): Boolean {
        val comment = item.getModel(Comment::class.java) ?: return false
        val news: BaseNewsBean = (comment.article ?: comment.video ?: comment.image ?: comment.gif ?: comment.essay) as? BaseNewsBean ?: return false
        goCommentMore(comment, news)
        return false
    }

    override fun onItemChildClick(position: Int, item: BaseItem<*, *>, id: Int): Boolean {
        val comment = item.getModel(Comment::class.java) ?: return false
        val news: BaseNewsBean = (comment.article ?: comment.video ?: comment.image ?: comment.gif ?: comment.essay) as? BaseNewsBean ?: return false
        when (id) {
            R.id.dig_btn -> {
                if (comment.isDigged) {
                    AnalyticsManager.logEvent(AnalyticsKey.Event.MY_COMMENT_PAGE, AnalyticsKey.Parameter.COMMENT_LIKE_CANCEL)
                } else {
                    AnalyticsManager.logEvent(AnalyticsKey.Event.MY_COMMENT_PAGE, AnalyticsKey.Parameter.COMMENT_LIKE)
                }
                DataManager.Remote.toggleDigComment(comment, news)
            }
            R.id.news_layout -> {
                AnalyticsManager.logEvent(AnalyticsKey.Event.MY_COMMENT_PAGE, AnalyticsKey.Parameter.CLICK_TO_ORIGIN_PAGE)
                when (news) {
                    is Article -> {
                        Article
                        mView?.goFromRoot(ArticleDetailFragment::class.java, ArticleDetailPresenterAutoBundle
                                .builder(news, false, AppLog.Refer.newBuilder()
                                        .setName(AppLogKey.Refer.MY_COMMENT)
                                        .build())
                                .bundle())
                    }
                    is Video -> {
                        mView?.goFromRoot(VideoDetailFragment::class.java, VideoDetailPresenterAutoBundle
                                .builder(news, false, AppLog.Refer.newBuilder()
                                        .setName(AppLogKey.Refer.MY_COMMENT)
                                        .build())
                                .bundle())

                    }
                    is Image -> {
                        mView?.goFromRoot(ImageDetailFragment::class.java, ImageDetailPresenterAutoBundle
                                .builder(news, false, AppLog.Refer.newBuilder()
                                        .setName(AppLogKey.Refer.MY_COMMENT)
                                        .build())
                                .bundle())
                    }
                    is GIF -> {
                        mView?.goFromRoot(GifDetailFragment::class.java, GifDetailPresenterAutoBundle
                                .builder(news, false, AppLog.Refer.newBuilder()
                                        .setName(AppLogKey.Refer.MY_COMMENT)
                                        .build())
                                .bundle())
                    }
                    is Essay -> {
                        mView?.goFromRoot(EssayDetailFragment::class.java, EssayDetailPresenterAutoBundle
                                .builder(news, false, AppLog.Refer.newBuilder()
                                        .setName(AppLogKey.Refer.MY_COMMENT)
                                        .build())
                                .bundle())
                    }
                }
            }
            R.id.more_btn -> {
                goCommentMore(comment, news)
            }
//            R.id.comment_item_holder -> {
//                var handlerContainer = MyCommentHandleView(CoreApp.getInstance())
//                var contentList = arrayListOf<String>()
//                contentList.add(CoreApp.getInstance().resources.getString(R.string.Common_Delete))
//                handlerContainer.setContent(contentList)
//                var container = RelativeLayout(CoreApp.getInstance())
//
//                val handlerContainerLp = RelativeLayout.LayoutParams(
//                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
//
//                val handlerIndicatorLp = RelativeLayout.LayoutParams(
//                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
//
//                val locationItem :IntArray = intArrayOf(1, 2)
//                val locationRecycleview :IntArray = intArrayOf(1, 2)
//                val holder = getAdapter()?.recyclerView?.findViewHolderForAdapterPosition(position)
//                if (holder !is MyCommentItem.ViewHolder) {
//                    return true
//                }
//                holder.itemView.getLocationOnScreen(locationItem)
//
//                getAdapter()?.recyclerView?.getLocationOnScreen(locationRecycleview)
//
//                var showTop : Boolean = true
//
//                var commentIndicator = ImageView(CoreApp.getInstance())
//                commentIndicator.setImageDrawable(CoreApp.getInstance().resources.getDrawable(R.drawable.comment_handler_indicator))
//
//                if(locationItem.get(1) - CoreApp.getInstance().dip(60) < locationRecycleview.get(1)) {
//                    showTop = false
//                    commentIndicator.setRotation(180f);
//                    handlerIndicatorLp.addRule(RelativeLayout.ABOVE,  handlerContainer.getId())
//                    handlerIndicatorLp.addRule(RelativeLayout.ALIGN_PARENT_TOP)
//                    handlerIndicatorLp.addRule(RelativeLayout.CENTER_HORIZONTAL)
//                    handlerContainerLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
//                } else {
//                    showTop = true
//                    handlerIndicatorLp.addRule(RelativeLayout.BELOW,  handlerContainer.getId())
//                    handlerIndicatorLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
//                    handlerIndicatorLp.addRule(RelativeLayout.CENTER_HORIZONTAL)
//                    handlerContainerLp.addRule(RelativeLayout.ALIGN_PARENT_TOP)
//                }
//
//                handlerContainer.setLayoutParams(handlerContainerLp)//设置布局参数
//                handlerContainer.background = CoreApp.getInstance().resources.getDrawable(R.drawable.comment_handler_item_background)
//                container.addView(handlerContainer)//RelativeLayout添加子View
//                commentIndicator.setLayoutParams(handlerIndicatorLp)
//                container.addView(commentIndicator)
//
//                val window = PopupWindow(container, wrapContent, CoreApp.getInstance().dip(60), true)
//                window.setOutsideTouchable(true);
//                if(showTop) {
//                    window.showAtLocation(holder?.itemView, Gravity.NO_GRAVITY,
//                            (locationItem[0] + holder!!.itemView.width.div(2) ) - window.width / 2, locationItem[1] - window.height + CoreApp.getInstance().dip(3));
//                } else {
//                    window.showAsDropDown(holder?.itemView, holder?.itemView?.width?.div(2)?:0,
//                            -CoreApp.getInstance().dip(3))
//                }
//                mCommentItemHandlers = handlerContainer.getCommitItemList()
//                for(item in mCommentItemHandlers) {
//                    if(item.content == CoreApp.getInstance().getString(R.string.Common_Delete)) {
//                        item.setOnClickListener({
//                            window.dismiss()
//                            mView?.showDeleteCommentDialog()
//                        })
//                    }
//                }
//            }
        }
        return super.onItemChildClick(position, item, id)
    }

    fun goCommentMore(comment: Comment, news: BaseNewsBean) {
        val targetType = when (news) {
            is Article -> DataDictionary.TargetType.ARTICLE
            is Video -> DataDictionary.TargetType.VIDEO
            is Image -> DataDictionary.TargetType.IMAGE
            is GIF -> DataDictionary.TargetType.GIF
            is Essay -> DataDictionary.TargetType.ESSAY
            else -> return
        }
        mView?.goFromRoot(CommentMoreDialogFragment::class.java, CommentMoreDialogPresenterAutoBundle
                .builder(comment, news, targetType, news.aid, AnalyticsKey.Event.MY_COMMENT_PAGE)
                .mCommentMoreAction(
                        arrayListOf(CommentMoreItem.More(CommentMoreItem.More.Type.DELETE, ResUtils.getString(R.string.Common_Delete)))) //只会有删除选项
                .bundle())
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCommentListChangeEvent(event: CommentListChangeEvent) {
        val comment = event.comment
        val position = getAdapter()?.currentItems?.indexOfFirst {
            it.getModel(Comment::class.java)?.id == comment.id
        } ?: return
        val item = getAdapter()?.getItem(position)
        val index = mListComments.comments.indexOf(comment)
        when (event.action) {
            DataAction.INSERT -> {
                mListComments.comments.add(0, comment)
                val it = CommentItem(comment)
                it.isMyCommentStyle = true
                getAdapter()?.addItem(0, it)
                getAdapter()?.scrollToPosition(0)
            }
            DataAction.DELETE -> {
                if (index < 0 || index >= mListComments.comments.size) {
                    return
                }
                mListComments.comments.removeAt(index)
                getAdapter()?.removeItem(position)
                if (getAdapter()?.itemCount == 1 && getAdapter()?.getItem(0) is DefaultNoMoreItem) {
                    getAdapter()?.removeItem(0)
                }
            }
            DataAction.UPDATE -> {
                if (index < 0 || index >= mListComments.comments.size) {
                    return
                }

                when (event.extra) {
                    CommentListChangeEvent.EXTRA.NORMAL -> {
                        mListComments.comments[index] = comment
                        if (item is CommentItem) {
                            item.setModel(comment)
                        }
                        getAdapter()?.notifyItemChanged(position)
                    }
                    CommentListChangeEvent.EXTRA.UPDATE_INFORMATION -> {
                        mListComments.comments[index].updateInfo(comment)
                        if (item is CommentItem) {
                            item.setModel(comment)
                        }
                        getAdapter()?.notifyItemChanged(position, CommentPayload.DIG)
                    }
                }
            }
        }
    }

}

