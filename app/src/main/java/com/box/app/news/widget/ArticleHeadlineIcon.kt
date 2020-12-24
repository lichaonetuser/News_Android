package com.box.app.news.widget

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.*
import com.box.app.news.R
import com.box.common.core.anim.AnimatorProperty
import com.box.common.core.log.Logger
import com.box.common.core.rx.schedulers.io
import com.box.common.core.rx.schedulers.ioToMain
import com.box.common.core.rx.schedulers.obOnMain
import com.box.common.core.util.ResUtils
import com.box.common.core.widget.CoreRelativeLayout
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.layout_breaking_news_logo.view.*
import java.util.*

class ArticleHeadlineIcon @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : CoreRelativeLayout(context, attrs, defStyleAttr) {

    val mContext = context
    // 旋转动画持续时间
    private val mRotateTime = 1050L
    // 颜色切换时间
    private val mColorSwitchTime = 20L
    // 数字扩大时间
    private val mNumberEnlargeTime = 40L
    // 数字缩小时间
    private val mNumberShrinkTime = 440L
    // 旋转后延时时间
    private val mDelay = 40L

    // 外侧图标旋转动画
    var mRotateAnimation: Animation? = null
    // 颜色由黑色渐变为黄色
    var mColorSwitchOnAnimation: ValueAnimator? = null
    // 颜色由黄色渐变为黑色
    var mColorSwitchOffAnimation: ValueAnimator? = null
    // 内侧图标放大
    var mEnlargeXAnimation: ObjectAnimator? = null
    var mEnlargeYAnimation: ObjectAnimator? = null
    // 内侧图标缩小
    var mShrinkXAnimation: ObjectAnimator? = null
    var mShrinkYAnimation: ObjectAnimator? = null

    // 当前动画是否可以播放
    var isAnimationAvailable: Boolean = false

    var isInit: Boolean = false

    var playTimes = 0

    init {
        LayoutInflater.from(mContext).inflate(R.layout.layout_breaking_news_logo, this)
    }

    fun play() {
        Completable.fromCallable {
            if (!isInit) {
                mRotateAnimation = initRotateAnimation()
                mColorSwitchOnAnimation = initColorSwitchAnimation(startValue = 0F, endValue = 1F)
                mColorSwitchOffAnimation = initColorSwitchAnimation(startValue = 1F, endValue = 0F)
                mEnlargeXAnimation = initEnlargeAnimation(AnimatorProperty.SCALE_X)
                mEnlargeYAnimation = initEnlargeAnimation(AnimatorProperty.SCALE_Y)
                mShrinkXAnimation = initShrinkAnimation(AnimatorProperty.SCALE_X)
                mShrinkYAnimation = initShrinkAnimation(AnimatorProperty.SCALE_Y)
            }
        }
                .io()
                .obOnMain()
                .subscribeBy(
                        onComplete = {
                            playTimes = 0
                            isAnimationAvailable = true
                            isInit = true
                            startPlay()
                        },
                        onError = {
                            Logger.d(it)
                        }
                )
    }

    private fun initRotateAnimation(): Animation {
        val anim = AnimationUtils.loadAnimation(mContext, R.anim.core_rotate_clockwise)
        anim?.duration = mRotateTime
        anim?.interpolator = DecelerateInterpolator()
        return anim
    }

    private fun initColorSwitchAnimation(startValue: Float, endValue: Float): ValueAnimator {
        val baseColor = "fc8c1f"
        val anim = ValueAnimator.ofFloat(startValue, endValue)
        anim?.addUpdateListener {
            val value = it.animatedValue as Float
            var result = (value * 255).toInt().toString(16)
            if (result.length < 2) {
                result = "0" + result
            }
            article_breaking_news_out_icon_img.setColorFilter(Color.parseColor("#" + result + baseColor))
            article_breaking_news_inner_icon_img.setColorFilter(Color.parseColor("#" + result + baseColor))
        }
        anim?.duration = mColorSwitchTime
        return anim
    }

    private fun initEnlargeAnimation(property: String): ObjectAnimator {
        val anim = ObjectAnimator.ofFloat(article_breaking_news_inner_icon_img, property, 1F, 1.2F)
        anim.duration = mNumberEnlargeTime
        return anim
    }

    private fun initShrinkAnimation(property: String): ObjectAnimator {
        val anim = ObjectAnimator.ofFloat(article_breaking_news_inner_icon_img, property, 1.2F, 1F)
        anim.duration = mNumberShrinkTime
        return anim
    }

    private fun startPlay() {
        if (!isAnimationAvailable || !isInit) {
            return
        }

        val set = AnimatorSet()
        set.play(mColorSwitchOnAnimation)
                .with(mEnlargeXAnimation)
                .with(mEnlargeYAnimation)
        set.play(mShrinkXAnimation)
                .with(mShrinkYAnimation)
                .after(mEnlargeXAnimation)
        set.play(mColorSwitchOffAnimation)
                .after(mShrinkXAnimation)
        set.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {

            }

            override fun onAnimationEnd(p0: Animator?) {
                isAnimationAvailable = true
                if (playTimes < 2) {
                    startPlay()
                }
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationStart(p0: Animator?) {
                playTimes++
            }
        })

        mRotateAnimation?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {
            }

            override fun onAnimationStart(p0: Animation?) {
                isAnimationAvailable = false
            }

            override fun onAnimationEnd(p0: Animation?) {
                set.startDelay = mDelay
                set.start()
            }
        })

        article_breaking_news_out_icon_img.startAnimation(mRotateAnimation)
    }
}