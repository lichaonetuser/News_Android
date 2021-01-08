package com.mynews.app.news.bean

import com.mynews.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

@PaperParcel
data class InterestData(@Expose @SerializedName("interest") var interest: Interest,
                        @Expose @SerializedName("e_flag") var e_flag: String,
                        @Expose @SerializedName("gender") var gender: String,
                        @Expose @SerializedName("unique_device_id") var unique_device_id: String) : BaseBean(), PaperParcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelInterestData.CREATOR
    }

}