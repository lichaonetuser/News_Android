package com.mynews.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.mynews.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class CommentPostResponseInfo(
        @Expose @SerializedName("comment_id") var commentId: String = "",
        @Expose @SerializedName("code") var code: String = "",
        @Expose @SerializedName("uid") var uid: String = "",
        @Expose @SerializedName("avatar_url") var avatarUrl: String = "",
        @Expose @SerializedName("screen_name") var screenName: String = ""
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelCommentPostResponseInfo.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelCommentPostResponseInfo.writeToParcel(this, dest, flags)
    }
}