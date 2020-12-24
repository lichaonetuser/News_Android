package com.box.app.news.widget

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.box.app.news.R
import com.box.common.core.environment.EnvDisplayMetrics
import kotlinx.android.synthetic.main.fragment_news_list.view.*
import kotlinx.android.synthetic.main.item_worldcup_match_like_ratio.view.*
import org.jetbrains.anko.windowManager

class LikeRatioView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    companion object {
    }
    private var mContext : Context
    private val mLayout : View
    private var mScreenWidth : Int

    init {
        mContext = context
        mLayout = LayoutInflater.from(context).inflate(R.layout.item_worldcup_match_like_ratio, this)
        mScreenWidth = EnvDisplayMetrics.WIDTH_PIXELS
    }

    public fun setRatio(voteAwayteamNum : Int, voteHometeamNum : Int) {

        var mAwayEndWidth = mLayout.like_away_team_ratio_end.width
        var mRemindWidth = mScreenWidth - mAwayEndWidth

        var mAwayParam = mLayout.like_away_team_ratio.layoutParams
        var mHomeParam = mLayout.like_home_team_ratio.layoutParams

        mLayout.like_away_team_num.text = voteAwayteamNum.toString()
        mLayout.like_home_team_num.text = voteHometeamNum.toString()
        mLayout.like_away_team_ratio_end.visibility = View.VISIBLE
        mLayout.like_home_team_ratio_end.visibility = View.VISIBLE
        if(voteAwayteamNum == 0 && voteHometeamNum == 0 ) {
            mAwayParam.width = mRemindWidth/2
//            mHomeParam.width = mRemindWidth/2
            mHomeParam.width = mRemindWidth - mAwayParam.width
        } else if(voteAwayteamNum == 0 && voteHometeamNum != 0) {
            mAwayParam.width = 0
            mHomeParam.width = mRemindWidth
        } else if (voteAwayteamNum != 0 && voteHometeamNum == 0) {
            mAwayParam.width = mRemindWidth
            mHomeParam.width = 0
        } else if (voteAwayteamNum != 0 && voteHometeamNum != 0) {
            var awayWidth : Int = (mRemindWidth * (voteAwayteamNum.toFloat())/(voteAwayteamNum + voteHometeamNum)).toInt()
            var homeWidth : Int = mRemindWidth - awayWidth
            mAwayParam.width = awayWidth
            mHomeParam.width = homeWidth

        }
        mLayout.like_away_team_ratio.layoutParams = mAwayParam
        mLayout.like_home_team_ratio.layoutParams = mHomeParam
        mLayout.requestLayout()

    }

}