package com.box.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel
import java.util.*

@PaperParcel
data class CommentPostRequestInfo(
        @Expose @SerializedName("target_type") var targetType: String = "",
        @Expose @SerializedName("target_id") var targetId: String = "",
        @Expose @SerializedName("content") var content: String = "",
        @Expose @SerializedName("anonymous") var anonymous: Boolean = true,
        @Expose @SerializedName("reply_comment_id") var replyCommentId: String? = null
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelCommentPostRequestInfo.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelCommentPostRequestInfo.writeToParcel(this, dest, flags)
    }
}