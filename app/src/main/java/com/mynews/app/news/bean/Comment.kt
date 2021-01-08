package com.mynews.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.mynews.app.news.bean.base.BaseBean
import com.mynews.app.news.bean.interfaces.IInboxMessageContentBean
import com.mynews.app.news.data.DataDictionary
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class Comment(
        @Expose @SerializedName("status") var status: Int = 0,
        @Expose @SerializedName("uid") var uid: String = "",
        @Expose @SerializedName("target_id") var targetId: String = "",
        @Expose @SerializedName("reply_id") var replyId: String = "",
        @Expose @SerializedName("dig_count") var digCount: Int = 0,
        @Expose @SerializedName("anonymous") var anonymous: Boolean = false,
        @Expose @SerializedName("id") var id: String = "",
        @Expose @SerializedName("is_digged") var isDigged: Boolean = false,
        @Expose @SerializedName("screen_name") var screenName: String = "",
        @Expose @SerializedName("ctime") var ctime: Long = 0,
        @Expose @SerializedName("content") var content: String = "",
        @Expose @SerializedName("platform") var platform: Int = 0,
        @Expose @SerializedName("avatar_url") var avatarUrl: String = "",
        @Expose @SerializedName("homepage") var homepage: String = "",
        @Expose @SerializedName("reply") var reply: Comment? = null,
        @Expose @SerializedName("user") var user: Account? = null,
        @Expose @SerializedName("delete_content") var deleteContent: Int = DataDictionary.DeleteContentType.NOT_DELETE.value,
        //服务端保证以下model只会返回其中一种，判断是否null的先后顺序随意
        @Expose @SerializedName("article") var article: Article? = null,
        @Expose @SerializedName("image") var image: Image? = null,
        @Expose @SerializedName("video") var video: Video? = null,
        @Expose @SerializedName("gif") var gif: GIF? = null,
        @Expose @SerializedName("essay") var essay: Essay? = null,
        @Expose @SerializedName("match") var match: WorldcupMatch? = null,
        @Expose @SerializedName("team") var team: WorldcupTeam? = null,
        @Expose @SerializedName("player") var player: WorldcupPlayer? = null
) : BaseBean(), IInboxMessageContentBean, Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelComment.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelComment.writeToParcel(this, dest, flags)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Comment
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    fun updateInfo(comment: Comment) {
        this.isDigged = comment.isDigged
        this.digCount = comment.digCount
    }

}