package com.box.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class AppConfigShare(
        @Expose @SerializedName("share") var share: Share? = null
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelAppConfigShare.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelAppConfigShare.writeToParcel(this, dest, flags)
    }
}
