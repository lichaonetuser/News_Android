package com.box.app.news.item

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.ViewPager
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import com.box.app.news.R
import com.box.app.news.analytics.AnalyticsKey
import com.box.app.news.bean.WorldcupBanner
import com.box.app.news.bean.WorldcupBannerNews
import com.box.app.news.data.DataManager
import com.box.app.news.openurl.OpenUrlManager
import com.box.app.news.widget.vp.LoopPagerAdapter
import com.box.app.news.widget.vp.LoopPagerHelper
import com.box.common.core.CoreApp
import com.box.common.core.analytics.AnalyticsManager
import com.box.common.core.environment.EnvDisplayMetrics
import com.box.common.core.image.fresco.ImageManager
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.box.common.extension.widget.recycler.holder.BaseViewHolder
import com.box.common.extension.widget.recycler.item.BaseItem
import eu.davidea.flexibleadapter.items.IHeader
import kotlinx.android.synthetic.main.item_banner_cover_flow.view.*
import kotlinx.android.synthetic.main.item_worldcup_banner_view.view.*
import org.jetbrains.anko.dip

class CoverFlowBannerItem(mBean: WorldcupBanner)
    : BaseItem<WorldcupBanner, CoverFlowBannerItem.ViewHolder>(mBean), IHeader<CoverFlowBannerItem.ViewHolder> {

    var mAnalyticsKey = ""

    override fun getLayoutRes(): Int {
        return R.layout.item_banner_cover_flow
    }

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        val internal = DataManager.Memory.getAppConfig().cover_flow_rool_interval
        holder.updateViewPageAdapter(mBean.items, (internal * 1000).toLong(), mAnalyticsKey) //浮点类型，单位毫秒
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter)
    }

    open class ViewHolder(view: View, mCommonAdapter: CommonRecyclerAdapter) :
            BaseViewHolder(view, mCommonAdapter) {
        val banner_vp = itemView.banner_vp

        val mScreenWidth = EnvDisplayMetrics.WIDTH_PIXELS
        var mBeans: ArrayList<WorldcupBannerNews> = arrayListOf()
        var mInternal: Long = 1000
        var mLoopPagerHelper: LoopPagerHelper? = null

        init {
            banner_vp.setScrollSpeed(500)
            banner_vp.pageMargin = CoreApp.getInstance().dip(4)
            banner_vp.setPageTransformer(true, ScaleInOutPageTransformer(), LAYER_TYPE_NONE)
            banner_vp.setPadding((mScreenWidth - CoreApp.getInstance().dip(212)) / 2, 0, (mScreenWidth - CoreApp.getInstance().dip(212)) / 2, 0)
        }

        fun updateViewPageAdapter(beans: ArrayList<WorldcupBannerNews>, internal: Long, analyticsKey: String) {
            if (mBeans == beans) {
                return
            }

            this.mInternal = internal

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
                    val bean = beans[position]
                    val view = CoverFlowInnerView(container.context)
                    val url = bean.cover_image
                    view.setContent(bean)
                    ImageManager.with(view.banner_view_image).load(url)
                    view.video_title.text = bean.title
                    view.video_title.ellipsize = (TextUtils.TruncateAt.valueOf("END"))
                    if (bean.is_video) {
                        view.play_btn.visibility = VISIBLE
                    } else {
                        view.play_btn.visibility = GONE
                    }
                    if (bean.is_gif) {
                        view.gif_flag.visibility = VISIBLE
                    } else {
                        view.gif_flag.visibility = GONE
                    }

                    view.banner_view_image.setOnClickListener {
                        if (bean.is_video) {
                            AnalyticsManager.logEvent(analyticsKey, AnalyticsKey.Parameter.LIST_BANNER_VIDEO_CLICK)
                        } else {
                            AnalyticsManager.logEvent(analyticsKey, AnalyticsKey.Parameter.LIST_BANNER_CLICK)
                        }
                        OpenUrlManager.checkOpenUrl(bean.open_url)
                    }
                    return view
                }
            }
            banner_vp.adapter?.notifyDataSetChanged()

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

        private class ScaleInOutPageTransformer : ViewPager.PageTransformer {

            val scaleMax = 0.841f
            val positionOffset = (EnvDisplayMetrics.WIDTH_PIXELS - CoreApp.getInstance().dip(220))
                    .toFloat() / (2 * CoreApp.getInstance()
                    .dip(212).toFloat()) + 0.02

            override fun transformPage(page: View, position: Float) {
                val scale: Float
                val pivotX: Float
                val pivotY: Float
                when {
                    position < positionOffset -> {
                        scale = (1 - scaleMax) * (position - positionOffset.toFloat()) + 1
                        pivotX = page.width.toFloat()
                        pivotY = page.height.toFloat()
                    }
                    else -> {
                        scale = (scaleMax - 1) * (position - positionOffset.toFloat()) + 1
                        pivotX = 0f
                        pivotY = page.height.toFloat()
                    }
                }

                page.pivotX = pivotX
                page.pivotY = pivotY
                page.scaleX = scale
                page.scaleY = scale
            }
        }

        private class CoverFlowInnerView @JvmOverloads constructor(
                context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
        ) : ConstraintLayout(context, attrs, defStyleAttr) {

            private var mContext: Context
            private val mLayout: View

            init {
                mContext = context
                mLayout = LayoutInflater.from(context).inflate(R.layout.item_worldcup_banner_view, this)
            }

            public fun setContent(bannerData: WorldcupBannerNews) {
                var list = arrayListOf<String>()
                for (i in bannerData.tags.indices) {
                    list.add(bannerData.tags[i].name)
                }
                mLayout.tag_layout.setContent(list, R.drawable.wordcup_videotag, 7f, "#d42f37", CoreApp.getInstance().dip(212), 35, 20, 2, 8, 5)
            }
        }
    }
}