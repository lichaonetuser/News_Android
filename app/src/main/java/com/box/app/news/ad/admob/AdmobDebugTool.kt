package com.box.app.news.ad.admob

import android.view.ViewGroup
import android.widget.TextView
import com.box.app.news.App
import com.box.app.news.R
import com.box.app.news.debug.DebugTool
import com.box.common.core.debug.DebugManager
import com.box.common.core.util.ResUtils
import com.box.common.core.widget.CoreTextView
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