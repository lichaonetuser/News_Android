package com.box.app.news.data.source.remote.http.api

import com.box.app.news.bean.DeviceAppRequestInfo
import com.box.common.core.net.http.bean.APIResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AdhocAPI {

    companion object {
        const val ROOT_PATH = "adhoc"
    }

    @POST("$ROOT_PATH/device/app/")
    @Headers("Content-Type: application/json", "Accept: application/json")
    fun deviceApp(@Body deviceApp: DeviceAppRequestInfo): Observable<APIResponse<Any>>

}