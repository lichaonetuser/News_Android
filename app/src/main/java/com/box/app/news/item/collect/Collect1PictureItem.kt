package com.box.app.news.item.collect

import androidx.constraintlayout.widget.ConstraintLayout
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import com.box.app.news.R
import com.box.app.news.bean.Image
import com.box.app.news.item.base.BaseNewsItem
import com.box.app.news.item.base.BasePictureItem
import com.box.common.core.CoreApp
import com.box.common.core.environment.EnvDisplayMetrics
import com.box.common.core.image.fresco.ImageManager
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import kotlinx.android.synthetic.main.item_news_collect_1_picture.view.*
import org.jetbrains.anko.dip

/**
 * 收藏单图
 */
class Collect1PictureItem(mBean: Image)
    : BasePictureItem<Collect1PictureItem.ViewHolder>(mBean) {

    override var isFavoriteStyle = true

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        super.bindViewHolder(adapter, holder, position, payloads)
        holder.bindInfo(mBean)
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): Collect1PictureItem.ViewHolder {
        return Collect1PictureItem.ViewHolder(view, adapter)
    }

    override fun getLayoutRes() = R.layout.item_news_collect_1_picture


    class ViewHolder(view: View, adapter: CommonRecyclerAdapter) : BaseNewsItem.ViewHolder(view, adapter) {

        private val newsImg = itemView.news_img
        private val pictureMoreIc = itemView.picture_more_ic
        private val sourceParams = itemView.news_source_txt.layoutParams as ConstraintLayout.LayoutParams

        fun bindInfo (mBean:Image) {
            var ratio: Float
            val imageInfos = mBean.images
            val url = if (imageInfos.isNotEmpty()) {
                ratio = imageInfos[0].width.toFloat() / imageInfos[0].height.toFloat()
                imageInfos[0].urls.firstOrNull()
            } else {
                ratio = mBean.info.width.toFloat() / mBean.info.height.toFloat()
                mBean.info.urls.firstOrNull()

            }
            if (ratio <= 0.4F) { //=1:2.5
                ratio = 0.4F
                pictureMoreIc.visibility = View.VISIBLE
            } else {
                pictureMoreIc.visibility = View.GONE
            }
            ImageManager.with(newsImg)
                    .setWidth(EnvDisplayMetrics.WIDTH_PIXELS - CoreApp.getInstance().dip(22))
                    .setAspectRatio(ratio)
                    .load(url)

            sourceParams.topToBottom = if (mBean.title.isEmpty()) R.id.news_img else R.id.news_title_txt
        }
    }
}