package com.box.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class UploadImageResult(
        @Expose @SerializedName("image_uri") val imageUri: String = "",
        @Expose @SerializedName("image_url") val imageUrl: String = ""
):BaseBean(), Parcelable {
  companion object {
    @JvmField val CREATOR = PaperParcelUploadImageResult.CREATOR
  }

  override fun describeContents() = 0

  override fun writeToParcel(dest: Parcel, flags: Int) {
    PaperParcelUploadImageResult.writeToParcel(this, dest, flags)
  }
}