package com.box.app.news.data.source.remote.http.api

import com.box.app.news.bean.FeedbackMessage
import com.box.app.news.bean.FeedbackUnread
import com.box.app.news.bean.list.ListFeedback
import com.box.common.core.net.http.bean.APIResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface FeedbackAPI {

    companion object {
        const val ROOT_PATH = "feedback"
    }

    @GET("$ROOT_PATH/has_unread/")
    fun getHasUnread(): Observable<APIResponse<FeedbackUnread>>

    @GET("$ROOT_PATH/get_message/")
    fun getMessage(): Observable<APIResponse<ListFeedback>>

    @POST("$ROOT_PATH/send_message/")
    @Headers("Content-Type: application/json", "Accept: application/json")
    fun sendMessage(@Body message: FeedbackMessage)
            : Observable<APIResponse<Any>>
}