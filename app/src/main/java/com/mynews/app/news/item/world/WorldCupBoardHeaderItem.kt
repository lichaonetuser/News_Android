package com.mynews.app.news.item.world

import android.annotation.SuppressLint
import android.view.View
import com.mynews.app.news.R
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.mynews.common.extension.widget.recycler.holder.BaseViewHolder
import com.mynews.common.extension.widget.recycler.item.BaseItem
import eu.davidea.flexibleadapter.items.IHeader
import kotlinx.android.synthetic.main.item_worldcup_board_header.view.*

class WorldCupBoardHeaderItem(var title: String, var count: Int = 0)
    : BaseItem<String, WorldCupBoardHeaderItem.ViewHolder>(title), IHeader<WorldCupBoardHeaderItem.ViewHolder> {

    init {
        isHidden = false
        isSelectable = false
    }

    override fun getLayoutRes(): Int {
        return R.layout.item_worldcup_board_header
    }

    @SuppressLint("SetTextI18n")
    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        holder.title_txt.text = "$title($count)"
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter)
    }

    class ViewHolder(view: View, mCommonAdapter: CommonRecyclerAdapter) :
            BaseViewHolder(view, mCommonAdapter, true) {
        val title_txt = itemView.title_txt
    }

}