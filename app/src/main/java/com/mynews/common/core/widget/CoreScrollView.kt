package com.mynews.common.core.widget

import android.content.Context
import androidx.core.widget.NestedScrollView
import android.util.AttributeSet

class CoreScrollView
@JvmOverloads
constructor(context: Context,
            attrs: AttributeSet? = null,
            defStyle: Int = android.R.attr.scrollViewStyle)
    : NestedScrollView(context, attrs, defStyle)
