package com.box.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.base.BaseBean
import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.PrimaryKey
import com.github.gfx.android.orma.annotation.Setter
import com.github.gfx.android.orma.annotation.Table
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@Table
@PaperParcel
data class Channel(
        @Expose @SerializedName("chid") @Setter("chid") @PrimaryKey var chid: String = "",
        @Expose @SerializedName("name") @Setter("name") @Column var name: String = "",
        @Expose @SerializedName("url") @Setter("url") @Column var url: String = "",
        @Expose @SerializedName("channel_type") @Setter("channel_type") @Column var channelType: Int = 0,
        @Expose @SerializedName("index") @Setter("index") @Column(indexed = true) var index: Int = -1, //客户端排序
        @Expose @SerializedName("red_dot") @Column var redDot:Long = 0
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelChannel.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelChannel.writeToParcel(this, dest, flags)
    }

}
