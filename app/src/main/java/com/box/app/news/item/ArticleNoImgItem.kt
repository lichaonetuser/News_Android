package com.box.app.news.item

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import android.view.View
import android.view.View.VISIBLE
import com.box.app.news.R
import com.box.app.news.bean.Article
import com.box.app.news.bean.Tag
import com.box.app.news.data.DataDictionary.ArticleStyle.UN_KNOW
import com.box.app.news.item.base.BaseArticleItem
import com.box.app.news.item.base.BaseNewsItem
import com.box.common.core.CoreApp
import com.box.common.core.util.ResUtils
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import org.jetbrains.anko.dip
import org.jetbrains.anko.textColor

open class ArticleNoImgItem(mBean: Article)
    : BaseArticleItem<ArticleNoImgItem.ViewHolder>(mBean) {

    val tags: List<Tag>

    init {
        assert((mBean.listImageUrls.isEmpty() || mBean.style == UN_KNOW.value),
                { "没有图片或者是UN_KNOW Style时创建，参见ARTICLE_ITEM_FACTORY" })
        tags = mBean.tags
    }

    override fun getLayoutRes() = R.layout.item_news_no_img

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        super.bindViewHolder(adapter, holder, position, payloads)
        holder.newsTag?.setTags(tags)
        holder.commentCount?.text = String.format(ResUtils.getString(R.string.article_item_comment_count), mBean.commentCount)
        holder.improveLayout(mBean, isFavoriteStyle)
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter)
    }

    class ViewHolder(val view: View, adapter: CommonRecyclerAdapter) : BaseNewsItem.ViewHolder(view, adapter) {

        fun improveLayout(mBean: Article, isFavorite:Boolean) {

            val option = mBean.displayOptions % 4
            val space = CoreApp.getInstance().dip(4)
            val tagsWidth = newsTag?.getRealViewWidth() ?: 0
            val timeWidth = if (option == 1 || option == 3)
                news_emit_time_txt?.paint?.measureText(news_emit_time_txt.text.toString())?.toInt() ?: 0 else 0
            val countWidth = if (option == 2 || option == 3) getCommonCountWidth(mBean.isCommentHot) else 0

            val maxSourceWidth = ArticleRightImgItem.bottomItemWidth - tagsWidth -
                    if (timeWidth == 0) 0 else { timeWidth + space } -
                    if (countWidth == 0) 0 else { countWidth + space }

            val sourceParams = news_source_txt?.layoutParams as? ConstraintLayout.LayoutParams ?: return
            val timeParams = news_emit_time_txt?.layoutParams as? ConstraintLayout.LayoutParams ?: return
            val countParams = commentCount?.layoutParams as? ConstraintLayout.LayoutParams ?: return

            sourceParams.height = ConstraintSet.WRAP_CONTENT
            sourceParams.width = maxSourceWidth
            sourceParams.width = Math.min(news_source_txt.paint?.measureText(news_source_txt.text.toString())?.toInt() ?: maxSourceWidth, maxSourceWidth)
            news_source_txt.maxWidth = maxSourceWidth

            timeParams.height = ConstraintSet.WRAP_CONTENT
            timeParams.width = timeWidth

            countParams.height = ConstraintSet.WRAP_CONTENT
            countParams.width = countWidth

            if (option == 1 || isFavorite) {
                news_emit_time_txt.visibility = VISIBLE
                commentCount.visibility = View.GONE
                timeParams.leftToRight = -1
                timeParams.rightToRight = R.id.news_title_txt
            } else if (option == 0) {
                news_emit_time_txt.visibility = View.GONE
                commentCount.visibility = View.GONE
            } else if (option == 2) {
                commentCount.visibility = VISIBLE
                news_emit_time_txt.visibility = View.GONE
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

        private fun getCommonCountWidth(isHotCommon: Boolean):Int {
            val textWidth = commentCount?.paint?.measureText(commentCount.text.toString())?.toInt() ?: 0
            val imgWidth = if (isHotCommon) CoreApp.getInstance().dip(15) else 0
            return textWidth + imgWidth
        }
    }
}