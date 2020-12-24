package com.box.app.news.data.source.remote.http.api

import com.box.app.news.bean.ChannelInput
import com.box.app.news.bean.RecommendChannel
import com.box.common.core.net.http.bean.APIResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ChannelAPI {

    companion object {
        const val ROOT_PATH = "channel"
    }

    //正常请求服务端的频道列表
    @GET("$ROOT_PATH/get_my_channel_list/v2/?use_server_config=0")
    fun list(): Observable<APIResponse<RecommendChannel>>

    //当用户更改完性别年龄后，并选择服务端推荐频道
    @GET("$ROOT_PATH/get_my_channel_list/v2/?use_server_config=1")
    fun serverList(): Observable<APIResponse<RecommendChannel>>

    @POST("$ROOT_PATH/save_my_channel_list/")
    fun saveClientChannelList(@Body input:ChannelInput):Observable<APIResponse<Any>>

}