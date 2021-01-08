package com.mynews.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.mynews.app.news.bean.base.BaseNewsBean
import com.mynews.app.news.bean.interfaces.IInboxMessageContentBean
import com.mynews.app.news.data.DataDictionary
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class ArticleHeadline(
        override @Expose @SerializedName("ban_flag") var banFlag: Int = 1,
        override @Expose @SerializedName("source_name")  var sourceName: String = "",
        override @Expose @SerializedName("published_at")  var publishedAt: Long = 0,
        override @Expose @SerializedName("is_favorite")  var isFavorite: Boolean = false,
        override @Expose @SerializedName("is_read")  var isRead: Boolean = false,
        override @Expose @SerializedName("dig_count") var digCount: Int = 0,
        override @Expose @SerializedName("style")  var style: Int = DataDictionary.ArticleStyle.UN_KNOW.value,
        override @Expose @SerializedName("bury_count")  var buryCount: Int = 0,
        override @Expose @SerializedName("title")  var title: String = "",
        override @Expose @SerializedName("share_url")  var shareUrl: String = "",
        override @Expose @SerializedName("emit_time")  var emitTime: Long = 0,
        override @Expose @SerializedName("display_time") var displayTime: Long = 0,
        override @Expose @SerializedName("favorite_time")  var favoriteTime: Long = 0,
        override @Expose @SerializedName("type")  var type: Int = 0,
        override @Expose @SerializedName("source_id")  var sourceId: Int = 0,
        override @Expose @SerializedName("is_buried")  var isBuried: Boolean = false,
        override @Expose @SerializedName("is_digged")  var isDigged: Boolean = false,
        override @Expose @SerializedName("source_pic")  var sourcePic: String = "",
        override @Expose @SerializedName("copyright_source_id")  var copyrightSourceId: Int = 0,
        override @Expose @SerializedName("comment_type")  var commentType: Int = 0,
        override @Expose @SerializedName("comment_count")  var commentCount: Int = 0,
        override @Expose @SerializedName("aid")  var aid: String = "",

        @Expose @SerializedName("open_url")  var openUrl : String = "",
        @Expose @SerializedName("icon")  var icon : String = "",
        @Expose @SerializedName("items") var news: ArrayList<Article> = arrayListOf(),
        @Expose @SerializedName("action_title")  var actionTitle : String = ""
) : BaseNewsBean(
        sourceName = sourceName,
        publishedAt = publishedAt,
        isFavorite = isFavorite,
        digCount = digCount,
        style = style,
        buryCount = buryCount,
        title = title,
        shareUrl = shareUrl,
        emitTime = emitTime,
        displayTime = displayTime,
        favoriteTime = favoriteTime,
        type = type,
        sourceId = sourceId,
        isBuried = isBuried,
        isDigged = isDigged,
        isRead = isRead,
        sourcePic = sourcePic,
        copyrightSourceId = copyrightSourceId,
        commentType = commentType,
        commentCount = commentCount,
        aid = aid
), IInboxMessageContentBean, Parcelable {

    companion object {
        @JvmField
        val CREATOR = PaperParcelArticleHeadline.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelArticleHeadline.writeToParcel(this, dest, flags)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ArticleHeadline
        if (aid != other.aid) return false
        return true
    }

    override fun hashCode(): Int {
        return aid.hashCode()
    }

}