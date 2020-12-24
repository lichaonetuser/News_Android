package com.box.app.news.data.source.remote.http.api

import com.box.app.news.bean.list.ListFavorite
import com.box.common.core.net.http.bean.APIResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query


interface CollectAPI {

    companion object {
        const val ROOT_PATH = "favorite"
    }

    @GET("$ROOT_PATH/")
    fun getFavorite(@Query("page_no") pageNo: Int?): Observable<APIResponse<ListFavorite>>

}