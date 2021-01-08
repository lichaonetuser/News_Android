package com.mynews.app.news.page.mvp.layer.main.inbox.base

import android.os.Bundle
import com.mynews.app.news.analytics.AnalyticsKey
import com.mynews.app.news.bean.InboxMessage
import com.mynews.app.news.bean.list.ListInbox
import com.mynews.app.news.data.DataDictionary
import com.mynews.app.news.data.DataManager
import com.mynews.app.news.item.factory.ItemFactory
import com.mynews.app.news.openurl.OpenUrlManager
import com.mynews.app.news.util.checkHasMore
import com.mynews.common.core.analytics.AnalyticsManager
import com.mynews.common.core.rx.schedulers.ioToMain
import com.mynews.common.core.util.extension.distinctWith
import com.mynews.common.extension.app.mvp.loading.list.MVPListPresent
import com.mynews.common.extension.widget.recycler.item.BaseItem
import com.mynews.common.extension.widget.recycler.item.DefaultNoMoreItem
import com.mynews.common.extension.widget.recycler.util.convertBeansToItems
import io.reactivex.rxkotlin.subscribeBy

abstract class BaseInboxContentPresenter<V : BaseInboxContentContract.View> : MVPListPresent<V>(),
        BaseInboxContentContract.Presenter<V> {

    abstract val mInboxType: Int
    abstract var mListInbox: ListInbox

    override fun onLazyCreate(savedState: Bundle?) {
        super.onLazyCreate(savedState)
        loadData(0)
    }

    override fun loadData(pageNum: Int, isRefresh: Boolean) {
        if (mInboxType == DataDictionary.InboxType.MESSAGE.intValue) {
            loadMessageData(pageNum, isRefresh)
        } else if(mInboxType == DataDictionary.InboxType.PUSH_HISTORY.intValue){
            loadPushData(pageNum, isRefresh)
        }
    }

    private fun loadMessageData(pageNum: Int, isRefresh: Boolean) {
        val lastId = if (isRefresh) null else mListInbox.messages.lastOrNull()?.messageId
        DataManager.Remote.loadInbox(type = mInboxType, lastId = lastId)
                .checkHasMore(getAdapter())
                .map {
                    val newMessages = it.messages.filterNotNull().distinctWith(mListInbox.messages)
                    mListInbox.hasMore = it.hasMore
                    mListInbox.messages.addAll(newMessages)
                    if (mIsRefreshing) {
                        mListInbox.messages.filterNotNull()
                    } else {
                        newMessages.filterNotNull()
                    }
                }
                .convertBeansToItems(ItemFactory.INBOX)
                .ioToMain()
                .subscribeBy(
                        onNext = {
                            loadDataComplete(it)
                            if (!mListInbox.hasMore && getAdapter()?.isAllEmpty() == false) {
                                getAdapter()?.addItem(DefaultNoMoreItem())
                            }
                        },
                        onError = {
                            loadDataFail()
                        }
                )
    }

    private fun loadPushData(pageNum: Int, isRefresh: Boolean) {
        val lastId = if (isRefresh) null else mListInbox.messages.lastOrNull()?.messageId
        DataManager.Remote.loadPushHistory(lastId = lastId)
                .checkHasMore(getAdapter())
                .map {
                    val newMessages = it.messages.filterNotNull().distinctWith(mListInbox.messages)
                    mListInbox.hasMore = it.hasMore
                    mListInbox.messages.addAll(newMessages)
                    if (mIsRefreshing) {
                        mListInbox.messages.filterNotNull()
                    } else {
                        newMessages.filterNotNull()
                    }
                }
                .convertBeansToItems(ItemFactory.INBOX)
                .ioToMain()
                .subscribeBy(
                        onNext = {
                            loadDataComplete(it)
                            if (!mListInbox.hasMore && getAdapter()?.isAllEmpty() == false) {
                                getAdapter()?.addItem(DefaultNoMoreItem())
                            }
                        },
                        onError = {
                            loadDataFail()
                        }
                )
    }

    override fun onItemClick(position: Int, item: BaseItem<*, *>): Boolean {
        val message = item.getModel(InboxMessage::class.java)
        val type = message?.type
        AnalyticsManager.logEvent(AnalyticsKey.Event.INBOX, String.format(AnalyticsKey.Parameter.CLICK_TYPE_STH, type))
        OpenUrlManager.checkOpenUrl(message?.openUrl ?: "")
        return super.onItemClick(position, item)
    }

}

