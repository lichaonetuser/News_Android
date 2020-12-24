package com.box.app.news.bean

import android.os.Parcelable
import com.box.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdNativeFeed(
        @Expose @SerializedName("disable")
        var disable: Boolean = false,
        @Expose @SerializedName("source")
        var source: String = "mopub",
        @Expose @SerializedName("mopub")
        var mopub: AdNativeFeedMoPubConfig = AdNativeFeedMoPubConfig()
) : BaseBean(), Parcelable