package com.box.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.base.BaseNewsBean
import com.box.app.news.bean.interfaces.IInboxMessageContentBean
import com.box.app.news.data.DataDictionary
import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.PrimaryKey
import com.github.gfx.android.orma.annotation.Setter
import com.github.gfx.android.orma.annotation.Table
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@Table
@PaperParcel
open class Article(
        override @Expose @SerializedName("ban_flag") @Setter @Column(defaultExpr = "true") var banFlag: Int = 1,
        override @Expose @SerializedName("source_name") @Setter @Column var sourceName: String = "",
        override @Expose @SerializedName("published_at") @Setter @Column var publishedAt: Long = 0,
        override @Expose @SerializedName("is_favorite") @Setter @Column var isFavorite: Boolean = false,
        override @Expose @SerializedName("is_read") @Setter @Column var isRead: Boolean = false,
        override @Expose @SerializedName("dig_count") @Setter @Column var digCount: Int = 0,
        override @Expose @SerializedName("style") @Setter @Column(defaultExpr = "0") var style: Int = DataDictionary.ArticleStyle.UN_KNOW.value,
        override @Expose @SerializedName("bury_count") @Setter @Column var buryCount: Int = 0,
        override @Expose @SerializedName("title") @Setter @Column var title: String = "",
        override @Expose @SerializedName("share_url") @Setter @Column var shareUrl: String = "",
        override @Expose @SerializedName("emit_time") @Setter @Column var emitTime: Long = 0,
        override @Expose @SerializedName("display_time") @Setter @Column var displayTime: Long = 0,
        override @Expose @SerializedName("favorite_time") @Setter @Column var favoriteTime: Long = 0,
        override @Expose @SerializedName("type") @Setter @Column var type: Int = 0,
        override @Expose @SerializedName("source_id") @Setter @Column var sourceId: Int = 0,
        override @Expose @SerializedName("is_buried") @Setter @Column var isBuried: Boolean = false,
        override @Expose @SerializedName("is_digged") @Setter @Column var isDigged: Boolean = false,
        override @Expose @SerializedName("source_pic") @Setter @Column var sourcePic: String = "",
        override @Expose @SerializedName("copyright_source_id") @Setter @Column var copyrightSourceId: Int = 0,
        override @Expose @SerializedName("comment_type") @Setter @Column var commentType: Int = 0,
        override @Expose @SerializedName("comment_count") @Setter @Column var commentCount: Int = 0,
        override @Expose @SerializedName("aid") @Setter @PrimaryKey var aid: String = "",
        @Expose @SerializedName("is_comment_hot") @Setter @Column var isCommentHot: Boolean = false,
        @Expose @SerializedName("newstime_status") @Setter @Column var newstimeStatus: Int = 0,
        @Expose @SerializedName("display_options") @Setter @Column var displayOptions: Int = 0,
        @Expose @SerializedName("detail_display_options") @Setter @Column var detailDisplayOptions: Int = 0,
        @Expose @SerializedName("detail_image_urls") @Setter @Column var detailImageUrls: List<String> = listOf(),
        @Expose @SerializedName("ban_comment") @Setter @Column var banComment: Boolean = false,
        @Expose @SerializedName("related") @Setter @Column var related: List<String> = listOf(),
        @Expose @SerializedName("list_image_urls") @Setter @Column var listImageUrls: List<String> = listOf(),
        @Expose @SerializedName("list_images") @Setter @Column open var listImages: RatioImagesInfo = RatioImagesInfo(),
        @Expose @SerializedName("url") @Setter @Column var url: String = "",
        @Expose @SerializedName("cr_type") @Setter @Column var crType: Int = DataDictionary.ArticleCopyrightType.LITE.value,
        @Expose @SerializedName("bignews_status") @Setter @Column var bignewsStatus: Int = 0,
        @Expose @SerializedName("content") @Setter @Column var content: String = "",
        @Expose @SerializedName("fe_flag") @Setter @Column var feFlag: String = "",
        @Column(defaultExpr = "0") @Setter var updateTime: Long = 0L,
        @Expose @SerializedName("tags") @Setter @Column var tags: ArrayList<Tag> = arrayListOf()
) : IInboxMessageContentBean,BaseNewsBean(
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
        val CREATOR = PaperParcelArticle.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelArticle.writeToParcel(this, dest, flags)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Article
        if (aid != other.aid) return false
        return true
    }

    override fun hashCode(): Int {
        return aid.hashCode()
    }
}