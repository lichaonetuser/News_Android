package com.box.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.base.BaseBean
import com.box.app.news.data.DataDictionary
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class LoginRequestInfo(
        @Expose @SerializedName("platform") var platform: Int = -1,
        @Expose @SerializedName("token") var token: String = "",
        @Expose @SerializedName("secret") var secret: String = "",
        @Expose @SerializedName("id_token") var idToken: String = "",
        @Expose @SerializedName("server_auth_code") var serverAuthCode: String = "",
        @Expose @SerializedName("refresh_token") var refreshToken: String = "",
        @Expose @SerializedName("code") var code: String = "",
        @Expose @SerializedName("username") var username: String = "",
        @Expose @SerializedName("password") var password: String = ""
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelLoginRequestInfo.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelLoginRequestInfo.writeToParcel(this, dest, flags)
    }
}