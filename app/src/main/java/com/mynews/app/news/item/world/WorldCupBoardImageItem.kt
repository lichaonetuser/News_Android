package com.mynews.app.news.item.world

import android.annotation.SuppressLint
import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.bean.Comment
import com.mynews.common.core.image.fresco.ImageManager
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import kotlinx.android.synthetic.main.item_worldcup_board_child_image.view.*

class WorldCupBoardImageItem(mBean: Comment) : BaseWorldCupBoardItem<WorldCupBoardImageItem.ViewHolder>(mBean) {

    override fun getLayoutRes(): Int {
        return R.layout.item_worldcup_board_child_image
    }

    @SuppressLint("SetTextI18n")
    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: WorldCupBoardImageItem.ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        super.bindViewHolder(adapter, holder, position, payloads)
        val image = mBean.image
        if (image == null) {
            holder.content_layout?.visibility = View.GONE
            return
        }

        ImageManager.with(holder.cover_img).load(image.info.urls.firstOrNull())
        holder.title_txt.text = image.title
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): WorldCupBoardImageItem.ViewHolder {
        return ViewHolder(view, adapter)
    }

    class ViewHolder(view: View, adapter: CommonRecyclerAdapter) : BaseWorldCupBoardItem.ViewHolder(view, adapter) {
        val cover_img = itemView.cover_img
        val title_txt = itemView.title_txt
    }

}