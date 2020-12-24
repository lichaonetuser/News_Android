package com.box.app.news.bean

import android.os.Parcelable
import com.box.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Ad(
        @Expose @SerializedName("interstitial")
        var interstitial: AdInterstitial = AdInterstitial(),
        @Expose @SerializedName("source")
        var source: AdSource = AdSource(),
        @Expose @SerializedName("splash")
        var splash: AdSplash = AdSplash(),
        @Expose @SerializedName("feed")
        var feed: AdNativeFeed = AdNativeFeed()
) : BaseBean(), Parcelable