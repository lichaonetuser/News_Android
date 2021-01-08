package com.mynews.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.mynews.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class ArticleContentWrapper(
        @Expose @SerializedName("aid") var aid: String = "",
        @Expose @SerializedName("article") var articleContent: ArticleContent = ArticleContent()
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelArticleContentWrapper.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelArticleContentWrapper.writeToParcel(this, dest, flags)
    }
}