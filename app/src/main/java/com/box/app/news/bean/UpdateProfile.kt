package com.box.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class UpdateProfile(
        @Expose @SerializedName("image_uri") var imageUri: String? = null,
        @Expose @SerializedName("screen_name") var screenName: String? = null
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelUpdateProfile.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelUpdateProfile.writeToParcel(this, dest, flags)
    }
}