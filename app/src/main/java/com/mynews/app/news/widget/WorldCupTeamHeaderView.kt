package com.mynews.app.news.widget

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.bean.WorldcupTeam
import com.mynews.common.core.image.fresco.ImageManager
import com.mynews.common.core.util.ResUtils
import kotlinx.android.synthetic.main.item_worldcup_team_head_view.view.*

class WorldCupTeamHeaderView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val mLayout: View = LayoutInflater.from(context).inflate(R.layout.item_worldcup_team_head_view, this)

    fun setContent(team: WorldcupTeam) {
        ImageManager.with(mLayout.team_image).load(team.avatar_url)
        mLayout.team_title_japan.text = team.name
        mLayout.team_title_eng.text = team.sub_name
        mLayout.team_other.text = team.desc
        if (team.is_subscribed) {
            mLayout.team_reserve.isActivated = false
            mLayout.team_reserve.text = ResUtils.getString(R.string.Cancel_Team_Reserve)
        } else {
            mLayout.team_reserve.isActivated = true
            mLayout.team_reserve.text = ResUtils.getString(R.string.Team_Reserve)
        }
    }

}