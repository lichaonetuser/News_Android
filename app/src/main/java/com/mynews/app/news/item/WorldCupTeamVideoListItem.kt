package com.mynews.app.news.item

import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.analytics.AnalyticsKey
import com.mynews.app.news.bean.WorldcupVideo
import com.mynews.app.news.page.mvp.layer.main.video.detail.VideoDetailFragment
import com.mynews.app.news.page.mvp.layer.main.video.detail.VideoDetailPresenterAutoBundle
import com.mynews.app.news.proto.AppLog
import com.mynews.app.news.util.GSYVideoTransferUtils
import com.mynews.app.news.openurl.OpenUrlManager
import com.mynews.common.core.CoreApp
import com.mynews.common.core.analytics.AnalyticsManager
import com.mynews.common.core.app.fragment.CoreBaseFragment
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.mynews.common.extension.widget.recycler.holder.BaseViewHolder
import com.mynews.common.extension.widget.recycler.item.BaseItem
import com.github.magiepooh.recycleritemdecoration.ItemDecorations
import com.shuyu.gsyvideoplayer.GSYVideoManager
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.items.IHeader
import kotlinx.android.synthetic.main.item_worldcup_video.view.*
import me.yokeyword.fragmentation.SupportFragment

class WorldCupTeamVideoListItem(mBean: WorldcupVideo)
    : BaseItem<WorldcupVideo, WorldCupTeamVideoListItem.ViewHolder>(mBean), IHeader<WorldCupTeamVideoListItem.ViewHolder> {

    var mAnalyticsKey: String = AnalyticsKey.Event.WORLD_CUP_MATCH
    var mRefer: AppLog.Refer = AppLog.Refer.newBuilder()
            .setItemId(mBean.aid)
            .build()

    override fun getLayoutRes(): Int {
        return R.layout.item_worldcup_video
    }

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        holder.video_title.text = mBean.title
        holder.video_more_title.text = mBean.action_title
        if (mBean.open_url.isNotBlank()) {
            holder.match_viedo_more_desc.visibility = View.VISIBLE
        }
        holder.updateWorldCupVideoRv(mBean)
    }

    override fun unbindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: ViewHolder?, position: Int) {
        super.unbindViewHolder(adapter, holder, position)
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter, mBean, mAnalyticsKey, mRefer)
    }

    class ViewHolder(view: View, mCommonAdapter: CommonRecyclerAdapter,
                     var bean: WorldcupVideo,
                     var analyticsKey: String,
                     var refer: AppLog.Refer) : BaseViewHolder(view, mCommonAdapter) {

        val video_title = itemView.video_title
        val worldcup_video_rv = itemView.worldcup_video_rv
        val video_more_title = itemView.video_more_title
        val match_viedo_more_desc = itemView.match_viedo_more_desc

        init {
            worldcup_video_rv.adapter = CommonRecyclerAdapter()
            worldcup_video_rv.layoutManager = LinearLayoutManager(worldcup_video_rv.context, LinearLayoutManager.HORIZONTAL, false)
            worldcup_video_rv.isNestedScrollingEnabled = true
            worldcup_video_rv.addItemDecoration(ItemDecorations.horizontal(worldcup_video_rv.context)
                    .type(R.layout.item_worldcup_video_view, R.drawable.divider_world_cup_team_video_list)
                    .create())

            (worldcup_video_rv.adapter as CommonRecyclerAdapter).addListener(object : CommonRecyclerAdapter.FlexibleListener() {
                override fun onItemClick(position: Int): Boolean {
                    AnalyticsManager.logEvent(analyticsKey, AnalyticsKey.Parameter.CLICK_VIDEO_CONTAINER_CELL)
                    GSYVideoTransferUtils.prepareTransfer()
                    GSYVideoManager.releaseAllVideos()
                    val fragment = CoreBaseFragment.instantiate(VideoDetailFragment::class.java,
                            VideoDetailPresenterAutoBundle.builder(bean.video_items[position], false, refer).bundle())
                    CoreApp.coreBaseActivities.firstOrNull()?.callRootFragmentStart(fragment, SupportFragment.STANDARD)
                    return true
                }
            })

            match_viedo_more_desc.setOnClickListener {
                AnalyticsManager.logEvent(analyticsKey, AnalyticsKey.Parameter.CLICK_VIDEO_CONTAINER_MORE)
                OpenUrlManager.checkOpenUrl(openUrl = bean.open_url)
            }
        }

        fun updateWorldCupVideoRv(bean: WorldcupVideo) {
            this.bean = bean
            (worldcup_video_rv.adapter as CommonRecyclerAdapter).updateDataSet(bean.video_items.map { WorldCupFeaturedVideoItem(it) })
        }

    }
}