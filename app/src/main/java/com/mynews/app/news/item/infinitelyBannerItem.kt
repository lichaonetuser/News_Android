package com.mynews.app.news.item

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mynews.app.news.R
import com.mynews.app.news.bean.WorldcupBanner
import com.mynews.app.news.bean.WorldcupBannerNews
import com.mynews.app.news.data.DataManager
import com.mynews.app.news.widget.vp.LoopPagerHelper
import com.mynews.app.news.openurl.OpenUrlManager
import com.mynews.app.news.widget.vp.LoopPagerAdapter
import com.mynews.common.core.CoreApp
import com.mynews.common.core.image.fresco.ImageManager
import com.mynews.common.core.util.ResUtils
import com.mynews.common.extension.widget.bar.indicator.buildins.circlenavigator.ColorCircleNavigator
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.mynews.common.extension.widget.recycler.holder.BaseViewHolder
import com.mynews.common.extension.widget.recycler.item.BaseItem
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.items.IHeader
import kotlinx.android.synthetic.main.item_banner_infinitely.view.*
import kotlinx.android.synthetic.main.item_worldcup_banner_view_style_1.view.*
import org.jetbrains.anko.dip

class infinitelyBannerItem(mBean: WorldcupBanner)
    : BaseItem<WorldcupBanner, infinitelyBannerItem.ViewHolder>(mBean), IHeader<infinitelyBannerItem.ViewHolder> {

    override fun getLayoutRes(): Int {
        return R.layout.item_banner_infinitely
    }

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        val internal = DataManager.Memory.getAppConfig().cover_flow_rool_interval
        holder.updateViewPageAdapter(mBean.items, (internal * 1000).toLong()) //浮点类型，单位毫秒
    }

    override fun unbindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: ViewHolder?, position: Int) {
        super.unbindViewHolder(adapter, holder, position)
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter)
    }

    class ViewHolder(view: View, mCommonAdapter: CommonRecyclerAdapter) : BaseViewHolder(view, mCommonAdapter) {
        val banner_vp = itemView.banner_vp
        val banner_vp_indicator = itemView.banner_vp_indicator

        var mBeans: ArrayList<WorldcupBannerNews> = arrayListOf()
        var mInternal: Long = 1000
        var mLoopPagerHelper: LoopPagerHelper? = null

        init {
            banner_vp.setScrollSpeed(500)
        }

        fun updateViewPageAdapter(beans: ArrayList<WorldcupBannerNews>, internal: Long) {
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
                    val view = LayoutInflater.from(container.context).inflate(R.layout.item_worldcup_banner_view_style_1, container, false)
                    val url = beans.get(position).cover_image
                    ImageManager.with(view.banner_view_style_1_image).load(url)
                    view.banner_view_style_1_image.setOnClickListener {
                        OpenUrlManager.checkOpenUrl(beans.get(position).open_url)
                    }
                    view.video_title.text = beans.get(position).title
                    view.video_title.ellipsize = (TextUtils.TruncateAt.valueOf("END"))
                    if (beans.get(position).is_video) {
                        view.play_btn.visibility = View.VISIBLE
                    } else {
                        view.play_btn.visibility = View.GONE
                    }
                    if (beans[position].is_gif) {
                        view.gif_flag.visibility = View.VISIBLE
                    } else {
                        view.gif_flag.visibility = View.GONE
                    }
                    return view
                }
            }
            banner_vp.adapter?.notifyDataSetChanged()

            val circleNavigator = ColorCircleNavigator(itemView.context)
            circleNavigator.isFollowTouch = false
            circleNavigator.circleCount = beans.size
            circleNavigator.circleColor = ResUtils.getColor(R.color.color_9_40)
            circleNavigator.circleSelectedColor = ResUtils.getColor(R.color.color_9)
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
    }

}