package com.mynews.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.mynews.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class ArticleUserAction(@Expose @SerializedName("ops") var Ops: List<Ops> = listOf())
    : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelArticleUserAction.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelArticleUserAction.writeToParcel(this, dest, flags)
    }
}