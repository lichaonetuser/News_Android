package com.box.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.base.BaseBean
import com.box.app.news.bean.base.BaseNewsBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class ArticleHeadlineCount(
        @Expose @SerializedName("count") var count : Int,
        @Expose @SerializedName("has_more") var hasMore : Boolean
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelArticleHeadlineCount.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelArticleHeadlineCount.writeToParcel(this, dest, flags)
    }
}