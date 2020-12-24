package com.box.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.base.BaseNewsBean
import com.box.app.news.bean.interfaces.IInboxMessageContentBean
import com.box.app.news.data.DataDictionary
import com.box.common.core.json.gson.util.CoreGsonUtils
import com.github.gfx.android.orma.annotation.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import paperparcel.PaperParcel

@Table
@PaperParcel
data class Video(
        override @Expose @SerializedName("ban_flag") @Setter @Column(value = "ban_flag", defaultExpr = "true") var banFlag: Int = 1,
        override @Expose @SerializedName("source_name") @Setter @Column("source_name") var sourceName: String = "",
        override @Expose @SerializedName("published_at") @Setter @Column("published_at") var publishedAt: Long = 0,
        override @Expose @SerializedName("is_favorite") @Setter @Column("is_favorite") var isFavorite: Boolean = false,
        override @Expose @SerializedName("is_read") @Setter @Column("is_read") var isRead: Boolean = false,
        override @Expose @SerializedName("dig_count") @Setter @Column("dig_count") var digCount: Int = 0,
        override @Expose @SerializedName("style") @Setter @Column(value = "style", defaultExpr = "0") var style: Int = DataDictionary.ArticleStyle.UN_KNOW.value,
        override @Expose @SerializedName("bury_count") @Setter @Column("bury_count") var buryCount: Int = 0,
        override @Expose @SerializedName("title") @Setter @Column("title") var title: String = "",
        override @Expose @SerializedName("share_url") @Setter @Column("share_url") var shareUrl: String = "",
        override @Expose @SerializedName("emit_time") @Setter @Column("emit_time") var emitTime: Long = 0,
        override @Expose @SerializedName("display_time") @Setter @Column("display_time") var displayTime: Long = 0,
        override @Expose @SerializedName("favorite_time") @Setter @Column("favorite_time") var favoriteTime: Long = 0,
        override @Expose @SerializedName("type") @Setter @Column("type") var type: Int = 0,
        override @Expose @SerializedName("source_id") @Setter @Column("source_id") var sourceId: Int = 0,
        override @Expose @SerializedName("is_buried") @Setter @Column("is_buried") var isBuried: Boolean = false,
        override @Expose @SerializedName("is_digged") @Setter @Column("is_digged") var isDigged: Boolean = false,
        override @Expose @SerializedName("source_pic") @Setter @Column("source_pic") var sourcePic: String = "",
        override @Expose @SerializedName("copyright_source_id") @Setter @Column("copyright_source_id") var copyrightSourceId: Int = 0,
        override @Expose @SerializedName("comment_type") @Setter @Column("comment_type") var commentType: Int = 0,
        override @Expose @SerializedName("comment_count") @Setter @Column("comment_count") var commentCount: Int = 0,
        override @Expose @SerializedName("aid") @Setter @PrimaryKey var aid: String = "",
        @Expose @SerializedName("cover_image") @Setter @Column("cover_image") var coverImage: String = "",
        @Expose @SerializedName("copyright_type") @Setter @Column("copyright_type") var copyrightType: Int = 0,
        @Expose @SerializedName("duration_interval") @Setter @Column("duration_interval") var durationInterval: Long = 0,
        @Expose @SerializedName("hd_url") @Setter @Column("hd_url") var hdUrl: String = "",
        @Expose @SerializedName("description") @Setter @Column("description") var description: String = "",
        @Expose @SerializedName("sd_url") @Setter @Column("sd_url") var sdUrl: String = "",
        @Expose @SerializedName("normal_url") @Setter @Column("normal_url") var normalUrl: String = "",
        @Expose @SerializedName("y_video_id") @Setter @Column("y_video_id") var yVideoId: String = "",
        @Expose @SerializedName("source_type") @Setter @Column("source_type") var sourceType: Int = 0,
        @Expose @SerializedName("channel_name") @Setter @Column("channel_name") var channelName: String = "",
        @Expose @SerializedName("avatar_url") @Setter @Column("avatar_url") var avatarUrl: String = "",
        @Expose @SerializedName("original_site_url") @Setter @Column("original_site_url") var originalSiteUrl: String = "",
        @Expose @SerializedName("tags") @Setter @Column var tags: ArrayList<Tag> = arrayListOf()
) : IInboxMessageContentBean, BaseNewsBean(
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
        val CREATOR = PaperParcelVideo.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelVideo.writeToParcel(this, dest, flags)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Video
        if (aid != other.aid) return false
        return true
    }

    override fun hashCode(): Int {
        return aid.hashCode()
    }

//    @Setter("is_buried")
//    fun ormaSetBuried(isBuried: Boolean) {
//        this.isBuried = isBuried
//    }
//
//    @Setter("is_digged")
//    fun ormaSetDigged(isDigged: Boolean) {
//        this.isDigged = isDigged
//    }
//
//    @Setter("is_favorite")
//    fun ormaSetFavorite(isFavorite: Boolean) {
//        this.isFavorite = isFavorite
//    }
//
//    @Setter("is_read")
//    fun ormaSetRead(isRead: Boolean) {
//        this.isRead = isRead
//    }
}