package com.box.common.core.widget.helper

import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import com.box.app.news.R

class CommonPressedEffectHelper(
        private val mView: View,
        set: AttributeSet?) {

    companion object {
        const val DEFAULT_COMMON_PRESSED_EFFECT_ALPHA = 0.5f
    }

    private val useCommonPressedEffect: Boolean
    private val commonPressedEffectAlpha: Float

    init {
        val ta: TypedArray = mView.context.obtainStyledAttributes(set, R.styleable.CoreCommonPressed)
        useCommonPressedEffect = ta.getBoolean(R.styleable.CoreCommonPressed_useCommonPressedEffect,
                false)
        commonPressedEffectAlpha = ta.getFloat(R.styleable.CoreCommonPressed_commonPressedEffectAlpha,
                DEFAULT_COMMON_PRESSED_EFFECT_ALPHA)
        ta.recycle()
    }

    fun dispatchSetPressed(pressed: Boolean) {
        if (useCommonPressedEffect) {
            mView.alpha = if (pressed) commonPressedEffectAlpha else 1.0f
        }
    }

}