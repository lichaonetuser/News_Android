package com.mynews.common.extension.widget.bar.title

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.mynews.app.news.R
import com.mynews.common.core.util.ResUtils
import kotlinx.android.synthetic.main.core_widget_title_bar.view.*

class TitleBar @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0)
    : RelativeLayout(context, attrs, defStyleAttr) {

    private var mShowBack: Boolean = true
    private var mShowTitle: Boolean = true
    private var mShowLine: Boolean = true
    private var mTitle: String? = null
    private var mBackTintColor: Int = -1

    init {
        View.inflate(context, R.layout.core_widget_title_bar, this)
        initViews(attrs)
    }

    private fun initViews(attrs: AttributeSet?) {
        val context = context
        val a = context.obtainStyledAttributes(attrs, R.styleable.TitleBar, 0, 0)
        mShowBack = a.getBoolean(R.styleable.TitleBar_showBack, true)
        mShowTitle = a.getBoolean(R.styleable.TitleBar_showTitle, true)
        mShowLine = a.getBoolean(R.styleable.TitleBar_showLine, true)
        mTitle = a.getString(R.styleable.TitleBar_title)
        mBackTintColor = a.getResourceId(R.styleable.TitleBar_backTintColor, -1)
        a.recycle()

        if (mShowBack) {
            back_btn.visibility = View.VISIBLE
        } else {
            back_btn.id = View.NO_ID
            back_btn.visibility = View.GONE
        }
        title_txt.visibility = if (mShowTitle) View.VISIBLE else View.GONE
        title_txt.text = mTitle
        line.visibility = if (mShowLine) View.VISIBLE else View.GONE
        if (mBackTintColor != -1) {
            back_btn.supportImageTintList = ColorStateList.valueOf(ResUtils.getColor(mBackTintColor))
        }
    }

    fun setTitle(title: String) {
        title_txt.text = title
    }

}
