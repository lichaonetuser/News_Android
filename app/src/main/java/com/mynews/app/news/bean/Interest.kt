package com.mynews.app.news.bean

import com.mynews.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

@PaperParcel
data class Interest(@Expose @SerializedName("items") var items: ArrayList<InterestItem>? = null,
                    @Expose @SerializedName("max") var max: Int = -1,
                    @Expose @SerializedName("min") var min: Int = 1) : BaseBean(), PaperParcelable {

    companion object {
        @JvmField
        val CREATOR = PaperParcelInterest.CREATOR
    }

}