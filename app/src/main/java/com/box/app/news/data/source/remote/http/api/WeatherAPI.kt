package com.box.app.news.data.source.remote.http.api

import com.box.app.news.bean.City
import com.box.app.news.bean.WeatherInfo
import com.box.app.news.bean.list.ListRegion
import com.box.common.core.net.http.bean.APIResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface WeatherAPI {

    companion object {
        const val ROOT_PATH = "weather"
    }

//    @GET("$ROOT_PATH/get_simple_info/")
//    fun getSimpleInfo(@Query("city_code") cityCode: String): Observable<APIResponse<WeatherInfo>>

    @GET("$ROOT_PATH/get_city_list/")
    @Headers("Accept-encoding: gzip, deflate")
    fun getCityList(): Observable<APIResponse<ListRegion>>

    @GET("$ROOT_PATH/check_city/")
    fun checkCity(@Query("postal_code") postalCode: String?,
                  @Query("thoroughfare") thoroughfare: String?,
                  @Query("sub_thoroughfare") subThoroughfare: String?,
                  @Query("locality") locality: String?,
                  @Query("sub_locality") subLocality: String?,
                  @Query("administrative_area") administrativeArea: String?,
                  @Query("sub_administrative_area") subAdministrativeArea: String?,
                  @Query("iso_country_code") isoCountryCode: String?,
                  @Query("country") country: String?): Observable<APIResponse<City>>


}