package com.mynews.app.news.item.collect

import androidx.constraintlayout.widget.ConstraintLayout
import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.bean.Image
import com.mynews.app.news.item.base.BaseNewsItem
import com.mynews.app.news.item.base.BasePictureItem
import com.mynews.common.core.image.fresco.ImageManager
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import kotlinx.android.synthetic.main.item_news_collect_4_picture.view.*

/**
 * 收藏四张图片
 */
class Collect4PictureItem(mBean: Image)
    : BasePictureItem<Collect4PictureItem.ViewHolder>(mBean) {

    override var isFavoriteStyle = true

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        super.bindViewHolder(adapter, holder, position, payloads)
        holder.bindInfo(mBean)
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): Collect4PictureItem.ViewHolder {
        return Collect4PictureItem.ViewHolder(view, adapter)
    }

    override fun getLayoutRes() = R.layout.item_news_collect_4_picture


    class ViewHolder(val view: View, adapter: CommonRecyclerAdapter) : BaseNewsItem.ViewHolder(view, adapter) {

        private val newsImg0 = itemView.mutiple_news_img0
        private val newsImg1 = itemView.mutiple_news_img1
        private val newsImg2 = itemView.mutiple_news_img2
        private val newsImg3 = itemView.mutiple_news_img3
        private val sourceParams = itemView.news_source_txt.layoutParams as ConstraintLayout.LayoutParams


        init {
            val params0 = newsImg0.layoutParams
            val params1 = newsImg1.layoutParams
            val params2 = newsImg2.layoutParams
            val params3 = newsImg3.layoutParams

            params0.height = COLLECT_HALF_HEIGHT
            params0.width = COLLECT_HALF_WIDTH
            params1.height = COLLECT_HALF_HEIGHT
            params1.width = COLLECT_HALF_WIDTH
            params2.height = COLLECT_HALF_HEIGHT
            params2.width = COLLECT_HALF_WIDTH
            params3.height = COLLECT_HALF_HEIGHT
            params3.width = COLLECT_HALF_WIDTH
        }

        fun bindInfo (mBean:Image) {
            ImageManager.with(newsImg0).load(mBean.images[0].urls.firstOrNull())
            ImageManager.with(newsImg1).load(mBean.images[1].urls.firstOrNull())
            ImageManager.with(newsImg2).load(mBean.images[2].urls.firstOrNull())
            ImageManager.with(newsImg3).load(mBean.images[3].urls.firstOrNull())

            sourceParams.topToBottom = if (mBean.title.isEmpty()) R.id.mutiple_news_img2 else R.id.news_title_txt
        }
    }
}