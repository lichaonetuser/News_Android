package com.box.app.news.item.base.holder

import androidx.constraintlayout.widget.ConstraintLayout
import android.text.Layout
import android.text.StaticLayout
import android.view.View
import android.view.ViewGroup
import com.box.app.news.R
import com.box.app.news.item.ArticleRightImgItem
import com.box.app.news.item.base.BaseNewsItem
import com.box.app.news.widget.FontTextView
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import kotlinx.android.synthetic.main.item_news_right_img.view.*

open class BaseNewsRightImgViewHolder(view: View, adapter: CommonRecyclerAdapter) : BaseNewsItem.ViewHolder(view, adapter) {
    val news_img = itemView.news_img

    open fun checkLineMax() {
        if (news_title_txt == null) {
            return
        }
        news_title_txt as FontTextView
        val sourceParams = news_source_txt?.layoutParams as? ConstraintLayout.LayoutParams ?: return
        val emitParams = news_emit_time_txt?.layoutParams as? ConstraintLayout.LayoutParams
                ?: return
        val removeParams = remove_btn?.layoutParams as? ConstraintLayout.LayoutParams ?: return

        val layout = StaticLayout(news_title_txt.text, news_title_txt.paint, ArticleRightImgItem.pxTitleWidth,
                Layout.Alignment.ALIGN_NORMAL, news_title_txt.lineSpacingMultiplier,
                news_title_txt.lineSpacingExtra, true)
        val height = layout.height

        if (height + ArticleRightImgItem.pxSourceHeight > ArticleRightImgItem.pxTitleMaxHeight) {
            sourceParams.height = ArticleRightImgItem.dp34
            sourceParams.topToBottom = R.id.news_img
            sourceParams.bottomToBottom = -1

            emitParams.height = ArticleRightImgItem.dp34

            removeParams.rightToLeft = -1
            removeParams.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
            removeParams.leftToLeft = R.id.space

            remove_btn.setPadding(0, 0, 0, ArticleRightImgItem.dp9)
            itemView.setPadding(itemView.paddingLeft, itemView.paddingTop, itemView.paddingRight, 0)

            val perLineHeight = (layout.height - layout.spacingAdd) / layout.lineCount
            val maxLine = ArticleRightImgItem.pxTitleMaxHeight / perLineHeight.toInt()
            news_title_txt.maxLines = maxLine
        } else {
            sourceParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            sourceParams.topToBottom = -1
            sourceParams.bottomToBottom = R.id.news_img

            emitParams.height = ViewGroup.LayoutParams.WRAP_CONTENT

            removeParams.rightToLeft = R.id.news_img
            removeParams.rightToRight = -1
            removeParams.leftToLeft = -1

            remove_btn.setPadding(0, ArticleRightImgItem.dp18, 0, 0)
            itemView.setPadding(itemView.paddingLeft, itemView.paddingTop, itemView.paddingRight, ArticleRightImgItem.dp12)
            news_title_txt.maxLines = layout.lineCount
        }
        news_source_txt.layoutParams = sourceParams
        news_emit_time_txt.layoutParams = emitParams
        remove_btn.layoutParams = removeParams
    }
}