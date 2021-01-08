package com.mynews.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.mynews.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

/**
 * 用户个人资料页/user/profile/接口返回bean
 */
@PaperParcel
data class UserProfileResp(
        @Expose @SerializedName("unique_device_id") var udid: String = "",
        @Expose @SerializedName("screen_name") var screenName: String = "",
        @Expose @SerializedName("avatar_url") var avatarUrl: String = "",
        @Expose @SerializedName("gender") var gender: Int = -1,
        @Expose @SerializedName("age_stage") var ageStage: Int = -1
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelUserProfileResp.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelUserProfileResp.writeToParcel(this, dest, flags)
    }
}