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

/**
 * 段子模型
 * 7.1 需求：新增段子频道
 */
@Table
@PaperParcel
open class Essay(
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
        @Expose @SerializedName("text") @Setter @Column var text: String = "",
        @Expose @SerializedName("url") @Setter @Column var url: String = "",
        @Expose @SerializedName("original_site_url") @Setter @Column var original_site_url: String = "",
        @Expose @SerializedName("tags") @Setter @Column var tags: ArrayList<Tag> = arrayListOf(),
        @Expose @SerializedName("likes") @Setter @Column var likes: Int = 0,
        @Expose @SerializedName("dislikes") @Setter @Column var dislikes: Int = 0,
        @Expose @SerializedName("item_id") @Setter @Column var itemId: Int = 0,
        @Expose @SerializedName("source_group_id") @Setter @Column var sourceGroup_id: Int = 0,
        @Expose @SerializedName("copyright_type") @Setter @Column var copyrightType: Int = 0
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
    var isExpanded:Boolean = false

    companion object {
        @JvmField
        val CREATOR = PaperParcelEssay.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelEssay.writeToParcel(this, dest, flags)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Essay
        if (aid != other.aid) return false
        return true
    }

    override fun hashCode(): Int {
        return aid.hashCode()
    }
}