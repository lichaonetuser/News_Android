package com.box.common.core.net.http.interceptor

import android.util.Log
import com.box.app.news.util.UDIDUtils
import com.box.common.core.log.Utils
import com.box.common.core.net.http.extension.addQueryParameters
import okhttp3.Interceptor
import okhttp3.Response

class HttpUrlParamsInterceptor(val urlParamsMap: HashMap<String, String>? = null) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        urlParamsMap?.put("unique_device_id",UDIDUtils.getUniqueDeviceId())
        val original = chain.request()
        var url = original.url()
        if (urlParamsMap != null) {
            url = url.newBuilder()
                    .addQueryParameters(urlParamsMap)
                    .build()
        }
        return chain.proceed(original.newBuilder().url(url).build())
    }

}