package com.box.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.base.BaseListBean
import com.box.app.news.bean.list.ListChannel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class RecommendChannel(
        @Expose @SerializedName("channels") var selectedChannels: ListChannel,
        @Expose @SerializedName("recommend_channels") var recommendChannels: ListChannel
) : BaseListBean<Channel>(), Parcelable {

    companion object {
        @JvmField
        val CREATOR = PaperParcelRecommendChannel.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelRecommendChannel.writeToParcel(this, dest, flags)
    }
}