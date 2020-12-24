package com.box.app.news.item.world

import android.annotation.SuppressLint
import android.view.View
import com.box.app.news.R
import com.box.app.news.bean.Comment
import com.box.common.core.image.fresco.ImageManager
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import kotlinx.android.synthetic.main.item_worldcup_board_child_player.view.*

class WorldCupBoardPlayerItem(mBean: Comment) : BaseWorldCupBoardItem<WorldCupBoardPlayerItem.ViewHolder>(mBean) {

    override fun getLayoutRes(): Int {
        return R.layout.item_worldcup_board_child_player
    }

    @SuppressLint("SetTextI18n")
    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: WorldCupBoardPlayerItem.ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        super.bindViewHolder(adapter, holder, position, payloads)
        val player = mBean.player
        if (player == null) {
            holder.content_layout?.visibility = View.GONE
            return
        }

        ImageManager.with(holder.player_img).load(player.avatar_url)
        holder.title_txt.text = player.name
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): WorldCupBoardPlayerItem.ViewHolder {
        return ViewHolder(view, adapter)
    }

    class ViewHolder(view: View, adapter: CommonRecyclerAdapter) : BaseWorldCupBoardItem.ViewHolder(view, adapter) {
        val player_img = itemView.player_img
        val title_txt = itemView.title_txt
    }

}