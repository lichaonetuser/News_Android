package com.box.app.news.data.source.remote.http.api

import com.box.app.news.bean.ArticleUserAction
import com.box.common.core.net.http.bean.APIResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface UserActionAPI {

    companion object {
        const val ROOT_PATH = "user_action"
    }

    @POST("$ROOT_PATH/article/")
    @Headers("Content-Type: application/json", "Accept: application/json")
    fun postArticleUserAction(@Body action: ArticleUserAction): Observable<APIResponse<Any>>

}