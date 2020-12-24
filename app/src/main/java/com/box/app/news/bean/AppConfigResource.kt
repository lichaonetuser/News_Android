package com.box.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class AppConfigResource(
        @Expose @SerializedName("css") var css: HtmlResource = HtmlResource(),
        @Expose @SerializedName("js") var js: HtmlResource = HtmlResource(),
        @Expose @SerializedName("js_lang") var jsLang: HtmlResource = HtmlResource()
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelAppConfigResource.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelAppConfigResource.writeToParcel(this, dest, flags)
    }
}


