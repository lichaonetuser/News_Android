package com.mynews.app.news.bean

import android.os.Parcelable
import com.mynews.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdInterstitial(
        @Expose @SerializedName("admob")
        var admob: AdInterstitialConfig = AdInterstitialConfig(),
        @Expose @SerializedName("vungle")
        var vungle: AdInterstitialConfig = AdInterstitialConfig(),
        @Expose @SerializedName("facebook")
        var facebook: AdInterstitialConfig = AdInterstitialConfig(),
        @Expose @SerializedName("mopub")
        var mopub: AdInterstitialConfig = AdInterstitialConfig(),
        @Expose @SerializedName("unity")
        var unity: AdInterstitialConfig = AdInterstitialConfig(),
        @Expose @SerializedName("jumpRaw")
        var jumpRaw: AdInterstitialConfig = AdInterstitialConfig(),
        @Expose @SerializedName("disable")
        var disable: Boolean = false,
        @Expose @SerializedName("min_show_interval")
        var minShowInterval: Int = 30,
        @Expose @SerializedName("source")
        var source: String = "admob"
) : BaseBean(), Parcelable