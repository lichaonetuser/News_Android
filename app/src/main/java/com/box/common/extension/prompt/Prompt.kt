package com.box.common.extension.prompt

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class Prompt(
        @Expose @SerializedName("content") var content: String = "",
        @Expose @SerializedName("buttons") var buttons: List<Button> = listOf(),
        @Expose @SerializedName("title") var title: String = "",
        @Expose @SerializedName("delay") var delay: Int = 0,
        @Expose @SerializedName("purpose") var purpose: Int = 0,
        @Expose @SerializedName("is_repeat") var isRepeat: Int = 0,
        @Expose @SerializedName("id") var id: Int = 0
) : Parcelable {

    companion object {
        @JvmField
        val CREATOR = PaperParcelPrompt.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelPrompt.writeToParcel(this, dest, flags)
    }
}