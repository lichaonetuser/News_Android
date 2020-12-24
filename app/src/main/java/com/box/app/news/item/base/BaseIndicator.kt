package com.box.app.news.item.base

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

public abstract class BaseIndicator  @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : View(context, attrs, defStyleAttr) {

    init {
    }
    public abstract fun setState(b : Boolean)
}