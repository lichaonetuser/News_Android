package com.box.app.news.widget

import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.box.app.news.R
import com.scwang.smartrefresh.layout.api.RefreshHeader
import com.scwang.smartrefresh.layout.api.RefreshKernel
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.constant.RefreshState
import com.scwang.smartrefresh.layout.constant.SpinnerStyle
import org.jetbrains.anko.dip

class ArticleRefreshHeader: LinearLayout, RefreshHeader {
    private var imageView: ImageView? = null
    private var pullAnimRes: IntArray? = null
    private var releaseAnimRes: IntArray? = null
    private var mCurrentArray: IntArray? = null
    private var mCurrentFrame: Int = 0
    private var mInterval = 60L
    private var isPlayOnce = false

    override fun getSpinnerStyle(): SpinnerStyle {
        return SpinnerStyle.Translate
    }

    override fun onFinish(layout: RefreshLayout?, success: Boolean): Int {
        stop()
        return 0
    }

    override fun onInitialized(kernel: RefreshKernel?, height: Int, extendHeight: Int) {
    }

    override fun onHorizontalDrag(percentX: Float, offsetX: Int, offsetMax: Int) {
    }

    override fun getView(): View {
        return this
    }

    override fun setPrimaryColors(vararg colors: Int) {
    }

    override fun onReleasing(percent: Float, offset: Int, headerHeight: Int, extendHeight: Int) {
    }

    override fun onStartAnimator(layout: RefreshLayout?, height: Int, extendHeight: Int) {
        releaseAnimRes?.run {
            playLoop(this, 0)
        }
    }

    override fun onStateChanged(refreshLayout: RefreshLayout?, oldState: RefreshState?, newState: RefreshState?) {
//        if (oldState == RefreshState.None && newState == RefreshState.PullDownToRefresh) {
//            pullAnimRes?.run {
//                playOnce(this, 0)
//            }
//        }
        if (newState == RefreshState.PullDownCanceled || newState == RefreshState.None) {
            stop()
        }
    }

    override fun onPullingDown(percent: Float, offset: Int, headerHeight: Int, extendHeight: Int) {
        pullAnimRes?.run {
            val level = (percent * size).toInt()
            if (level >= 0 && level < this.size) {
                imageView?.setBackgroundResource(this[level])
            }
        }
    }

    override fun isSupportHorizontalDrag(): Boolean {
        return false
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    private fun initView() {
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                .apply {
                    orientation = VERTICAL
                    gravity = Gravity.CENTER_HORIZONTAL
                    topMargin = dip(10)
                    bottomMargin = dip(10)
                }
        imageView = ImageView(context)
                .apply {
                    layoutParams = params
                    setBackgroundResource(R.drawable.article_pull_0)
                }
        addView(imageView)

        pullAnimRes = getAnimRes(R.array.frame_anim_news_refresh_pull)
        releaseAnimRes = getAnimRes(R.array.frame_anim_news_refresh_release)
    }

    private fun getAnimRes(arrayResId: Int): IntArray {
        val typedArray = resources.obtainTypedArray(arrayResId)
        val len = typedArray.length()
        val resId = IntArray(len)
        for (i in 0 until len) {
            resId[i] = typedArray.getResourceId(i, -1)
        }
        typedArray.recycle()
        return resId
    }

    private fun play(array: IntArray, startFrame: Int) {
        mHandler.removeMessages(PLAY_ANIMATION)
        mCurrentArray = array
        mCurrentFrame = startFrame
        mInterval = FRAME_ANIM_DURATION / array.size
        mHandler.sendEmptyMessageDelayed(PLAY_ANIMATION, mInterval)
    }

    private fun playOnce(array: IntArray, startFrame: Int) {
        isPlayOnce = true
        play(array, startFrame)
    }

    private fun playLoop(array: IntArray, startFrame: Int) {
        isPlayOnce = false
        play(array, startFrame)
    }

    private val mHandler = object : Handler(){
        override fun handleMessage(msg: Message?) {
            when(msg?.what) {
                PLAY_ANIMATION -> {
                    removeMessages(PLAY_ANIMATION)
                    mCurrentArray?.run {
                        if (mCurrentFrame >= this.size) {
                            if (isPlayOnce) {
                                return
                            } else {
                                mCurrentFrame = 0
                            }
                        }
                        setImageBg()
                        sendEmptyMessageDelayed(PLAY_ANIMATION, mInterval)
                    }
                }
            }
        }
    }

    fun setImageBg() {
        mCurrentArray?.run {
            if (mCurrentFrame >= 0 && mCurrentFrame < this.size) {
                imageView?.setBackgroundResource(this[mCurrentFrame])
            }
            mCurrentFrame++
        }
    }

    private fun stop() {
        mHandler.removeMessages(PLAY_ANIMATION)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mHandler.removeCallbacksAndMessages(null)
    }

    companion object {
        const val FRAME_ANIM_DURATION = 1300L
        const val PLAY_ANIMATION = 1001
    }

}