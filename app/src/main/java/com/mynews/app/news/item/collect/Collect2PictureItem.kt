package com.mynews.app.news.item.collect

import androidx.constraintlayout.widget.ConstraintLayout
import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.bean.Image
import com.mynews.app.news.item.base.BaseNewsItem
import com.mynews.app.news.item.base.BasePictureItem
import com.mynews.common.core.image.fresco.ImageManager
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import kotlinx.android.synthetic.main.item_news_collect_2_picture.view.*

/**
 * 收藏两张图片
 */
class Collect2PictureItem(mBean: Image)
    : BasePictureItem<Collect2PictureItem.ViewHolder>(mBean) {

    override var isFavoriteStyle = true

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        super.bindViewHolder(adapter, holder, position, payloads)
        holder.bindInfo(mBean)
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): Collect2PictureItem.ViewHolder {
        return Collect2PictureItem.ViewHolder(view, adapter)
    }

    override fun getLayoutRes() = R.layout.item_news_collect_2_picture


    class ViewHolder(val view: View, adapter: CommonRecyclerAdapter) : BaseNewsItem.ViewHolder(view, adapter){

        private val newsImg0 = itemView.mutiple_news_img0
        private val newsImg1 = itemView.mutiple_news_img1
        private val sourceParams = itemView.news_source_txt.layoutParams as ConstraintLayout.LayoutParams


        init {
            val params0 = newsImg0.layoutParams
            val params1 = newsImg1.layoutParams
            params0.height = COLLECT_HEIGHT
            params0.width = COLLECT_HALF_WIDTH
            params1.height = COLLECT_HEIGHT
            params1.width = COLLECT_HALF_WIDTH
        }

        fun bindInfo (mBean:Image) {
            ImageManager.with(newsImg0).load(mBean.images[0].urls.firstOrNull())
            ImageManager.with(newsImg1).load(mBean.images[1].urls.firstOrNull())

            sourceParams.topToBottom = if (mBean.title.isEmpty()) R.id.mutiple_news_img0 else R.id.news_title_txt
        }
    }
}