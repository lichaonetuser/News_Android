package com.box.app.news.bean

import android.os.Parcelable
import com.box.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdSource(
        @Expose @SerializedName("admob")
        var admob: AdSourceConfig = AdSourceConfig(),
        @Expose @SerializedName("facebook")
        var facebook: AdSourceConfig = AdSourceConfig(),
        @Expose @SerializedName("mopub")
        var mopub: AdSourceConfig = AdSourceConfig(),
        @Expose @SerializedName("vungle")
        var vungle: AdSourceConfig = AdSourceConfig(),
        @Expose @SerializedName("unity")
        var unity: AdSourceConfig = AdSourceConfig(),
        @Expose @SerializedName("jumpRaw")
        var jumpRaw: AdSourceConfig = AdSourceConfig()
) : BaseBean(), Parcelable