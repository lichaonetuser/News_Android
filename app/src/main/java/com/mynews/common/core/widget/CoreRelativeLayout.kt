package com.mynews.common.core.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.mynews.app.news.R
import com.mynews.common.core.widget.helper.CommonPressedEffectHelper
import android.view.ViewTreeObserver

open class CoreRelativeLayout
@JvmOverloads
constructor(context: Context,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = 0)
    : RelativeLayout(context, attrs, defStyleAttr) {

    private var yFraction = 0f
    private var xFraction = 0f

    private var preDrawListener: ViewTreeObserver.OnPreDrawListener? = null

    fun setYFraction(fraction: Float) {
        this.yFraction = fraction
        if (height == 0) {
            if (preDrawListener == null) {
                preDrawListener = ViewTreeObserver.OnPreDrawListener {
                    viewTreeObserver.removeOnPreDrawListener(
                            preDrawListener)
                    setYFraction(yFraction)
                    true
                }
                viewTreeObserver.addOnPreDrawListener(preDrawListener)
            }
            return
        }
        val translationY = height * fraction
        setTranslationY(translationY)
    }

    fun setXFraction(fraction: Float) {
        this.xFraction = fraction
        if (width == 0) {
            if (preDrawListener == null) {
                preDrawListener = ViewTreeObserver.OnPreDrawListener {
                    viewTreeObserver.removeOnPreDrawListener(
                            preDrawListener)
                    setXFraction(xFraction)
                    true
                }
                viewTreeObserver.addOnPreDrawListener(preDrawListener)
            }
            return
        }
        val translationX = width * fraction
        setTranslationX(translationX)
    }

    init {
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.CoreRelativeLayout)
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
