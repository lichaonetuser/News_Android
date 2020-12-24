package com.box.app.news.bean.base

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.ArticleInformationDetail
import paperparcel.PaperParcel

@PaperParcel
open class BaseNewsBean(
        open var banFlag: Int = 1,
        open var sourceName: String,
        open var publishedAt: Long,
        open var isFavorite: Boolean,
        open var isRead: Boolean,
        open var digCount: Int,
        open var style: Int,
        open var buryCount: Int,
        open var title: String,
        open var shareUrl: String,
        open var emitTime: Long,
        open var displayTime: Long,
        open var type: Int,
        open var sourceId: Int,
        open var isBuried: Boolean,
        open var isDigged: Boolean,
        open var sourcePic: String,
        open var favoriteTime: Long,
        open var copyrightSourceId: Int,
        open var commentType: Int,
        open var commentCount: Int,
        open var aid: String = "",
        open var showTimerTitle : Boolean = false,
        open var headline : Boolean = false
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelBaseNewsBean.CREATOR

        const val FROM_VIDEO_DEATIL = 1
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelBaseNewsBean.writeToParcel(this, dest, flags)
    }

    fun updateInformation(info: ArticleInformationDetail) {
        this.buryCount = info.buryCount
        this.isBuried = info.isBuried
        this.isDigged = info.isDigged
        this.digCount = info.digCount
        this.isFavorite = info.isFavorite
        this.commentCount = info.commentCount
    }

    fun updateInformation(news: BaseNewsBean) {
        this.buryCount = news.buryCount
        this.isBuried = news.isBuried
        this.isDigged = news.isDigged
        this.digCount = news.digCount
        this.isFavorite = news.isFavorite
        this.commentCount = news.commentCount
    }

    var from: Int = 0

}