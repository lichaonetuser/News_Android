package com.mynews.app.news.data.source.remote.http.api

import com.mynews.app.news.bean.*
import com.mynews.common.core.net.http.bean.APIResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface UserAPI {

    companion object {
        const val ROOT_PATH = "user"
    }

    @GET("$ROOT_PATH/unique_device_id/")
    fun getUniqueDeviceId(): Observable<APIResponse<User>>

    @POST("$ROOT_PATH/login/")
    @Headers("Content-Type: application/json", "Accept: application/json")
    fun login(@Body info: LoginRequestInfo): Observable<APIResponse<LoginResponseInfo>>

    @POST("$ROOT_PATH/logout/")
    @Headers("Content-Type: application/json", "Accept: application/json")
    fun logout(): Observable<APIResponse<LogoutResponseInfo>>

    @POST("$ROOT_PATH/update_profile/")
    @Headers("Content-Type: application/json", "Accept: application/json")
    fun updateProfile(@Body profile: UpdateProfile): Observable<APIResponse<Account>>

    @POST("$ROOT_PATH/sync_profile_status/")
    fun syncProfileStatus(): Observable<APIResponse<SyncProfileStatus>>

    @POST("$ROOT_PATH/select_info/")
    @Headers("Content-Type: application/json", "Accept: application/json")
    fun selectInfo(@Body selectInfo: SelectInfo): Observable<APIResponse<SelectInfo>>

    @GET("$ROOT_PATH/profile/")
    @Headers("Content-Type: application/json", "Accept: application/json")
    fun profile(): Observable<APIResponse<UserProfileWrapper>>

}