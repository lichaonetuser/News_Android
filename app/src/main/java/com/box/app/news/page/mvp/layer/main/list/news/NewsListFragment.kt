package com.box.app.news.page.mvp.layer.main.list.news

import android.os.Bundle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import android.view.View
import android.view.ViewGroup
import com.box.app.news.R
import com.box.app.news.bean.Channel
import com.box.app.news.bean.Tip
import com.box.app.news.bean.base.BaseNewsBean
import com.box.app.news.data.DataDictionary
import com.box.app.news.item.base.BaseNewsItem
import com.box.app.news.widget.NewsVideoView
import com.box.app.news.widget.VideoRefreshHeader
import com.box.common.core.util.ResUtils
import com.box.common.core.widget.CoreTextView
import com.box.common.extension.app.mvp.loading.list.MVPListFragment
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.box.common.extension.widget.recycler.decoration.CommonItemDecoration
import com.box.common.extension.widget.recycler.item.BaseItem
import com.scwang.smartrefresh.layout.api.RefreshFooter
import com.scwang.smartrefresh.layout.api.RefreshHeader
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.constant.RefreshState
import com.scwang.smartrefresh.layout.listener.OnMultiPurposeListener
import kotlinx.android.synthetic.main.fragment_common_list_refresh.*
import org.jetbrains.anko.noButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.longToast
import org.jetbrains.anko.yesButton

open class NewsListFragment<in V : NewsListContract.View, out P : NewsListContract.Presenter<V>>
    : MVPListFragment<V, P>(), NewsListContract.View {

    override val mLayoutRes: Int = R.layout.fragment_news_list
    @Suppress("UNCHECKED_CAST")
    override val mPresenter = NewsListPresenter<V>() as P
    override var mDispatchBack = false
    override var mCommonItemDecorations = CommonItemDecoration().withDivider(R.drawable.divider_news_list)

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        common_content_rv.setItemViewCacheSize(3)
        (common_content_rv.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        /**
         * 启用滑动动画，目前看效果不是很好，待优化后尝试启用
        mCommonAdapter
        .setAnimationInterpolator(DecelerateInterpolator())
        .setAnimationDuration(300L)
        .setAnimationOnScrolling(true) // 启用正向滑动动画
        .setAnimationOnReverseScrolling(true) // 启用反向滑动动画
         */
        mCommonAdapter.addListener(object : CommonRecyclerAdapter.OnBindViewHolderListener {
            override fun onBindViewHolder(item: BaseItem<*, *>, holder: RecyclerView.ViewHolder?, position: Int, payloads: MutableList<Any?>?) {
                mPresenter.onBindItem(position, item, payloads)
            }
        })
        mCommonAdapter.addListener(object : CommonRecyclerAdapter.OnCreateViewHolderListener {
            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int, holder: RecyclerView.ViewHolder) {
                if (holder is BaseNewsItem.ViewHolder) {
                    holder.bindItemAttachedListener { isAttached ->
                        val position = holder.flexibleAdapterPosition
                        mPresenter.onItemVisibility(position, mCommonAdapter.getItem(position)
                                ?: return@bindItemAttachedListener, isAttached)
                    }
                }
            }
        })
        refresh_layout.setOnMultiPurposeListener(object : OnMultiPurposeListener {
            override fun onFooterPulling(footer: RefreshFooter?, percent: Float, offset: Int, footerHeight: Int, extendHeight: Int) {}
            override fun onLoadmore(refreshlayout: RefreshLayout?) {}
            override fun onHeaderReleasing(header: RefreshHeader?, percent: Float, offset: Int, headerHeight: Int, extendHeight: Int) {}
            override fun onHeaderStartAnimator(header: RefreshHeader?, headerHeight: Int, extendHeight: Int) {}
            override fun onStateChanged(refreshLayout: RefreshLayout?, oldState: RefreshState?, newState: RefreshState?) {}
            override fun onFooterFinish(footer: RefreshFooter?, success: Boolean) {}
            override fun onFooterStartAnimator(footer: RefreshFooter?, footerHeight: Int, extendHeight: Int) {}
            override fun onFooterReleasing(footer: RefreshFooter?, percent: Float, offset: Int, footerHeight: Int, extendHeight: Int) {}
            override fun onHeaderPulling(header: RefreshHeader?, percent: Float, offset: Int, headerHeight: Int, extendHeight: Int) {}
            override fun onRefresh(refreshlayout: RefreshLayout?) {}
            override fun onHeaderFinish(header: RefreshHeader?, success: Boolean) {
                mPresenter.onRefreshScrollFinish(success)
            }
        })
        val bundle = arguments?.getParcelable<Channel>("mChannel")
        bundle?.run {
            setRefreshHeader(this)
        }
    }

    override fun onPause() {
        super.onPause()
        mPresenter.onPause()
        mCommonAdapter.state = CommonRecyclerAdapter.FragmentState.PAUSE
        if ((mPresenter is NewsListPresenter<*>)) {
            val presenter = mPresenter as NewsListPresenter<*>
            if (presenter.getChannel().channelType == DataDictionary.ChannelType.GIF.value) {
                mCommonAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mCommonAdapter.state = CommonRecyclerAdapter.FragmentState.DESTROY
    }

    override fun onResume() {
        super.onResume()
        mCommonAdapter.state = CommonRecyclerAdapter.FragmentState.VISIBLE
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            mCommonAdapter.state = CommonRecyclerAdapter.FragmentState.HIDDEN
            if ((mPresenter is NewsListPresenter<*>)) {
                val presenter = mPresenter as NewsListPresenter<*>
                if (presenter.getChannel().channelType == DataDictionary.ChannelType.GIF.value) {
                    mCommonAdapter.notifyDataSetChanged()
                }
            }
        } else {
            mCommonAdapter.state = CommonRecyclerAdapter.FragmentState.VISIBLE
        }
    }

    override fun onRefreshComplete(newItems: List<BaseItem<*, *>>, isUpdate: Boolean, position: Int, scrollToPosition: Int) {
        disableAnimWhenRefresh()
        super.onRefreshComplete(newItems, isUpdate, position, scrollToPosition)
        enableAnimAfterRefresh()
    }

    private fun disableAnimWhenRefresh() {
        common_content_rv.itemAnimator = null
    }

    private val mDefaultItemAnimator = DefaultItemAnimator()
    private val mEnableAnimRunner: Runnable = Runnable {
        common_content_rv?.itemAnimator = mDefaultItemAnimator
    }

    private fun enableAnimAfterRefresh() {
        common_content_rv.removeCallbacks(mEnableAnimRunner)
        common_content_rv.postDelayed(mEnableAnimRunner, 260)
    }

    override fun showTip(tip: Tip) {
        longToast(tip.tip)
    }

    override fun showRemoveDialog(news: BaseNewsBean) {
        alert(R.string.Tip_ContentNotInterested) {
            yesButton { mPresenter.onClickRemoveNews(news, true) }
            noButton { mPresenter.onClickRemoveNews(news, false) }
            onCancelled { mPresenter.onClickRemoveNews(news, false) }
        }.show()
    }

    override fun scrollCommonRVBy(y: Int) {
        common_content_rv.scrollBy(0, y)
    }

    override fun scrollCommonRVTo(position: Int) {
        common_content_rv.scrollToPosition(position)
    }

    override fun smoothScrollCommonRVTo(position: Int) {
        common_content_rv.smoothScrollToPosition(position)
    }

    override fun onBackPressedSupport(): Boolean {
        if (NewsVideoView.FullScreen.backFromWindowFull(_mActivity)) {
            return true
        }
        return super.onBackPressedSupport()
    }

    //DEBUG
    override fun setShowRefer(text: String) {
        val textView = CoreTextView(_mActivity)
        textView.text = text
        textView.setTextColor(ResUtils.getColor(R.color.color_11))
        if (loading_layout.indexOfChild(textView) == -1) {
            loading_layout.addView(textView)
        }
    }

    private fun setRefreshHeader(channel: Channel) {
        context?.run {
            if (channel.chid == DataDictionary.CHANNEL_ID_RECOMMEND_ARTICLE_VIDEO
                    || channel.chid == DataDictionary.CHANNEL_ID_RECOMMEND_VIDEO
                    || channel.channelType == DataDictionary.ChannelType.VIDEO.value
                    || channel.channelType == DataDictionary.ChannelType.GIF.value) {
                refresh_layout.refreshHeader = VideoRefreshHeader(this)
            }
        }
    }

}


