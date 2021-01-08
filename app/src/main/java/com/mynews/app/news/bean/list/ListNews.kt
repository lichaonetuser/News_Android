package com.mynews.app.news.bean.list

import android.os.Parcel
import android.os.Parcelable
import com.mynews.app.news.bean.Article
import com.mynews.app.news.bean.Tip
import com.mynews.app.news.bean.base.BaseListBean
import com.mynews.app.news.bean.base.BaseNewsBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class ListNews(
        @Expose @SerializedName("items") var news: ArrayList<BaseNewsBean?> = arrayListOf(),
        @Expose @SerializedName("tip") var tip: Tip = Tip(),
        @Expose @SerializedName("min_emit_time") var minEmitTime: Long = 0,
        @Expose @SerializedName("max_emit_time") var maxEmitTime: Long = 0,
        @Expose @SerializedName("has_more_to_refresh") var hasMoreToRefresh: Boolean = false
) : BaseListBean<Article>(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelListNews.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelListNews.writeToParcel(this, dest, flags)
}
}