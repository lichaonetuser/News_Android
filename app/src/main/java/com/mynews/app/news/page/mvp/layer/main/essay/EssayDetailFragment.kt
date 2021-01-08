package com.mynews.app.news.page.mvp.layer.main.essay

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.bean.Essay
import com.mynews.app.news.data.DataDictionary
import com.mynews.app.news.page.mvp.layer.main.digbury.DigBuryragment
import com.mynews.app.news.page.mvp.layer.main.image.browser.ImageBrowserFragment
import com.mynews.app.news.page.mvp.layer.main.list.comment.CommentFragment
import com.mynews.app.news.page.mvp.layer.main.list.related.RelatedFragment
import com.mynews.app.news.widget.nested.NestedChildRecyclerView
import com.mynews.common.core.app.fragment.CoreBaseFragment
import com.mynews.common.core.image.fresco.ImageManager
import com.mynews.common.core.util.ResUtils
import com.mynews.common.extension.app.mvp.loading.MVPLoadingFragment
import kotlinx.android.synthetic.main.fragment_joke_detail.*
import me.yokeyword.fragmentation.ISupportFragment
import org.jetbrains.anko.findOptional

class EssayDetailFragment : MVPLoadingFragment<EssayDetailContract.View,
        EssayDetailContract.Presenter<EssayDetailContract.View>>(), EssayDetailContract.View {

    override val mAttachSwipeBack = true
    override var mDispatchBack = true
    override val mPresenter = EssayDetailPresenter()
    override val mLayoutRes = R.layout.fragment_joke_detail
    private var mRelatedFragment: RelatedFragment? = null
    private var mDigBuryFragment: DigBuryragment? = null
    private var mCommentFragment: CommentFragment? = null
    private val mGoFullImageRequestCode = 0x111

    private var mRememberScrollY = -1
    private var mRememberCommentScrollPosition = -1

    private var mEssay: Essay? = null

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        essay_user_img.setOnClickListener {
            mPresenter.onClickAuthor()
        }
        essay_user_name_txt.setOnClickListener {
            mPresenter.onClickAuthor()
        }
        font_size_btn.setOnClickListener {
            mPresenter.onClickFontSize()
        }
        write_comment_btn.setOnClickListener {
            mPresenter.onClickWriteComment()
        }
        comment_btn.setOnClickListener {
            mPresenter.onClickComment()
        }
        collect_btn.setOnClickListener {
            mEssay?.run {
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
    }

    override fun onSupportVisible() {
        super.onSupportVisible()
        notifyStatusBarIsLight()
    }

    override fun onResume() {
        super.onResume()
        mPresenter.onResume()
    }

    override fun onPause() {
        super.onPause()
        mPresenter.onPause()
    }

    override fun onFragmentResult(requestCode: Int, resultCode: Int, data: Bundle?) {
        super.onFragmentResult(requestCode, resultCode, data)
        if (requestCode == mGoFullImageRequestCode && resultCode == ISupportFragment.RESULT_OK) {
            mPresenter.onFragmentResultFromFullImage(data)
        }
    }

    override fun setNews(essay: Essay) {
        mEssay = essay
        font_size_btn.visibility = View.VISIBLE
        essay_user_name_txt.text = essay.sourceName
        ImageManager.with(essay_user_img).load(essay.sourcePic)

        essay_detail_content.setText(essay.text)
        essay_detail_content.setIsExpand(true)
        essay_detail_content.updateTextSize()

        collect_btn.isActivated = essay.isFavorite
        comment_count_txt.visibility = if (essay.commentCount == 0) {
            View.GONE
        } else {
            View.VISIBLE
        }
        comment_count_txt.text = essay.commentCount.toString()
        write_comment_btn.isEnabled = (essay.commentType == DataDictionary.CommentType.ENABLE.value)
        write_comment_btn.text = if (write_comment_btn.isEnabled) {
            ResUtils.getString(R.string.Tip_WriteAComment)
        } else {
            ResUtils.getString(R.string.Tip_NewsCommentUnsupported)
        }
    }

//    fun log(essay: Essay, mPositionSourceRefer: AppLog.Refer, mAnalyticsKey: String, mPositionRefer: AppLog.Refer) {
//        AppLogManager.logEvent(name = AppLog.EventName.EVENT_GIF,
//                label = AppLogKey.Label.START_PLAYING,
//                body = AppLog.EventBody.newBuilder()
//                        .setEnterTime(System.currentTimeMillis())
//                        .setItemId(essay.aid)
//                        .setRefer(mPositionSourceRefer)
//                        .build())
//    }

//    private fun animateCollect() {
//        val rotateAni = RotateAnimation(0f, 145f,
//                RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f)
//        rotateAni.duration = 300
//        rotateAni.interpolator = AccelerateDecelerateInterpolator()
//
//        val scaleAni = ScaleAnimation(1f, 1.3f, 1f, 1.3f,
//                RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f)
//        scaleAni.duration = 300
//
//        val set = AnimationSet(true)
//        set.addAnimation(rotateAni)
//        set.addAnimation(scaleAni)
//
//        collect_btn.startAnimation(set)
//
//        val alphaAni = AlphaAnimation(0f, 1f)
//        alphaAni.duration = 300
//        alphaAni.setAnimationListener(object : Animation.AnimationListener {
//            override fun onAnimationRepeat(p0: Animation?) {
//            }
//
//            override fun onAnimationStart(p0: Animation?) {
//                collect_btn_star_bg.visibility = View.VISIBLE
//            }
//
//            override fun onAnimationEnd(p0: Animation?) {
//                collect_btn_star_bg.visibility = View.GONE
//            }
//        })
//        collect_btn_star_bg.startAnimation(alphaAni)
//    }

    override fun goFullImage(bundle: Bundle) {
        extraTransaction()
                .setCustomAnimations(
                        R.anim.core_fade_in,
                        R.anim.core_fade_out,
                        R.anim.no_anim,
                        R.anim.core_fade_out)
                .startForResultDontHideSelf(instantiate(ImageBrowserFragment::class.java, bundle), mGoFullImageRequestCode)
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

    override fun changeText() {
        essay_detail_content?.updateTextSize()
    }
}


