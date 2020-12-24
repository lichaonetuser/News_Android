package com.box.common.core.net.http.cookie

import android.os.Build
import android.preference.PreferenceManager
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import com.box.common.core.CoreApp
import com.box.common.core.log.Logger

object HttpCookieStore {

    fun syncCookie(host: String, cookie: String?) {
        try {
            CookieSyncManager.createInstance(CoreApp.getInstance())
            val manager = CookieManager.getInstance()
            manager.setAcceptCookie(true)
            manager.setCookie(host, cookie)
            if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
                CookieSyncManager.getInstance().sync()
            } else {
                CookieManager.getInstance().flush()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}