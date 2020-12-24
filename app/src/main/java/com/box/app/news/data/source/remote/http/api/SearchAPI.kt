package com.box.app.news.data.source.remote.http.api

import com.box.app.news.bean.list.ListHotWords
import com.box.common.core.net.http.bean.APIResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchAPI {

    companion object {
        const val ROOT_PATH = "search"
    }

    @GET("$ROOT_PATH/hot_words/")
    fun getHotWords(@Query("type") type: Int): Observable<APIResponse<ListHotWords>>

}