package com.box.app.news.data.source.remote.http.api

import com.box.app.news.bean.VideoUrls
import com.box.common.core.net.http.bean.APIResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface VideoAPI {

    companion object {
        const val ROOT_PATH = "video"
    }

    @GET("$ROOT_PATH/url/")
    fun url(@Query("y_video_id") youTubeId: String): Observable<APIResponse<VideoUrls>>

}