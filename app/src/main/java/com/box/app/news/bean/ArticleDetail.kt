package com.box.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.base.BaseBean
import com.box.app.news.bean.base.BaseNewsBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class ArticleDetail(
        @Expose @SerializedName("aid") var aid: String = "",
        @Expose @SerializedName("article") var news: BaseNewsBean? = null
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelArticleDetail.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelArticleDetail.writeToParcel(this, dest, flags)
    }
}