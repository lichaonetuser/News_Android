package com.mynews.app.news.data.source.remote.http.api

import com.mynews.app.news.bean.ReportPostRequestInfo
import com.mynews.common.core.net.http.bean.APIResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface PublicReportAPI {

    companion object {
        const val ROOT_PATH = "public_report"
    }

    @POST("$ROOT_PATH/")
    @Headers("Content-Type: application/json", "Accept: application/json")
    fun post(@Body post: ReportPostRequestInfo): Observable<APIResponse<Any>>

}