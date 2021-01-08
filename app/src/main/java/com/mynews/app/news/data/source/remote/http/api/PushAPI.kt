package com.mynews.app.news.data.source.remote.http.api

import com.mynews.app.news.bean.SubmitPushToken
import com.mynews.common.core.net.http.bean.APIResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface PushAPI {

    companion object {
        const val ROOT_PATH = "push"
    }

    @POST("$ROOT_PATH/token/submit/")
    @Headers("Content-Type: application/json", "Accept: application/json")
    fun submitToken(@Body submitPushToken: SubmitPushToken): Observable<APIResponse<Any>>

}