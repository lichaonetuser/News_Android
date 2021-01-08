package com.mynews.app.news.bean.list

import android.os.Parcel
import android.os.Parcelable
import com.mynews.app.news.bean.base.BaseListBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class ListHotWords(
        @Expose @SerializedName("keywords") var keywords: ArrayList<String> = arrayListOf()
) : BaseListBean<String>(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelListHotWords.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelListHotWords.writeToParcel(this, dest, flags)
    }
}