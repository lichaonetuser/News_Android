package com.box.app.news.item

import android.view.View
import com.box.app.news.R
import com.box.app.news.bean.Region
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.box.common.extension.widget.recycler.holder.BaseViewHolder
import com.box.common.extension.widget.recycler.item.BaseItem
import eu.davidea.flexibleadapter.items.IFilterable
import eu.davidea.flexibleadapter.utils.FlexibleUtils
import kotlinx.android.synthetic.main.item_region.view.*

class RegionItem(mBean: Region, val autoShowArrow: Boolean = true)
    : BaseItem<Region, RegionItem.ViewHolder>(mBean), IFilterable {


    private val arrowImgVisibility by lazy {
        when {
            !autoShowArrow -> View.GONE
            mBean.subRegions.isEmpty() -> View.GONE
            else -> View.VISIBLE
        }
    }

    override fun getLayoutRes(): Int {
        return R.layout.item_region
    }

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        holder.city_name_txt.text = mBean.name
        holder.arrow_img.visibility = arrowImgVisibility

        if (adapter.hasSearchText()) {
            FlexibleUtils.highlightWords(holder.city_name_txt, mBean.name, adapter.searchText)
        } else {
            holder.city_name_txt.text = (mBean.name)
        }
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun filter(constraint: String): Boolean {
        return constraint.split(FlexibleUtils.SPLIT_EXPRESSION.toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()
                .any { mBean.name.toLowerCase().contains(it) }
    }

    class ViewHolder(view: View, mCommonAdapter: CommonRecyclerAdapter) : BaseViewHolder(view, mCommonAdapter) {

        val city_name_txt = itemView.city_name_txt
        val arrow_img = itemView.arrow_img

    }

}