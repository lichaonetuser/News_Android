package com.mynews.app.news.bean.list

import android.os.Parcel
import android.os.Parcelable
import com.mynews.app.news.bean.Region
import com.mynews.app.news.bean.base.BaseListBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class ListRegion(
        @Expose @SerializedName("regions") var regions: ArrayList<Region> = arrayListOf()
) : BaseListBean<Region>(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelListRegion.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelListRegion.writeToParcel(this, dest, flags)
    }
}