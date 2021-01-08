package com.mynews.app.news.page.mvp.layer.main.article.detail.newsjet

import android.os.Bundle
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.LinearLayout
import com.mynews.app.news.R
import com.mynews.app.news.page.mvp.layer.main.digbury.DigBuryragment
import com.mynews.app.news.page.mvp.layer.main.image.browser.ImageBrowserFragment
import com.mynews.app.news.page.mvp.layer.main.list.comment.CommentFragment
import com.mynews.app.news.page.mvp.layer.main.list.related.RelatedFragment
import com.mynews.app.news.widget.nested.NestedChildRecyclerView
import com.mynews.common.core.app.fragment.CoreBaseFragment
import com.mynews.common.core.browser.agent.IWebLayout
import com.mynews.common.extension.app.mvp.base.MVPBaseFragment
import com.mynews.common.extension.app.mvp.loading.browser.MVPBrowserFragment
import kotlinx.android.synthetic.main.fragment_article_detail_transcode.*
import org.jetbrains.anko.findOptional

class ArticleDetailTranscodeFragment : MVPBrowserFragment<ArticleDetailTranscodeContract.View,
        ArticleDetailTranscodeContract.Presenter<ArticleDetailTranscodeContract.View>>(),
        ArticleDetailTranscodeContract.View {

    override val mAttachSwipeBack = false
    override var mDispatchBack = false
    override val mPresenter = ArticleDetailTranscodePresenter()
    override val mLayoutRes = R.layout.fragment_article_detail_transcode
    override val mIWebLayout = object : IWebLayout<WebView, ViewGroup> {
        override fun getRootLayout(view: WebView?): ViewGroup {
            return scroll_view
        }

        override fun getLayout(view: WebView?): ViewGroup {
            return web_view_attach_layout
        }

        override fun getWebIndex(): Int {
            return 0
        }

        override fun getWebLayoutParams(): ViewGroup.LayoutParams {
            return LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }
    private var mMaxScrollY = 0

    private var mRelatedFragment: RelatedFragment? = null
    private var mDigBuryFragment: DigBuryragment? = null
    private var mCommentFragment: CommentFragment? = null

    private var mRememberScrollY = -1
    private var mRememberCommentScrollPosition = -1

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        scroll_view.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            if (scrollY > mMaxScrollY) {
                mMaxScrollY = scrollY
            }
        }
//        VerticalOverScrollBounceEffectDecorator(NestedScrollOverScrollDecorAdapter(scroll_view))
    }

    override fun back() {
        if (parentFragment is MVPBaseFragment<*, *>) {
            (parentFragment as MVPBaseFragment<*, *>).back()
        } else {
            super.back()
        }
    }

    override fun goFullImage(bundle: Bundle) {
        if (parentFragment is CoreBaseFragment) {
            (parentFragment as CoreBaseFragment).extraTransaction()
                    .setCustomAnimations(
                            R.anim.core_fade_in,
                            R.anim.no_anim,
                            R.anim.no_anim,
                            R.anim.core_fade_out)
                    .startDontHideSelf(instantiate(ImageBrowserFragment::class.java, bundle))
        }
    }

    override fun getPageHeight(): Long {
        return mAgentWeb.webCreator.get().height.toLong()
    }

    override fun getReadHeight(): Long {
        return scroll_view.height + mMaxScrollY.toLong()
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

    override fun loadRelated(bundle: Bundle) {
        container_related_layout.visibility = View.VISIBLE
        val relatedFragment = findChildFragment(RelatedFragment::class.java)
        if (relatedFragment == null) {
            mRelatedFragment = CoreBaseFragment.instantiate(RelatedFragment::class.java, bundle)
            loadRootFragment(R.id.container_related_layout, mRelatedFragment)
        } else {
            mRelatedFragment = relatedFragment
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

    override fun toggleToComment() {
        val rv = container_comment_layout?.findOptional<NestedChildRecyclerView>(R.id.common_content_rv)
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

}


