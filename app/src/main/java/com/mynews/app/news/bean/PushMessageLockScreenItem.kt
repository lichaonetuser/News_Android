package com.mynews.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.mynews.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class PushMessageLockScreenItem(
        @Expose @SerializedName("title") var title: String? = null, //标题
        @Expose @SerializedName("content") var content: String? = null, //内容
        @Expose @SerializedName("images") var images: ArrayList<String>? = null, //图片
        @Expose @SerializedName("open_url") var openUrl: String? = null, //图片
        @Expose @SerializedName("flag") var flag: Int? = 1, //图片
        @Expose @SerializedName("style") var style: Int? = 0 //图片
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelPushMessageLockScreenItem.CREATOR

        const val LOCK_SCREEN_ITEM_STYLE_NORMAL = 0
        const val LOCK_SCREEN_ITEM_STYLE_LARGE_IMAGE = 1
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelPushMessageLockScreenItem.writeToParcel(this, dest, flags)
    }
}