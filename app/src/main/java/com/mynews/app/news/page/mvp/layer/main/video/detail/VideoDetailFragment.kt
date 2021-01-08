package com.mynews.app.news.page.mvp.layer.main.video.detail

import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.analytics.AnalyticsKey
import com.mynews.app.news.bean.Video
import com.mynews.app.news.data.DataDictionary
import com.mynews.app.news.page.mvp.layer.main.digbury.DigBuryragment
import com.mynews.app.news.page.mvp.layer.main.list.comment.CommentFragment
import com.mynews.app.news.page.mvp.layer.main.list.related.RelatedFragment
import com.mynews.app.news.proto.AppLog
import com.mynews.app.news.widget.NestedScrollOverScrollDecorAdapter
import com.mynews.app.news.widget.NewsVideoView
import com.mynews.app.news.widget.nested.NestedChildRecyclerView
import com.mynews.common.core.app.fragment.CoreBaseFragment
import com.mynews.common.core.image.fresco.ImageManager
import com.mynews.common.core.util.ResUtils
import com.mynews.common.core.util.StatusBarUtils
import com.mynews.common.extension.app.mvp.loading.MVPLoadingFragment
import kotlinx.android.synthetic.main.fragment_video_detail.*
import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator
import org.jetbrains.anko.findOptional
import org.jetbrains.anko.support.v4.dip

class VideoDetailFragment : MVPLoadingFragment<VideoDetailContract.View,
        VideoDetailContract.Presenter<VideoDetailContract.View>>(),
        VideoDetailContract.View {

    override val mAttachSwipeBack = true
    override var mDispatchBack = true
    override val mPresenter = VideoDetailPresenter()
    override val mLayoutRes = R.layout.fragment_video_detail

    private var mRelatedFragment: RelatedFragment? = null
    private var mDigBuryFragment: DigBuryragment? = null
    private var mCommentFragment: CommentFragment? = null

    private var mRememberScrollY = -1
    private var mRememberCommentScrollPosition = -1
    private var mVideo: Video? = null

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        desc_open_btn.setOnClickListener {
            mPresenter.onClickDescription(desc_txt.visibility == View.VISIBLE)
        }
        origin_btn.setOnClickListener {
            mPresenter.onClickOrigin()
        }
        video_back_btn.setOnClickListener {
            back()
        }
        write_comment_btn.setOnClickListener {
            mPresenter.onClickWriteComment()
        }
        comment_btn.setOnClickListener {
            mPresenter.onClickComment()
        }
        collect_btn.setOnClickListener {
            mVideo?.run {
                if (!isFavorite) {
                    collect_btn.collect()
                } else {
                    collect_btn.unCollect()
                }
                mPresenter.onClickCollect()
            }
        }
        share_btn.setOnClickListener {
            mPresenter.onClickShare()
        }
        video_view.setCurrentTimeTxtLeft(dip(14))
        VerticalOverScrollBounceEffectDecorator(NestedScrollOverScrollDecorAdapter(scroll_view))
    }

    override fun onSupportVisible() {
        super.onSupportVisible()
        StatusBarUtils.notifyStatusBarIsDark(_mActivity)
    }

    override fun onResume() {
        super.onResume()
        mPresenter.onResume()
    }

    override fun onPause() {
        super.onPause()
        mPresenter.onPause()
    }

    override fun loadRelated(bundle: Bundle) {
        val relatedFragment = findChildFragment(RelatedFragment::class.java)
        if (relatedFragment == null) {
            mRelatedFragment = CoreBaseFragment.instantiate(RelatedFragment::class.java, bundle)
            loadRootFragment(R.id.container_related_layout, mRelatedFragment)
        } else {
            mRelatedFragment = relatedFragment
        }
    }

    override fun loadDigBury(bundle: Bundle) {
        val digBuryFragment = findChildFragment(DigBuryragment::class.java)
        if (digBuryFragment == null) {
            mDigBuryFragment = CoreBaseFragment.instantiate(DigBuryragment::class.java, bundle)
            loadRootFragment(R.id.container_dig_bury_layout, mDigBuryFragment)
        } else {
            mDigBuryFragment = digBuryFragment
        }
    }

    override fun loadComment(bundle: Bundle) {
        val commentFragment = findChildFragment(CommentFragment::class.java)
        if (commentFragment == null) {
            mCommentFragment = CoreBaseFragment.instantiate(CommentFragment::class.java, bundle)
            loadRootFragment(R.id.container_comment_layout, mCommentFragment)
        } else {
            mCommentFragment = commentFragment
        }
    }

    override fun scrollToComment() {
        if (mRememberScrollY == -1) {
            mRememberScrollY = 0
        }
        val rv = container_comment_layout.findOptional<NestedChildRecyclerView>(R.id.common_content_rv)
                ?: return
        scroll_view.fling(0)
        rv.stopScroll()
        scroll_view.smoothScrollTo(0, container_comment_layout.top)
        rv.smoothScrollToPosition(0)
    }

    override fun toggleToComment() {
        val rv = container_comment_layout.findOptional<NestedChildRecyclerView>(R.id.common_content_rv)
                ?: return
        if (mRememberScrollY == -1) {
            mRememberScrollY = scroll_view.scrollY
            if (rv.isScrolledToTop()) {
                scroll_view.fling(0)
                scroll_view.smoothScrollTo(0, container_comment_layout.top)
            } else {
                val layoutManager: LinearLayoutManager = rv.layoutManager as LinearLayoutManager
                mRememberCommentScrollPosition = layoutManager.findLastCompletelyVisibleItemPosition()
                rv.stopScroll()
                rv.smoothScrollToPosition(0)
                scroll_view.fling(0)
                scroll_view.smoothScrollTo(0, container_comment_layout.top)
            }
        } else {
            if (mRememberCommentScrollPosition != -1) {
                rv.stopScroll()
                rv.smoothScrollToPosition(mRememberCommentScrollPosition)
                mRememberCommentScrollPosition = -1
            }
            scroll_view.fling(0)
            scroll_view.smoothScrollTo(0, mRememberScrollY)
            mRememberScrollY = -1
        }
    }

    override fun setVideo(video: Video) {
        mVideo = video
        ImageManager.with(user_img).load(video.avatarUrl)
        video_title_txt.text = video.title
        user_name_txt.text = video.sourceName
        desc_txt.text = if (video.description.isNotBlank()) {
            video.description
        } else {
            ResUtils.getString(R.string.Common_NoContent)
        }
        collect_btn.isActivated = video.isFavorite
        comment_count_txt.visibility = if (video.commentCount == 0) {
            View.GONE
        } else {
            View.VISIBLE
        }
        comment_count_txt.text = video.commentCount.toString()
        write_comment_btn.isEnabled = (video.commentType == DataDictionary.CommentType.ENABLE.value)
        write_comment_btn.text = if (write_comment_btn.isEnabled) {
            ResUtils.getString(R.string.Tip_WriteAComment)
        } else {
            ResUtils.getString(R.string.Tip_NewsCommentUnsupported)
        }
    }

    override fun setPlayerVideo(video: Video, positionSourceRefer: AppLog.Refer, positionRefer: AppLog.Refer) {
        video_view.setUpLazy(video = video, style = NewsVideoView.LayoutStyle.DETAIL,
                positionSourceRefer = positionSourceRefer,
                positionRefer = positionRefer,
                analyticsEventKey = AnalyticsKey.Event.VIDEO_DETAIL)
    }

    override fun showVideoLoadingUI() {
        video_view.changeUiToPreparingShow()
    }

    override fun showVideoErrorUI() {
        video_view.changeUiToErrorShow()
    }

    override fun playVideo() {
        video_view.startPlayLogic(NewsVideoView.StartType.TRANSFER)
    }

    override fun toggleDescription() {
        if (desc_txt.visibility == View.VISIBLE) {
            hideDescription()
        } else {
            showDescription()
        }
    }

    override fun showDescription() {
        desc_txt.visibility = View.VISIBLE
        ObjectAnimator.ofFloat(desc_open_btn, "rotation", 0f, 180f)
                .setDuration(150L)
                .start()
    }

    override fun hideDescription() {
        desc_open_btn.rotation = 0f
        desc_txt.visibility = View.GONE
        ObjectAnimator.ofFloat(desc_open_btn, "rotation", 180f, 0f)
                .setDuration(150L)
                .start()
    }

    override fun onBackPressedSupport(): Boolean {
        if (NewsVideoView.FullScreen.backFromWindowFull(_mActivity)) {
            return true
        }
        return super.onBackPressedSupport()
    }

    override fun backToPrev() {
        if (preFragment == null) {
            super.backToPrev()
            return
        }
        if (preFragment::class == this::class) {
            super.back()
        } else {
            super.backToPrev()
        }
    }

}


