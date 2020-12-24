package com.box.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

/**
 * 用户个人资料页/user/profile/接口返回bean的外层
 */
@PaperParcel
data class UserProfileWrapper(
        @Expose @SerializedName("user") var user: UserProfileResp = UserProfileResp()
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelUserProfileWrapper.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelUserProfileWrapper.writeToParcel(this, dest, flags)
    }
}