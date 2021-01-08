package com.mynews.common.extension.app.mvp.loading.list

import androidx.recyclerview.widget.RecyclerView
import com.mynews.app.news.R
import com.mynews.app.news.bean.base.BaseBean
import com.mynews.common.extension.app.mvp.loading.MVPLoadingContract
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.mynews.common.extension.widget.recycler.decoration.CommonItemDecoration
import com.mynews.common.extension.widget.recycler.item.BaseItem

interface MVPListContract {

    interface View : MVPLoadingContract.View {

        fun getCommonAdapter(id: Int = R.id.common_content_rv): CommonRecyclerAdapter

        fun autoRefresh()

        /**
         * 刷新完成
         * @param isUpdate 是否是对已有数据进行更新，是，比对更新。否，将数据放置在头部。默认是。
         */
        fun onRefreshComplete(newItems: List<BaseItem<*, *>>, isUpdate: Boolean = true,
                              position: Int = 0, scrollToPosition: Int = 0)

        fun onRefreshRetry(message: String = "")

        /**
         * 加载更多
         */
        fun onLoadMoreComplete(newItems: List<BaseItem<*, *>>)

        fun onLoadMoreRetry(message: String = "")

        fun setLoadMoreEnable(enable: Boolean)

        fun setCommonItemDecoration(decoration: CommonItemDecoration)

    }

    interface Presenter<in V : View> : MVPLoadingContract.Presenter<V> {

        /**
         * 返回结果决定是否激活isActivity状态
         */
        fun onItemClick(position: Int, item: BaseItem<*, *>): Boolean

        fun onItemLongClick(position: Int)

        /**
         * 返回结果暂时未实现，预计为true触发点击效果，false不做处理
         * @param id 被点击视图的id
         */
        fun onItemChildClick(position: Int, item: BaseItem<*, *>, id: Int): Boolean

        fun onItemChildItemClick(childPosition: Int, item: BaseItem<*, *>, bean: BaseBean) : Boolean
        /**
         * 返回结果决定是否可以移动（拖拽）Item
         */
        fun shouldMoveItem(fromPosition: Int, toPosition: Int): Boolean

        fun onItemMove(fromPosition: Int, toPosition: Int)

        fun onItemSwipe(position: Int, direction: Int)

        fun onStickyHeaderChange(newPosition: Int, oldPosition: Int)

        fun onUpdateEmptyView(size: Int)

        fun onActionStateChanged(viewHolder: RecyclerView.ViewHolder, actionState: Int)

        fun onDeleteConfirmed(event: Int)

        fun noMoreLoad(newItemsSize: Int)

        fun onRefresh(isAutoRefresh: Boolean)

        fun onLoadMore(lastPosition: Int, currentPage: Int)
    }

}