package com.box.app.news.item

import androidx.core.view.ViewCompat
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.box.app.news.R
import com.box.app.news.bean.Tip
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.box.common.extension.widget.recycler.holder.BaseViewHolder
import com.box.common.extension.widget.recycler.item.BaseItem
import eu.davidea.flexibleadapter.items.IHeader
import kotlinx.android.synthetic.main.item_tip.view.*

class TipBannerItem(mBean: Tip)
    : BaseItem<Tip, TipBannerItem.ViewHolder>(mBean), IHeader<TipBannerItem.ViewHolder> {

    fun updateTip(tip: Tip) {
        this.mBean = tip
    }

    fun isValid(): Boolean {
        return mBean.tip.isNotBlank()
    }

    override fun getLayoutRes(): Int {
        return R.layout.item_tip
    }

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        holder.tip_txt.text = mBean.tip
        holder.anim_view.scaleX = 0.5f
        ViewCompat.animate(holder.anim_view)
                .scaleX(1f)
                .setDuration(300)
                .setInterpolator(DecelerateInterpolator(2.5f))
                .start()
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter)
    }

    class ViewHolder(view: View, mCommonAdapter: CommonRecyclerAdapter) : BaseViewHolder(view, mCommonAdapter) {
        val tip_txt = itemView.tip_txt
        val anim_view = itemView.anim_view
    }

}