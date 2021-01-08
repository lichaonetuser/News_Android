package com.mynews.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.mynews.app.news.bean.base.BaseListBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class WorldCupBoard(
        @Expose @SerializedName("hot_count") var hotCount: Int? = 0,
        @Expose @SerializedName("recent_count") var recentCount: Int? = 0,
        @Expose @SerializedName("aggregated_count") var aggregatedCount: Int? = 0,
        @Expose @SerializedName("hot_has_more") var hotHasMore: Boolean = false,
        @Expose @SerializedName("hot") var hot: ArrayList<Comment> = arrayListOf(),
        @Expose @SerializedName("recent") var recent: ArrayList<Comment> = arrayListOf(),
        @Expose @SerializedName("aggregated") var aggregated: ArrayList<Comment> = arrayListOf()
) : BaseListBean<Comment>(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelWorldCupBoard.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelWorldCupBoard.writeToParcel(this, dest, flags)
    }
}
