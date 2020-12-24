package com.box.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.base.BaseBean
import com.box.app.news.data.DataDictionary
import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.Setter
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class ArticleInformationDetail(
        @Expose @SerializedName("bury_count") var buryCount: Int = 0,
        @Expose @SerializedName("comment_count") var commentCount: Int = 0,
        @Expose @SerializedName("is_favorite") var isFavorite: Boolean = false,
        @Expose @SerializedName("dig_count") var digCount: Int = 0,
        @Expose @SerializedName("aid") var aid: String = "",
        @Expose @SerializedName("is_digged") var isDigged: Boolean = false,
        @Expose @SerializedName("delete_content") var deleteContent: Int = DataDictionary.DeleteContentType.NOT_DELETE.value,
        @Expose @SerializedName("is_buried") var isBuried: Boolean = false,
        @Expose @SerializedName("forwardable") var forwardable: Boolean = false
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelArticleInformationDetail.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelArticleInformationDetail.writeToParcel(this, dest, flags)
    }
}
