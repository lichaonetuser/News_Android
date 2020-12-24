package com.box.app.news.page.mvp.layer.main.feedback

import com.box.app.news.R
import com.box.app.news.analytics.AnalyticsKey
import com.box.app.news.bean.Feedback
import com.box.app.news.bean.list.ListFeedback
import com.box.app.news.data.DataManager
import com.box.app.news.event.EventManager
import com.box.app.news.event.refresh.FeedbackRefreshEvent
import com.box.app.news.item.factory.ItemFactory
import com.box.app.news.page.mvp.layer.main.feedback.submit.FeedbackSubmitFragment
import com.box.app.news.page.mvp.layer.main.image.browser.ImageBrowserFragment
import com.box.app.news.page.mvp.layer.main.image.browser.ImageBrowserPresenterAutoBundle
import com.box.app.news.openurl.OpenUrlManager
import com.box.app.news.util.checkHasMore
import com.box.common.core.analytics.AnalyticsManager
import com.box.common.core.log.Logger
import com.box.common.core.rx.schedulers.ioToMain
import com.box.common.extension.app.mvp.bindToLifecycle
import com.box.common.extension.app.mvp.loading.list.MVPListPresent
import com.box.common.extension.widget.recycler.item.BaseItem
import com.box.common.extension.widget.recycler.util.convertBeansToItems
import com.yatatsu.autobundle.AutoBundleField
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class FeedbackPresenter : MVPListPresent<FeedbackContract.View>(),
        FeedbackContract.Presenter<FeedbackContract.View> {

    @AutoBundleField(required = false)
    var mListFeedback = ListFeedback()
    @AutoBundleField(required = false)
    var mIsFirstRefreshComplete = true

    override fun onEnterEnd() {
        super.onEnterEnd()
        AnalyticsManager.logEvent(AnalyticsKey.Event.FEEDBACK, AnalyticsKey.Parameter.ENTER)
        //页面创建，不关心用户是否可见
        when {
        //首次创建,
            isNotRestore() -> {
                loadData(0)
            }
        //从销毁状态中恢复，且销毁前数据不为空，加载销毁前的数据
            isRestore() && mListFeedback.feedbacks.isNotEmpty() -> {
                //恢复是否可以加载更多
                mView?.setLoadMoreEnable(mListFeedback.hasMore)
                //同步查询并加载销毁前的数据
                loadDataComplete(Observable.just(mListFeedback.feedbacks)
                        .convertBeansToItems(ItemFactory.FEEDBACK)
                        .blockingSingle())
            }
        }
        EventManager.register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventManager.unregister(this)
    }

    override fun loadData(pageNum: Int, isRefresh: Boolean) {
        mIsRefreshing = true // 反馈服务端支持全量返回
        DataManager.Remote.getFeedbackMessage()
                .checkHasMore(getAdapter())
                .map { it : ListFeedback->
                    mListFeedback = it
                    mListFeedback.feedbacks
                }
                .convertBeansToItems(ItemFactory.FEEDBACK)
                .ioToMain()
                .bindToLifecycle(this)
                .subscribeBy(
                        onNext = { items ->
                            Logger.d(items)
                            loadDataComplete(items, true)
                            if (mIsFirstRefreshComplete) {
                                mIsFirstRefreshComplete = false
                                mView?.scrollTo(items.size - 1)
                            } else {
                                mView?.scrollTo(items.size, true)
                            }

                        },
                        onError = { throwable ->
                            Logger.i(throwable.toString())
                            loadDataFail()
                        })
    }

    override fun onItemClick(position: Int, item: BaseItem<*, *>): Boolean {
        val feedback = item.getModel(Feedback::class.java) ?: return false
        OpenUrlManager.checkOpenUrl(feedback.content)
        return false
    }

    override fun onItemChildClick(position: Int, item: BaseItem<*, *>, id: Int): Boolean {
        when (id) {
            R.id.content_img -> {
                val url = mListFeedback.feedbacks[position].image.urls.firstOrNull()
                mView?.go(ImageBrowserFragment::class.java,
                        ImageBrowserPresenterAutoBundle.builder(arrayListOf(url),0).bundle())
            }
        }
        return super.onItemChildClick(position, item, id)
    }

    override fun onClickFeedback() {
        mView?.goFromRoot(FeedbackSubmitFragment::class.java)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFeedbackRefreshEvent(event: FeedbackRefreshEvent) {
        loadData(getAdapter()?.endlessCurrentPage ?: 0)
    }

}

