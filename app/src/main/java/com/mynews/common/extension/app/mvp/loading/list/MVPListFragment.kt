package com.mynews.common.extension.app.mvp.loading.list

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.bean.base.BaseBean
import com.mynews.common.extension.app.mvp.loading.MVPLoadingFragment
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.mynews.common.extension.widget.recycler.decoration.CommonItemDecoration
import com.mynews.common.extension.widget.recycler.item.BaseItem
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import kotlinx.android.synthetic.main.fragment_common_list_refresh.*
import org.jetbrains.anko.support.v4.findOptional
import org.jetbrains.anko.support.v4.longToast

abstract class MVPListFragment<in V : MVPListContract.View,
        out P : MVPListContract.Presenter<V>>
    : MVPLoadingFragment<V, P>(), MVPListContract.View {

    override val mLayoutRes: Int = R.layout.fragment_common_list_refresh

    protected open var mCommonAdapter: CommonRecyclerAdapter = CommonRecyclerAdapter()
    protected open var mCommonLayoutManage: RecyclerView.LayoutManager = LinearLayoutManager(null, LinearLayoutManager.VERTICAL, false)
    protected open var mCommonItemDecorations: CommonItemDecoration = CommonItemDecoration.DEFAULT
    // protected open var mCommonItemAnimator: FlexibleItemAnimator = FlexibleItemAnimator()

    protected val mRefreshLayout by lazy { findOptional<SmartRefreshLayout>(R.id.refresh_layout) }
    protected open var mIsAutoRefresh = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initRefreshLayout()
        initCommonContentRecyclerView()
        super.onViewCreated(view, savedInstanceState)
    }

    open protected fun initRefreshLayout() {
        if (mRefreshLayout != null) {
            refresh_layout.isEnableLoadmore = false
            refresh_layout.setOnRefreshListener {
                mPresenter.onRefresh(mIsAutoRefresh)
                mIsAutoRefresh = false
            }
        }
    }

    open protected fun initCommonContentRecyclerView() {
        mCommonAdapter.setEnableLoadMore(true, 1)

        common_content_rv.setHasFixedSize(true)
        common_content_rv.layoutManager = mCommonLayoutManage
        common_content_rv.adapter = mCommonAdapter

        mCommonAdapter.addListener(object : CommonRecyclerAdapter.FlexibleListener() {

            override fun onItemClick(position: Int): Boolean {
                val item = mCommonAdapter.getItem(position) ?: return false
                return mPresenter.onItemClick(position, item)
            }

            override fun onItemLongClick(position: Int) {
                mPresenter.onItemLongClick(position)
            }

            override fun onItemChildClick(position: Int, view: View): Boolean {
                val item = mCommonAdapter.getItem(position) ?: return false
                return mPresenter.onItemChildClick(position, item, view.id)
            }

            override fun onItemChildItemClick(childPosition: Int, item: BaseItem<*, *>, bean: BaseBean): Boolean {
                return mPresenter.onItemChildItemClick(childPosition, item, bean)
            }

            override fun shouldMoveItem(fromPosition: Int, toPosition: Int): Boolean {
                return mPresenter.shouldMoveItem(fromPosition, toPosition)
            }

            override fun onItemMove(fromPosition: Int, toPosition: Int) {
                mPresenter.onItemMove(fromPosition, toPosition)
            }

            override fun onItemSwipe(position: Int, direction: Int) {
                mPresenter.onItemSwipe(position, direction)
            }

            override fun onStickyHeaderChange(newPosition: Int, oldPosition: Int) {
                mPresenter.onStickyHeaderChange(newPosition, oldPosition)
            }

            override fun onUpdateEmptyView(size: Int) {
                mPresenter.onUpdateEmptyView(size)
            }

            override fun onActionStateChanged(viewHolder: RecyclerView.ViewHolder, actionState: Int) {
                mPresenter.onActionStateChanged(viewHolder, actionState)
            }

            override fun onDeleteConfirmed(event: Int) {
                mPresenter.onDeleteConfirmed(event)
            }

            override fun noMoreLoad(newItemsSize: Int) {
                mPresenter.noMoreLoad(newItemsSize)
            }

            override fun onLoadMore(lastPosition: Int, currentPage: Int) {
                mPresenter.onLoadMore(lastPosition, currentPage)
            }
        })
    }

    /**
     * R.id.common_content_rv = mCommonAdapter
     */
    override fun getCommonAdapter(id: Int): CommonRecyclerAdapter {
        return mCommonAdapter
    }

    override fun autoRefresh() {
        mIsAutoRefresh = true
        mRefreshLayout?.autoRefresh(0)
    }

    override fun onRefreshComplete(newItems: List<BaseItem<*, *>>, isUpdate: Boolean,
                                   position: Int, scrollToPosition: Int) {
        when {
        //数据为空，转为第一次分页加载
        //    mCommonAdapter.isEmpty -> {
        //        mCommonAdapter.resetloadMore()
        //        mCommonAdapter.onLoadMoreComplete(newItems)
        //    }
        //根据isUpdate决定是将数据追加到头部还是刷新所有内容
            newItems.isNotEmpty() -> {
                mCommonAdapter.resetloadMore()
                if (isUpdate) {
                    mCommonAdapter.updateDataSet(newItems)
                } else {
                    mCommonAdapter.addItems(position, newItems)
                    common_content_rv.scrollToPosition(scrollToPosition)
                }
            }
        }
        mRefreshLayout?.finishRefresh(true)
    }

    override fun onRefreshRetry(message: String) {
        if (!message.isEmpty()) {
            longToast(message)
        }
        mRefreshLayout?.finishRefresh(false)
    }

    override fun onLoadMoreComplete(newItems: List<BaseItem<*, *>>) {
        mCommonAdapter.onLoadMoreComplete(newItems)
    }

    override fun onLoadMoreRetry(message: String) {
        mCommonAdapter.showLoadMoreRetry()
    }

    override fun setLoadMoreEnable(enable: Boolean) {
        mCommonAdapter.setEnableLoadMore(enable)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mCommonAdapter.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        mCommonAdapter.onRestoreInstanceState(savedInstanceState)
    }

    override fun setCommonItemDecoration(decoration: CommonItemDecoration) {
        common_content_rv.removeItemDecoration(mCommonItemDecorations)
        this.mCommonItemDecorations = decoration
        common_content_rv.addItemDecoration(mCommonItemDecorations)
    }

}