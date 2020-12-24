package com.box.app.news.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import androidx.annotation.ColorInt;
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.box.app.news.R
import com.box.common.core.util.ResUtils

@Suppress("MemberVisibilityCanPrivate")
/**
 * 替换原来的字体大小的SeekBar
 * 继承View，可改变的属性申明在 com.box.app.news.widget.FontSeekBar 中
 */
class FontSeekBar
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null,
            defStyleAttr: Int = 0)
    : View(context, attrs, defStyleAttr) {

    companion object {
        val DEFAULT_SLIDER_RADIUS = 32
        val DEFAULT_SLIDER_LINE_THINNESS = 2
        val DEFAULT_SLIDER_LINE_COLOR = ResUtils.getColor(R.color.color_4)
        val DEFAULT_SLIDER_INNER_COLOR = Color.WHITE
        val DEFAULT_HORI_LINE_THINNESS = 3
        val DEFAULT_VERT_LINE_HEIGHT = 35
        val DEFAULT_VERT_LINE_THINNESS = 6
        val DEFAULT_LINE_COLOR = ResUtils.getColor(R.color.color_4)
        val DEFAULT_TEXT_SIZE = 45
        val DEFAULT_TEXT_COLOR = ResUtils.getColor(R.color.color_1)
        val DEFAULT_TEXT_IS_BOLD = false
    }

    var sliderRadius = DEFAULT_SLIDER_RADIUS  //滑块半径
        set(value) {
            if (value < 20) {
                field = 20
            }
            field = value
        }
    var sliderLineThinness = DEFAULT_SLIDER_LINE_THINNESS //滑块外面线的粗细
        set(value) {
            if (value < 1) {
                field = 1
            }
            field = value
        }
    var horiLineThinness = DEFAULT_HORI_LINE_THINNESS  //横线的粗细
        set(value) {
            if (value < 1) {
                field = 1
            }
            field = value
        }
    var vertLineHeight = DEFAULT_VERT_LINE_HEIGHT  //竖线的高度
        set(value) {
            if (value < 1) {
                field = 1
            }
            field = value
        }
    var vertLineThinness = DEFAULT_VERT_LINE_THINNESS  //竖线的粗细
        set(value) {
            if (value < 1) {
                field = 1
            }
            field = value
        }
    var textSize = DEFAULT_TEXT_SIZE  //字体大小
        set(value) {
            if (value < 1) {
                field = 1
            }
            field = value
        }
    @ColorInt var sliderLineColor = DEFAULT_SLIDER_LINE_COLOR  //滑块外面线的颜色
    @ColorInt var sliderInnerColor = DEFAULT_SLIDER_INNER_COLOR  //滑块外面线的颜色
    @ColorInt var lineColor = DEFAULT_LINE_COLOR  //线的颜色
    @ColorInt var textColor = DEFAULT_TEXT_COLOR  //字体颜色
    var isTextBold = DEFAULT_TEXT_IS_BOLD  //字体是否加粗

    private var textHorLinePadding = 30 //字与横线之间的间距
    private var textBaseline = 0  //字的baseline
    private var range = 0 //变化范围的个数

    private var lineY = 0
    private var textHeight = 0
    var index = 0
        set(value) {
            field = if (value < 0) 0 else if (value >= textArray.size) textArray.size - 1 else value
            invalidate()
        }
    private var segmentWidth = 4 * sliderRadius //默认每段长度等于四个滑块半径那么长

    var textArray = arrayListOf("S", "M", "L")
        set(value) {
            if (value.size <= 2) {
                return
            }
            field = value
        }
    private var action: ((Int) -> Unit)? = null

    private val sliderPaint: Paint
    private val horiLinePaint: Paint
    private val vertLinePaint: Paint
    private val textPaint: Paint
    private val horiLinePath: Path
    private val vertLinePath: Path

    private val rect = Rect()

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.FontSeekBar)
        initByAttrs(ta)
        ta.recycle()

        range = textArray.size

        horiLinePath = Path()
        vertLinePath = Path()
        sliderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        horiLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        vertLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

        sliderPaint.color = sliderLineColor
        sliderPaint.style = Paint.Style.STROKE
        sliderPaint.strokeWidth = sliderLineThinness.toFloat()

        horiLinePaint.color = lineColor
        vertLinePaint.color = lineColor
        horiLinePaint.style = Paint.Style.STROKE
        vertLinePaint.style = Paint.Style.STROKE
        horiLinePaint.strokeWidth = horiLineThinness.toFloat()
        vertLinePaint.strokeWidth = vertLineThinness.toFloat()

        textPaint.textSize = textSize.toFloat()
        textPaint.color = textColor

        textPaint.textAlign = Paint.Align.CENTER
    }

    private fun initByAttrs(ta: TypedArray) {
        lineColor = ta.getColor(R.styleable.FontSeekBar_line_color, DEFAULT_LINE_COLOR)
        sliderLineColor = ta.getColor(R.styleable.FontSeekBar_slider_line_color, DEFAULT_SLIDER_LINE_COLOR)
        sliderInnerColor = ta.getColor(R.styleable.FontSeekBar_slider_inner_color, DEFAULT_SLIDER_INNER_COLOR)
        textColor = ta.getColor(R.styleable.FontSeekBar_slider_text_color, DEFAULT_TEXT_COLOR)
        vertLineThinness = ta.getDimensionPixelSize(R.styleable.FontSeekBar_vertical_line_thinness, DEFAULT_VERT_LINE_THINNESS)
        horiLineThinness = ta.getDimensionPixelSize(R.styleable.FontSeekBar_horizon_line_thinness, DEFAULT_HORI_LINE_THINNESS)
        sliderLineThinness = ta.getDimensionPixelSize(R.styleable.FontSeekBar_slider_line_thinness, DEFAULT_SLIDER_LINE_THINNESS)
        sliderRadius = ta.getDimensionPixelSize(R.styleable.FontSeekBar_slider_radius, DEFAULT_SLIDER_RADIUS)
        vertLineHeight = ta.getDimensionPixelSize(R.styleable.FontSeekBar_vertical_line_height, DEFAULT_VERT_LINE_HEIGHT)
        textSize = ta.getDimensionPixelSize(R.styleable.FontSeekBar_slider_text_size, DEFAULT_TEXT_SIZE)
        isTextBold = ta.getBoolean(R.styleable.FontSeekBar_is_slider_text_bold, DEFAULT_TEXT_IS_BOLD)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) {
            return super.onTouchEvent(event)
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_MOVE,
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> changeRange(event.x.toInt(), event.y.toInt())
        }
        return true
    }

    fun onIndexChanged(action: (Int) -> Unit) {
        this.action = action
    }

    private fun changeRange(x:Int, y:Int) {
        if (y < lineY - 1.5 * sliderRadius || y > lineY + 1.5 * sliderRadius) {
            return
        }
        val curIndex = (x - paddingLeft + segmentWidth / 2) / segmentWidth
        if (index != curIndex) {
            action?.invoke(curIndex)
            index = curIndex
            invalidate()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var finalWidth = widthMeasureSpec
        var finalHeight = heightMeasureSpec
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.AT_MOST) {
            finalWidth = sliderRadius * 2 + segmentWidth * (range - 1)
        } else {
            val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
            segmentWidth = if (range <= 1) 0 else (viewWidth - 2 * sliderRadius) / (range - 1)
        }
        textArray.forEach {
            textPaint.getTextBounds(it, 0, it.length, rect)
            if (rect.height() > textHeight) {
                textHeight = rect.height()
                textBaseline = -rect.top
            }
        }
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            finalHeight = textHeight + textHorLinePadding + sliderRadius
        } else {
            textHorLinePadding = MeasureSpec.getSize(heightMeasureSpec) - textHeight - sliderRadius
        }
        lineY = textHeight + textHorLinePadding - horiLineThinness
        setMeasuredDimension(finalWidth, finalHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas == null) {
            return
        }
        drawLine(canvas)
        drawCircle(canvas)
        drawText(canvas)
    }

    private fun drawLine(canvas: Canvas){
        horiLinePath.moveTo(sliderRadius.toFloat(), lineY.toFloat())
        horiLinePath.lineTo(sliderRadius.toFloat() + (range - 1) * segmentWidth, lineY.toFloat())
        canvas.drawPath(horiLinePath, horiLinePaint)

        var i = 0
        while (i < range) {
            vertLinePath.moveTo((sliderRadius + i * segmentWidth).toFloat(), (lineY - vertLineHeight / 2).toFloat())
            vertLinePath.lineTo((sliderRadius + i * segmentWidth).toFloat(), (lineY + vertLineHeight / 2).toFloat())
            canvas.drawPath(vertLinePath, vertLinePaint)
            i++
        }
    }

    private fun drawCircle(canvas: Canvas) {
        sliderPaint.color = sliderLineColor
        sliderPaint.style = Paint.Style.STROKE
        canvas.drawCircle((index * segmentWidth + sliderRadius).toFloat(),
                lineY.toFloat(), (sliderRadius - sliderLineThinness).toFloat(), sliderPaint)
        sliderPaint.color = sliderInnerColor
        sliderPaint.style = Paint.Style.FILL_AND_STROKE
        canvas.drawCircle((index * segmentWidth + sliderRadius).toFloat(),
                lineY.toFloat(), (sliderRadius - 2 * sliderLineThinness).toFloat(), sliderPaint)
    }

    private fun drawText(canvas: Canvas) {
        textArray.indices
                .filter { it < range }
                .forEach { canvas.drawText(textArray[it],
                        (it * segmentWidth + sliderRadius).toFloat(),
                        textBaseline.toFloat(),
                        textPaint) }
    }
}