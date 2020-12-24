package com.box.app.news.item

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet.WRAP_CONTENT
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import com.box.app.news.R
import com.box.app.news.bean.Article
import com.box.app.news.bean.Tag
import com.box.app.news.data.DataManager
import com.box.app.news.item.base.BaseArticleItem
import com.box.app.news.item.base.holder.BaseNewsRightImgViewHolder
import com.box.common.core.CoreApp
import com.box.common.core.environment.EnvDisplayMetrics
import com.box.common.core.image.fresco.ImageManager
import com.box.common.core.log.Logger
import com.box.common.core.util.ResUtils
import com.box.common.core.widget.CoreSimpleDraweeView
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import org.jetbrains.anko.dip
import org.jetbrains.anko.sp
import org.jetbrains.anko.textColor

open class ArticleRightImgItem(mBean: Article) : BaseArticleItem<ArticleRightImgItem.ViewHolder>(mBean) {

    val tags: List<Tag>

    companion object {
        val dp9 = CoreApp.getInstance().dip(9)
        val dp12 = CoreApp.getInstance().dip(12)
        val dp18 = CoreApp.getInstance().dip(18)
        val dp34 = CoreApp.getInstance().dip(34)
        val bottomItemWidth = EnvDisplayMetrics.WIDTH_PIXELS - CoreApp.getInstance().dip(166)
        val pxTitleWidth = EnvDisplayMetrics.WIDTH_PIXELS - CoreApp.getInstance().dip(173)
        val pxTitleMaxHeight = CoreApp.getInstance().dip(80)
        val pxSourceHeight = CoreApp.getInstance().dip(11)
        val img16_9 = (CoreApp.getInstance().dip(137) * 0.59f).toInt() // 图片比例为16：9时的高度
        val img16_12 = (CoreApp.getInstance().dip(137) * 0.75f).toInt() // 图片比例为4：3时的高度
        val img16_14 = (CoreApp.getInstance().dip(137) * 0.875f).toInt() // 图片比例为4：3.5时的高度
        val img16_16 = CoreApp.getInstance().dip(137) // 图片比例为1：1时的高度
    }

    init {
//        assert(mBean.listImageUrls.size > 3, { "至少包含三张图片才可创建，参见ARTICLE_ITEM_FACTORY" })
        tags = mBean.tags
    }

    override fun getLayoutRes(): Int = R.layout.item_news_article_right_img

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        super.bindViewHolder(adapter, holder, position, payloads)
        val enableNotInterested = DataManager.Memory.getAppConfig().enableNotInterested
        holder.newsTag?.setTags(mBean.tags)
//        holder.checkLineMax()
        if (!enableNotInterested) {
            ImageManager.with(holder.news_img).load(mBean.listImageUrls.firstOrNull())
        }
        holder.commentCount?.text = String.format(ResUtils.getString(R.string.article_item_comment_count), mBean.commentCount)
        holder.improveLayout(mBean, isFavoriteStyle)
        holder.adjustImageHeight(mBean)
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        view.setOnClickListener({
            val v = view.findViewById<CoreSimpleDraweeView>(R.id.news_img)
            v.layoutParams
        })
        return ViewHolder(view, adapter, mBean)
    }

    class ViewHolder(val view: View, adapter: CommonRecyclerAdapter, val mBean: Article) : BaseNewsRightImgViewHolder(view, adapter) {

        //有图的尺寸和标题高度的适配
        fun adjustImageHeight(mBean:Article) {
            val titleHeight = news_title_txt?.getHeightByExactWidth(bottomItemWidth) ?: 0
            val totalHeight = titleHeight + CoreApp.getInstance().sp(9) + CoreApp.getInstance().dip(10)

            val titleMaxHeight:Int
            val imgHeight:Int
            //获取应该展示的图片url
            val urlList = mBean.listImages
            var urls: List<String> = when (totalHeight) {
                in 0..(img16_9) -> {
                    imgHeight = img16_9
                    titleMaxHeight = titleHeight
                    urlList.ration4_3
                }
                in img16_9 + 1..(img16_12) -> {
                    imgHeight = img16_12
                    titleMaxHeight = titleHeight
                    urlList.ration16_9_medium
                }
                in img16_12 + 1..(img16_14) -> {
                    imgHeight = img16_14
                    titleMaxHeight = titleHeight
                    urlList.ration16_95
                }
                else -> {
                    imgHeight = img16_16
                    titleMaxHeight = CoreApp.getInstance().dip(127) - CoreApp.getInstance().sp(9)
                    urlList.ration1_1
                }
            }
            if (urls.isEmpty()) {
                urls = mBean.listImageUrls
            }
            ImageManager.with(news_img).load(urls.firstOrNull())

            val titleParams = news_title_txt?.getLayoutParams()
            titleParams?.height = titleMaxHeight
            titleParams?.width = bottomItemWidth

            val newsImgParams = news_img?.layoutParams as ConstraintLayout.LayoutParams
            newsImgParams.height = imgHeight
        }

        fun improveLayout(mBean: Article, isFavorite:Boolean) {
            val option = mBean.displayOptions % 4
            val space = CoreApp.getInstance().dip(4)
            val tagsWidth = newsTag?.getRealViewWidth() ?: 0
            val timeWidth = if (option == 1 || option == 3)
                news_emit_time_txt?.paint?.measureText(news_emit_time_txt.text.toString())?.toInt() ?: 0 else 0
            val countWidth = if (option == 2 || option == 3) getCommonCountWidth(mBean.isCommentHot) else 0

            val maxSourceWidth = bottomItemWidth - tagsWidth -
                    if (timeWidth == 0) 0 else { timeWidth + space } -
                    if (countWidth == 0) 0 else { countWidth + space }

            val sourceParams = news_source_txt?.layoutParams as? ConstraintLayout.LayoutParams ?: return
            val timeParams = news_emit_time_txt?.layoutParams as? ConstraintLayout.LayoutParams ?: return
            val countParams = commentCount?.layoutParams as? ConstraintLayout.LayoutParams ?: return

            sourceParams.height = WRAP_CONTENT
            sourceParams.width = Math.min(news_source_txt.paint?.measureText(news_source_txt.text.toString())?.toInt() ?: maxSourceWidth, maxSourceWidth)
            news_source_txt.maxWidth = maxSourceWidth

            timeParams.height = WRAP_CONTENT
            timeParams.width = timeWidth

            countParams.height = WRAP_CONTENT
            countParams.width = countWidth

            if (option == 1 || isFavorite) {
                news_emit_time_txt.visibility = VISIBLE
                commentCount.visibility = GONE
                timeParams.leftToRight = -1
                timeParams.rightToLeft = R.id.news_img
                timeParams.rightMargin = CoreApp.getInstance().dip(9)
            } else if (option == 0) {
                news_emit_time_txt.visibility = GONE
                commentCount.visibility = GONE
            } else if (option == 2) {
                commentCount.visibility = VISIBLE
                news_emit_time_txt.visibility = GONE
                if (mBean.isCommentHot) {
                    commentCount.textColor = ResUtils.getColor(R.color.color_17)
                    commentCount.setCompoundDrawablesWithIntrinsicBounds(ResUtils.getDrawable(R.drawable.list_hot_comment),
                            null, null, null)
                } else {
                    commentCount.setCompoundDrawables(null, null, null, null)
                    commentCount.textColor = ResUtils.getColor(R.color.color_3)
                }
            } else if (option == 3) {
                news_emit_time_txt.visibility = VISIBLE
                timeParams.leftToRight = R.id.news_source_txt
                timeParams.rightToLeft = -1
                commentCount.visibility = VISIBLE
                if (mBean.isCommentHot) {
                    commentCount.textColor = ResUtils.getColor(R.color.color_17)
                    commentCount.setCompoundDrawablesWithIntrinsicBounds(ResUtils.getDrawable(R.drawable.list_hot_comment),
                            null, null, null)
                } else {
                    commentCount.setCompoundDrawables(null, null, null, null)
                    commentCount.textColor = ResUtils.getColor(R.color.color_3)
                }
            }
        }

        //获取评论数量控件的宽度，考虑文字本身的宽度和图片的宽度
        private fun getCommonCountWidth(isHotCommon: Boolean):Int {
            val textWidth = commentCount?.paint?.measureText(commentCount.text.toString())?.toInt() ?: 0
            val imgWidth = if (isHotCommon) CoreApp.getInstance().dip(15) else 0
            return textWidth + imgWidth
        }
    }
}