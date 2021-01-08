package com.mynews.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.mynews.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class Ops(@Expose @SerializedName("action_type") var actionType: Int = 0,
               @Expose @SerializedName("aid") var aid: String? = null,
               @Expose @SerializedName("cid") var cid: String? = null,
               @Expose @SerializedName("item_id") var item_id: String? = null,
               @Expose @SerializedName("pk") var pk: String = "") : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelOps.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelOps.writeToParcel(this, dest, flags)
    }
}