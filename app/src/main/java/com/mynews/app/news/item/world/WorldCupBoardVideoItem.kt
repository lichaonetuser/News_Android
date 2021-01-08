package com.mynews.app.news.item.world

import android.annotation.SuppressLint
import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.bean.Comment
import com.mynews.common.core.image.fresco.ImageManager
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import kotlinx.android.synthetic.main.item_worldcup_board_child_video.view.*

class WorldCupBoardVideoItem(mBean: Comment) : BaseWorldCupBoardItem<WorldCupBoardVideoItem.ViewHolder>(mBean) {

    override fun getLayoutRes(): Int {
        return R.layout.item_worldcup_board_child_video
    }

    @SuppressLint("SetTextI18n")
    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: WorldCupBoardVideoItem.ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        super.bindViewHolder(adapter, holder, position, payloads)
        val video = mBean.video
        if (video == null) {
            holder.content_layout?.visibility = View.GONE
            return
        }

        ImageManager.with(holder.cover_img).load(video.coverImage)
        holder.title_txt.text = video.title
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): WorldCupBoardVideoItem.ViewHolder {
        return ViewHolder(view, adapter)
    }

    class ViewHolder(view: View, adapter: CommonRecyclerAdapter) : BaseWorldCupBoardItem.ViewHolder(view, adapter) {
        val cover_img = itemView.cover_img
        val title_txt = itemView.title_txt
    }

}