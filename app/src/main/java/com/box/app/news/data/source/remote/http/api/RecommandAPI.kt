package com.box.app.news.data.source.remote.http.api

import com.box.app.news.bean.InterestData
import com.box.common.core.net.http.bean.APIResponse
import io.reactivex.Observable
import retrofit2.http.*


interface RecommandAPI {

    @GET("user/unique_device_id/")
    fun getInterest(): Observable<APIResponse<InterestData>>

    @POST("interest/post/")
    fun sendInterest(@Body jsonBody:HashMap<String,Any>): Observable<APIResponse<Any>>

}