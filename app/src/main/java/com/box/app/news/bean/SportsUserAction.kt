package com.box.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class SportsUserAction (@Expose @SerializedName("ops") var Ops: List<Ops> = listOf())
    : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelSportsUserAction.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelSportsUserAction.writeToParcel(this, dest, flags)
    }
}