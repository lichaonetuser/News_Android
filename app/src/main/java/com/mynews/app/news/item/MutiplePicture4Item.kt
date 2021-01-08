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
import kotlinx.android.synthetic.main.item_news_mutiple_4_pictures.view.*
import org.jetbrains.anko.dip

/**
 * 多图功能列表项的Item
 * Android 6.9 支持多图功能
 */
class MutiplePicture4Item(mBean: Image) : BasePictureItem<MutiplePicture4Item.ViewHolder>(mBean) {

    companion object {
        val dp10 = CoreApp.getInstance().dip(10)
        private val RATIO_9_16 = 0.561f
        private val RATIO_3_4 = 0.75f
        private val MARGIN = CoreApp.getInstance().dip(3)
    }

    override fun getLayoutRes(): Int = R.layout.item_news_mutiple_4_pictures

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        super.bindViewHolder(adapter, holder, position, payloads)
        holder.news_title_txt?.setTitle(mBean.title)
//        val height = holder.news_title_txt?.getHeightByExactWidth(EnvDisplayMetrics.WIDTH_PIXELS - CoreApp.getInstance().dip(20))
//        val titleParams = holder.news_title_txt?.getLayoutParams()
//        titleParams?.width = EnvDisplayMetrics.WIDTH_PIXELS - CoreApp.getInstance().dip(20)
//        titleParams?.height = height

        if (payloads?.contains(NewsPayload.UPDATE_INFORMATION) == true) {
            holder.digBtn.text = mBean.digCount.toString()
            holder.buryBtn.text = mBean.buryCount.toString()
            holder.commentTxt.text = mBean.commentCount.toString()
            holder.buryBtn.isSelected = mBean.isBuried
            holder.digBtn.isSelected = mBean.isDigged
            return
        }

        val titleParams = holder.news_title_txt?.getLayoutParams() as ConstraintLayout.LayoutParams
        holder.news_title_txt?.setTitleVisibility(View.VISIBLE)
        if (mBean.title.isEmpty()) {
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

        val screenWidth = EnvDisplayMetrics.WIDTH_PIXELS - if (isFavoriteStyle) dp10 * 2 else 0
        val imageWidth = (screenWidth - MARGIN) / 2
        val imageHeight = (screenWidth * (if (isFavoriteStyle) RATIO_3_4 else RATIO_9_16) - MARGIN) / 2

        val imgParams0 = holder.newsImg0?.layoutParams as? ConstraintLayout.LayoutParams ?: return
        imgParams0.height = imageHeight.toInt()
        imgParams0.width = imageWidth

        val imgParams1 = holder.newsImg1?.layoutParams as? ConstraintLayout.LayoutParams ?: return
        imgParams1.height = imageHeight.toInt()
        imgParams1.width = imageWidth

        val imgParams2 = holder.newsImg2?.layoutParams as? ConstraintLayout.LayoutParams ?: return
        imgParams2.height = imageHeight.toInt()
        imgParams2.width = imageWidth

        val imgParams3 = holder.newsImg3?.layoutParams as? ConstraintLayout.LayoutParams ?: return
        imgParams3.height = imageHeight.toInt()
        imgParams3.width = imageWidth

        val imageInfos = mBean.images
        ImageManager.with(holder.newsImg0).load(imageInfos[0].urls.firstOrNull())
        ImageManager.with(holder.newsImg1).load(imageInfos[1].urls.firstOrNull())
        ImageManager.with(holder.newsImg2).load(imageInfos[2].urls.firstOrNull())
        ImageManager.with(holder.newsImg3).load(imageInfos[3].urls.firstOrNull())

        ImageManager.with(holder.userImg).load(mBean.avatarUrl)
        holder.userName.text = mBean.sourceName
        holder.digBtn.text = mBean.digCount.toString()
        holder.buryBtn.text = mBean.buryCount.toString()
        holder.commentTxt.text = mBean.commentCount.toString()
        holder.buryBtn.isSelected = mBean.isBuried
        holder.digBtn.isSelected = mBean.isDigged

        if (isFavoriteStyle) {
            holder.news_item_layout?.setPadding(dp10, 0, dp10, 0)
            val titleParams = holder.news_title_txt?.getLayoutParams() as ConstraintLayout.LayoutParams
            titleParams.leftMargin = 0
            titleParams.rightMargin = 0

            val userParams = holder.userImg?.layoutParams as ConstraintLayout.LayoutParams
            userParams.leftMargin = 0

            val timeParams = holder.news_emit_time_txt?.layoutParams as ConstraintLayout.LayoutParams
            timeParams.rightMargin = 0

            holder.news_emit_time_txt.visibility = View.VISIBLE
            holder.digBtn.visibility = View.INVISIBLE
            holder.buryBtn.visibility = View.INVISIBLE
            holder.commentTxt.visibility = View.INVISIBLE
            holder.moreBtn.visibility = View.INVISIBLE
            holder.userImg.visibility = View.INVISIBLE

            holder.news_emit_time_txt?.textSize = 9f
            holder.userName?.textSize = 9f

            val userNameParams = holder.userName?.layoutParams as ConstraintLayout.LayoutParams
            userNameParams.leftMargin = 0
            userNameParams.leftToLeft = 0
        } else {
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
    class ViewHolder(view: View, adapter: CommonRecyclerAdapter)
        : BaseNewsItem.ViewHolder(view, adapter) {
        val newsImg0 = itemView.mutiple_news_img0
        val newsImg1 = itemView.mutiple_news_img1
        val newsImg2 = itemView.mutiple_news_img2
        val newsImg3 = itemView.mutiple_news_img3
        val userImg = itemView.user_img
        val userName = itemView.user_name_txt
        val commentTxt = itemView.comment_txt
        val digBtn = itemView.dig_btn
        val buryBtn = itemView.bury_btn
        val moreBtn = itemView.more_btn

        init {
            bindItemChildViewClick(digBtn)
            bindItemChildViewClick(buryBtn)
            bindItemChildViewClick(moreBtn)
            bindItemChildViewClick(newsImg0)
            bindItemChildViewClick(newsImg1)
            bindItemChildViewClick(newsImg2)
            bindItemChildViewClick(newsImg3)
        }
    }
}