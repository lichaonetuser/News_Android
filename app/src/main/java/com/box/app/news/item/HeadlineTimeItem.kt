package com.box.app.news.item

import androidx.core.view.ViewCompat
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.box.app.news.R
import com.box.app.news.bean.Headline
import com.box.app.news.bean.Tip
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.box.common.extension.widget.recycler.holder.BaseViewHolder
import com.box.common.extension.widget.recycler.item.BaseItem
import eu.davidea.flexibleadapter.items.IHeader
import kotlinx.android.synthetic.main.item_headline_time.view.*

class HeadlineTimeItem(mBean: Headline)
    : BaseItem<Headline, HeadlineTimeItem.ViewHolder>(mBean), IHeader<HeadlineTimeItem.ViewHolder> {

    override fun getLayoutRes(): Int {
        return R.layout.item_headline_time
    }

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        holder.time_txt.setText(mBean.dateContent)
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter)
    }

    class ViewHolder(view: View, mCommonAdapter: CommonRecyclerAdapter) : BaseViewHolder(view, mCommonAdapter) {
        val time_txt = itemView.time_txt
    }

}