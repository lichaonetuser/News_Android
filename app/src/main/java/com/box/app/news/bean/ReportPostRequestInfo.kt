package com.box.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class ReportPostRequestInfo(
        @Expose @SerializedName("content") var content: String = "",
        @Expose @SerializedName("item_id") var itemId: String = "",
        @Expose @SerializedName("report_key") var reportKey: Int = 0,
        @Expose @SerializedName("report_type") var reportType: Int = 0
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelReportPostRequestInfo.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelReportPostRequestInfo.writeToParcel(this, dest, flags)
    }
}