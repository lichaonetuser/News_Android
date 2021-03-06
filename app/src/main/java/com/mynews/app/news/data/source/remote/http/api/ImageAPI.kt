package com.mynews.app.news.data.source.remote.http.api

import com.mynews.app.news.bean.UploadImageResult
import com.mynews.common.core.net.http.bean.APIResponse
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ImageAPI {

    companion object {
        const val ROOT_PATH = "image"
    }

    @Multipart
    @POST("$ROOT_PATH/upload/")
    fun upload(@Part file: MultipartBody.Part): Observable<APIResponse<UploadImageResult>>
}