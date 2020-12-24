package com.box.common.core.debug

import android.os.Build
import android.webkit.WebView
import com.box.app.news.data.DataManager
import com.box.common.core.CoreApp
import com.box.common.core.log.Logger
import com.box.common.core.net.http.HttpManager
import com.box.common.core.net.http.interceptor.HttpLoggingInterceptor
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.firebase.perf.metrics.AddTrace
import com.gu.toolargetool.TooLargeTool
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.utils.Log
import me.yokeyword.fragmentation.Fragmentation

object DebugManager {

    var enable: Boolean = false

    @AddTrace(name = "InitDebugManager", enabled = true)
    fun init(enable: Boolean) {
        this.enable = enable
        if (enable) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true)
            }
            Stetho.initializeWithDefaults(CoreApp.getInstance())
            HttpManager.setBaseUrl(DataManager.Local.getBaseUrl())
            HttpManager.setBaseOkHttpClient { builder ->
                builder.addNetworkInterceptor(StethoInterceptor())
                        .addInterceptor(HttpLoggingInterceptor("HttpManager", true))
                        .build()
            }
            TooLargeTool.startLogging(CoreApp.getInstance())
            FlexibleAdapter.useTag("CommonRecyclerAdapter")
            FlexibleAdapter.enableLogs(Log.Level.DEBUG)
            try {
                Fragmentation.builder()
                        .stackViewMode(Fragmentation.BUBBLE)
                        .debug(true)
                        .install()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}