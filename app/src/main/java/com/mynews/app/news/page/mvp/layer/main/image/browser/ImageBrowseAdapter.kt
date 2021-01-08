package com.mynews.app.news.page.mvp.layer.main.image.browser

import android.content.Context
import android.graphics.drawable.Animatable
import android.net.Uri
import android.os.Build
import androidx.viewpager.widget.PagerAdapter
import android.text.TextUtils
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.facebook.common.util.UriUtil
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.drawable.ProgressBarDrawable
import com.facebook.imagepipeline.image.ImageInfo
import com.umeng.commonsdk.statistics.common.MLog

import java.util.ArrayList
import java.util.Locale

import com.mynews.app.news.R
import com.mynews.common.core.environment.EnvDisplayMetrics
import com.mynews.common.core.image.fresco.ImageManager
import com.mynews.common.core.image.fresco.photoview.entity.PhotoInfo
import com.mynews.common.core.image.fresco.photoview.loading.LoadingProgressBarView
import com.mynews.common.core.image.fresco.photoview.photodraweeview.LargePhotoView
import com.mynews.common.core.image.fresco.photoview.photodraweeview.OnPhotoTapListener
import com.mynews.common.core.image.fresco.photoview.photodraweeview.OnViewTapListener
import com.mynews.common.core.image.fresco.photoview.photodraweeview.PhotoDraweeView

class ImageBrowseAdapter(context: Context, private val mItems: ArrayList<PhotoInfo>,
                         private var mOnPhotoTapListener: OnPhotoTapListener?,
                         private var mOnLongClickListener: View.OnLongClickListener?)
    : PagerAdapter() {

    private val widthPixels: Int = EnvDisplayMetrics.WIDTH_PIXELS
    private val heightPixels: Int = EnvDisplayMetrics.HEIGHT_PIXELS
    private val mMaxValue: Long = 10000

    override fun getCount(): Int {
        return mItems.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): View {
        val photoInfo = mItems[position]

        if (isBigImage(photoInfo)) {
            val contentView = createLargePhotoView(container.context, photoInfo)
            container.addView(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            return contentView
        }

        val contentView = createPhotoDraweeView(container.context, photoInfo)
        container.addView(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        return contentView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        if (mItems.size > 0 && position < mItems.size) {
            val photoInfo = mItems[position]
            if (isBigImage(photoInfo)) {
                val imageView = container.findViewById<View>(R.id.photo_view) as SubsamplingScaleImageView
                imageView.recycle()
            } else {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                    evictFromMemoryCache(photoInfo)
                }
            }
        }
        container.removeView(`object` as View)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    private fun createPhotoDraweeView(context: Context, photoInfo: PhotoInfo): View {
        val contentView = View.inflate(context, R.layout.item_iamge_browser_normal_image, null)

        val progressBarView = contentView.findViewById<View>(R.id.progress_view) as LoadingProgressBarView
        progressBarView.progress = 0
        progressBarView.text = String.format(Locale.getDefault(), "%d%%", 0)
        progressBarView.visibility = View.VISIBLE

        val photoDraweeView = contentView.findViewById<View>(R.id.photo_drawee_view) as PhotoDraweeView
        val controller = Fresco.newDraweeControllerBuilder()

        var uri = Uri.parse(photoInfo.originalUrl)
        if (!UriUtil.isNetworkUri(uri)) {
            uri = Uri.Builder()
                    .scheme(UriUtil.LOCAL_FILE_SCHEME)
                    .path(photoInfo.originalUrl)
                    .build()
        }
        controller.setUri(uri)

        val hierarchy = photoDraweeView.hierarchy
        hierarchy.setProgressBarImage(object : ProgressBarDrawable() {
            override fun onLevelChange(level: Int): Boolean {
                val progress = (level.toDouble() / mMaxValue * 100).toInt()
                MLog.i("progress = " + progress)
                progressBarView.progress = progress
                progressBarView.text = String.format(Locale.getDefault(), "%d%%", progress)
                if (progress == 100) {
                    progressBarView.visibility = View.GONE
                }
                return super.onLevelChange(progress)
            }
        })

        controller.oldController = photoDraweeView.controller
        controller.controllerListener = object : BaseControllerListener<ImageInfo>() {
            override fun onFinalImageSet(id: String?, imageInfo: ImageInfo?, animatable: Animatable?) {
                super.onFinalImageSet(id, imageInfo, animatable)
                if (imageInfo == null) {
                    return
                }

                photoDraweeView.update(imageInfo.width, imageInfo.height)

                val layoutParams = photoDraweeView.layoutParams
                if (photoInfo.height > EnvDisplayMetrics.HEIGHT_PIXELS) {
                    layoutParams.height = photoInfo.height
                    layoutParams.width = photoInfo.width
                } else {
                    layoutParams.height = EnvDisplayMetrics.HEIGHT_PIXELS
                    layoutParams.width = EnvDisplayMetrics.WIDTH_PIXELS
                }
                photoDraweeView.layoutParams = layoutParams
            }

            override fun onIntermediateImageSet(id: String?, imageInfo: ImageInfo?) {
                super.onIntermediateImageSet(id, imageInfo)
            }
        }
        photoDraweeView.controller = controller
                .setAutoPlayAnimations(true)
                .build()

        photoDraweeView.onPhotoTapListener = OnPhotoTapListener { view, x, y ->
            if (mOnPhotoTapListener != null) {
                mOnPhotoTapListener!!.onPhotoTap(view, x, y)
            }
        }

        if (mOnLongClickListener != null) {
            photoDraweeView.setOnLongClickListener(mOnLongClickListener)
        }

        photoDraweeView.onViewTapListener = OnViewTapListener { view, x, y ->
            if (mOnPhotoTapListener != null) {
                mOnPhotoTapListener!!.onPhotoTap(view, x, y)
            }
        }
        return contentView
    }

    private fun createLargePhotoView(context: Context, photoInfo: PhotoInfo): View {
        val contentView = View.inflate(context, R.layout.item_iamge_browser_big_image, null)
        val progressBarView = contentView.findViewById<View>(R.id.progress_view) as LoadingProgressBarView
        progressBarView.progress = 0
        progressBarView.text = String.format(Locale.getDefault(), "%d%%", 0)
        progressBarView.visibility = View.VISIBLE

        val imageView = contentView.findViewById<View>(R.id.photo_view) as LargePhotoView
        imageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM)
        imageView.minScale = 1.0f
        imageView.maxScale = 2.0f
        imageView.setOnProgressListener { progress ->
            MLog.i("progress = " + progress)
            progressBarView.progress = progress
            progressBarView.text = String.format(Locale.getDefault(), "%d%%", progress)

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                if (progress == 100) {
                    progressBarView.visibility = View.GONE
                }
            } else {
                if (progress > 98) {
                    progressBarView.visibility = View.GONE
                }
            }
        }

        val gestureDetector = GestureDetector(context,
                object : GestureDetector.SimpleOnGestureListener() {
                    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                        if (imageView.isReady) {
                            val sCoord = imageView.viewToSourceCoord(e.x, e.y)

                            if (mOnPhotoTapListener != null) {
                                mOnPhotoTapListener!!.onPhotoTap(imageView, sCoord.x.toInt().toFloat(), sCoord.y.toInt().toFloat())
                            }
                        } else {
                            MLog.i("onSingleTapConfirmed onError")
                        }
                        return false
                    }

                    override fun onLongPress(e: MotionEvent) {
                        if (imageView.isReady) {
                            val sCoord = imageView.viewToSourceCoord(e.x, e.y)

                            if (mOnLongClickListener != null) {
                                mOnLongClickListener!!.onLongClick(imageView)
                            }
                        } else {
                            MLog.i("onLongPress onError")
                        }
                    }

                    override fun onDoubleTap(e: MotionEvent): Boolean {
                        if (imageView.isReady) {
                            val sCoord = imageView.viewToSourceCoord(e.x, e.y)
                        } else {
                            MLog.i("onDoubleTap onError")
                        }
                        return false
                    }
                })

        imageView.setOnTouchListener { view, motionEvent -> gestureDetector.onTouchEvent(motionEvent) }

        // 加载网络的
        val fileCacheDir = context.cacheDir.absolutePath
        ImageManager.with(imageView)
                .setDiskCacheDir(fileCacheDir)
                .load(photoInfo.originalUrl)

        return contentView
    }

    private fun evictFromMemoryCache(photoInfo: PhotoInfo) {
        if (!TextUtils.isEmpty(photoInfo.originalUrl)) {
            val imagePipeline = Fresco.getImagePipeline()
            val uri = Uri.parse(photoInfo.originalUrl)
            if (imagePipeline.isInBitmapMemoryCache(uri)) {
                imagePipeline.evictFromMemoryCache(uri)
            }
        }
    }

    private fun isBigImage(photoInfo: PhotoInfo): Boolean {
        return false
//        return photoInfo.width > 2 * widthPixels || photoInfo.height > 2 * heightPixels
    }

    fun recycler() {
        mOnPhotoTapListener = null
        mOnLongClickListener = null
    }

}