package com.box.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class SportsStatus(
        @Expose @SerializedName("is_fresh") var isFresh: Boolean = false
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelSportsStatus.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelSportsStatus.writeToParcel(this, dest, flags)
    }
}