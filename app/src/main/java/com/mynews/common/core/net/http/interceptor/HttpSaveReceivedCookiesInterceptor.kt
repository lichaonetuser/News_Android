package com.mynews.common.core.net.http.interceptor

import android.preference.PreferenceManager
import com.mynews.common.core.CoreApp
import com.mynews.common.core.net.http.cookie.HttpCookieStore
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class HttpSaveReceivedCookiesInterceptor(private val cookiesKey: String) : Interceptor {

    @JvmField
    val setCookieHeader = "Set-Cookie"

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        val host = chain.request().url().host()
        val cookie = originalResponse.header(setCookieHeader)
        if (!cookie.isNullOrBlank()) {
            PreferenceManager
                    .getDefaultSharedPreferences(CoreApp.getInstance())
                    .edit()
                    .putString(cookiesKey, cookie)
                    .apply()
            HttpCookieStore.syncCookie(host, cookie)
        }
        return originalResponse
    }
}
