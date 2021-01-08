package com.mynews.app.news.bean

import android.os.Parcelable
import com.mynews.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdSplash(
        @Expose @SerializedName("admob")
        var admob: AdSplashConfig = AdSplashConfig(),
        @Expose @SerializedName("facebook")
        var facebook: AdSplashConfig = AdSplashConfig(),
        @Expose @SerializedName("mopub")
        var mopub: AdSplashConfig = AdSplashConfig(),
        @Expose @SerializedName("vungle")
        var vungle: AdSplashConfig = AdSplashConfig(),
        @Expose @SerializedName("unity")
        var unity: AdSplashConfig = AdSplashConfig(),
        @Expose @SerializedName("jumpRaw")
        var jumpRaw: AdSplashConfig = AdSplashConfig(),
        @Expose @SerializedName("disable")
        var disable: Boolean = false,
        @Expose @SerializedName("min_show_interval")
        var minShowInterval: Int = 30,
        @Expose @SerializedName("source")
        var source: String = "admob"
) : BaseBean(), Parcelable