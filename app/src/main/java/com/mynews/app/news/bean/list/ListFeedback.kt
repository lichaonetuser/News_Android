package com.mynews.app.news.bean.list

import android.os.Parcel
import android.os.Parcelable
import com.mynews.app.news.bean.Feedback
import com.mynews.app.news.bean.base.BaseListBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class ListFeedback(
        @Expose @SerializedName("feedbacks") var feedbacks: ArrayList<Feedback> = arrayListOf()
) : BaseListBean<Feedback>(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelListFeedback.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelListFeedback.writeToParcel(this, dest, flags)
    }
}