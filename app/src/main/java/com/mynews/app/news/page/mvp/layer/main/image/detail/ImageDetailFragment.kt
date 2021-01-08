package com.mynews.app.news.page.mvp.layer.main.image.detail

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import com.mynews.app.news.R
import com.mynews.app.news.bean.Image
import com.mynews.app.news.data.DataDictionary
import com.mynews.app.news.page.mvp.layer.main.digbury.DigBuryragment
import com.mynews.app.news.page.mvp.layer.main.image.browser.ImageBrowserFragment
import com.mynews.app.news.page.mvp.layer.main.list.comment.CommentFragment
import com.mynews.app.news.page.mvp.layer.main.list.related.RelatedFragment
import com.mynews.app.news.widget.nested.NestedChildRecyclerView
import com.mynews.common.core.CoreApp
import com.mynews.common.core.app.fragment.CoreBaseFragment
import com.mynews.common.core.environment.EnvDisplayMetrics
import com.mynews.common.core.image.fresco.ImageManager
import com.mynews.common.core.util.ResUtils
import com.mynews.common.extension.app.mvp.loading.MVPLoadingFragment
import kotlinx.android.synthetic.main.fragment_image_detail.*
import me.yokeyword.fragmentation.ISupportFragment
import org.jetbrains.anko.dip
import org.jetbrains.anko.findOptional

class ImageDetailFragment : MVPLoadingFragment<ImageDetailContract.View,
        ImageDetailContract.Presenter<ImageDetailContract.View>>(),
        ImageDetailContract.View {

    companion object {
        val RATIO = 0.561f
        val PADDING = CoreApp.getInstance().dip(3)
    }

    override val mAttachSwipeBack = true
    override var mDispatchBack = true
    override val mPresenter = ImageDetailPresenter()
    override val mLayoutRes = R.layout.fragment_image_detail
    private var mRelatedFragment: RelatedFragment? = null
    private var mDigBuryFragment: DigBuryragment? = null
    private var mCommentFragment: CommentFragment? = null
    private val mGoFullImageRequestCode = 0x111

    private var mRememberScrollY = -1
    private var mRememberCommentScrollPosition = -1

    private var imagesSize = 0
    private var hasSetUpImage = false

    private var mImage: Image? = null

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        news_img0.setOnClickListener{
            mPresenter.onClickNewsImg(0)
        }
        news_img1.setOnClickListener {
            mPresenter.onClickNewsImg(2)
        }
        news_img2.setOnClickListener {
            mPresenter.onClickNewsImg(1)
        }
        news_img3.setOnClickListener {
            mPresenter.onClickNewsImg(if (imagesSize == 4) 3 else 2)
        }
        user_img.setOnClickListener {
            mPresenter.onClickAuthor()
        }
        user_name_txt.setOnClickListener {
            mPresenter.onClickAuthor()
        }
        write_comment_btn.setOnClickListener {
            mPresenter.onClickWriteComment()
        }
        comment_btn.setOnClickListener {
            mPresenter.onClickComment()
        }
        collect_btn.setOnClickListener {
            mImage?.run {
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

    override fun setNews(image: Image) {
        mImage = image
        if (image.title.isBlank()) {
            news_title_txt.visibility = View.GONE
        } else {
            news_title_txt.visibility = View.VISIBLE
        }
        news_title_txt.text = image.title
        user_name_txt.text = image.sourceName
        ImageManager.with(user_img).load(image.avatarUrl)

        setUpImage(image)

        collect_btn.isActivated = image.isFavorite
        comment_count_txt.visibility = if (image.commentCount == 0) {
            View.GONE
        } else {
            View.VISIBLE
        }
        comment_count_txt.text = image.commentCount.toString()
        write_comment_btn.isEnabled = (image.commentType == DataDictionary.CommentType.ENABLE.value)
        write_comment_btn.text = if (write_comment_btn.isEnabled) {
            ResUtils.getString(R.string.Tip_WriteAComment)
        } else {
            ResUtils.getString(R.string.Tip_NewsCommentUnsupported)
        }
    }

    private fun setUpImage(image: Image) {
        if (hasSetUpImage) return
        val imageInfos = image.images
        imagesSize = image.images.size
        when (imagesSize) {
            0,1 -> {
                val ratio: Float
                val url = if (imageInfos.isNotEmpty()) {
                    ratio = imageInfos[0].width.toFloat() / imageInfos[0].height.toFloat()
                    imageInfos[0].urls.firstOrNull()
                } else {
                    ratio = image.info.width.toFloat() / image.info.height.toFloat()
                    image.info.urls.firstOrNull()
                }
                val width = EnvDisplayMetrics.WIDTH_PIXELS - scroll_layout.paddingLeft - scroll_layout.paddingRight
                ImageManager.with(news_img0)
                        .setWidth(width)
                        .setAspectRatio(if (ratio < 0.4F) 0.4F else ratio)
                        .load(url)

                news_img0.visibility = VISIBLE
                news_img1.visibility = GONE
                news_img2.visibility = GONE
                news_img3.visibility = GONE
            }
            2 -> {
                val width = EnvDisplayMetrics.WIDTH_PIXELS - scroll_layout.paddingLeft - scroll_layout.paddingRight
                val imageWidth = (width - PADDING) / 2
                val imageHeight = (width * RATIO).toInt()

                val leftParams = news_img0.layoutParams
                leftParams.width = imageWidth
                leftParams.height = imageHeight
                val rightParams = news_img2.layoutParams
                rightParams.width = imageWidth
                rightParams.height = imageHeight

                ImageManager.with(news_img0).load(imageInfos[0].urls.firstOrNull())
                ImageManager.with(news_img2).load(imageInfos[1].urls.firstOrNull())

                news_img0.visibility = VISIBLE
                news_img1.visibility = GONE
                news_img2.visibility = VISIBLE
                news_img3.visibility = GONE
            }
            3 -> {
                val width = EnvDisplayMetrics.WIDTH_PIXELS - scroll_layout.paddingLeft - scroll_layout.paddingRight
                val imageWidth = (width - PADDING) / 2
                val bigImageHeight = (width * RATIO).toInt()
                val smallImageHeight = (bigImageHeight - PADDING) / 2

                val leftParams = news_img0.layoutParams
                leftParams.width = imageWidth
                leftParams.height = bigImageHeight

                val rightTopParams = news_img2.layoutParams
                rightTopParams.width = imageWidth
                rightTopParams.height = smallImageHeight

                val rightBottomParams = news_img3.layoutParams
                rightBottomParams.width = imageWidth
                rightBottomParams.height = smallImageHeight

                ImageManager.with(news_img0).load(imageInfos[0].urls.firstOrNull())
                ImageManager.with(news_img2).load(imageInfos[1].urls.firstOrNull())
                ImageManager.with(news_img3).load(imageInfos[2].urls.firstOrNull())

                news_img0.visibility = VISIBLE
                news_img1.visibility = GONE
                news_img2.visibility = VISIBLE
                news_img3.visibility = VISIBLE
            }
            4 -> {
                val width = EnvDisplayMetrics.WIDTH_PIXELS - scroll_layout.paddingLeft - scroll_layout.paddingRight
                val imageWidth = (width - PADDING) / 2
                val imageHeight = ((width * RATIO).toInt() - PADDING) / 2

                val leftTopParams = news_img0.layoutParams
                leftTopParams.width = imageWidth
                leftTopParams.height = imageHeight

                val rightTopParams = news_img1.layoutParams
                rightTopParams.width = imageWidth
                rightTopParams.height = imageHeight

                val leftBottomParams = news_img2.layoutParams
                leftBottomParams.width = imageWidth
                leftBottomParams.height = imageHeight

                val rightBottomParams = news_img3.layoutParams
                rightBottomParams.width = imageWidth
                rightBottomParams.height = imageHeight

                ImageManager.with(news_img0).load(imageInfos[0].urls.firstOrNull())
                ImageManager.with(news_img2).load(imageInfos[1].urls.firstOrNull())
                ImageManager.with(news_img1).load(imageInfos[2].urls.firstOrNull())
                ImageManager.with(news_img3).load(imageInfos[3].urls.firstOrNull())

                news_img0.visibility = VISIBLE
                news_img1.visibility = VISIBLE
                news_img2.visibility = VISIBLE
                news_img3.visibility = VISIBLE
            }
        }
        hasSetUpImage = true
    }

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

}


