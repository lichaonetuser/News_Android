package com.mynews.app.news.item

import android.view.View
import com.mynews.app.news.R
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.mynews.common.extension.widget.recycler.holder.BaseViewHolder
import com.mynews.common.extension.widget.recycler.item.BaseItem
import eu.davidea.flexibleadapter.items.IHeader
import kotlinx.android.synthetic.main.item_clarity.view.*

class ClarityItem(mBean: String)
    : BaseItem<String, ClarityItem.ViewHolder>(mBean), IHeader<ClarityItem.ViewHolder> {

    override fun getLayoutRes(): Int {
        return R.layout.item_clarity
    }

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        holder.clarity_txt.text = mBean
        if (adapter.isSelected(position)) {
            holder.choose_img.visibility = View.VISIBLE
        } else {
            holder.choose_img.visibility = View.INVISIBLE
        }
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter)
    }

    class ViewHolder(view: View, mCommonAdapter: CommonRecyclerAdapter) : BaseViewHolder(view, mCommonAdapter) {
        val clarity_txt = itemView.clarity_txt
        val choose_img = itemView.choose_img

        override fun toggleActivation() {
            super.toggleActivation()
            if (mCommonAdapter.isSelected(flexibleAdapterPosition)) {
                choose_img.visibility = View.VISIBLE
            } else {
                choose_img.visibility = View.INVISIBLE
            }
        }
    }

}