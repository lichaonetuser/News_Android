package com.box.app.news.bean.list

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.Channel
import com.box.app.news.bean.Comment
import com.box.app.news.bean.base.BaseListBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class ListComment(
        @Expose @SerializedName("has_more_hot") var hasMoreHot: Boolean = false,
        @Expose @SerializedName("comments") var comments: ArrayList<Comment> = arrayListOf(),
        @Expose @SerializedName("hot_comments") var hotComments: ArrayList<Comment> = arrayListOf()
) : BaseListBean<Channel>(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelListComment.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelListComment.writeToParcel(this, dest, flags)
    }
}
