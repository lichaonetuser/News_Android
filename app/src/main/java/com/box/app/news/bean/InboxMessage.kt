package com.box.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.base.BaseBean
import com.box.app.news.bean.interfaces.IInboxMessageContentBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class InboxMessage(
        @Expose @SerializedName("message_id") var messageId: String = "",
        @Expose @SerializedName("avatar_url") var avatarUrl: String = "",
        @Expose @SerializedName("icon_url") var iconUrl: String = "",
        @Expose @SerializedName("title") var title: String = "",
        @Expose @SerializedName("description") var description: String = "",
        @Expose @SerializedName("type") var type: Int = 0,
        @Expose @SerializedName("open_url") var openUrl: String = "",
        @Expose @SerializedName("display_time") var displayTime: Long? = null,
        @Expose(deserialize = false) @SerializedName("item") var item: IInboxMessageContentBean? = null,
        @Expose(deserialize = false) @SerializedName("items") var items: ArrayList<IInboxMessageContentBean?>? = null
) : BaseBean(), Parcelable {

    companion object {
        @JvmField
        val CREATOR = PaperParcelInboxMessage.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelInboxMessage.writeToParcel(this, dest, flags)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as InboxMessage
        if (messageId != other.messageId) return false
        return true
    }

    override fun hashCode(): Int {
        return messageId.hashCode()
    }

    var needShowTimeHeader = false
}
