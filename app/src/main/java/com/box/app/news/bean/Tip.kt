package com.box.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class Tip(
        @Expose @SerializedName("tip") var tip: String = ""
) : BaseBean(),Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelTip.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelTip.writeToParcel(this, dest, flags)
    }
}