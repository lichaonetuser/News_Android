package com.box.app.news.item

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import com.box.app.news.R
import com.box.app.news.bean.GIF
import com.box.app.news.item.base.BaseGifItem
import com.box.app.news.item.base.BaseNewsItem
import com.box.app.news.item.payload.NewsPayload
import com.box.common.core.image.fresco.ImageManager
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder
import com.facebook.drawee.interfaces.DraweeController
import kotlinx.android.synthetic.main.item_gif_detail.view.*

class GifItem(val gif: GIF): BaseGifItem<BaseNewsItem.ViewHolder>(gif) {

    override fun getLayoutRes(): Int {
        return R.layout.item_gif_detail
    }

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: BaseNewsItem.ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        super.bindViewHolder(adapter, holder, position, payloads)
        if (holder is ViewHolder) {
            holder.bindInfo(gif)
            if (!isFavoriteStyle) {
                if (payloads?.contains(NewsPayload.UPDATE_INFORMATION) == false) {
                    holder.bindGif(gif)
                }
            }
        }
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter)
    }

    inner class ViewHolder(val view: View, adapter: CommonRecyclerAdapter) : BaseNewsItem.ViewHolder(view, adapter) {
        private val controllerBuilder: PipelineDraweeControllerBuilder = Fresco.newDraweeControllerBuilder().setAutoPlayAnimations(false)

        fun bindInfo(gif: GIF) = with(view) {
            if (TextUtils.isEmpty(gif.title)) {
                gif_title.visibility = View.GONE
            } else {
                gif_title.visibility = View.VISIBLE
                gif_title.text = gif.title
            }
            ImageManager.with(user_img).load(gif.sourcePic)

            bury_btn.text = gif.buryCount.toString()
            dig_btn.text = gif.digCount.toString()

            bury_btn.isSelected = gif.isBuried
            dig_btn.isSelected = gif.isDigged

            gif_comment.text = gif.commentCount.toString()
            user_name_txt.text = gif.sourceName

            if (isFavoriteStyle) {
                news_emit_time_txt?.gravity = Gravity.CENTER_VERTICAL
                news_emit_time_txt?.visibility = View.VISIBLE
                dig_btn.visibility = View.INVISIBLE
                bury_btn.visibility = View.INVISIBLE
                gif_comment.visibility = View.INVISIBLE
                more_btn.visibility = View.INVISIBLE

                ImageManager.with(gif_img)
                        .load(gif.coverImage)
            } else {
                news_emit_time_txt?.visibility = View.GONE
                dig_btn.visibility = View.VISIBLE
                bury_btn.visibility = View.VISIBLE
                gif_comment.visibility = View.VISIBLE
                more_btn.visibility = View.VISIBLE

                bindItemChildViewClick(bury_btn)
                bindItemChildViewClick(dig_btn)
                bindItemChildViewClick(more_btn)
            }
            gif_play_btn.visibility = View.VISIBLE
        }

        fun bindGif(gif: GIF) = with(view) {
            val urls = gif.image?.urls
//            urls?.add("https://upload.wikimedia.org/wikipedia/commons/thumb/2/2c/Rotating_earth_%28large%29.gif/200px-Rotating_earth_%28large%29.gif")
//            gif.gifType = GIF.GIF_TYPE_IMAGE

            if (urls != null && urls.isNotEmpty()) {
                val uri = try {
                    Uri.parse(urls.firstOrNull())
                } catch (e: Exception) {
                    null
                } ?: return@with
                val controller = controllerBuilder.setUri(uri).build()
                gif_img.controller = controller

                gif_img.setOnClickListener {
                    val animate= controller.animatable
                    animate?.run {
                        if (isRunning){ //判断是否正在运行
                            gif_play_btn.visibility = View.VISIBLE
                            stop()
                        } else {
                            gif_play_btn.visibility = View.GONE
                            start()
                        }
                    }
                }

                gif_play_btn.setOnClickListener {
                    val animate= controller.animatable
                    animate?.run {
                        if (isRunning){ //判断是否正在运行
                            gif_play_btn.visibility = View.VISIBLE
                            stop()
                        } else {
                            gif_play_btn.visibility = View.GONE
                            start()
                        }
                    }
                }
            } else {
                ImageManager.with(gif_img).load(gif.coverImage)
            }
        }

        override fun onViewDetachedFromWindow() {
            super.onViewDetachedFromWindow()
            view.gif_play_btn.visibility = View.VISIBLE
        }
    }

}