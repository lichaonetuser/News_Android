package com.mynews.app.news.bean.list

import android.os.Parcel
import android.os.Parcelable
import com.mynews.app.news.bean.Channel
import com.mynews.app.news.bean.base.BaseListBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class ListChannel(
        @Expose @SerializedName("channels") var articleChannels: ArrayList<Channel> = arrayListOf(),
        @Expose @SerializedName("v_channels") var videoChannels: ArrayList<Channel> = arrayListOf()
//        @Expose @SerializedName("worldcup_channels") var worldcupChannels: ArrayList<Channel> = arrayListOf()
) : BaseListBean<Channel>(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelListChannel.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelListChannel.writeToParcel(this, dest, flags)
    }
}
