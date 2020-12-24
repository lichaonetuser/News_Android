package com.box.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class Weather(
        @Expose @SerializedName("status") var status: List<String> = listOf(),
        @Expose @SerializedName("description") var description: String = "",
        @Expose @SerializedName("probability") var probability: Int = 0,
        @Expose @SerializedName("icons") var icons: List<String> = listOf(),
        @Expose @SerializedName("city_name") var cityName: String = "",
        @Expose @SerializedName("high_temperature") var highTemperature: Int = 0,
        @Expose @SerializedName("date") var date: Long = 0, // 单位秒
        @Expose @SerializedName("low_temperature") var lowTemperature: Int = 0
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelWeather.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelWeather.writeToParcel(this, dest, flags)
    }

    fun isEmptyWeather(): Boolean {
        return status.isEmpty() || cityName.isEmpty() || date == 0L
    }

}