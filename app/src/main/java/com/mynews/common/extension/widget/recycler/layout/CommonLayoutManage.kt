package com.mynews.common.extension.widget.recycler.layout

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager

class CommonLayoutManage(
        context: Context? = null,
        orientation: Int = VERTICAL,
        reverseLayout: Boolean = false)
    : LinearLayoutManager(context, orientation, reverseLayout) {

}