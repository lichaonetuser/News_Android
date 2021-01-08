package com.mynews.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.mynews.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class InboxCountResponse(
        @Expose @SerializedName("message_count") var messageCount: Int = 0,
        @Expose @SerializedName("push_history_count") var pushHistoryCount: Int = 0
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelInboxCountResponse.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelInboxCountResponse.writeToParcel(this, dest, flags)
    }
}