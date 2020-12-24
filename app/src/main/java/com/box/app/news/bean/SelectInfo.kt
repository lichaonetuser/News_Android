package com.box.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

/**
 * 7.1 性别年龄段选择接口入参 和 出参
 */
@PaperParcel
data class SelectInfo(
    @Expose @SerializedName("gender") var gender: Int = -1,
    @Expose @SerializedName("age_stage") var ageStage: Int = -1
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelSelectInfo.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelSelectInfo.writeToParcel(this, dest, flags)
    }
}