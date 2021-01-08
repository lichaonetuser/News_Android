package com.mynews.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.mynews.app.news.bean.base.BaseBean
import paperparcel.PaperParcel

@PaperParcel
data class SearchData(var mType: Int = 0, var mContent: String = "", var mSpanSize: Int = 2)
    : BaseBean(), Parcelable {

    companion object {
        @JvmField
        val CREATOR = PaperParcelSearchData.CREATOR

        val TYPE_SEARCH_HOTWORD = 0
        val TYPE_SEARCH_HOTWORD_TITLE = 1
        val TYPE_SEARCH_HISTORY = 2
        val TYPE_SEARCH_DIVIDER = 3
        val TYPE_SEARCH_CLEAR_HISTORY = 4
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelSearchData.writeToParcel(this, dest, flags)
    }

    override fun describeContents(): Int = 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as SearchData
        if (mType != other.mType
                && mContent != other.mContent
                && mSpanSize != other.mSpanSize) return false
        return true
    }

    override fun hashCode(): Int {
        return mType.hashCode() + mContent.hashCode() + mSpanSize.hashCode()
    }
}