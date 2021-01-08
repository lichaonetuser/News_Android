package com.mynews.app.news.data.source.remote.http.api

import com.mynews.app.news.bean.AppConfig
import com.mynews.app.news.bean.AppConfigResource
import com.mynews.app.news.bean.AppConfigShare
import com.mynews.common.core.net.http.bean.APIResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface AppConfigAPI {

    companion object {
        const val ROOT_PATH = "app_config"
    }

    @GET("$ROOT_PATH/")
    fun config(@Query("af_id") afId: String): Observable<APIResponse<AppConfig>>

    @GET("$ROOT_PATH/resource/")
    fun resource(): Observable<APIResponse<AppConfigResource>>

    @GET("$ROOT_PATH/share/")
    fun share(@Query("url") url: String): Observable<APIResponse<AppConfigShare>>

}