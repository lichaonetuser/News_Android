package com.box.common.core.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import androidx.annotation.IntDef
import androidx.annotation.LongDef
import android.util.AttributeSet
import android.view.Gravity

import com.box.app.news.R
import com.box.common.core.widget.helper.CommonPressedEffectHelper

class CoreCheckedTextView
@JvmOverloads
constructor(context: Context,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = androidx.appcompat.R.attr.checkedTextViewStyle)
    : androidx.appcompat.widget.AppCompatCheckedTextView(context, attrs, defStyleAttr) {

}
