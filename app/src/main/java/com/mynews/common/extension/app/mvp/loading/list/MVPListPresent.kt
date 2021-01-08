package com.mynews.common.extension.app.mvp.loading.list

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.mynews.app.news.R
import com.mynews.app.news.bean.base.BaseBean
import com.mynews.common.extension.app.mvp.loading.MVPLoadingPresent
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.mynews.common.extension.widget.recycler.item.BaseItem

abstract class MVPListPresent<V : MVPListContract.View>
    : MVPLoadingPresent<V>(), MVPListContract.Presenter<V> {

    companion object {
        private val STATE_KEY_IS_REFRESHING = "mIsRefreshing"
    }

    abstract fun loadData(pageNum: Int = 0, isRefresh: Boolean = mIsRefreshing)
    protected open var mIsRefreshing = true
    protected open var mContentFailMessage: String = ""
    protected open var mRefreshFailMessage: String = ""
    protected open var mLoadMoreFailMessage: String = ""

    fun getAdapter(id: Int = R.id.common_content_rv): CommonRecyclerAdapter? {
        return mView?.getCommonAdapter(id)
    }

    /**
     * 完成数据加载，自动判断当前状态来决定是完成刷新还是加载更多
     */
    protected open fun loadDataComplete(items: List<BaseItem<*, *>>, isUpdate: Boolean = true,
                                        position: Int = 0, scrollToPosition: Int = 0) {
        if (mIsRefreshing) {
            mView?.onRefreshComplete(items, isUpdate, position, scrollToPosition)
            if (mView?.getCommonAdapter()?.isAllEmpty() == true) {
                mView?.showEmpty()
            }
        } else {
            mView?.onLoadMoreComplete(items)
        }
    }

    /**
     * 加载数据失败，自动判断当前状态来通知UI是刷新失败还是加载失败
     */
    protected fun loadDataFail(
            fail: String = mContentFailMessage,
            refresh: String = mRefreshFailMessage,
            loadMore: String = mLoadMoreFailMessage) {
        when {
            getAdapter()?.isEmpty == true -> mView?.showFail(fail)
            mIsRefreshing -> mView?.onRefreshRetry(refresh)
            else -> mView?.onLoadMoreRetry(loadMore)
        }
    }

    override fun onLoadingLayoutRetryClicked(id: Int) {
        mView?.showLoading()
        loadData(0)
    }

    override fun onItemClick(position: Int, item: BaseItem<*, *>): Boolean {
        return true
    }

    override fun onItemLongClick(position: Int) {}

    override fun onItemChildClick(position: Int, item: BaseItem<*, *>, id: Int): Boolean {
        return true
    }

    override fun onItemChildItemClick(childPosition: Int, item: BaseItem<*, *>, bean: BaseBean): Boolean {
        return true
    }

    override fun shouldMoveItem(fromPosition: Int, toPosition: Int): Boolean {
        return true
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {}

    override fun onItemSwipe(position: Int, direction: Int) {}

    override fun onStickyHeaderChange(newPosition: Int, oldPosition: Int) {}

    override fun onUpdateEmptyView(size: Int) {
        if (size == 0) mView?.showEmpty() else mView?.showContent()
    }

    override fun onActionStateChanged(viewHolder: RecyclerView.ViewHolder, actionState: Int) {}

    override fun onDeleteConfirmed(event: Int) {}

    override fun noMoreLoad(newItemsSize: Int) {}

    override fun onRefresh(isAutoRefresh: Boolean) {
        mIsRefreshing = true
        loadData(0, true)
    }

    override fun onLoadMore(lastPosition: Int, currentPage: Int) {
        mIsRefreshing = false
        loadData(currentPage + 1, false)
    }

    override fun onSave(outState: Bundle) {
        super.onSave(outState)
        outState.putBoolean(STATE_KEY_IS_REFRESHING, mIsRefreshing)
    }

    override fun onRestore(saveState: Bundle) {
        super.onRestore(saveState)
        mIsRefreshing = saveState.getBoolean(STATE_KEY_IS_REFRESHING, true)
    }

}