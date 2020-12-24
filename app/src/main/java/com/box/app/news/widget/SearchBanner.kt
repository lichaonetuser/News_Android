package com.box.app.news.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.box.app.news.R

/**
 *
 */
class SearchBanner
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null,
            defStyleAttr: Int = 0)
    : View(context, attrs, defStyleAttr) {

    companion object {
        private val SCROLL_DURATION = 300L
    }

    private val animator: ValueAnimator = ValueAnimator()
    private var isPlaying = false
    private var textX = 0F
    private var lastBaseline = 0F
    private var currentBaseline = 0F
    private var delta = 0F
    private var textImgPadding = dp2px(7)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rect = Rect()
    private val rectf = RectF()


    private val textColor = Color.GRAY
    private val textSize = 13
    private val imgSize = sp2px(textSize)


    private var lastText = ""
    private var currentText = ""
    private var imageId = R.drawable.main_titlebar_search_icon
    private var bitmap: Bitmap? = null

    private fun sp2px(sxValue: Int): Float {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return sxValue * fontScale + 0.5f
    }

    private fun dp2px(dpValue: Int):Float {
        val density = context.resources.displayMetrics.density
        return dpValue * density + 0.5f
    }

    init {
        paint.color = textColor
        paint.textSize = sp2px(textSize)
        bitmap = BitmapFactory.decodeResource(context.resources, imageId)
        rectf.set(0f, 0f, imgSize, imgSize)
    }

    fun setDefaultText(text: String?) {
        val s = text ?: ""
        currentText = s
        measureText(s)
        invalidate()
    }

    fun nextText(text: String?) {
        if (isPlaying) {
            return
        }
        isPlaying = true
        val s = text ?: ""
        lastText = currentText
        currentText = s
        measureText(s)
        playAnimation()
    }

    private fun playAnimation() {
        animator.setIntValues(height, -1)
        animator.duration = SCROLL_DURATION
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener {
            delta = (it.animatedValue as? Int ?: 0).toFloat()
            if (delta == -1F) {
                isPlaying = false
            } else {
                invalidate()
            }
        }
        animator.start()
    }

    private fun measureText(text: String) {
        paint.getTextBounds(text, 0, text.length, rect)
        lastBaseline = currentBaseline
        currentBaseline = -(rect.top.toFloat() + rect.bottom.toFloat()) / 2
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) {
            return
        }
        if (bitmap != null) {
            rectf.set(paddingLeft.toFloat(),
                    height / 2 - sp2px(textSize) * 0.65f,
                    paddingLeft + sp2px(textSize) * 1.3F,
                    height / 2 + sp2px(textSize) * 0.65f)
            canvas.drawBitmap(bitmap, null, rectf, paint)
        }
        textX = paddingLeft + sp2px(textSize) + textImgPadding
        canvas.drawText(lastText, textX, lastBaseline + delta - height / 2, paint)
        canvas.drawText(currentText, textX, currentBaseline + delta + height / 2, paint)
    }
}
