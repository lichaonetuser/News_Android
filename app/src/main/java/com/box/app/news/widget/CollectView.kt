package com.box.app.news.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.*
import android.widget.ImageView
import android.widget.RelativeLayout
import com.box.app.news.R
import org.jetbrains.anko.dip

class CollectView: RelativeLayout {
    private var mStarView: ImageView? = null
    private var mBlinkView: BlinkView? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mStarView = ImageView(context)
        mStarView?.setImageResource(R.drawable.user_collect_btn)

        addView(mStarView, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))

        mBlinkView = BlinkView(context)
        addView(mBlinkView, LayoutParams(dip(34), dip(34)))
    }

    fun collect() {
        mStarView?.isActivated = true
        startAnimation()
    }

    fun unCollect() {
        mStarView?.isActivated = false
    }

    private fun startAnimation() {
        val rotateAni = RotateAnimation(0f, 145f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f)
        rotateAni.duration = 450
        rotateAni.interpolator = LinearInterpolator()

        val scaleAni = ScaleAnimation(1f, 1.3f, 1f, 1.3f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f)
        scaleAni.duration = 450

        val set = AnimationSet(true)
        set.addAnimation(rotateAni)
        set.addAnimation(scaleAni)

        set.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationStart(animation: Animation?) {
//                mBlinkView?.startBlink()
            }

            override fun onAnimationEnd(animation: Animation?) {
//                mBlinkView?.stopBlink()
            }

        })
        mBlinkView?.postDelayed({
            mBlinkView?.playFirst()
        }, 200)
        mBlinkView?.postDelayed({
            mBlinkView?.playSecond()
        }, 450)
        mBlinkView?.postDelayed({
            mBlinkView?.stop()
        }, 700)
        mStarView?.startAnimation(set)
    }

    class BlinkView(context: Context) : ImageView(context) {
        val paint = Paint()
        init {
            paint.color = resources.getColor(R.color.color_21)
            paint.strokeJoin = Paint.Join.ROUND
            paint.strokeCap = Paint.Cap.ROUND
            paint.strokeWidth = 3f
        }

        companion object {
            const val MIN_SIZE = 3f
            const val MAX_SIZE = 12f
        }

        private var dotSize = 3f
        private var dotAlpha = 0

        private var pos = dip(8).toFloat()

        private var playFirst = false
        private var playSecond = false

        fun playFirst() {
            dotSize = MAX_SIZE
            dotAlpha = 0
            playFirst = true
            playSecond = false
            visibility = View.VISIBLE

            invalidate()
        }

        fun stop() {
            playFirst = false
            playSecond = false
            visibility = View.GONE
        }

        fun playSecond() {
            dotSize = MIN_SIZE
            dotAlpha = 255
            playFirst = false
            playSecond = true

            invalidate()
        }

        override fun onDraw(canvas: Canvas?) {
            super.onDraw(canvas)
            if (playFirst) {
                paint.alpha = dotAlpha

                canvas?.drawCircle(pos , pos, dotSize, paint)

                canvas?.drawCircle(width - pos , pos, dotSize, paint)

                canvas?.drawCircle(pos , height - pos, dotSize, paint)

                canvas?.drawCircle(width - pos , height - pos, dotSize, paint)

                dotSize--
                dotAlpha += 20

                if (dotSize < MIN_SIZE) {
                    dotSize = MIN_SIZE
                }
                if (dotAlpha > 255) {
                    dotAlpha = 255
                }
                invalidate()
            }
            if (playSecond) {
                paint.alpha = dotAlpha

                canvas?.drawCircle(pos , pos, dotSize, paint)

                canvas?.drawCircle(width - pos , pos, dotSize, paint)

                canvas?.drawCircle(pos , height - pos, dotSize, paint)

                canvas?.drawCircle(width - pos , height - pos, dotSize, paint)

                dotAlpha -= 20

                if (dotAlpha < 0) {
                    dotAlpha = 0
                }
                invalidate()
            }
        }
    }
}