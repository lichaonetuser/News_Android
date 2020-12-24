package com.box.app.news.widget

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import com.box.app.news.R
import com.box.app.news.data.DataDictionary.FontSize.*
import com.box.app.news.data.DataManager
import com.box.app.news.event.EventManager
import com.box.app.news.item.base.TitleInterface
import com.box.common.core.CoreApp
import com.box.common.core.widget.CoreTextView
import org.jetbrains.anko.dip
import org.jetbrains.anko.textColor

@Suppress("unused")


class FontTextView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.textViewStyle)
    : CoreTextView(context, attrs, defStyleAttr),TitleInterface {

    override fun setTitle(text: String) {
        this.text = text
        requestLayout()
    }

    override fun getTitle(): String? {
        return text.toString()
    }

    override fun updateTitleTextSize() {
        updateFontSize()
    }

    override fun setTitleColor(color: Int) {
        this.textColor = color
        invalidate()
    }

    override fun setTitleVisibility(visibility: Int) {
        this.visibility = visibility
        requestLayout()
    }

    override fun setTitleMaxLines(maxLines: Int) {
        this.maxLines = maxLines
        requestLayout()
    }

    override fun getHeightByExactWidth(width:Int):Int {
        return 0
    }

    override fun getHeightByExactLines(lines: Int): Int {
        return 0
    }

    companion object {
        val L_TEXT_SIZE = 18f
        val L_LINE_SPACING = CoreApp.getInstance().dip(3).toFloat()
        val M_TEXT_SIZE = 16f
        val M_LINE_SPACING = CoreApp.getInstance().dip(2).toFloat()
        val S_TEXT_SIZE = 14f
        val S_LINE_SPACING = CoreApp.getInstance().dip(2).toFloat()
    }

    private var subscribeTextSizeChangeEvent: Boolean = false
        set(value) {
            val isRegistered = EventManager.isRegistered(this)
            if (value) {
                if (!isRegistered) EventManager.register(this)
            } else {
                if (isRegistered) EventManager.unregister(this)
            }
            field = value
        }

    private var updateFontSizeWhenInit = true

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.FontTextView)
        init(ta)
        ta.recycle()
    }

    private fun init(ta: TypedArray) {
        subscribeTextSizeChangeEvent = ta.getBoolean(R.styleable.FontTextView_subscribeTextSizeChangeEvent, false)
        updateFontSizeWhenInit = ta.getBoolean(R.styleable.FontTextView_updateFontSizeWhenInit, true)
        if (updateFontSizeWhenInit){
            updateFontSize()
        }
    }

    fun updateFontSize() {
        when (DataManager.Local.getFontSize()) {
            L -> {
                textSize = L_TEXT_SIZE
                setLineSpacing(L_LINE_SPACING, 1.0f)
            }
            M -> {
                textSize = M_TEXT_SIZE
                setLineSpacing(M_LINE_SPACING, 1.0f)
            }
            S -> {
                textSize = S_TEXT_SIZE
                setLineSpacing(S_LINE_SPACING, 1.0f)
            }
        }
    }
}