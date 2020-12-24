package com.box.common.core.widget

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View

import com.box.app.news.R
import com.box.common.core.widget.helper.CommonPressedEffectHelper

class CoreSwitch @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = androidx.appcompat.R.attr.switchStyle) : androidx.appcompat.widget.SwitchCompat(context, attrs, defStyleAttr) {

    init {
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.CoreSwitch)
            initCommonPressedEffect(attrs)
            initAllMeasureMode(ta)
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

    /*------------------*/
    /* ALL MEASURE MODE */
    /*------------------*/

    private var mUseAllMeasureMode: Boolean = false

    private fun initAllMeasureMode(ta: TypedArray) {
        mUseAllMeasureMode = ta.getBoolean(R.styleable.CoreSwitch_useAllMeasureMode, false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (mUseAllMeasureMode) {
            try {
                val switchWidth = View.MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight
                val switchHeight = View.MeasureSpec.getSize(heightMeasureSpec) - paddingTop - paddingBottom

                val f1 = androidx.appcompat.widget.SwitchCompat::class.java.getDeclaredField("mSwitchWidth")
                f1.isAccessible = true
                f1.set(this, switchWidth)
                val f2 = androidx.appcompat.widget.SwitchCompat::class.java.getDeclaredField("mSwitchHeight")
                f2.isAccessible = true
                f2.set(this, switchHeight)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }
}
