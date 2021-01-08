package com.mynews.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.mynews.app.news.bean.base.BaseNewsBean
import com.mynews.app.news.data.DataDictionary
import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.PrimaryKey
import com.github.gfx.android.orma.annotation.Setter
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class WorldcupMatchDetail (
        override @Expose @SerializedName("ban_flag") @Setter("ban_flag") @Column(defaultExpr = "true") var banFlag: Int = 1,
        override @Expose @SerializedName("source_name") @Setter("source_name") @Column var sourceName: String = "",
        override @Expose @SerializedName("published_at") @Setter("published_at") @Column var publishedAt: Long = 0,
        override @Expose @SerializedName("is_favorite") @Setter("is_favorite") @Column var isFavorite: Boolean = false,
        override @Expose @SerializedName("is_read") @Setter("is_read") @Column var isRead: Boolean = false,
        override @Expose @SerializedName("dig_count") @Setter("dig_count") @Column var digCount: Int = 0,
        override @Expose @SerializedName("style") @Setter("style") @Column(defaultExpr = "0") var style: Int = DataDictionary.ArticleStyle.UN_KNOW.value,
        override @Expose @SerializedName("bury_count") @Setter("bury_count") @Column var buryCount: Int = 0,
        override @Expose @SerializedName("title") @Setter("title") @Column var title: String = "",
        override @Expose @SerializedName("share_url") @Setter("share_url") @Column var shareUrl: String = "",
        override @Expose @SerializedName("emit_time") @Setter("emit_time") @Column var emitTime: Long = 0,
        override @Expose @SerializedName("display_time") @Setter("display_time") @Column var displayTime: Long = 0,
        override @Expose @SerializedName("favorite_time") @Setter("favorite_time") @Column var favoriteTime: Long = 0,
        override @Expose @SerializedName("type") @Setter("type") @Column var type: Int = DataDictionary.NewsType.WORLDCUPMATCH.value,
        override @Expose @SerializedName("source_id") @Setter("source_id") @Column var sourceId: Int = 0,
        override @Expose @SerializedName("is_buried") @Setter("is_buried") @Column var isBuried: Boolean = false,
        override @Expose @SerializedName("is_digged") @Setter("is_digged") @Column var isDigged: Boolean = false,
        override @Expose @SerializedName("source_pic") @Setter("source_pic") @Column var sourcePic: String = "",
        override @Expose @SerializedName("copyright_source_id") @Setter("copyright_source_id") @Column var copyrightSourceId: Int = 0,
        override @Expose @SerializedName("comment_type") @Setter("comment_type") @Column var commentType: Int = 0,
        override @Expose @SerializedName("comment_count") @Setter("comment_count") @Column var commentCount: Int = 0,
        override @Expose @SerializedName("aid") @Setter("aid") @PrimaryKey var aid: String = "",
        @Expose @SerializedName("id") var id: Int = 0,
        @Expose @SerializedName("date") var date: String = "",
        @Expose @SerializedName("items") var items: ArrayList<WorldcupMatch> = arrayListOf()
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
), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelWorldcupMatchDetail.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelWorldcupMatchDetail.writeToParcel(this, dest, flags)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as WorldcupMatchDetail
        if (aid != other.aid) return false
        return true
    }

    override fun hashCode(): Int {
        return aid.hashCode()
    }
}