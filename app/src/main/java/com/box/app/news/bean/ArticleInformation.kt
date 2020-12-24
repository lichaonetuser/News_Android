package com.box.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.base.BaseBean
import com.box.app.news.bean.base.BaseNewsBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class ArticleInformation(
        @Expose @SerializedName("article") var detail: ArticleInformationDetail = ArticleInformationDetail(),
        // @Expose @SerializedName("ads") var ads: Ads = Ads(),
        @Expose @SerializedName("related") var related: ArrayList<BaseNewsBean?> = arrayListOf()
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelArticleInformation.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelArticleInformation.writeToParcel(this, dest, flags)
    }
}


