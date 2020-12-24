package com.box.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.base.BaseBean
import com.box.app.news.data.DataDictionary
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class Account(
        @Expose @SerializedName("uid") var uid: String = "",
        @Expose @SerializedName("screen_name") var screenName: String = "",
        @Expose @SerializedName("avatar_url") var avatarUrl: String = "",
        @Expose @SerializedName("gender") var gender: Int = DataDictionary.SelectInfoObject.UNSET,
        @Expose @SerializedName("email") var email: String = "",
        @Expose @SerializedName("birthday") var birthday: Long = 0L,
        @Expose @SerializedName("phone") var phone: String = "",
        @Expose @SerializedName("ageStage")var ageStage:Int = DataDictionary.SelectInfoObject.UNSET
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelAccount.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelAccount.writeToParcel(this, dest, flags)
    }
}