package com.box.app.news.data.source.remote.http.api

import com.box.app.news.bean.PLinkRequest
import com.box.app.news.bean.PLinkResponse
import com.box.common.core.net.http.bean.APIResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface PLinkAPI {

    companion object {
        const val ROOT_PATH = "plink"
    }

    @POST("$ROOT_PATH/report/")
    @Headers("Content-Type: application/json", "Accept: application/json")
    fun report(@Body pLinkRequest: PLinkRequest): Observable<APIResponse<PLinkResponse>>

}