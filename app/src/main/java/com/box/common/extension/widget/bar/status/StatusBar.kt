package com.box.common.extension.widget.bar.status

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import com.box.common.core.environment.EnvUI

class StatusBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : View(context, attrs, defStyleAttr) {

    init {
        initViews(attrs)
    }

    private fun initViews(attrs: AttributeSet?) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            visibility = GONE
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), EnvUI.STATUS_BAR_HEIGHT)
    }

}