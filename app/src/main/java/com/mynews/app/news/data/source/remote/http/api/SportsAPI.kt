package com.mynews.app.news.data.source.remote.http.api

import com.mynews.app.news.bean.*
import com.mynews.common.core.net.http.bean.APIResponse
import io.reactivex.Observable
import retrofit2.http.*

interface SportsAPI {

    companion object {
        const val ROOT_PATH = "sports"
    }

    @POST("$ROOT_PATH/user_action/")
    @Headers("Content-Type: application/json", "Accept: application/json")
    fun postUserAction(@Body action: SportsUserAction): Observable<APIResponse<Any>>

    @GET("$ROOT_PATH/status/")
    fun status(): Observable<APIResponse<SportsStatus>>

    @GET("$ROOT_PATH/board/")
    fun board(@Query("page_no") pageNo: Int,
              @Query("chid") chid: String,
              @Query("last_comment_id") lastCommentId: String?,
              @Query("type") type: Int?): Observable<APIResponse<WorldCupBoard>>

    @GET("$ROOT_PATH/homepage/match/")
    fun getMatch(@Query("match_id") matchId: String): Observable<APIResponse<WorldcupMatch>>

    @GET("$ROOT_PATH/homepage/player/")
    fun getPlayer(@Query("player_id") playerId: String): Observable<APIResponse<WorldcupPlayer>>

    @GET("$ROOT_PATH/homepage/team/")
    fun getTeam(@Query("team_id") teamId: String): Observable<APIResponse<WorldcupTeam>>

}