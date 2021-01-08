package com.mynews.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.mynews.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class ExtraInfo(
        @Expose @SerializedName("c_e") var cE: String? = null,
        @Expose @SerializedName("c_l") var cL: String? = null,
        @Expose @SerializedName("c_rn") var cRN: String? = null,
        @Expose @SerializedName("c_ri") var cRI: String? = null,
        @Expose @SerializedName("enter_time") var enterTime: Long? = null,
        @Expose @SerializedName("duration") var duration: Long? = null,
        @Expose @SerializedName("is_open_url_track") var isOpenUrlTrack: Boolean? = null,
        @Expose @SerializedName("url") var url: String? = null,
        @Expose @SerializedName("count") var count: Int? = null
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelExtraInfo.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelExtraInfo.writeToParcel(this, dest, flags)
    }
}