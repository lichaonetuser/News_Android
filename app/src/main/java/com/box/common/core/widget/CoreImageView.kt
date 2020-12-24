package com.box.common.core.widget

import android.content.Context
import android.util.AttributeSet
import com.box.app.news.R

import com.box.common.core.widget.helper.CommonPressedEffectHelper

class CoreImageView
@JvmOverloads
constructor(context: Context,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = 0)
    : androidx.appcompat.widget.AppCompatImageView(context, attrs, defStyleAttr) {

    init {
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.CoreImageView)
            initCommonPressedEffect(attrs)
            ta.recycle()
        }
    }

    override fun dispatchSetPressed(pressed: Boolean) {
        super.dispatchSetPressed(pressed)
        commonPressedEffectHelper.dispatchSetPressed(pressed)
    }

    /*-----------------------*/
    /* COMMON PRESSED EFFECT */
    /*-----------------------*/

    private lateinit var commonPressedEffectHelper: CommonPressedEffectHelper

    private fun initCommonPressedEffect(attrs: AttributeSet) {
        commonPressedEffectHelper = CommonPressedEffectHelper(this, attrs)
    }
}
