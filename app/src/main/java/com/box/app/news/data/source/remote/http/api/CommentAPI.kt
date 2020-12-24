package com.box.app.news.data.source.remote.http.api

import com.box.app.news.bean.ArticleUserAction
import com.box.app.news.bean.CommentDelete
import com.box.app.news.bean.CommentPostRequestInfo
import com.box.app.news.bean.CommentPostResponseInfo
import com.box.app.news.bean.list.ListComment
import com.box.common.core.net.http.bean.APIResponse
import io.reactivex.Observable
import retrofit2.http.*

interface CommentAPI {

    companion object {
        const val ROOT_PATH = "comment"
    }

    @GET("$ROOT_PATH/load/")
    fun load(@Query("target_type") targetType: String,
             @Query("target_id") targetId: String): Observable<APIResponse<ListComment>>

    @GET("$ROOT_PATH/load_more/")
    fun loadMore(@Query("target_type") targetType: String,
                 @Query("target_id") targetId: String,
                 @Query("page_no") pageNo: Int,
                 @Query("last_comment_id") lastCommentId: String): Observable<APIResponse<ListComment>>

    @POST("$ROOT_PATH/post/")
    @Headers("Content-Type: application/json", "Accept: application/json")
    fun post(@Body commentPost: CommentPostRequestInfo): Observable<APIResponse<CommentPostResponseInfo>>

    @POST("$ROOT_PATH/user_action/")
    @Headers("Content-Type: application/json", "Accept: application/json")
    fun postUserAction(@Body action: ArticleUserAction): Observable<APIResponse<Any>>

    @POST("$ROOT_PATH/delete/")
    @Headers("Content-Type: application/json", "Accept: application/json")
    fun delete(@Body commentDelete: CommentDelete): Observable<APIResponse<Any>>

    @GET("$ROOT_PATH/my_comments/")
    fun getMyComments(@Query("page_no") pageNo: Int?,
                      @Query("last_comment_id") lastCommentId: String?): Observable<APIResponse<ListComment>>

}