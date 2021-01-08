package com.mynews.app.news.data.source.remote.http.api

import com.mynews.app.news.bean.ArticleHeadlineCount
import com.mynews.app.news.bean.ArticleContentWrapper
import com.mynews.app.news.bean.ArticleDetail
import com.mynews.app.news.bean.ArticleInformation
import com.mynews.app.news.bean.list.ListNews
import com.mynews.common.core.net.http.bean.APIResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ArticleAPI {

    companion object {
        const val ROOT_PATH = "article"
    }

    @GET("$ROOT_PATH/detail/")
    fun detail(@Query("aid") aid: String): Observable<APIResponse<ArticleDetail>>

    @GET("$ROOT_PATH/detail/")
    fun content(@Query("aid") aid: String): Observable<APIResponse<ArticleContentWrapper>>

    @GET("$ROOT_PATH/information/")
    fun information(@Query("aid") aid: String): Observable<APIResponse<ArticleInformation>>

    @GET("$ROOT_PATH/{pub}/{cid}/")
    fun feed(@Path("pub") pub: String,
            @Path("cid") chid: String,
             @Query("min_emit_time") minEmitTime: Long?,
             @Query("max_emit_time") maxEmitTime: Long?,
             @Query("channel_type") channelType: Int?,
             @Query("rc") refreshCount: Int?,
             @Query("lc") loadMoreCount: Int?,
             @Query("tc") totalCount: Int?): Observable<APIResponse<ListNews>>

    @GET("$ROOT_PATH/headline/tip/")
    fun headlineCount(@Query("min_emit_time") minEmitTime: Long?) : Observable<APIResponse<ArticleHeadlineCount>>

}