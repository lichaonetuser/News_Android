package com.box.app.news.data.source.remote.http.api.global

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface DownloadGlobalAPI {

    @Streaming
    @GET
    fun download(@Url url: String): Observable<ResponseBody>

}