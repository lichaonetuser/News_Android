package com.mynews.app.news.ad.admob

import android.view.ViewGroup
import android.widget.TextView
import com.mynews.app.news.App
import com.mynews.app.news.R
import com.mynews.app.news.debug.DebugTool
import com.mynews.common.core.debug.DebugManager
import com.mynews.common.core.util.ResUtils
import com.mynews.common.core.widget.CoreTextView
import org.jetbrains.anko.findOptional

object AdmobDebugTool {

    fun addDebugTextView(parent: ViewGroup) {
        if (App.isDebug() && DebugManager.enable && DebugTool.mListAdTest) {
            val ad_debug_txt = CoreTextView(parent.context)
            ad_debug_txt.id = R.id.list_ad_test
            ad_debug_txt.setTextColor(ResUtils.getColor(R.color.color_11))
            if (parent.indexOfChild(ad_debug_txt) == -1) {
                parent.addView(ad_debug_txt)
            }
        }
    }

    fun setDebugTextViewText(parent: ViewGroup, text: String) {
        if (App.isDebug() && DebugManager.enable && DebugTool.mListAdTest) {
            parent.findOptional<TextView>(R.id.list_ad_test)?.text = text
        }
    }

}