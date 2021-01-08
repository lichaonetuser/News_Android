package com.mynews.common.core.widget

import android.content.Context
import android.util.AttributeSet
import com.mynews.app.news.R
import com.mynews.common.core.widget.helper.CommonPressedEffectHelper
import com.facebook.drawee.view.SimpleDraweeView

class CoreSimpleDraweeView
@JvmOverloads
constructor(context: Context,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = 0)
    : SimpleDraweeView(context, attrs, defStyleAttr) {

    init {
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.CoreSimpleDraweeView)
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
