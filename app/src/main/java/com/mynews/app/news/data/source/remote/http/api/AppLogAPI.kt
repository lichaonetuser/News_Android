package com.mynews.app.news.data.source.remote.http.api

import com.mynews.app.news.bean.AppLogConfig
import com.mynews.app.news.bean.PushArrival
import com.mynews.app.news.proto.AppLog
import com.mynews.common.core.net.http.bean.APIResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface AppLogAPI {

    companion object {
        const val ROOT_PATH = "app_log"
    }

    @GET("$ROOT_PATH/config/")
    fun config(): Observable<APIResponse<AppLogConfig>>

    @POST("$ROOT_PATH/push_arrival/")
    @Headers("Content-Type: application/json", "Accept: application/json")
    fun pushArrival(@Body arrival: PushArrival): Observable<APIResponse<Any>>

    @POST("$ROOT_PATH/log/")
    fun log(@Body appLog: AppLog.NewsAppLog): Observable<APIResponse<Any>>
}