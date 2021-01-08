package com.mynews.app.news.page.mvp.layer.main.image.browser

import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import android.view.View
import com.mynews.app.news.R
import com.mynews.common.core.image.fresco.photoview.entity.PhotoInfo
import com.mynews.common.core.image.fresco.photoview.photodraweeview.OnPhotoTapListener
import com.mynews.common.core.rx.permission.RxPermission
import com.mynews.common.extension.app.mvp.base.MVPBaseFragment
import com.yanzhenjie.permission.Permission
import kotlinx.android.synthetic.main.fragment_common_browser_image.*
import me.yokeyword.fragmentation.anim.FragmentAnimator
import java.util.*

class ImageBrowserFragment : MVPBaseFragment<ImageBrowserContract.View,
        ImageBrowserContract.Presenter<ImageBrowserContract.View>>(),
        ImageBrowserContract.View {

    override val mAttachSwipeBack = false
    override val mPresenter = ImageBrowserPresenter()
    override val mLayoutRes = R.layout.fragment_common_browser_image

//    private lateinit var mTransitionCompat: TransitionCompat

    override fun initView(view: View?, savedInstanceState: Bundle?) {
//        mTransitionCompat = TransitionCompat(_mActivity)
        image_view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                picture_count_txt.text = String.format(Locale.getDefault(), "%d/%d", position + 1, image_view_pager.adapter?.count
                        ?: 0)
                mPresenter.onPageSelected(position)
            }
        })
        download_btn.setOnClickListener {
            mPresenter.onClickDownload(image_view_pager.currentItem)
        }
    }

    override fun hideDownload() {
        download_btn.visibility = View.GONE
    }

    override fun showDownload() {
        download_btn.visibility = View.VISIBLE
    }

    override fun setPhotoInfo(info: ArrayList<PhotoInfo>, index: Int) {
        image_view_pager.adapter = ImageBrowseAdapter(_mActivity, info, OnPhotoTapListener { view, x, y ->
            back()
        }, null)
        image_view_pager.currentItem = index
        picture_count_txt.text = String.format(Locale.getDefault(), "%d/%d", index + 1, info.size)
    }

    override fun startTransitionCompat() {
//        mTransitionCompat.setCurrentPosition(image_view_pager.currentItem)
//        mTransitionCompat.startTransition()
    }

    override fun showSettingDialogIfAlwaysDenied() {
        RxPermission.showSettingDialogIfAlwaysDenied(this, *Permission.STORAGE)
    }

    override fun onCreateFragmentAnimator(): FragmentAnimator {
        return FragmentAnimator(
                R.anim.core_fade_in,
                R.anim.core_fade_out,
                R.anim.no_anim,
                R.anim.pop_exit_no_anim)
    }

    protected fun getLayoutResId(): Int {
        return R.layout.core_activity_picture_browse
    }


}


