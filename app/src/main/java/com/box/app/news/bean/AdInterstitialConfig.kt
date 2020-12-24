package com.box.app.news.bean

import android.os.Parcelable
import com.box.app.news.ad.AdIdCenter
import com.box.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdInterstitialConfig(
        @Expose @SerializedName("check_id")
        var checkId: String = AdIdCenter.ADMOB_DEFAULT_INTERSTITIAL_COLD_CHECK,
        @Expose @SerializedName("count_down_interval")
        var countDownInterval: Int = 5,
        @Expose @SerializedName("id")
        var id: String = AdIdCenter.ADMOB_DEFAULT_INTERSTITIAL_COLD,
        @Expose @SerializedName("mediation")
        var mediation: AdMediation = AdMediation()
) : BaseBean(), Parcelable