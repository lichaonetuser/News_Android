package com.mynews.common.extension.share

import android.os.Parcel
import android.os.Parcelable
import paperparcel.PaperParcel

@PaperParcel
class ContentLink(val title: String? = null,
                  val linkUrl: String,
                  val imageUrl: String? = null,
                  val text: String? = null) : Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelContentLink.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelContentLink.writeToParcel(this, dest, flags)
    }
}