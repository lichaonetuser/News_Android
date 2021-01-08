package com.mynews.app.news.item.world

import android.annotation.SuppressLint
import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.bean.Comment
import com.mynews.common.core.image.fresco.ImageManager
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import kotlinx.android.synthetic.main.item_worldcup_board_child_match.view.*

class WorldCupBoardMatchItem(mBean: Comment) : BaseWorldCupBoardItem<WorldCupBoardMatchItem.ViewHolder>(mBean) {

    override fun getLayoutRes(): Int {
        return R.layout.item_worldcup_board_child_match
    }

    @SuppressLint("SetTextI18n")
    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: WorldCupBoardMatchItem.ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        super.bindViewHolder(adapter, holder, position, payloads)
        val match = mBean.match
        if (match == null) {
            holder.content_layout?.visibility = View.GONE
            return
        }

        holder.vs_title.text = match.sport_season_round
        holder.vs_time.text = match.date_week

        val homeTeam = match.home_team
        ImageManager.with(holder.home_flag_img).load(homeTeam.avatar_url)
        holder.home_title_txt.text = homeTeam.name

        val awayTeam = match.away_team
        ImageManager.with(holder.away_flag_img).load(awayTeam.avatar_url)
        holder.away_title_txt.text = awayTeam.name

    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): WorldCupBoardMatchItem.ViewHolder {
        return ViewHolder(view, adapter)
    }

    class ViewHolder(view: View, adapter: CommonRecyclerAdapter) : BaseWorldCupBoardItem.ViewHolder(view, adapter) {
        val vs_title = itemView.vs_title
        val vs_time = itemView.vs_time
        val home_flag_img = itemView.home_flag_img
        val home_title_txt = itemView.home_title_txt
        val away_flag_img = itemView.away_flag_img
        val away_title_txt = itemView.away_title_txt
    }

}