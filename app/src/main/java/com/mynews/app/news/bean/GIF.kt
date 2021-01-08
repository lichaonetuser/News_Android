package com.mynews.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.mynews.app.news.bean.base.BaseNewsBean
import com.mynews.app.news.bean.interfaces.IInboxMessageContentBean
import com.mynews.app.news.data.DataDictionary
import com.github.gfx.android.orma.annotation.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

/**
 * Android version : 6.9 support GIF
 */
@Table
@PaperParcel
data class GIF(
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
        override @Expose @SerializedName("type") @Setter("type") @Column var type: Int = DataDictionary.NewsType.GIF.value,
        override @Expose @SerializedName("source_id") @Setter("source_id") @Column var sourceId: Int = 0,
        override @Expose @SerializedName("is_buried") @Setter("is_buried") @Column var isBuried: Boolean = false,
        override @Expose @SerializedName("is_digged") @Setter("is_digged") @Column var isDigged: Boolean = false,
        override @Expose @SerializedName("source_pic") @Setter("source_pic") @Column var sourcePic: String = "",
        override @Expose @SerializedName("copyright_source_id") @Setter("copyright_source_id") @Column var copyrightSourceId: Int = 0,
        override @Expose @SerializedName("comment_type") @Setter("comment_type") @Column var commentType: Int = 0,
        override @Expose @SerializedName("comment_count") @Setter("comment_count") @Column var commentCount: Int = 0,
        override @Expose @SerializedName("aid") @Setter("aid") @PrimaryKey var aid: String = "",

        // todo 这个应该定义在基类，具体用处不详，后面再说
        @Expose @SerializedName("display_options") @Setter("display_options") @Column var displayOptions : Int = 0,
        //测试json没有定义，用的wiki上的数据构造的model，复用的之前的model
        @Expose @SerializedName("image") @Setter("image") @Column var image : ImageInfo? = null,
        // 内容
        @Expose @SerializedName("content") @Setter("content") @Column var content : String = "",
        // 不喜欢数量
        @Expose @SerializedName("dislikes") @Setter("dislikes") @Column var dislikes : Int = 0,
        // 覆盖的图片链接
        @Expose @SerializedName("cover_image") @Setter("cover_image") @Column var coverImage : String = "",
        // 以后要用到，先存着
        @Expose @SerializedName("is_comment_hot") @Setter("is_comment_hot") @Column var isCommentHot : Boolean = false,
        // 查看 {@link com.box.app.news.data.DataDictionary.GifType}
        @Expose @SerializedName("gif_type") @Setter("gif_type") @Column var gifType : Int = DataDictionary.GifType.UNKNOWN.type,
        // 暂时没用
        @Expose @SerializedName("avatar_url") @Setter("avatar_url") @Column var avatarUrl : String = "",
        // 单独写出
        @Expose @SerializedName("original_site_url") @Setter("original_site_url") @Column var originalSiteUrl : String = "",
        // video相关数据 测试json里定义的数据跟wiki上的不一样,目前使用的是测试json版本，后期需要确认
        @Expose @SerializedName("video") @Setter("video") @Column var video : VideoInGIF? = null,
        // 喜欢数量
        @Expose @SerializedName("likes") @Setter("likes") @Column var likes : Int = 0
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
        const val GIF_TYPE_IMAGE = 1
        const val GIF_TYPE_MP4 = 2

        @JvmField
        val CREATOR = PaperParcelGIF.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelGIF.writeToParcel(this, dest, flags)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as GIF
        if (aid != other.aid) return false
        return true
    }

    override fun hashCode(): Int {
        return aid.hashCode()
    }

    var playState: Int = 0
}