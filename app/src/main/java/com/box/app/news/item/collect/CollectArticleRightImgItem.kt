package com.box.app.news.item.collect

import androidx.constraintlayout.widget.ConstraintLayout
import android.view.View
import com.box.app.news.R
import com.box.app.news.bean.Article
import com.box.app.news.item.base.BaseArticleItem
import com.box.app.news.item.base.BaseNewsItem
import com.box.common.core.CoreApp
import com.box.common.core.environment.EnvDisplayMetrics
import com.box.common.core.image.fresco.ImageManager
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import kotlinx.android.synthetic.main.item_news_collect_right_img.view.*
import org.jetbrains.anko.dip
import org.jetbrains.anko.sp

/**
 * 收藏文章右图
 */
class CollectArticleRightImgItem(mBean: Article)
    : BaseArticleItem<CollectArticleRightImgItem.ViewHolder>(mBean) {

    override var isFavoriteStyle = true
    
    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        super.bindViewHolder(adapter, holder, position, payloads)
        holder.adjustImageHeight(mBean)
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): CollectArticleRightImgItem.ViewHolder {
        return CollectArticleRightImgItem.ViewHolder(view, adapter)
    }

    override fun getLayoutRes() = R.layout.item_news_collect_right_img

    class ViewHolder(val view: View, adapter: CommonRecyclerAdapter) : BaseNewsItem.ViewHolder(view, adapter) {

        companion object {
            val titleWidth = EnvDisplayMetrics.WIDTH_PIXELS - CoreApp.getInstance().dip(168)
            val titleSourcePadding = CoreApp.getInstance().dip(10)
            val sourceHeight = CoreApp.getInstance().sp(9)
            private val imgMaxHeight = CoreApp.getInstance().dip(137)
            val titleMaxHeight = imgMaxHeight - titleSourcePadding - sourceHeight

            val img16_9 = (CoreApp.getInstance().dip(137) * 0.59f).toInt() // 图片比例为16：9时的高度
            val img16_12 = (CoreApp.getInstance().dip(137) * 0.75f).toInt() // 图片比例为4：3时的高度
            val img16_14 = (CoreApp.getInstance().dip(137) * 0.875f).toInt() // 图片比例为4：3.5时的高度
            val img16_16 = CoreApp.getInstance().dip(137) // 图片比例为1：1时的高度
        }
        
        private val newsImg = itemView.news_img
        private val titleTextView = itemView.news_title_txt

        fun bindInfo (mBean:Article) {
            ImageManager.with(newsImg).load(mBean.listImageUrls.firstOrNull())
        }

        fun adjustImageHeight(mBean:Article) {
            val titleHeight = titleTextView.getHeightByExactWidth(titleWidth)
            val totalHeight = titleHeight + sourceHeight + titleSourcePadding

            val maxHeight:Int
            val imgHeight:Int

            val urlList = mBean.listImages
            var urls: List<String> = when (totalHeight) {
                in 0..(img16_9) -> {
                    imgHeight = img16_9
                    maxHeight = titleHeight
                    urlList.ration4_3
                }
                in img16_9 + 1..(img16_12) -> {
                    imgHeight = img16_12
                    maxHeight = titleHeight
                    urlList.ration16_9_medium
                }
                in img16_12 + 1..(img16_14) -> {
                    imgHeight = img16_14
                    maxHeight = titleHeight
                    urlList.ration16_95
                }
                else -> {
                    imgHeight = img16_16
                    maxHeight = titleMaxHeight
                    urlList.ration1_1
                }
            }
            if (urls.isEmpty()) {
                urls = mBean.listImageUrls
            }
            ImageManager.with(newsImg).load(urls.firstOrNull())

            val titleParams = news_title_txt?.getLayoutParams()
            titleParams?.height = maxHeight
            titleParams?.width = titleWidth

            val newsImgParams = newsImg.layoutParams as ConstraintLayout.LayoutParams
            newsImgParams.height = imgHeight

            titleTextView.setTitle(mBean.title)
        }
    }
}