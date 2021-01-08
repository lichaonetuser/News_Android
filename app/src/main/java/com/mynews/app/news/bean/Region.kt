package com.mynews.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.mynews.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class Region(
        @Expose @SerializedName("name") var name: String = "",
        @Expose @SerializedName("city_code") var cityCode: String = "",
        @Expose @SerializedName("sub_regions") var subRegions: ArrayList<Region> = arrayListOf()
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelRegion.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelRegion.writeToParcel(this, dest, flags)
    }

    fun isEmptyRegion(): Boolean {
        return name.isEmpty() || cityCode.isEmpty()
    }

}
