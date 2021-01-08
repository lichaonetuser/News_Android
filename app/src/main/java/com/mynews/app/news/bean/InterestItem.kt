package com.mynews.app.news.bean

import com.mynews.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

@PaperParcel
data class InterestItem(@Expose @SerializedName("category") var category: String = "",
                        @Expose @SerializedName("color") var color: String = "",
                        @Expose @SerializedName("icon") var icon: String = "",
                        @Expose @SerializedName("tags") var tags: ArrayList<String>) : BaseBean(), PaperParcelable {

    companion object {
        @JvmField
        val CREATOR = PaperParcelInterestItem.CREATOR
    }

}