package com.mynews.common.core.net.http.interceptor

import android.preference.PreferenceManager
import com.mynews.common.core.CoreApp
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class HttpSendSavedCookiesInterceptor(private val cookiesKey: String) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        val cookie = PreferenceManager
                .getDefaultSharedPreferences(CoreApp.getInstance())
                .getString(cookiesKey, null)
        if (!cookie.isNullOrBlank()) {
            builder.addHeader("Cookie", cookie)
        }
        return chain.proceed(builder.build())
    }
}
