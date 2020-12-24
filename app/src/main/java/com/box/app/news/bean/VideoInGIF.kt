package com.box.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.base.BaseBean
import com.github.gfx.android.orma.annotation.Column
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

/**
 * Android 支持gif
 * GIF 类型里的video字段
 */
@PaperParcel
data class VideoInGIF(@Expose @SerializedName("duration") var duration: Int = 0,
                      @Expose @SerializedName("width") var width: Int = 0,
                      @Expose @SerializedName("height") var height: Int = 0,
                      @Expose @SerializedName("kps") var kps: Int = 0,
                      @Expose @SerializedName("urls") var urls: List<String> = listOf(),
                      @Expose @SerializedName("hd_url") @Column("hd_url") var hdUrl: String = "",
                      @Expose @SerializedName("sd_url") @Column("sd_url") var sdUrl: String = "",
                      @Expose @SerializedName("normal_url") @Column("normal_url") var normalUrl: String = ""
                      ) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelVideoInGIF.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelVideoInGIF.writeToParcel(this, dest, flags)
    }
}