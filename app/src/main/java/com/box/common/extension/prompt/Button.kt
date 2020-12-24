package com.box.common.extension.prompt;

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName;

import kotlin.jvm.JvmField;
import paperparcel.PaperParcel;

@PaperParcel
data class Button(
        @Expose @SerializedName("type") var type: Int = 0,
        @Expose @SerializedName("name") var name: String = "",
        @Expose @SerializedName("open_url") var openUrl: String = ""
) : Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelButton.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelButton.writeToParcel(this, dest, flags)
    }
}