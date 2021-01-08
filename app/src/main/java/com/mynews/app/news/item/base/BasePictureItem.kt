package com.mynews.app.news.item.base

import android.view.View
import com.mynews.app.news.bean.Image
import com.mynews.common.core.CoreApp
import com.mynews.common.core.environment.EnvDisplayMetrics
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import org.jetbrains.anko.dip

abstract class BasePictureItem<VH : BaseNewsItem.ViewHolder>(mBean: Image)
    : BaseNewsItem<Image, VH>(mBean) {

    companion object {
        const val ItemViewTypeExtra = 20000
        private const val RATIO_9_16 = 0.561f
        private const val RATIO_3_4 = 0.75f
        private val SCREEN_WIDTH = EnvDisplayMetrics.WIDTH_PIXELS
        val NORMAL_HALF_WIDTH = (SCREEN_WIDTH - dp(3)) / 2
        val COLLECT_HALF_WIDTH = (SCREEN_WIDTH - dp(25)) / 2
        val NORMAL_HALF_HEIGHT = (((SCREEN_WIDTH - dp(3)) * RATIO_9_16 - dp(3)) / 2).toInt()
        val COLLECT_HALF_HEIGHT = (((SCREEN_WIDTH - dp(25)) * RATIO_3_4 - dp(3)) / 2).toInt()
        val NORMAL_HEIGHT = (SCREEN_WIDTH * RATIO_9_16).toInt()
        val COLLECT_HEIGHT = ((SCREEN_WIDTH - dp(25)) * RATIO_3_4).toInt()

        private fun dp(value:Int):Int = CoreApp.getInstance().dip(value)
        private fun dp(value:Float):Int = CoreApp.getInstance().dip(value)
    }

    override fun getItemViewType(): Int {
        return super.getItemViewType() - ItemViewTypeExtra
    }

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: VH, position: Int, payloads: MutableList<Any?>?) {
        super.bindViewHolder(adapter, holder, position, payloads)
        if (isFavoriteStyle) {
            holder.news_emit_time_txt?.visibility = View.VISIBLE
        } else {
            holder.news_emit_time_txt?.visibility = View.INVISIBLE
        }
    }

}