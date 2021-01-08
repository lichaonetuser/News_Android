package com.mynews.app.news.bean.list

import android.os.Parcel
import android.os.Parcelable
import com.mynews.app.news.bean.base.BaseListBean
import com.mynews.app.news.bean.base.BaseNewsBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class ListFavorite(
        @Expose @SerializedName("items") var favoriteNews: ArrayList<BaseNewsBean> = arrayListOf()
) : BaseListBean<BaseNewsBean>(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelListFavorite.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelListFavorite.writeToParcel(this, dest, flags)
    }
}
