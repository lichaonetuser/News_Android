package com.mynews.app.news.bean

import android.os.Parcelable
import com.mynews.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LoginResponseInfo(
        @Expose @SerializedName("login_status") var login_status: Int = 0,
        @Expose @SerializedName("user") var account: Account = Account(),
        @Expose @SerializedName("code") var code: Int = 0,
        @Expose @SerializedName("msg") var msg: String = ""
) : BaseBean(), Parcelable {

}