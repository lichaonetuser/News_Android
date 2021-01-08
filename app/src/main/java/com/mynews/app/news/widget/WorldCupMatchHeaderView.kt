package com.mynews.app.news.widget

import android.annotation.SuppressLint
import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.bean.WorldcupMatch
import com.mynews.app.news.data.DataDictionary
import com.mynews.common.core.image.fresco.ImageManager
import com.mynews.common.core.util.ResUtils
import kotlinx.android.synthetic.main.item_worldcup_match_head_view.view.*

class WorldCupMatchHeaderView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val mLayout: View = LayoutInflater.from(context).inflate(R.layout.item_worldcup_match_head_view, this)

    @SuppressLint("SetTextI18n")
    fun setContent(match: WorldcupMatch) {
        ImageManager.with(mLayout.player_away_image).load(match.away_team.avatar_url)
        mLayout.player_away_title.text = match.away_team.name
        mLayout.player_away_title.ellipsize = (TextUtils.TruncateAt.valueOf("END"))
        ImageManager.with(mLayout.player_home_image).load(match.home_team.avatar_url)
        mLayout.player_home_title.text = match.home_team.name
        mLayout.player_home_title.ellipsize = (TextUtils.TruncateAt.valueOf("END"))
        mLayout.match_desc.setText(match.desc)

        if (match.game_status == DataDictionary.WorldcupMatchStatus.NOT_START.value) {
            mLayout.match_vs.visibility = View.VISIBLE
            mLayout.match_vs.text = "VS"
            mLayout.title_line.visibility = View.GONE
            mLayout.away_score.visibility = View.GONE
            mLayout.home_score.visibility = View.GONE
        } else if (match.game_status == DataDictionary.WorldcupMatchStatus.ONGOING.value) {
            mLayout.match_vs.visibility = View.GONE
            mLayout.title_line.visibility = View.VISIBLE
            mLayout.away_score.visibility = View.VISIBLE
            mLayout.away_score.text = match.away_team.score.toString()
            mLayout.home_score.visibility = View.VISIBLE
            mLayout.home_score.text = match.home_team.score.toString()
        } else {
            mLayout.match_vs.visibility = View.GONE
            mLayout.title_line.visibility = View.VISIBLE
            mLayout.away_score.visibility = View.VISIBLE
            mLayout.away_score.text = match.away_team.score.toString()
            mLayout.home_score.visibility = View.VISIBLE
            mLayout.home_score.text = match.home_team.score.toString()
        }

        if (match.game_status == DataDictionary.WorldcupMatchStatus.NOT_START.value) {
            mLayout.match_reserve.visibility = View.VISIBLE
            if (match.is_subscribed) {
                mLayout.match_reserve.isActivated = false
                mLayout.match_reserve.setText(ResUtils.getString(R.string.Cancel_Match_Reserve))
            } else {
                mLayout.match_reserve.isActivated = true
                mLayout.match_reserve.setText(ResUtils.getString(R.string.Match_Reserve))
            }
            mLayout.match_ongoing.visibility = View.GONE
            mLayout.match_end.visibility = View.GONE
        } else if (match.game_status == DataDictionary.WorldcupMatchStatus.ONGOING.value) {
            mLayout.match_reserve.visibility = View.INVISIBLE
            mLayout.match_ongoing.visibility = View.VISIBLE
            mLayout.match_end.visibility = View.GONE
        } else {
            mLayout.match_reserve.visibility = View.INVISIBLE
            mLayout.match_ongoing.visibility = View.GONE
            mLayout.match_end.visibility = View.VISIBLE
        }

        if (match.home_team.is_digged) {
            mLayout.like_home_team.setImageDrawable(ResUtils.getDrawable(R.drawable.team_diged))
        } else {
            mLayout.like_home_team.setImageDrawable(ResUtils.getDrawable(R.drawable.team_not_dig))
        }
        if (match.away_team.is_digged) {
            mLayout.like_away_team.setImageDrawable(ResUtils.getDrawable(R.drawable.team_reverse_diged))
        } else {
            mLayout.like_away_team.setImageDrawable(ResUtils.getDrawable(R.drawable.team_reverse_not_dig))
        }
    }
}