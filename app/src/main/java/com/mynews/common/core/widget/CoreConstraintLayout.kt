package com.mynews.common.core.widget


import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import android.util.AttributeSet
import android.view.View
import com.mynews.app.news.R
import com.mynews.common.core.widget.helper.CommonPressedEffectHelper

class CoreConstraintLayout
@JvmOverloads
constructor(context: Context,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = 0)
    : ConstraintLayout(context, attrs, defStyleAttr) {

    var onVisibilityChangedListener: ((changedView: View?, visibility: Int) -> Unit)? = null
    var onVisibilityAggregatedListener: ((isVisible: Boolean) -> Unit)? = null

    init {
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.CoreConstraintLayout)
            initCommonPressedEffect(attrs)
            ta.recycle()
        }
    }

    override fun dispatchSetPressed(pressed: Boolean) {
        super.dispatchSetPressed(pressed)
        commonPressedEffectHelper.dispatchSetPressed(pressed)
    }

    override fun onVisibilityChanged(changedView: View?, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        onVisibilityChangedListener?.invoke(changedView, visibility)
    }

    override fun onVisibilityAggregated(isVisible: Boolean) {
        super.onVisibilityAggregated(isVisible)
        onVisibilityAggregatedListener?.invoke(isVisible)
    }

    /*-----------------------*/
    /* COMMON PRESSED EFFECT */
    /*-----------------------*/

    private lateinit var commonPressedEffectHelper: CommonPressedEffectHelper

    private fun initCommonPressedEffect(attrs: AttributeSet) {
        commonPressedEffectHelper = CommonPressedEffectHelper(this, attrs)
    }

}
