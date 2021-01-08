package com.mynews.app.news.item

import androidx.constraintlayout.widget.ConstraintLayout
import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.bean.Image
import com.mynews.app.news.item.base.BaseNewsItem
import com.mynews.app.news.item.base.BasePictureItem
import com.mynews.app.news.item.payload.NewsPayload
import com.mynews.common.core.CoreApp
import com.mynews.common.core.environment.EnvDisplayMetrics
import com.mynews.common.core.image.fresco.ImageManager
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import kotlinx.android.synthetic.main.item_news_picture.view.*
import org.jetbrains.anko.dip

class SinglePictureItem(mBean: Image) : BasePictureItem<SinglePictureItem.ViewHolder>(mBean) {

    companion object {
        val dp10 = CoreApp.getInstance().dip(10)
    }

    override fun getLayoutRes(): Int = R.layout.item_news_picture

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        super.bindViewHolder(adapter, holder, position, payloads)
        if (payloads?.contains(NewsPayload.UPDATE_INFORMATION) == true) {
            holder.digBtn.text = mBean.digCount.toString()
            holder.buryBtn.text = mBean.buryCount.toString()
            holder.commentTxt.text = mBean.commentCount.toString()
            holder.buryBtn.isSelected = mBean.isBuried
            holder.digBtn.isSelected = mBean.isDigged
            return
        }

        val titleParams = holder.news_title_txt?.getLayoutParams() as ConstraintLayout.LayoutParams
        if (mBean.title.isEmpty()) {
            holder.news_title_txt?.setTitleVisibility(View.VISIBLE)
            titleParams.height = 0
            titleParams.topMargin = 0
        } else {
            titleParams.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
            titleParams.topMargin = CoreApp.getInstance().dip(12)
        }
//        if (mBean.title.isEmpty()) {
//            holder.news_title_txt?.setTitleVisibility(View.GONE)
//            val imgParams = holder.newsImg0?.layoutParams as ConstraintLayout.LayoutParams
//            imgParams.topToTop = 0
//            imgParams.topMargin = CoreApp.getInstance().dip(5)
//        } else {
//            holder.news_title_txt?.setTitleVisibility(View.VISIBLE)
//            val imgParams = holder.newsImg0?.layoutParams as ConstraintLayout.LayoutParams
//            imgParams.topToTop = 0
//            imgParams.bottomToTop = R.id.news_title_txt
//            imgParams.topMargin = CoreApp.getInstance().dip(5)
//        }

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
            holder.pictureMoreIc.visibility = View.VISIBLE
        } else {
            holder.pictureMoreIc.visibility = View.GONE
        }

        ImageManager.with(holder.userImg).load(mBean.avatarUrl)
        holder.userName.text = mBean.sourceName
        holder.digBtn.text = mBean.digCount.toString()
        holder.buryBtn.text = mBean.buryCount.toString()
        holder.commentTxt.text = mBean.commentCount.toString()
        holder.buryBtn.isSelected = mBean.isBuried
        holder.digBtn.isSelected = mBean.isDigged

        if (isFavoriteStyle) {
            holder.news_emit_time_txt?.visibility = View.VISIBLE
            holder.digBtn.visibility = View.INVISIBLE
            holder.buryBtn.visibility = View.INVISIBLE
            holder.commentTxt.visibility = View.INVISIBLE
            holder.moreBtn.visibility = View.INVISIBLE
            holder.userImg.visibility = View.INVISIBLE

            holder.news_item_layout?.setPadding(dp10, 0, dp10, 0)
            ImageManager.with(holder.newsImg)
                    .setWidth(EnvDisplayMetrics.WIDTH_PIXELS - 2 * dp10)
                    .setAspectRatio(ratio)
                    .load(url)

            if (!mBean.title.isEmpty()) {
                val titleParams = holder.news_title_txt?.getLayoutParams() as ConstraintLayout.LayoutParams
                titleParams.leftMargin = 0
                titleParams.rightMargin = 0
                titleParams.leftToLeft = 0
            }

            val userParams = holder.userImg?.layoutParams as ConstraintLayout.LayoutParams
            userParams.leftMargin = 0

            val timeParams = holder.news_emit_time_txt?.layoutParams as ConstraintLayout.LayoutParams
            timeParams.rightMargin = 0

            holder.news_emit_time_txt?.textSize = 9f
            holder.userName?.textSize = 9f

            val userNameParams = holder.userName?.layoutParams as ConstraintLayout.LayoutParams
            userNameParams.leftMargin = 0
            userNameParams.leftToLeft = 0
        } else {
            ImageManager.with(holder.newsImg)
                    .setWidth(EnvDisplayMetrics.WIDTH_PIXELS)
                    .setAspectRatio(ratio)
                    .load(url)

            holder.news_emit_time_txt?.visibility = View.GONE
            holder.digBtn.visibility = View.VISIBLE
            holder.buryBtn.visibility = View.VISIBLE
            holder.commentTxt.visibility = View.VISIBLE
            holder.moreBtn.visibility = View.VISIBLE
        }
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter)
    }

    @Suppress("HasPlatformType")
    class ViewHolder(view: View, adapter: CommonRecyclerAdapter) : BaseNewsItem.ViewHolder(view, adapter) {
        val newsImg = itemView.news_img
        val userImg = itemView.user_img
        val userName = itemView.user_name_txt
        val commentTxt = itemView.comment_txt
        val digBtn = itemView.dig_btn
        val buryBtn = itemView.bury_btn
        val moreBtn = itemView.more_btn
        val pictureMoreIc = itemView.picture_more_ic

        init {
            bindItemChildViewClick(digBtn)
            bindItemChildViewClick(buryBtn)
            bindItemChildViewClick(moreBtn)
            bindItemChildViewClick(newsImg)
        }
    }

}