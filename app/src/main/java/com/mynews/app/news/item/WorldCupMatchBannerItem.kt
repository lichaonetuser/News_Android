package com.mynews.app.news.item

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mynews.app.news.R
import com.mynews.app.news.analytics.AnalyticsKey
import com.mynews.app.news.applog.AppLogKey
import com.mynews.app.news.bean.WorldcupMatch
import com.mynews.app.news.bean.WorldcupMatchDetail
import com.mynews.app.news.data.DataDictionary
import com.mynews.app.news.data.DataManager
import com.mynews.app.news.proto.AppLog
import com.mynews.app.news.widget.vp.LoopPagerAdapter
import com.mynews.app.news.widget.vp.LoopPagerHelper
import com.mynews.common.core.CoreApp
import com.mynews.common.core.image.fresco.ImageManager
import com.mynews.common.core.util.ResUtils
import com.mynews.common.extension.widget.bar.indicator.buildins.circlenavigator.ColorCircleNavigator
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.mynews.common.extension.widget.recycler.holder.BaseViewHolder
import com.mynews.common.extension.widget.recycler.item.BaseItem
import eu.davidea.flexibleadapter.items.IHeader
import kotlinx.android.synthetic.main.item_worldcup_match_detail.view.*
import kotlinx.android.synthetic.main.item_worldcup_match_detail_view.view.*
import org.jetbrains.anko.dip
import java.util.*

class WorldCupMatchBannerItem(mBean: WorldcupMatchDetail)
    : BaseItem<WorldcupMatchDetail, WorldCupMatchBannerItem.ViewHolder>(mBean), IHeader<WorldCupMatchBannerItem.ViewHolder> {

    var mAnalyticsKey: String = AnalyticsKey.Event.WORLD_CUP
    var mPositionSourceRefer: AppLog.Refer = AppLog.Refer.newBuilder()
            .setName(AppLogKey.Label.LAUNCH)
            .build()
    var mPositionRefer: AppLog.Refer = AppLog.Refer.newBuilder()
            .setItemId(mBean.aid)
            .build()

    override fun getLayoutRes(): Int {
        return R.layout.item_worldcup_match_detail
    }

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        val internal = DataManager.Memory.getAppConfig().matches_rool_interval
        holder.updateViewPageAdapter(mBean.items, (internal * 1000).toLong()) //浮点类型，单位毫秒
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter, mPositionRefer)
    }

    class ViewHolder(view: View, mCommonAdapter: CommonRecyclerAdapter, val positionRefer: AppLog.Refer) :
            BaseViewHolder(view, mCommonAdapter) {

        val banner_vp = itemView.banner_vp
        val banner_vp_indicator = itemView.banner_vp_indicator

        var mBeans: ArrayList<WorldcupMatch> = arrayListOf()
        var mInternal: Long = 1000
        var mLoopPagerHelper: LoopPagerHelper? = null

        init {
            banner_vp.setScrollSpeed(500)
        }

        fun updateViewPageAdapter(beans: ArrayList<WorldcupMatch>, internal: Long) {
            this.mInternal = internal
            if (mBeans == beans) {
                return
            }

            mLoopPagerHelper?.stopInfiniteScroll()
            mLoopPagerHelper?.initialDelay = internal

            mBeans.clear()
            mBeans.addAll(beans)
            val offscreenLimit = (beans.size + -1) / 2 + 1
            banner_vp.offscreenPageLimit = offscreenLimit
            banner_vp.isScrollable = beans.size > 1
            banner_vp.adapter = object : LoopPagerAdapter(banner_vp) {

                override fun getRealCount(): Int {
                    return beans.size
                }

                override fun getView(container: ViewGroup, position: Int): View {
                    val view = WorldCupMatchDetailInnerView(container.context)
                    val data = beans[position]
                    view.setContent(data)
                    view.setOnClickListener {
//                        AnalyticsManager.logEvent(AnalyticsKey.Event.WORLD_CUP, AnalyticsKey.Parameter.CLICK_MATCH)
//                        val fragment = CoreBaseFragment.instantiate(WorldCupMatchFragment::class.java,
//                                WorldCupMatchPresenterAutoBundle.builder(data.match_id, positionRefer).bundle())
//                        CoreApp.coreBaseActivities.firstOrNull()?.callRootFragmentStart(fragment, SupportFragment.STANDARD)
                    }
                    return view
                }
            }
            banner_vp.adapter?.notifyDataSetChanged()

            val circleNavigator = ColorCircleNavigator(itemView.context)
            circleNavigator.isFollowTouch = false
            circleNavigator.circleCount = beans.size
            circleNavigator.circleColor = ResUtils.getColor(R.color.color_6)
            circleNavigator.circleSelectedColor = ResUtils.getColor(R.color.color_indicator_select)
            circleNavigator.circleSpacing = CoreApp.getInstance().dip(10)
            circleNavigator.radius = CoreApp.getInstance().dip(2)
            banner_vp_indicator.navigator = circleNavigator
            banner_vp_indicator.bind(banner_vp)
            if (beans.size > 1) {
                banner_vp_indicator.visibility = View.VISIBLE
            } else {
                banner_vp_indicator.visibility = View.GONE
            }

            if (beans.size > 1) {
                mLoopPagerHelper?.startInfiniteScroll()
            }
        }

        override fun onViewAttachedToWindow() {
            super.onViewAttachedToWindow()
            if (mLoopPagerHelper == null) {
                mLoopPagerHelper = LoopPagerHelper(banner_vp, mInternal, mInternal)
            }
            if (mBeans.size > 1) {
                mLoopPagerHelper?.startInfiniteScroll()
            }
        }

        override fun onViewDetachedFromWindow() {
            super.onViewDetachedFromWindow()
            mLoopPagerHelper?.stopInfiniteScroll()
        }

        private class WorldCupMatchDetailInnerView @JvmOverloads constructor(
                context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
        ) : ConstraintLayout(context, attrs, defStyleAttr) {

            private var mContext: Context
            private val mLayout: View

            init {
                mContext = context
                mLayout = LayoutInflater.from(context).inflate(R.layout.item_worldcup_match_detail_view, this)
            }

            fun setContent(data: WorldcupMatch) {
                ImageManager.with(mLayout.team_away_image).load(data.away_team.avatar_url)
                mLayout.team_away_title?.setText(data.away_team.name)
                mLayout.team_away_title.ellipsize = (TextUtils.TruncateAt.valueOf("END"))
                ImageManager.with(mLayout.team_home_image).load(data.home_team.avatar_url)
                mLayout.team_home_title?.setText(data.home_team.name)
                mLayout.team_home_title.ellipsize = (TextUtils.TruncateAt.valueOf("END"))

                if (data.game_status == DataDictionary.WorldcupMatchStatus.NOT_START.value) {
                    mLayout.match_status.visibility = View.VISIBLE
                    mLayout.match_status_detail?.setText(ResUtils.getString(R.string.WorldCup2018_Match_Not_Start_Banner))
                    mLayout.match_time?.setText(data.start_time)
                    mLayout.match_score_status.visibility = View.INVISIBLE
                } else if (data.game_status == DataDictionary.WorldcupMatchStatus.ONGOING.value) {
                    mLayout.match_status.visibility = View.INVISIBLE
                    mLayout.match_score_status.visibility = View.VISIBLE
                    mLayout.match_score_status_detail?.setText(ResUtils.getString(R.string.WorldCup2018_Match_Ongoing_Banner))
                    mLayout.home_score.text = data.home_team.score.toString()
                    mLayout.away_score.text = data.away_team.score.toString()
                } else {
                    mLayout.match_status.visibility = View.INVISIBLE
                    mLayout.match_score_status.visibility = View.VISIBLE
                    mLayout.match_score_status_detail?.setText(ResUtils.getString(R.string.WorldCup2018_Match_End_Banner))
                    mLayout.home_score.text = data.home_team.score.toString()
                    mLayout.away_score.text = data.away_team.score.toString()
                }

                mLayout.match_season_round.text = data.sport_season_round
                mLayout.match_season_round.ellipsize = (TextUtils.TruncateAt.valueOf("END"))
                mLayout.match_date_week.text = data.date_week
            }
        }
    }
}