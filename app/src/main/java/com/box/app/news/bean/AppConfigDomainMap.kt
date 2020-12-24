package com.box.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class AppConfigDomainMap(
        @Expose @SerializedName("original_domain") var originalDomain: String = "",
        @Expose @SerializedName("target_domain") var targetDomain: String = ""
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelAppConfigDomainMap.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelAppConfigDomainMap.writeToParcel(this, dest, flags)
    }
}