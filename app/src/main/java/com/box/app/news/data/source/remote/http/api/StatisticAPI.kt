package com.box.app.news.data.source.remote.http.api

import com.box.common.core.net.http.bean.APIResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface StatisticAPI {

    companion object {
        const val ROOT_PATH = "stats"
    }

    @GET("$ROOT_PATH/au/")
    fun report(@Query("e") type: Int): Observable<APIResponse<Any>>
}