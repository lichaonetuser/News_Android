package com.mynews.common.core.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import androidx.annotation.LongDef
import android.util.AttributeSet
import android.view.Gravity

import com.mynews.app.news.R
import com.mynews.common.core.widget.helper.CommonPressedEffectHelper

class CoreButton
@JvmOverloads
constructor(context: Context,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = androidx.appcompat.R.attr.buttonStyle)
    : androidx.appcompat.widget.AppCompatButton(context, attrs, defStyleAttr) {

    init {
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.CoreButton)
            initCommonPressedEffect(attrs)
            initCenterDrawable(ta)
            ta.recycle()
        }
    }

    override fun dispatchSetPressed(pressed: Boolean) {
        super.dispatchSetPressed(pressed)
        commonPressedEffectHelper.dispatchSetPressed(pressed)
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        if (mUseCenterDrawable) {
            drawables
                    .filterIsInstance<StateListDrawable>()
                    .forEach {
                        it.state = drawableState
                    }
        }
    }

    /*-----------------------*/
    /* COMMON PRESSED EFFECT */
    /*-----------------------*/

    private lateinit var commonPressedEffectHelper: CommonPressedEffectHelper

    private fun initCommonPressedEffect(attrs: AttributeSet) {
        commonPressedEffectHelper = CommonPressedEffectHelper(this, attrs)
    }


    /*-----------------*/
    /* CENTER DRAWABLE */
    /*-----------------*/

    private var mUseCenterDrawable: Boolean = false
    private lateinit var drawables: Array<Drawable?>
    private lateinit var widths: IntArray
    private lateinit var heights: IntArray

    private fun initCenterDrawable(ta: TypedArray) {
        mUseCenterDrawable = ta.getBoolean(R.styleable.CoreButton_useCenterDrawable, false)

        if (mUseCenterDrawable) {
            drawables = arrayOfNulls(4)
            widths = IntArray(4)
            heights = IntArray(4)

            gravity = Gravity.CENTER

            drawables[0] = ta.getDrawable(R.styleable.CoreButton_centerDrawableLeft)
            drawables[1] = ta.getDrawable(R.styleable.CoreButton_centerDrawableTop)
            drawables[2] = ta.getDrawable(R.styleable.CoreButton_centerDrawableRight)
            drawables[3] = ta.getDrawable(R.styleable.CoreButton_centerDrawableBottom)

            widths[0] = ta.getDimensionPixelSize(R.styleable.CoreButton_centerDrawableLeftWidth, 0)
            widths[1] = ta.getDimensionPixelSize(R.styleable.CoreButton_centerDrawableTopWidth, 0)
            widths[2] = ta.getDimensionPixelSize(R.styleable.CoreButton_centerDrawableRightWidth, 0)
            widths[3] = ta.getDimensionPixelSize(R.styleable.CoreButton_centerDrawableBottomWidth, 0)

            heights[0] = ta.getDimensionPixelSize(R.styleable.CoreButton_centerDrawableLeftHeight, 0)
            heights[1] = ta.getDimensionPixelSize(R.styleable.CoreButton_centerDrawableTopHeight, 0)
            heights[2] = ta.getDimensionPixelSize(R.styleable.CoreButton_centerDrawableRightHeight, 0)
            heights[3] = ta.getDimensionPixelSize(R.styleable.CoreButton_centerDrawableBottomHeight, 0)
        }
    }

    @LongDef(LEFT.toLong(), TOP.toLong(), RIGHT.toLong(), BOTTOM.toLong())
    @Retention(AnnotationRetention.SOURCE)
    annotation class DrawGravity

    fun setCenterDrawable(@DrawGravity gravity: Int, drawable: Drawable, width: Int, height: Int) {
        drawables[gravity] = drawable
        widths[gravity] = width
        heights[gravity] = height
        postInvalidate()
    }

    fun setCenterDrawables(drawables: Array<Drawable?>, widths: IntArray?, heights: IntArray?) {
        if (drawables.size >= 4
                && widths != null && widths.size >= 4
                && heights != null && heights.size >= 4) {
            this.drawables = drawables
            this.widths = widths
            this.heights = heights
            postInvalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (!mUseCenterDrawable) {
            super.onDraw(canvas)
            return
        }

        val drawablePadding = compoundDrawablePadding
        translateText(canvas, drawablePadding)
        super.onDraw(canvas)

        val centerX = ((width + paddingLeft - paddingRight) / 2).toFloat()
        val centerY = ((height + paddingTop - paddingBottom) / 2).toFloat()

        val halfTextWidth = paint.measureText(text.toString()) / 2
        val fontMetrics = paint.fontMetrics
        val halfTextHeight = (fontMetrics.descent - fontMetrics.ascent) / 2

        if (drawables[0] != null) {
            val left = (centerX - drawablePadding.toFloat() - halfTextWidth - widths[0].toFloat()).toInt()
            val top = (centerY - heights[0] / 2).toInt()
            drawables[0]!!.setBounds(
                    left,
                    top,
                    left + widths[0],
                    top + heights[0])
            canvas.save()
            drawables[0]!!.draw(canvas)
            canvas.restore()
        }


        if (drawables[2] != null) {
            val left = (centerX + halfTextWidth + drawablePadding.toFloat()).toInt()
            val top = (centerY - heights[2] / 2).toInt()
            drawables[2]!!.setBounds(
                    left,
                    top,
                    left + widths[2],
                    top + heights[2])
            canvas.save()
            drawables[2]!!.draw(canvas)
            canvas.restore()
        }

        if (drawables[1] != null) {
            val left = (centerX - widths[1] / 2).toInt()
            val bottom = (centerY - halfTextHeight - drawablePadding.toFloat()).toInt()
            drawables[1]!!.setBounds(
                    left,
                    bottom - heights[1],
                    left + widths[1],
                    bottom)
            canvas.save()
            drawables[1]!!.draw(canvas)
            canvas.restore()
        }


        if (drawables[3] != null) {
            val left = (centerX - widths[3] / 2).toInt()
            val top = (centerY + halfTextHeight + drawablePadding.toFloat()).toInt()
            drawables[3]!!.setBounds(
                    left,
                    top,
                    left + widths[3],
                    top + heights[3])
            canvas.save()
            drawables[3]!!.draw(canvas)
            canvas.restore()
        }
    }

    private fun translateText(canvas: Canvas, drawablePadding: Int) {

        var translateWidth = 0
        if (drawables[0] != null && drawables[2] != null) {
            translateWidth = (widths[0] - widths[2]) / 2
        } else if (drawables[0] != null) {
            translateWidth = (widths[0] + drawablePadding) / 2
        } else if (drawables[2] != null) {
            translateWidth = -(widths[2] + drawablePadding) / 2
        }

        var translateHeight = 0
        if (drawables[1] != null && drawables[3] != null) {
            translateHeight = (heights[1] - heights[3]) / 2
        } else if (drawables[1] != null) {
            translateHeight = (heights[1] + drawablePadding) / 2
        } else if (drawables[3] != null) {
            translateHeight = -(heights[3] - drawablePadding) / 2
        }

        canvas.translate(translateWidth.toFloat(), translateHeight.toFloat())
    }

    companion object {
        const val LEFT = 0
        const val TOP = 1
        const val RIGHT = 2
        const val BOTTOM = 3
    }

}
