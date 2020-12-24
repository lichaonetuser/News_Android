package com.box.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.base.BaseBean
import com.box.app.news.data.DataDictionary
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class PushMessageLockScreen(
        @Expose @SerializedName("show") var show: Boolean = false, //是否展示
        @Expose @SerializedName("title") var title: String? = null, //标题
        @Expose @SerializedName("content") var content: String? = null, //内容
        @Expose @SerializedName("images") var images: ArrayList<String>? = null, //图片
        @Expose @SerializedName("wake_time") var wakeTime: Long = -1, //唤醒时间
        @Expose @SerializedName("wake_type") var wakeType: Int = DataDictionary.PushMessageWakeType.NONE.value, //唤醒类型
        @Expose @SerializedName("style") var style: Int = DataDictionary.PushMessageDialogStyle.TEXT.value, //样式
        @Expose @SerializedName("items") var items: ArrayList<PushMessageLockScreenItem>? = null, //样式
        @Expose @SerializedName("more_url") var moreUrl: String? = null //样式
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelPushMessageLockScreen.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelPushMessageLockScreen.writeToParcel(this, dest, flags)
    }
}