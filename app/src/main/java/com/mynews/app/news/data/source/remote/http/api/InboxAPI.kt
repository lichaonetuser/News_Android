package com.mynews.app.news.data.source.remote.http.api

import com.mynews.app.news.bean.InboxCountResponse
import com.mynews.app.news.bean.list.ListInbox
import com.mynews.common.core.net.http.bean.APIResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface InboxAPI {

    companion object {
        const val ROOT_PATH = "inbox"
    }

    @GET("$ROOT_PATH/load/")
    fun load(@Query("type") type: Int,
             @Query("last_id") lastId: String?): Observable<APIResponse<ListInbox>>

    @GET("$ROOT_PATH/push_history/")
    fun loadPush(@Query("last_id") lastId: String?): Observable<APIResponse<ListInbox>>

    @GET("$ROOT_PATH/count/")
    fun count(): Observable<APIResponse<InboxCountResponse>>

}