package com.box.app.news.page.mvp.layer.main.article.detail

import android.os.Bundle
import android.view.View
import android.view.animation.*
import com.box.app.news.R
import com.box.app.news.bean.Article
import com.box.app.news.data.DataDictionary
import com.box.app.news.event.EventManager
import com.box.app.news.page.mvp.layer.main.article.detail.newsjet.ArticleDetailTranscodeFragment
import com.box.app.news.page.mvp.layer.main.article.detail.web.ArticleDetailWebFragment
import com.box.app.news.page.mvp.layer.main.list.comment.CommentFragment
import com.box.common.core.app.fragment.CoreBaseFragment
import com.box.common.core.image.fresco.ImageManager
import com.box.common.core.util.ResUtils
import com.box.common.extension.app.mvp.loading.MVPLoadingFragment
import kotlinx.android.synthetic.main.fragment_article_detail.*

class ArticleDetailFragment : MVPLoadingFragment<ArticleDetailContract.View,
        ArticleDetailContract.Presenter<ArticleDetailContract.View>>(),
        ArticleDetailContract.View {

    override val mAttachSwipeBack = true
    override var mDispatchBack = true
    override val mPresenter = ArticleDetailPresenter()
    override val mLayoutRes = R.layout.fragment_article_detail

    private var mNewsJetFragment: ArticleDetailTranscodeFragment? = null
    private var mWebFragment: ArticleDetailWebFragment? = null
    private var mLastSwitchTime = 0L

    private var mArticle: Article? = null

    override fun initView(view: View?, savedInstanceState: Bundle?) {
//        newsjet_btn.setOnClickListener {
//            mPresenter.onClickNewsJet()
//        }
//        web_btn.setOnClickListener {
//            mPresenter.onClickWeb()
//        }
        article_view_sign.setOnClickListener {
            val now = System.currentTimeMillis()
            if (now - mLastSwitchTime < 630) {
                return@setOnClickListener
            }
            mLastSwitchTime = now
            it.isSelected = !it.isSelected
            if (it.isSelected) {
                mPresenter.onClickWeb()
            } else {
                mPresenter.onClickNewsJet()
            }
        }
        font_size_btn.setOnClickListener {
            mPresenter.onClickFontSize()
        }
        refresh_btn.setOnClickListener {
            mPresenter.onClickRefresh()
        }
        write_comment_btn.setOnClickListener {
            mPresenter.onClickWriteComment()
        }
        share_btn.setOnClickListener {
            mPresenter.onClickShare()
        }
        comment_btn.setOnClickListener {
            mPresenter.onClickComment()
        }
        collect_btn.setOnClickListener {
            mArticle?.run {
                if (!isFavorite) {
                    collect_btn.collect()
                } else {
                    collect_btn.unCollect()
                }
                mPresenter.onClickCollect()
            }
        }
        nocomment_locate_img.setOnClickListener {
            mPresenter.onClickComment()
        }
        nocomment_collect_img.setOnClickListener {
            mArticle?.run {
                if (!isFavorite) {
                    nocomment_collect_img.collect()
                } else {
                    nocomment_collect_img.unCollect()
                }
                mPresenter.onClickCollect()
            }
        }
        nocomment_share_img.setOnClickListener {
            mPresenter.onClickShare()
        }
    }

//    private fun animateCollect(view: View) {
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
//        view.startAnimation(set)
//
//        val alphaAni = AlphaAnimation(0f, 1f)
//        alphaAni.duration = 300
//        alphaAni.setAnimationListener(object : Animation.AnimationListener {
//            override fun onAnimationRepeat(p0: Animation?) {
//            }
//
//            override fun onAnimationStart(p0: Animation?) {
//                collect_btn_star_bg?.visibility = View.VISIBLE
//            }
//
//            override fun onAnimationEnd(p0: Animation?) {
//                collect_btn_star_bg?.visibility = View.GONE
//            }
//        })
//        collect_btn_star_bg?.startAnimation(alphaAni)
//    }

    override fun loadPages(webBundle: Bundle, newsJetBundle: Bundle, defaultShowWeb: Boolean) {
        val newsJetFragment = findChildFragment(ArticleDetailTranscodeFragment::class.java)
        if (newsJetFragment == null) {
            mWebFragment = CoreBaseFragment.instantiate(ArticleDetailWebFragment::class.java, webBundle)
            mNewsJetFragment = CoreBaseFragment.instantiate(ArticleDetailTranscodeFragment::class.java, newsJetBundle)
            loadMultipleRootFragment(R.id.container_layout, 0, mWebFragment, mNewsJetFragment)
        } else {
            mNewsJetFragment = newsJetFragment
            mWebFragment = findChildFragment(ArticleDetailWebFragment::class.java)
        }
    }

    override fun loadRelated(bundle: Bundle) {
        mNewsJetFragment?.loadRelated(bundle)
    }

    override fun onSupportVisible() {
        super.onSupportVisible()
        notifyStatusBarIsLight()
    }

    override fun setArticle(article: Article) {
        mArticle = article
        if (article.commentType == DataDictionary.CommentType.ENABLE.value) {
            bottom_bar.visibility = View.VISIBLE
            nocomment_bottom_bar.visibility = View.GONE
        } else {
            bottom_bar.visibility = View.GONE
            nocomment_bottom_bar.visibility = View.VISIBLE
        }

        write_comment_btn.isEnabled = (article.commentType == DataDictionary.CommentType.ENABLE.value)
        write_comment_btn.text = if (write_comment_btn.isEnabled) {
            ResUtils.getString(R.string.Tip_WriteAComment)
        } else {
            ResUtils.getString(R.string.Tip_NewsCommentUnsupported)
        }
        comment_count_txt.visibility = if (article.commentCount == 0) {
            View.GONE
        } else {
            View.VISIBLE
        }
        collect_btn.isActivated = article.isFavorite
        nocomment_collect_img.isActivated = article.isFavorite
        comment_count_txt.text = article.commentCount.toString()

        if (article.detailDisplayOptions % 2 != 1) {
            ImageManager.with(article_source_pic).load(article.sourcePic)
        }
    }

    override fun showNewsJet(anim: Boolean) {
        if (anim) {
            val ft = childFragmentManager.beginTransaction()
            ft.setCustomAnimations(R.animator.core_slide_in_bottom, R.anim.no_anim)
            ft.show(mNewsJetFragment ?: return)
            ft.hide(mWebFragment ?: return)
            ft.commitAllowingStateLoss()
        } else {
            showHideFragment(mNewsJetFragment ?: return, mWebFragment ?: return)
        }
    }

    override fun showWeb(anim: Boolean) {
        if (anim) {
            val ft = childFragmentManager.beginTransaction()
            ft.setCustomAnimations(R.anim.no_anim, R.animator.core_slide_out_bottom)
            ft.show(mWebFragment ?: return)
            ft.hide(mNewsJetFragment ?: return)
            ft.commitAllowingStateLoss()
        } else {
            showHideFragment(mWebFragment ?: return, mNewsJetFragment ?: return)
        }
    }

    override fun activateNewsJet() {
//        newsjet_btn.isActivated = true
//        web_btn.isActivated = false
        article_view_sign.isSelected = false
        refresh_btn.visibility = View.GONE
        font_size_btn.visibility = View.VISIBLE
    }

    override fun activateWeb() {
//        newsjet_btn.isActivated = false
//        web_btn.isActivated = true
        article_view_sign.isSelected = true
        refresh_btn.visibility = View.VISIBLE
        font_size_btn.visibility = View.INVISIBLE
    }

    override fun hideSwitch() {
//        newsjet_btn.visibility = View.GONE
//        web_btn.visibility = View.GONE
        article_view_sign.visibility = View.GONE
    }

    override fun showSwitch() {
//        newsjet_btn.visibility = View.VISIBLE
        article_view_sign.visibility = View.VISIBLE
    }

    override fun onPause() {
        super.onPause()
        mPresenter.onPause()
    }

    override fun onResume() {
        super.onResume()
        mPresenter.onResume()
    }

    override fun scrollTransCodeToComment() {
        mNewsJetFragment?.scrollToComment()
    }

    override fun toggleTransCodeToComment() {
        mNewsJetFragment?.toggleToComment()
    }

    override fun isNewsJetHidden(): Boolean {
        return mNewsJetFragment?.isHidden ?: true
    }

    override fun updateCommentFragmentShowForwardBoard(showForwardBoard: Boolean) {
        val commentFragment = mNewsJetFragment?.findChildFragment(CommentFragment::class.java)
        commentFragment?.updateShowForwardBoard(showForwardBoard)
    }


}


