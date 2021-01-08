package com.mynews.app.news.widget

import android.content.Context
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextUtils
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView
import com.mynews.app.news.R
import com.mynews.app.news.analytics.AnalyticsKey
import com.mynews.app.news.data.DataDictionary
import com.mynews.app.news.data.DataManager
import com.mynews.common.core.analytics.AnalyticsManager
import com.mynews.common.core.util.ResUtils

/**
 *
 */
class EssayTextView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null,
            defStyleAttr: Int = 0)
    : ViewGroup(context, attrs, defStyleAttr) {

    companion object {
        val EXPAND_TEXT = ResUtils.getString(R.string.Tip_ReadMore) //右下角指示展开全文的文字

        val L_ESSAY_TEXT_SIZE = 18
        val M_ESSAY_TEXT_SIZE = 16
        val S_ESSAY_TEXT_SIZE = 15

        val L_LINE_SPACE = 8
        val M_LINE_SPACE = 7
        val S_LINE_SPACE = 6
    }

    private var text = ""
    fun setText(text:String?) {
        if (text == null) {
            this.text = ""
        } else {
            this.text = text.trim()
        }
        isExpanded = false
        reset()
        requestLayout()
    }

    private var textColor = ResUtils.getColor(R.color.color_1)
    private var expandColor = ResUtils.getColor(R.color.color_11)
    private var textSize = 16f
    private var lineSpace = 1f

    private var isExpanded = false //是否已经展开
    private var isReachMaxLines = false //是否达到了最大行数
    private var canExpand = false //是否可以展开

    private var viewHeight = 0
    private var mainHeight = 0
    private var expandWidth = 0
    private var lastWidth = 0
    private var lastHeight = 0
    private var expandHeight = 0

    private val mainTextView = TextView(context) //除了最后一行可见的正文view
    private val lastLineTextView = TextView(context) //最后一行正文view
    private val expandTextView = TextView(context) //右下角指示展开全文的view

    private var maxLines = 4

    private var onExpandListener = ({})

    private fun dp(value:Int):Int {
        val scale = context.resources.displayMetrics.density
        return (value * scale + 0.5f).toInt()
    }

    init {
        expandTextView.setOnClickListener {
            if (canExpand && !isExpanded) {
                isExpanded = true
                reset()
                requestLayout()
                onExpandListener.invoke()

                AnalyticsManager.logEvent(AnalyticsKey.Event.NEWS, AnalyticsKey.Parameter.ESSAY_EXPAND)
            }
        }
        mainTextView.textSize = textSize
        lastLineTextView.textSize = textSize
        expandTextView.textSize = textSize
        mainTextView.setTextColor(textColor)
        lastLineTextView.setTextColor(textColor)
        expandTextView.setTextColor(expandColor)
        addView(mainTextView)
        addView(lastLineTextView)
        addView(expandTextView)

        setBold(false)
    }

    //设置最大行数，下一次布局时生效
    fun setMaxLines(maxLines:Int) {
        this.maxLines = maxLines
    }

    //设置是否可以展开，下一次布局时生效
    fun setCanExpand(canExpand: Boolean) {
        this.canExpand = canExpand
    }

    //设置是否已经展开，下一次布局时生效
    fun setIsExpand(isExpand:Boolean) {
        this.isExpanded = isExpand
    }

    //耦合系统的更新字体函数
    fun updateTextSize() {
        when (DataManager.Local.getFontSize()) {
            DataDictionary.FontSize.L -> {
                lineSpace = dp(L_LINE_SPACE).toFloat()
                mainTextView.setLineSpacing(lineSpace, 1f)
                this.textSize = L_ESSAY_TEXT_SIZE.toFloat()
                mainTextView.textSize = textSize
                lastLineTextView.textSize = textSize
                expandTextView.textSize = textSize
            }
            DataDictionary.FontSize.M -> {
                lineSpace = dp(M_LINE_SPACE).toFloat()
                mainTextView.setLineSpacing(lineSpace, 1f)
                this.textSize = M_ESSAY_TEXT_SIZE.toFloat()
                mainTextView.textSize = textSize
                lastLineTextView.textSize = textSize
                expandTextView.textSize = textSize
            }
            DataDictionary.FontSize.S -> {
                lineSpace = dp(S_LINE_SPACE).toFloat()
                mainTextView.setLineSpacing(lineSpace, 1f)
                this.textSize = S_ESSAY_TEXT_SIZE.toFloat()
                mainTextView.textSize = textSize
                lastLineTextView.textSize = textSize
                expandTextView.textSize = textSize
            }
        }
        requestLayout()
    }

    //设置正文字体大小，dp值
    fun setEssayTextSize(dpValue:Int) {
        this.textSize = dpValue.toFloat()
        mainTextView.textSize = textSize
        lastLineTextView.textSize = textSize
        expandTextView.textSize = textSize
        requestLayout()
    }

    //设置是否粗体
    fun setBold(isBold:Boolean) {
        val typeface = if (isBold) Typeface.defaultFromStyle(Typeface.BOLD) else Typeface.defaultFromStyle(Typeface.NORMAL)
        mainTextView.typeface = typeface
        lastLineTextView.typeface = typeface
        expandTextView.typeface = typeface
    }

    //设置字体行间距
    fun setLineSpace(dpValue:Int) {
        lineSpace = dp(dpValue).toFloat()
        mainTextView.setLineSpacing(lineSpace, 1f)
        lastLineTextView.setLineSpacing(lineSpace, 1f)
        expandTextView.setLineSpacing(lineSpace, 1f)
    }

    fun setOnExpandListener(onExpand: () -> Unit = {}) {
        this.onExpandListener = onExpand
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        customMeasure(width)
        setMeasuredDimension(width, viewHeight + 20)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        mainTextView.layout(0, 0, width, viewHeight)
        lastLineTextView.layout(0, viewHeight - lastHeight, lastWidth, viewHeight)
        expandTextView.layout(width - expandWidth, viewHeight - expandHeight, width, viewHeight)
    }

    private fun customMeasure(width:Int) {
        if (text.isEmpty()) {
            viewHeight = 0
            mainHeight = 0
            return
        }

        val l = StaticLayout(text, mainTextView.paint, width,
                Layout.Alignment.ALIGN_NORMAL, mainTextView.lineSpacingMultiplier, lineSpace,
                false)

        //消除最上层字体与边框padding的代码
//        val a = text
//        val firstLineString = a.substring(l.getLineStart(0), l.getLineEnd(0))
//        val rect = Rect()
//        mainTextView.paint.getTextBounds(firstLineString, 0, firstLineString.length, rect)
//
//        val first = StaticLayout(firstLineString, mainTextView.paint, width,
//                Layout.Alignment.ALIGN_NORMAL, mainTextView.lineSpacingMultiplier, 0f,
//                false)
//        delta = first.height - rect.height()

        if (isExpanded) {
            mainTextView.text = text
            mainHeight = l.height
            viewHeight = l.height
            expandTextView.isClickable = false
        } else {
            if (l.lineCount > maxLines) {
                isReachMaxLines = true
                expandTextView.isClickable = canExpand
                val lastLineString = text.substring(l.getLineStart(maxLines - 1), l.getLineEnd(maxLines - 1)).trim()
                expandWidth = mainTextView.paint.measureText(EXPAND_TEXT).toInt()
                val mainString = if (text[l.getLineEnd(maxLines - 2) - 1] == '\n') {
                    text.substring(0, l.getLineEnd(maxLines - 2) - 1)
                } else {
                    text.substring(0, l.getLineEnd(maxLines - 2))
                }
                val main = StaticLayout(mainString, mainTextView.paint, width,
                        Layout.Alignment.ALIGN_NORMAL, mainTextView.lineSpacingMultiplier, lineSpace,
                        false)
                mainHeight = main.height

                val last = StaticLayout(lastLineString, mainTextView.paint, width,
                        Layout.Alignment.ALIGN_NORMAL, mainTextView.lineSpacingMultiplier, lineSpace,
                        false)

                val expand = StaticLayout(EXPAND_TEXT, mainTextView.paint, width,
                        Layout.Alignment.ALIGN_NORMAL, mainTextView.lineSpacingMultiplier, lineSpace,
                        false)

                lastHeight = last.height
                expandHeight = expand.height
                expandWidth = expandWidth
                viewHeight = main.height + (if (last.height >= expand.height) last.height else expand.height) + lineSpace.toInt()

                mainTextView.text = text.substring(0, l.getLineEnd(maxLines - 2))
                val mainParams = mainTextView.layoutParams
                mainParams.height = LayoutParams.WRAP_CONTENT
                mainParams.width = width

                lastLineTextView.text = lastLineString
                lastLineTextView.setSingleLine()
                lastLineTextView.maxLines = 1
                lastLineTextView.ellipsize = TextUtils.TruncateAt.END
                val lastLineParams = lastLineTextView.layoutParams
                lastLineParams.height = lastHeight
                lastLineParams.width = width - expandWidth - dp(12)
                lastWidth = width - expandWidth - dp(12)

                expandTextView.text = EXPAND_TEXT
                val expandParams = expandTextView.layoutParams
                expandParams.height = expandHeight
                expandParams.width = expandWidth
            } else {
                mainTextView.text = text
                viewHeight = l.height
                mainHeight = l.height
                expandTextView.isClickable = false
            }
        }
    }

    private fun reset() {
        isReachMaxLines = false
        mainHeight = 0
        expandWidth = 0
        lastWidth = 0
        mainTextView.text = ""
        lastLineTextView.text = ""
        expandTextView.text = ""
    }
}