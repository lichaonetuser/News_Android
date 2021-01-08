package com.mynews.app.news.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.data.DataDictionary
import com.mynews.app.news.data.DataManager
import com.mynews.app.news.item.base.TitleInterface
import com.mynews.common.core.CoreApp
import com.crashlytics.android.Crashlytics
import org.jetbrains.anko.dip
import java.lang.Exception
import java.util.*

@Suppress("MemberVisibilityCanPrivate")
/**
 *
 */
class TitleTextView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null,
            defStyleAttr: Int = 0)
    : View(context, attrs, defStyleAttr), TitleInterface {

    companion object {
        private val ESLI = "…"
        private val DEFAULT_HORI_SPACE = 3

        private val L_TEXT_SIZE = 18
        private val L_LINE_SPACING = 7
        private val M_TEXT_SIZE = 16
        private val M_LINE_SPACING = 5
        private val S_TEXT_SIZE = 14
        private val S_LINE_SPACING = 5
    }

    override fun setTitle(text: String) {
        setText(text)
    }

    override fun getTitle(): String {
        return text
    }

    override fun updateTitleTextSize() {
        updateFontSize()
    }

    override fun setTitleColor(color: Int) {
        this.textColor = color
        paint.color = color
        invalidate()
    }

    override fun setTitleVisibility(visibility: Int) {
        this.visibility = visibility
        requestLayout()
    }

    override fun setTitleMaxLines(maxLines: Int) {
        setMaxLines(maxLines)
    }

    override fun getHeightByExactWidth(width: Int): Int {
        if (emojiTextList.isEmpty()) {
            return 0
        }
        var widthTemp = 0
        var heightTemp = 0
        var lastNewLineIndex = 0
        for (index in emojiTextList.indices) {
            measureText(index)
            val w = if (emojiTextList[index] == "\n") maxWidth - widthTemp else getCharWidth()
            if (widthTemp + w > width) {
                measureText(lastNewLineIndex, index)
                heightTemp += getLineHeight() + verPadding
                lastNewLineIndex = index
                widthTemp = w + horPadding
            } else {
                widthTemp += w + horPadding
            }
        }
        measureText(lastNewLineIndex, emojiTextList.size)
        return heightTemp + getLineHeight()
    }

    override fun getHeightByExactLines(lines: Int): Int {
        if (emojiTextList.isEmpty()) {
            return 0
        }
        var widthTemp = 0
        var heightTemp = 0
        var lastNewLineIndex = 0
        var lineTemp = 0
        for (index in emojiTextList.indices) {
            measureText(index)
            val w = if (emojiTextList[index] == "\n") maxWidth - widthTemp else getCharWidth()
            if (widthTemp + w > width) {
                lineTemp++
                measureText(lastNewLineIndex, index)

                if (lineTemp == lines) {
                    return heightTemp + getLineHeight()
                } else {
                    lastNewLineIndex = index
                    widthTemp = w + horPadding
                    heightTemp += getLineHeight() + verPadding
                }
            } else {
                widthTemp += w + horPadding
            }
        }
        measureText(lastNewLineIndex, emojiTextList.size)
        return heightTemp + getLineHeight()
    }

    private fun dpToPx(dp: Int): Int {
        return CoreApp.getInstance().dip(dp)
    }

    private fun sp(value: Float): Float = (value * resources.displayMetrics.scaledDensity)

    /*********可配置区域*********/
    private var text = ""

    fun setText(title: String?) {
        if (title == null) {
            this.text = ""
        } else {
            this.text = title
        }
        convertEmoji()
        requestLayout()
    }

    private var textSize = 20F
    private var textColor = Color.BLACK
    private var horPadding = dpToPx(DEFAULT_HORI_SPACE)
    private var verPadding = dpToPx(M_LINE_SPACING)
    private var isBold = true

    /***********变量记录***********/
    private var emojiTextList = LinkedList<String>()

    private var leftList = LinkedList<Int>()
    private var baselineList = LinkedList<Int>()
    private var lastVisibleIndex = -1

    private var maxWidth = 100
    private var maxHeight = 100
    private var maxLines = 3
    fun setMaxLines(maxLines: Int) {
        if (maxLines >= 1) {
            this.maxLines = maxLines
            baseOnHeightNotLines = false
            requestLayout()
        } else {
            this.maxLines = -1
            baseOnHeightNotLines = true
            requestLayout()
        }
    }

    private var viewHeight = 0
    private var viewWidth = 0
    var isSubscribeTextSizeChange = true

    /********************/
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rect = Rect()
    private val sb = StringBuilder()
    private var reachMaxWidth = false
    private var singleLineWidth = 0
    private var baseOnHeightNotLines = true //根据最大高度而不是最大行数限定

    /**
     * 将原始的字符串转换为字符list，list中每个元素长度为1或者2，目的是防止占两个字符长度的表情被分割
     */
    private fun convertEmoji() {
        emojiTextList.clear()
        val charArray = text.toCharArray()
        var added = false
        for (it in 0 until text.length) {
            if (added) {
                added = false
                continue
            }
            val hs = charArray[it].toInt()
            if (hs in 0xd800..0xdbff) {
                val ls = charArray[it + 1].toInt()
                if (it + 1 < charArray.size) {
                    val uc = ((hs - 0xd800) * 0x400) + ls - 0xdc00 + 0x10000
                    if (uc in 0x1d000..0x1f77ff) {
                        emojiTextList.add(charArray[it].toString() + charArray[it + 1].toString())
                        added = true
                        continue
                    }
                }
            }
            emojiTextList.add(charArray[it].toString())
        }
    }

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.TitleTextView)
        initByAttrs(ta)
        ta.recycle()

        paint.color = textColor
        paint.textSize = sp(textSize)
        paint.style = Paint.Style.FILL_AND_STROKE
        if (isBold) paint.strokeWidth = getTextStrokeWidth()

        baseOnHeightNotLines = maxLines == -1
    }

    private fun initByAttrs(ta: TypedArray) {
        textSize = ta.getInt(R.styleable.TitleTextView_title_text_size, 18).toFloat()
        textColor = ta.getColor(R.styleable.TitleTextView_title_text_color, Color.BLACK)
        horPadding = ta.getDimensionPixelSize(R.styleable.TitleTextView_text_hor_space, dpToPx(DEFAULT_HORI_SPACE))
        verPadding = ta.getDimensionPixelSize(R.styleable.TitleTextView_text_ver_space, dpToPx(M_LINE_SPACING))
        isBold = ta.getBoolean(R.styleable.TitleTextView_is_title_text_bold, true)
        maxLines = ta.getInt(R.styleable.TitleTextView_title_max_lines, -1)
        isSubscribeTextSizeChange = ta.getBoolean(R.styleable.TitleTextView_is_subscribe_text_size_change, true)
    }

    fun updateFontSize() {
        if (!isSubscribeTextSizeChange) {
            return
        }
        textSize = when (DataManager.Local.getFontSize()) {
            DataDictionary.FontSize.L -> {
                verPadding = dip(TitleTextView.L_LINE_SPACING).toInt()
                TitleTextView.L_TEXT_SIZE.toFloat()
            }
            DataDictionary.FontSize.M -> {
                verPadding = dip(TitleTextView.M_LINE_SPACING).toInt()
                TitleTextView.M_TEXT_SIZE.toFloat()
            }
            DataDictionary.FontSize.S -> {
                verPadding = dip(TitleTextView.S_LINE_SPACING).toInt()
                TitleTextView.S_TEXT_SIZE.toFloat()
            }
        }
        paint.textSize = sp(textSize)
        requestLayout()
    }

    private fun dip(value: Int): Float = value * resources.displayMetrics.density

    private fun getTextStrokeWidth(): Float {
        return if (!isBold) {
            0F
        } else if (resources.displayMetrics.widthPixels >= 1080) {
            dip(1) * 2 / 3
        } else {
            dip(1) * 2 / 3 * resources.displayMetrics.widthPixels / 1080
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        paint.textSize = sp(textSize)
        val suggestWidth = View.MeasureSpec.getSize(widthMeasureSpec)
        val suggestHeight = View.MeasureSpec.getSize(heightMeasureSpec)
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        maxWidth = suggestWidth
        maxHeight = suggestHeight
        wholeMeasure()
        viewWidth = if (widthMode == View.MeasureSpec.EXACTLY) {
            suggestWidth
        } else {
            if (reachMaxWidth) {
                suggestWidth
            } else {
                singleLineWidth
            }
        }
        viewHeight = if (heightMode == View.MeasureSpec.EXACTLY) {
            suggestHeight
        } else {
            viewHeight
        }
        setMeasuredDimension(viewWidth, viewHeight)
    }

    /**
     * 全局测量，达到最大行数或者最大行高测量停止
     */
    fun wholeMeasure() {
        clear()
        if (emojiTextList.isEmpty()) {
            return
        }
        var widthTemp = 0
        var heightTemp = 0
        var lines = 0
        var lastNewLineIndex = 0
        for (index in emojiTextList.indices) {
            measureText(index)
            val w = if (emojiTextList[index] == "\n") maxWidth - widthTemp else getCharWidth()
            val left = getCharLeft()

            if (widthTemp + w > maxWidth) {
                reachMaxWidth = true
                lines++
                measureText(lastNewLineIndex, index)
                val height = getLineHeight()
                val temp = heightTemp + getLineBaseline()
                repeat(index - lastNewLineIndex, {
                    baselineList.add(temp)
                })

                when {
                    !baseOnHeightNotLines && lines == maxLines -> {
                        lastVisibleIndex = index - 1
                        heightTemp += height
                        viewHeight = heightTemp
                        addEsli()
                        return
                    }
                    baseOnHeightNotLines && heightTemp + height > maxHeight -> {
                        lastVisibleIndex = lastNewLineIndex - 1
                        viewHeight = heightTemp
                        addEsli()
                        return
                    }
                    else -> {
                        heightTemp += height + verPadding
                        lastNewLineIndex = index
                        widthTemp = w + horPadding
                        leftList.add(-left)
                    }
                }
            } else {
                leftList.add(widthTemp - left)
                widthTemp += horPadding + w
                singleLineWidth = widthTemp - horPadding
            }

            if (index == emojiTextList.size - 1) {
                lines++
                measureText(lastNewLineIndex, index + 1)
                val height = getLineHeight()
                if (baseOnHeightNotLines && heightTemp + height > maxHeight) {
                    lastVisibleIndex = lastNewLineIndex - 1
                    viewHeight = heightTemp
                    addEsli()
                } else {
                    val temp = heightTemp + getLineBaseline()
                    repeat(index - lastNewLineIndex + 1, {
                        baselineList.add(temp)
                    })
                    heightTemp += height
                    viewHeight = heightTemp
                }
            }
        }
    }

    /**
     * 每次测量前将记录信息的变量清空
     */
    private fun clear() {
        lastVisibleIndex = -1
        viewWidth = 0
        viewHeight = 0
        leftList.clear()
        baselineList.clear()
        reachMaxWidth = false
        singleLineWidth = 0
    }

    /**
     * 测量字体宽度和左边距，或者行高与行baseline，考虑空格换行的特殊性,还有如果一整行都是空格的情况
     * 空格的宽度自己设定为字体大小的一半
     * 换行符
     */
    private fun measureText(start: Int, end: Int = -1) {
        if (end == -1) {
            when {
                emojiTextList[start] == "\n" -> {
                    rect.left = 0
                    rect.right = maxWidth + 1
                }
                emojiTextList[start] == " " -> {
                    rect.left = 0
                    rect.right = (sp(textSize) / 4).toInt()
                }
                else -> paint.getTextBounds(emojiTextList[start], 0, emojiTextList[start].length, rect)
            }
        } else {
            sb.setLength(0)
            for (char in start until end) {
                sb.append(emojiTextList[char])
            }
            val line = sb.toString()
            if (line == "\n" || line.trim().isEmpty()) {
                rect.top = (-sp(textSize)).toInt()
                rect.bottom = 0
            } else {
                paint.getTextBounds(line, 0, line.length, rect)
                if (rect.bottom - rect.top < sp(textSize) / 2) {
                    rect.top = (-sp(textSize) / 2).toInt()
                }
            }
        }
    }

    /**
     * 获取字符的左边距
     */
    private fun getCharLeft(): Int {
        return rect.left
    }

    /**
     * 获取字符的宽度
     */
    private fun getCharWidth(): Int {
        return rect.right - rect.left
    }

    /**
     * 获取行baseline
     */
    private fun getLineBaseline(): Int {
        return -rect.top
    }

    /**
     * 获取行高
     */
    private fun getLineHeight(): Int {
        return rect.bottom - rect.top
    }

    /**
     * 测量省略符
     */
    private fun measureEsli() {
        paint.getTextBounds(ESLI, 0, 1, rect)
    }

    /**
     * 添加省略符
     */
    private fun addEsli() {
        if (lastVisibleIndex <= 0) {
            return
        }
        var temp = 0
        var get = false
        measureEsli()
        val esliWidth = getCharWidth()
        val esliLeft = getCharLeft()
        val baseline = baselineList[lastVisibleIndex]
        for (index in lastVisibleIndex downTo 0) {
            if (baseline == baselineList[index]) {
                measureText(index)
                val w = getCharWidth()
                temp += w
                if (temp >= esliWidth) {
                    val left = getCharLeft()
                    lastVisibleIndex = index
                    leftList[index] = leftList[index] + left - esliLeft
                    get = true
                    break
                }
            } else {
                break
            }
        }
        if (!get) {
            lastVisibleIndex = -2
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        try {
            if (canvas == null) {
                return
            }
            if (emojiTextList.isEmpty()) {
                return
            }
            if (lastVisibleIndex == -1) {
                repeat(emojiTextList.size, {
                    canvas.drawText(emojiTextList[it], leftList[it].toFloat() + 2, baselineList[it].toFloat(), paint)
                })
            } else {
                repeat(lastVisibleIndex, {
                    canvas.drawText(emojiTextList[it], leftList[it].toFloat() + 2, baselineList[it].toFloat(), paint)
                })
                canvas.drawText(ESLI, leftList[lastVisibleIndex].toFloat() + 2, baselineList[lastVisibleIndex].toFloat(), paint)
            }
        } catch (e: Exception) {
            Crashlytics.logException(e)
        }
    }
}