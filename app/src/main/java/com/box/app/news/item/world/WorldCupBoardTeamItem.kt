package com.box.app.news.item.world

import android.annotation.SuppressLint
import android.view.View
import com.box.app.news.R
import com.box.app.news.bean.Comment
import com.box.common.core.image.fresco.ImageManager
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import kotlinx.android.synthetic.main.item_worldcup_board_child_team.view.*

class WorldCupBoardTeamItem(mBean: Comment) : BaseWorldCupBoardItem<WorldCupBoardTeamItem.ViewHolder>(mBean) {

    override fun getLayoutRes(): Int {
        return R.layout.item_worldcup_board_child_team
    }

    @SuppressLint("SetTextI18n")
    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: WorldCupBoardTeamItem.ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        super.bindViewHolder(adapter, holder, position, payloads)
        val team = mBean.team
        if (team == null) {
            holder.content_layout?.visibility = View.GONE
            return
        }

        ImageManager.with(holder.team_img).load(team.avatar_url)
        holder.title_txt.text = team.name
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): WorldCupBoardTeamItem.ViewHolder {
        return ViewHolder(view, adapter)
    }

    class ViewHolder(view: View, adapter: CommonRecyclerAdapter) : BaseWorldCupBoardItem.ViewHolder(view, adapter) {
        val team_img = itemView.team_img
        val title_txt = itemView.title_txt
    }

}