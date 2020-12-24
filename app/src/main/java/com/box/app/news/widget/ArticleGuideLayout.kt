package com.box.app.news.widget

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator.INFINITE
import android.animation.ValueAnimator.RESTART
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import com.box.app.news.R
import com.box.app.news.data.source.local.preference.PreferenceAPI
import com.box.common.core.anim.AnimatorProperty
import com.box.common.core.widget.CoreRelativeLayout
import kotlinx.android.synthetic.main.layout_article_guide.view.*
import org.jetbrains.anko.displayMetrics

class ArticleGuideLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : CoreRelativeLayout(context, attrs, defStyleAttr) {

    val mContext = context
    val dpValue = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80F, context.displayMetrics)

    var mTranslateYAnimation: ObjectAnimator? = null
    var mAlphaInAnimation: ObjectAnimator? = null
    var mAlphaOutAnimation: ObjectAnimator? = null

    var mTranslateDuration = 2000L
    var mAlphaDuration = 400L
    var mDelayTime = mTranslateDuration - mAlphaDuration

    init {
        visibility = View.GONE
    }

    fun init() {
        LayoutInflater.from(mContext).inflate(R.layout.layout_article_guide, this)
        mTranslateYAnimation = initTranslateYAnimation()
        mAlphaInAnimation = initAlphaAnimation(0F, 1F)
        mAlphaOutAnimation = initAlphaAnimation(1F, 0F)
        visibility = View.VISIBLE

        play()
    }

    private fun initTranslateYAnimation() : ObjectAnimator {
        val anim = ObjectAnimator.ofFloat(article_guide_pointer_img,
                AnimatorProperty.TRANSLATION_Y,
                0F,
                dpValue)
        anim.duration = mTranslateDuration
        anim.repeatMode = RESTART
        anim.repeatCount = INFINITE
        return anim
    }

    private fun initAlphaAnimation(startAlpha : Float, endAlpha : Float) : ObjectAnimator {
        val anim = ObjectAnimator.ofFloat(article_guide_pointer_img, AnimatorProperty.ALPHA, startAlpha, endAlpha)
        anim.duration = mAlphaDuration
        return anim
    }

    private fun play() {
        mTranslateYAnimation?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {
                delayPlayAlphaAnimation()
            }

            override fun onAnimationEnd(p0: Animator?) {
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationStart(p0: Animator?) {
            }

        })
        mTranslateYAnimation?.start()
    }

    private fun delayPlayAlphaAnimation() {
        mAlphaInAnimation?.start()
        mAlphaOutAnimation?.startDelay = mDelayTime
        mAlphaOutAnimation?.start()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                clearAnimation(mTranslateYAnimation)
                clearAnimation(mAlphaInAnimation)
                clearAnimation(mAlphaOutAnimation)
                article_guide_pointer_img.clearAnimation()
                visibility = View.GONE
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun clearAnimation(animator : ObjectAnimator?) {
        animator?.removeAllListeners()
        animator?.end()
        animator?.cancel()
    }
}