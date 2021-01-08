package com.mynews.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.mynews.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

/**
 * 6.9 支持不同比例图片的图片链接容器  对应list_images字段
 */
@PaperParcel
data class RatioImagesInfo(
        @Expose @SerializedName("4:3") var ration4_3: List<String> = listOf(),
        @Expose @SerializedName("16:9_medium") var ration16_9_medium: List<String> = listOf(),
        @Expose @SerializedName("16:9_large") var ration16_9_large: List<String> = listOf(),
        @Expose @SerializedName("1:1") var ration1_1: List<String> = listOf(),
        @Expose @SerializedName("4:3.5") var ration4_35: List<String> = listOf(),
        @Expose @SerializedName("16:9.5") var ration16_95: List<String> = listOf()
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelRatioImagesInfo.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelRatioImagesInfo.writeToParcel(this, dest, flags)
    }

}