package com.mynews.common.extension.widget.recycler.adapter

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.mynews.app.news.bean.Channel
import com.mynews.app.news.bean.base.BaseBean
import com.mynews.app.news.proto.AppLog
import com.mynews.common.core.log.Logger
import com.mynews.common.extension.widget.recycler.holder.BaseViewHolder
import com.mynews.common.extension.widget.recycler.item.BaseItem
import com.mynews.common.extension.widget.recycler.item.DefaultProgressItem
import com.mynews.common.extension.widget.recycler.item.DefaultProgressItem.Companion.PAYLOADS_UPDATE_FOR_RETRY
import eu.davidea.flexibleadapter.FlexibleAdapter
import java.util.*

open class CommonRecyclerAdapter @JvmOverloads constructor(
        items: List<BaseItem<*, *>>?, listeners: Any = Unit, stableIds: Boolean = true
) : FlexibleAdapter<BaseItem<*, *>>(items, listeners, stableIds) {

    @JvmOverloads
    constructor(stableIds: Boolean = true) : this(null, Unit, stableIds)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any?>) {
        super.onBindViewHolder(holder, position, payloads)
        mOnBindViewHolderListener?.onBindViewHolder(getItem(position)
                ?: return, holder, position, payloads)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val holder = super.onCreateViewHolder(parent, viewType)
        mOnCreateViewHolderListener?.onCreateViewHolder(parent, viewType, holder)
        return holder
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (holder is BaseViewHolder) {
            holder.onViewAttachedToWindow()
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if (holder is BaseViewHolder) {
            holder.onViewDetachedFromWindow()
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is BaseViewHolder) {
            holder.onViewRecycled()
        }
    }

    override fun isEmpty(): Boolean {
        return mainItemCount == 0
    }

    fun isAllEmpty(): Boolean {
        return itemCount == 0
    }

    /*-----------*/
    /* Load More */
    /*-----------*/

    private var mEnableLoadMore = false
    private val mProgressItem: DefaultProgressItem by lazy { DefaultProgressItem(this) }

    var loadMoreCurrentPage = 0 // 当前页面位置
    var loadMoreFixPageSize = false // 是否使用固定分页数来计算当前页面位置，如80条数据，一页固定20条，第三页
    var loadMoreListItemType: Array<Int>? = null // 页面中的type种类数据，用于处理非mainItem需要计数的情况

    var channel: Channel? = null
    var refer: AppLog.Refer? = null

    fun resetloadMore() {
        loadMoreCurrentPage = 0
        endlessTargetCount = 0
    }

    @JvmOverloads
    fun setEnableLoadMore(enable: Boolean, callLoadMoreAtLastPosition: Int = 1)
            : CommonRecyclerAdapter {
        if (enable == mEnableLoadMore) {
            return this
        }
        mEnableLoadMore = enable
        if (enable) {
            setEndlessProgressItem(mProgressItem)
            setEndlessScrollThreshold(callLoadMoreAtLastPosition)
        } else {
            setEndlessProgressItem(null)
        }
        return this
    }

    fun invalidateLoadMore(newItems: List<BaseItem<*, *>>?) {
        if (mEnableLoadMore) {
            setEndlessProgressItem(mProgressItem)
        } else {
            setEndlessProgressItem(null)
        }

        if (loadMoreFixPageSize) {
            if (endlessPageSize <= 0 && newItems != null) {
                endlessPageSize = if (loadMoreListItemType == null) {
                    newItems.size
                } else {
                    val viewTypeList = listOf(*loadMoreListItemType!!)
                    val count = newItems.count { viewTypeList.contains(it.layoutRes) }
                    count
                }
            }
        } else {
            loadMoreCurrentPage++
        }

        try {
            val positionToNotify = getGlobalPositionOf(mProgressItem)
            if (positionToNotify >= 0) {
                if (isTopEndless) {
                    removeScrollableHeader(mProgressItem)
                } else {
                    removeScrollableFooter(mProgressItem)
                }
            }
            val field = FlexibleAdapter::class.java.getDeclaredField("endlessLoading")
            field.isAccessible = true
            field.set(this, false)
        } catch (e: Exception) {
            Logger.w("can not fix endlessLoading!")
        }
    }

    fun showLoadMoreRetry() {
        mProgressItem.isLoadMore = false
        updateItem(mProgressItem, PAYLOADS_UPDATE_FOR_RETRY)
    }

    fun getCurrentItemsWithoutProgressItem(): ArrayList<BaseItem<*, *>> {
        val items = ArrayList<BaseItem<*, *>>(currentItems)
        items.remove(mProgressItem)
        return items
    }

    /**
     * Start With 0
     */
    override fun getEndlessCurrentPage(): Int {
        return if (loadMoreFixPageSize) {
            val endlessPageSize = endlessPageSize
            val itemCount = if (loadMoreListItemType == null) {
                mainItemCount
            } else {
                getItemCountOfTypes(*loadMoreListItemType!!)
            }
            if (endlessPageSize > 0) itemCount / endlessPageSize - 1 else 0
        } else {
            loadMoreCurrentPage //从0开始
        }
    }

    override fun onLoadMoreComplete(newItems: MutableList<BaseItem<*, *>>?, delay: Long) {
        if (isEmpty && ((newItems == null) || newItems.isEmpty())) {
            mUpdateListener.onUpdateEmptyView(0)
        }

        if (loadMoreFixPageSize) {
            if (endlessPageSize <= 0 && newItems != null) {
                endlessPageSize = if (loadMoreListItemType == null) {
                    newItems.size
                } else {
                    val viewTypeList = listOf(*loadMoreListItemType!!)
                    val count = newItems.count { viewTypeList.contains(it.layoutRes) }
                    count
                }
            }
        } else {
            loadMoreCurrentPage++
        }
        super.onLoadMoreComplete(newItems, delay)
    }

    internal fun callOnLoadMore() {
        mEndlessScrollListener?.onLoadMore(mainItemCount, endlessCurrentPage)
    }

    /*----------*/
    /* Listener */
    /*----------*/

    override fun addListener(listeners: Any?): CommonRecyclerAdapter {
        super.addListener(listeners)
        if (listeners is FlexibleAdapter.EndlessScrollListener) {
            mEndlessScrollListener = listeners
        }
        if (listeners is OnItemChildClickListener) {
            mItemChildClickListener = listeners
        }
        if (listeners is OnItemChildItemClickListener) {
            mItemChildItemClickListener = listeners
        }
        if (listeners is OnBindViewHolderListener) {
            mOnBindViewHolderListener = listeners
        }
        if (listeners is OnCreateViewHolderListener) {
            mOnCreateViewHolderListener = listeners
        }
        return this
    }

    open class FlexibleListener : FlexibleAdapter.OnUpdateListener,
            FlexibleAdapter.OnDeleteCompleteListener,
            FlexibleAdapter.OnItemClickListener,
            FlexibleAdapter.OnItemLongClickListener,
            FlexibleAdapter.OnItemMoveListener,
            FlexibleAdapter.OnItemSwipeListener,
            FlexibleAdapter.OnActionStateListener,
            FlexibleAdapter.OnStickyHeaderChangeListener,
            FlexibleAdapter.EndlessScrollListener,
            OnItemChildClickListener,
            OnItemChildItemClickListener {

        override fun onItemClick(position: Int): Boolean {
            return true
        }

        override fun onItemChildClick(position: Int, view: View): Boolean {
            return true
        }

        override fun onItemChildItemClick(childPosition: Int, item: BaseItem<*, *>, bean: BaseBean) : Boolean {
            return true
        }

        override fun onItemLongClick(position: Int) {

        }

        override fun shouldMoveItem(fromPosition: Int, toPosition: Int): Boolean {
            return false
        }

        override fun onItemMove(fromPosition: Int, toPosition: Int) {

        }

        override fun onItemSwipe(position: Int, direction: Int) {

        }

        override fun onStickyHeaderChange(newPosition: Int, oldPosition: Int) {

        }

        override fun onUpdateEmptyView(size: Int) {

        }

        override fun onActionStateChanged(viewHolder: RecyclerView.ViewHolder, actionState: Int) {

        }

        override fun onDeleteConfirmed(event: Int) {

        }

        override fun noMoreLoad(newItemsSize: Int) {

        }

        override fun onLoadMore(lastPosition: Int, currentPage: Int) {

        }
    }

    var mItemChildClickListener: OnItemChildClickListener? = null
    var mOnBindViewHolderListener: OnBindViewHolderListener? = null
    var mOnCreateViewHolderListener: OnCreateViewHolderListener? = null
    var mItemChildItemClickListener : OnItemChildItemClickListener? = null

    interface OnItemChildItemClickListener {
        /*
        * [childPosition] item在子列表中的位置
        * */
        fun onItemChildItemClick(childPosition: Int, item : BaseItem<*, *>, bean: BaseBean) : Boolean
    }

    interface OnItemChildClickListener {
        fun onItemChildClick(position: Int, view: View): Boolean
    }

    interface OnBindViewHolderListener {
        fun onBindViewHolder(item: BaseItem<*, *>, holder: RecyclerView.ViewHolder?, position: Int, payloads: MutableList<Any?>?)
    }

    interface OnCreateViewHolderListener {
        fun onCreateViewHolder(parent: ViewGroup?, viewType: Int, holder: RecyclerView.ViewHolder)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putIntegerArrayList("mSelectedPositions", ArrayList(selectedPositions))
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        val saveSelectedPositions = savedInstanceState?.getIntegerArrayList("mSelectedPositions")
        if (saveSelectedPositions == null || saveSelectedPositions.isEmpty()) {
            return
        }
        for (positions in saveSelectedPositions) {
            toggleSelection(positions)
        }
    }

    fun scrollToPosition(position: Int) {
        mRecyclerView.scrollToPosition(position)
    }

    fun smoothScrollToPosition(position: Int) {
        mRecyclerView.smoothScrollToPosition(position)
    }

    private var mIsEditing = false
    private var mIsCollectSelected = false;

    fun isEditing(): Boolean {
        return mIsEditing
    }

    fun startEdit(notify: Boolean) {
        mIsEditing = true
        if (notify) {
            notifyDataSetChanged()
        }

    }

    fun stopEdit(notify: Boolean) {
        mIsEditing = false
        if (notify) {
            notifyDataSetChanged()
        }
    }

    fun toggleEditing(notify: Boolean) {
        mIsEditing = !mIsEditing
        if (notify) {
            notifyDataSetChanged()
        }
    }

    open var state: FragmentState = FragmentState.VISIBLE
    enum class FragmentState{
        PAUSE, DESTROY, HIDDEN, VISIBLE
    }

    var flag = false

    //用来标记上次阅读到上面的段子在列表里的position
    var postion0 = -1
    var postion1 = -1
}
