package com.mynews.app.news.widget

import android.content.Context
import android.util.AttributeSet
import com.mynews.common.core.widget.CoreTextView

/**
 * 智能宽度TextView
 * 应用场景：专用于展示sourceName，具体以后有时间解释
 */
class SmartWidthTextView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null,
            defStyleAttr: Int = 0)
    : CoreTextView(context, attrs, defStyleAttr) {

    var alwaysMatchParent = false

    /**
     * 重写测量方法，将此控件放在LinearLayout中，宽度设置为0
     * 设置layout_weight属性，这个控件会将父布局传递的宽度值当成最大宽度，
     * 然后将宽度mode变为wrap_content，
     * 结果就是文字过短时，控件表现为wrap_content，过长时表现为match_parent
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var width = widthMeasureSpec
        if (!alwaysMatchParent) {
            val originWidth = MeasureSpec.getSize(widthMeasureSpec)
            maxWidth = originWidth
            width = MeasureSpec.makeMeasureSpec(originWidth, MeasureSpec.AT_MOST)
        }
        super.onMeasure(width, heightMeasureSpec)
    }

    /**
     * 开关：控制是否开启特色功能，具体场景参照6.9需求里 "来源、时间、评论" displayOptions不同时三者交互
     */
    fun setMatchParent(alwaysMatchParent:Boolean) {
        this.alwaysMatchParent = alwaysMatchParent
    }
}