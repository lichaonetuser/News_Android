package com.mynews.common.core.widget

import android.content.Context
import android.util.AttributeSet

class CoreCheckedTextView
@JvmOverloads
constructor(context: Context,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = androidx.appcompat.R.attr.checkedTextViewStyle)
    : androidx.appcompat.widget.AppCompatCheckedTextView(context, attrs, defStyleAttr) {

}
