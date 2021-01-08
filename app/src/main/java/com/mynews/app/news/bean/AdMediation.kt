package com.mynews.app.news.bean

import android.os.Parcelable
import com.mynews.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdMediation(
        @Expose @SerializedName("vungle")
        var vungle: AdMediationConfig = AdMediationConfig()
) : BaseBean(), Parcelable