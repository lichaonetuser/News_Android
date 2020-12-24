package com.box.common.core.widget

import android.content.Context
import android.text.InputFilter
import android.util.AttributeSet
import android.view.KeyEvent
import android.widget.EditText
import com.box.app.news.R
import com.box.common.core.widget.helper.CommonPressedEffectHelper

open class CoreEditText
@JvmOverloads
constructor(context: Context,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = androidx.appcompat.R.attr.editTextStyle)
    : androidx.appcompat.widget.AppCompatEditText(context, attrs, defStyleAttr) {

    fun setMaxLength(length: Int) {
        filters = arrayOf<InputFilter>(InputFilter.LengthFilter(length))
    }

    init {
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.CoreCommonPressed)
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

    /*---------------*/
    /* BACK ON INPUT */
    /*---------------*/

    private var mOnBackListener: OnBackListener? = null

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return mOnBackListener?.back(this) ?: false
        }
        return false
    }

    fun setOnBackListener(l: OnBackListener) {
        this.mOnBackListener = l
    }

    interface OnBackListener {
        fun back(editText: EditText): Boolean
    }

}
