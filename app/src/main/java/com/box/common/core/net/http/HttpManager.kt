package com.box.common.core.net.http

import com.box.app.news.data.source.remote.http.url.HttpBaseUrls
import com.box.common.core.CoreApp
import com.box.common.core.log.Logger
import com.box.common.core.net.http.bean.APIResponse
import com.box.common.core.net.http.func.HandleFuc
import com.box.common.core.net.http.func.HttpResponseFunc
import com.box.common.core.net.http.https.HttpsUtils
import com.box.common.core.net.http.interceptor.HttpSaveReceivedCookiesInterceptor
import com.box.common.core.net.http.interceptor.HttpSendSavedCookiesInterceptor
import com.box.common.core.net.http.interceptor.HttpUrlParamsInterceptor
import com.google.firebase.perf.metrics.AddTrace
import com.qiniu.android.netdiag.Output
import com.qiniu.android.netdiag.Ping
import com.qiniu.android.netdiag.TraceRoute

import com.readystatesoftware.chuck.ChuckInterceptor
import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.net.InetAddress
import java.net.URL


object HttpManager {

    private const val COOKIE_KEY = "HttpCookie"

    private lateinit var mBaseOkHttpClient: OkHttpClient
    private lateinit var mBaseRetrofit: Retrofit

    private val mCommonParams = HashMap<String, String>()

    @AddTrace(name = "InitHttpManager", enabled = true)
    fun init(baseUrl: String,
             commonParams: HashMap<String, String>? = null,
             client: OkHttpClient = OkHttpClient.Builder()
                     .addInterceptor(ChuckInterceptor(CoreApp.getInstance()))
                     .addInterceptor(HttpUrlParamsInterceptor(mCommonParams))
                     .addInterceptor(HttpSaveReceivedCookiesInterceptor(COOKIE_KEY))
                     .addInterceptor(HttpSendSavedCookiesInterceptor(COOKIE_KEY))
                     .build(),
             retrofit: Retrofit = Retrofit.Builder()
                     .baseUrl(baseUrl)
                     .client(client)
                     .build()) {
        mBaseOkHttpClient = client
        mBaseRetrofit = retrofit
        putCommonParams(commonParams ?: return)
        useAllTrustHttps()
    }

    fun useAllTrustHttps() {
        setBaseOkHttpClient { builder ->
            val sslParams = HttpsUtils.getAllTrustSSLParams()
            if (sslParams.sSLSocketFactory != null && sslParams.trustManager != null) {
                builder.sslSocketFactory(sslParams.sSLSocketFactory!!, sslParams.trustManager!!)
            }
            builder.build()
        }
    }

    fun setBaseRetrofit(retrofit: (builder: Retrofit.Builder) -> Retrofit) {
        mBaseRetrofit = retrofit.invoke(mBaseRetrofit.newBuilder())
    }

    fun setBaseOkHttpClient(client: (builder: OkHttpClient.Builder) -> OkHttpClient) {
        mBaseOkHttpClient = client.invoke(mBaseOkHttpClient.newBuilder())
        mBaseRetrofit = mBaseRetrofit.newBuilder().client(mBaseOkHttpClient).build()
    }

    fun setBaseUrl(url: String) {
        mBaseRetrofit = mBaseRetrofit.newBuilder().baseUrl(url).build()
    }

    fun getBaseRetrofit(): Retrofit {
        return mBaseRetrofit
    }

    fun getBaseOkHttpClient(): OkHttpClient {
        return mBaseOkHttpClient
    }

    fun getBaseUrl(): String {
        return mBaseRetrofit.baseUrl().toString()
    }

    fun putCommonParams(vararg pairs: Pair<String, String>) {
        pairs.forEach {
            mCommonParams.put(it.first, it.second)
        }
    }

    fun putCommonParams(params: HashMap<String, String>) {
        mCommonParams.putAll(params)
    }

    fun removeCommonParams(key: String) {
        mCommonParams.remove(key)
    }

    fun getCommonParams(): HashMap<String, String> {
        return HashMap(mCommonParams)
    }

    fun <T> callAPI(observable: Observable<APIResponse<T>>): Observable<T> {
        return observable
                .map(HandleFuc())
                .onErrorResumeNext(HttpResponseFunc<T>())
    }

    val output = object : Output {
        override fun write(line: String?) {
            Logger.d(line)
        }
    }

    fun pingNewsBoxServer() {
        Thread({
            val address = InetAddress.getByName(URL(HttpBaseUrls.getReleaseUrl()).getHost())
            val ip = address.hostAddress
            Ping.start(ip, output, Ping.Callback { })
        }).start()
    }

    fun traceRoute() {
        Thread({
            val address = InetAddress.getByName(URL(HttpBaseUrls.getReleaseUrl()).getHost())
            val ip = address.hostAddress
            TraceRoute.start(ip, output, TraceRoute.Callback { })
        }).start()

    }


}
