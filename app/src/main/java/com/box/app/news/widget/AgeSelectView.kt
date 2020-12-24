package com.box.app.news.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Scroller
import com.box.app.news.R
import com.box.app.news.data.DataDictionary
import com.box.common.core.util.ResUtils
import java.util.*
import kotlin.collections.ArrayList

/**
 *
 */
class AgeSelectView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null,
            defStyleAttr: Int = 0)
    : View(context, attrs, defStyleAttr) {

    companion object {
        private val UP_DURATION = 300
    }

    private val rect = Rect()
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var lastY = 0f

    val textSize = 14
    private var dividerLineWidth = 1f
    private val textPadding = 80f
    private var itemHeight = sp2px(textSize) + textPadding
    private val unselectedColor = ResUtils.getColor(R.color.color_4)
    private val selectedColor = ResUtils.getColor(R.color.color_1)
    private val dividerLineColor = ResUtils.getColor(R.color.color_6)

    private var textList = DataDictionary.SelectInfoObject.AGE_STRING_LIST
    private val textHeight = LinkedList<Int>()
    private val textBaseline = LinkedList<Int>()

    private val scroller = Scroller(context)

    init {
        borderPaint.style = Paint.Style.STROKE
        borderPaint.textSize = sp2px(textSize)
        borderPaint.textAlign = Paint.Align.CENTER

        linePaint.style = Paint.Style.STROKE
        linePaint.color = dividerLineColor
        linePaint.strokeWidth = dividerLineWidth

        measureText()
    }

    private fun sp2px(sxValue: Int): Float {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return sxValue * fontScale + 0.5f
    }

    private fun measureText() {
        textBaseline.clear()
        textHeight.clear()
        textList.forEach {
            borderPaint.textSize  = sp2px(textSize)
            borderPaint.getTextBounds(it, 0, it.length, rect)
            textHeight.add(rect.bottom - rect.top)
            textBaseline.add(-(rect.top + rect.bottom) / 2)
        }
    }

    fun setAges(ages:ArrayList<String>?) {
        textList = ages ?: arrayListOf("")
        requestLayout()
    }

    fun getSelectIndex():Int {
        val topLineY = (scrollY.toFloat() + partHeight).toInt()
        val selectedIndex = topLineY / (itemHeight.toInt())
        return when {
            selectedIndex < 0 -> 0
            selectedIndex >= textList.size -> textList.size - 1
            else -> selectedIndex
        }
    }

    private var min = 0f
    private var max = 0f
    private var partHeight = 0f
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        partHeight = (height - itemHeight) / 2
        min = -(height + itemHeight) / 2
        max = itemHeight * textList.size - (height - itemHeight) / 2
        scrollTo(0, -partHeight.toInt())
        super.onLayout(changed, left, top, right, bottom)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        return when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                lastY = event.y
                scroller.forceFinished(true)
//                if (event.y + textPadding / 2 < partHeight && scrollY > min + itemHeight) {
//                    val count = (partHeight - event.y) / itemHeight + 1
//                    scrollBy(0, -(count.toInt() * itemHeight).toInt())
//                } else if (event.y - textPadding / 2 > partHeight + itemHeight && scrollY < max - itemHeight) {
//                    val count = (event.y - partHeight - itemHeight) / itemHeight + 1
//                    scrollBy(0, (count.toInt() * itemHeight).toInt())
//                }
                true
            }
            MotionEvent.ACTION_MOVE -> {
                val move = (lastY - event.y)
                scroller.forceFinished(true)
                scrollBy(0, move.toInt())
                lastY = event.y
                invalidate()
                true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (scrollY <= min + itemHeight) {
                    scroller.startScroll(0, scrollY, 0, (min + itemHeight).toInt() - scrollY, UP_DURATION)
                } else if (scrollY >= max - itemHeight) {
                    scroller.startScroll(0, scrollY, 0, (max - itemHeight).toInt() - scrollY, UP_DURATION)
                } else {
                    val topLineY = scrollY.toFloat() + partHeight
                    val delta = topLineY % itemHeight
                    if (delta * 2 > itemHeight) {
                        scroller.startScroll(0, scrollY, 0, (itemHeight - delta).toInt(), UP_DURATION)
                    } else {
                        scroller.startScroll(0, scrollY, 0, (-delta).toInt(), UP_DURATION)
                    }
                }
                postInvalidate()
//                when {
//                    scrollY <= min + itemHeight -> scroller.startScroll(0, scrollY, 0, (min + itemHeight).toInt() - scrollY, UP_DURATION)
//                    scrollY >= max - itemHeight -> scroller.startScroll(0, scrollY, 0, (max - itemHeight).toInt() - scrollY, UP_DURATION)
//                    else -> {
//                        val topLineY = scrollY.toFloat() + partHeight
//                        val delta = topLineY % itemHeight
//                        if (delta * 2 > itemHeight) {
//                            scroller.startScroll(0, scrollY, 0, (itemHeight - delta).toInt(), UP_DURATION)
//                        } else {
//                            scroller.startScroll(0, scrollY, 0, (-delta).toInt(), UP_DURATION)
//                        }
//                    }
//                }
                true
            }
            else -> super.onTouchEvent(event)
        }
    }

    override fun computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(0, scroller.currY)
            postInvalidate()
        }
        super.computeScroll()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) {
            return
        }

        val topLineY = scrollY.toFloat() + height / 2 - itemHeight / 2
        val bottomLineY = scrollY.toFloat() + height / 2 + itemHeight / 2

        val itemCountsInPart = (partHeight / itemHeight).toInt() + 1

        val selectedIndex = (topLineY / itemHeight).toInt()

        canvas.save()
        canvas.clipRect(0f, topLineY - partHeight, width.toFloat(), topLineY)
        repeat(itemCountsInPart + 1, {
            val i = selectedIndex - it
            if (i >=0 && i < textList.size) {
                borderPaint.textSize = sp2px(textSize)
                borderPaint.color = unselectedColor
                canvas.drawText(textList[i], (width / 2).toFloat(), itemHeight * i + itemHeight / 2 + textBaseline[i], borderPaint)
            }
        })
        canvas.restore()

        canvas.save()
        canvas.clipRect(0f, topLineY, width.toFloat(), bottomLineY)
        repeat(2, {
            val i = selectedIndex + it
            if (i >=0 && i < textList.size) {
                borderPaint.textSize = sp2px(textSize) * 1.2f
                borderPaint.color = selectedColor
                canvas.drawText(textList[i], (width / 2).toFloat(), itemHeight * i + itemHeight / 2 + textBaseline[i] * 1.2f, borderPaint)
            }
        })
        canvas.restore()

        canvas.save()
        canvas.clipRect(0f, bottomLineY, width.toFloat(), bottomLineY + partHeight)
        repeat(itemCountsInPart + 2, {
            val i = selectedIndex + it
            if (i >=0 && i < textList.size) {
                borderPaint.textSize = sp2px(textSize)
                borderPaint.color = unselectedColor
                canvas.drawText(textList[i], (width / 2).toFloat(), itemHeight * i + itemHeight / 2 + textBaseline[i], borderPaint)
            }
        })
        canvas.restore()

        canvas.drawLine(0f, topLineY , width.toFloat(), topLineY, linePaint)
        canvas.drawLine(0f, bottomLineY, width.toFloat(), bottomLineY, linePaint)
    }
}
