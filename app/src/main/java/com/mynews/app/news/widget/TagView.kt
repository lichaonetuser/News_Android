package com.mynews.app.news.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.bean.Tag
import java.util.*

/**
 * 文章显示标签的自定义view
 *
 * 使用 fun setTags(tags: List<Tag>) 方法设置标签
 */
@Suppress("unused")
class TagView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null,
            defStyleAttr: Int = 0)
    : View(context, attrs, defStyleAttr) {

    companion object {
        private val DEFAULT_OUT_HOR_SPACE_DP = 2
        private val DEFAULT_IN_VER_SPACE_DP = 1
        private val DEFAULT_IN_HOR_SPACE_DP = 1
        private val DEFAULT_BORDER_THINNESS_PX = 2
        private val DEFAULT_BORDER_RADIUS_DP = 2
        private val DEFAULT_TEXT_SIZE_SP = 8
    }

    private val nameLeft = LinkedList<Int>()
    private val nameWidth = LinkedList<Int>()
    private val nameBaselineDelta = LinkedList<Int>()

    private val rect = Rect()
    private val rectf = RectF()
    private val borderPath = Path()
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var maxTextHeight = 0
    private var totalHeight = 0
    private var totalWidth = 0


    private var tags: List<Tag> = listOf()
    /**
     * 自己做一遍类型转换防止类型转换异常
     */
    fun setTags(tags: List<Any>?) {
        val temp = LinkedList<Tag>()
        if (tags != null && tags.isNotEmpty()) {
            var tag:Map<String, Any>
            tags.forEach {
                @Suppress("UNCHECKED_CAST")
                try {
                    temp.add(it as Tag)
                } catch (e: ClassCastException) {
                    tag = it as Map<String, Any>
                    temp.add(Tag(color = tag["color"].toString(), name = tag["name"].toString()))
                } catch (e:Exception) {

                }
            }
        }
        this.tags = temp
        requestLayout()
    }

    private var roundRadius = dpToPx(DEFAULT_BORDER_RADIUS_DP)
        set(value) {
            field = if (value < 0) {
                0
            } else {
                dpToPx(value)
            }
            requestLayout()
        }

    private var borderWidth = DEFAULT_BORDER_THINNESS_PX
        set(value) {
            field = if (value < 0) {
                0
            } else {
                value
            }
            requestLayout()
        }

    private var inHorSpace = dpToPx(DEFAULT_IN_HOR_SPACE_DP)
        set(value) {
            field = if (value < 0) {
                0
            } else {
                dpToPx(value)
            }
            requestLayout()
        }

    private var outHorSpace = dpToPx(DEFAULT_OUT_HOR_SPACE_DP)
        set(value) {
            field = if (value < 0) {
                0
            } else {
                dpToPx(value)
            }
            requestLayout()
        }

    private var inVerSpace = dpToPx(DEFAULT_IN_VER_SPACE_DP)
        set(value) {
            field = if (value < 0) {
                0
            } else {
                dpToPx(value)
            }
            requestLayout()
        }

    private var textSize = DEFAULT_TEXT_SIZE_SP
        set(value) {
            if (value < 0) {
                field = 0
                textPaint.textSize = 0F
            } else {
                field = value
                textPaint.textSize = spToPx(field).toFloat()
            }
            requestLayout()
        }

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.TagView)
        initFromAttr(ta)
        ta.recycle()

        borderPaint.style = Paint.Style.STROKE
        textPaint.style = Paint.Style.STROKE

        textPaint.textSize = if (textSize == 0) 0F else spToPx(textSize).toFloat()
        borderPaint.strokeWidth = borderWidth.toFloat()
    }

    private fun initFromAttr(ta: TypedArray) {
        outHorSpace = ta.getDimensionPixelSize(R.styleable.TagView_out_hor_space, dpToPx(DEFAULT_OUT_HOR_SPACE_DP))
        inVerSpace = ta.getDimensionPixelSize(R.styleable.TagView_in_ver_space, dpToPx(DEFAULT_IN_VER_SPACE_DP))
        inHorSpace = ta.getDimensionPixelSize(R.styleable.TagView_in_hor_space, dpToPx(DEFAULT_IN_HOR_SPACE_DP))
        borderWidth = ta.getInt(R.styleable.TagView_border_thinness, DEFAULT_BORDER_THINNESS_PX)
        roundRadius = ta.getDimensionPixelSize(R.styleable.TagView_border_radius, dpToPx(DEFAULT_BORDER_RADIUS_DP))
        textSize = ta.getInt(R.styleable.TagView_name_text_size, DEFAULT_TEXT_SIZE_SP)
    }

    private fun dpToPx(value: Int): Int {
//        return CoreApp.getInstance().dip(value)
        return value * 2
    }

    private fun spToPx(value: Int): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (value * fontScale + 0.5f).toInt()
    }

    fun getRealViewWidth(): Int {
        wholeMeasure()
        return totalWidth
    }

    fun getRealViewHeight(): Int {
        wholeMeasure()
        return totalHeight
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        wholeMeasure()
        val viewHeight = if (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED) {
            totalHeight
        } else {
            heightSize
        }
        val viewWidth = if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED) {
            totalWidth
        } else {
            widthSize
        }
        setMeasuredDimension(viewWidth, viewHeight)
    }

    private fun clear() {
        maxTextHeight = 0
        totalWidth = paddingLeft
        totalHeight = paddingTop + paddingBottom
        nameLeft.clear()
        nameBaselineDelta.clear()
        nameWidth.clear()
    }

    private fun wholeMeasure() {
        clear()
        if (tags.isEmpty()) {
            totalWidth = 0
            totalHeight = 0
            return
        }
        repeat(tags.size, {
            measureName(tags[it].name)
            val width = rect.right - rect.left
            val height = rect.bottom - rect.top
            if (height > maxTextHeight) {
                maxTextHeight = height
            }
            val left = rect.left
            nameLeft.add(totalWidth + borderWidth + inHorSpace - left)
            nameWidth.add(width)
            nameBaselineDelta.add(-rect.top - (rect.bottom - rect.top) / 2)
            totalWidth += 2 * (inHorSpace + borderWidth) + outHorSpace + width
        })
        totalWidth += paddingRight - outHorSpace
        totalHeight = 2 * (inVerSpace + borderWidth) + maxTextHeight + paddingTop + paddingBottom
    }

    private fun measureName(name: String) {
        textPaint.getTextBounds(name, 0, name.length, rect)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) {
            return
        }
        if (tags.isEmpty()) {
            return
        }
        val verCenterLine = paddingTop + borderWidth + inVerSpace + maxTextHeight / 2
        var curLeft = paddingLeft
        val borderTop = paddingTop + borderWidth / 2
        val borderBottom = paddingTop + 2 * (inVerSpace + borderWidth) + maxTextHeight - borderWidth / 2
        repeat(tags.size, {
            try {
                borderPaint.color = Color.parseColor(tags[it].color)
                textPaint.color = Color.parseColor(tags[it].color)
            } catch (e: Exception) {
                borderPaint.color = Color.BLACK
                textPaint.color = Color.BLACK
            }
            canvas.drawText(tags[it].name,
                    nameLeft[it].toFloat(),
                    (verCenterLine + nameBaselineDelta[it]).toFloat(),
                    textPaint)
            val borderLeft = curLeft + borderWidth / 2
            val borderRight = curLeft + 2 * (inHorSpace + borderWidth) + nameWidth[it] - borderWidth / 2
            rectf.set(borderLeft.toFloat(), borderTop.toFloat(), borderRight.toFloat(), borderBottom.toFloat())
            borderPath.reset()
            borderPath.addRoundRect(rectf, roundRadius.toFloat() * 3 / 4, roundRadius.toFloat() * 3 / 4, Path.Direction.CCW)
            canvas.drawPath(borderPath, borderPaint)
            curLeft += 2 * (borderWidth + inHorSpace) + nameWidth[it] + outHorSpace
        })
    }
}