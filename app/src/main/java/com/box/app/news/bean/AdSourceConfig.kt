package com.box.app.news.bean

import android.os.Parcelable
import com.box.app.news.ad.AdIdCenter
import com.box.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdSourceConfig(
        @Expose @SerializedName("application_id")
        var applicationId: String = AdIdCenter.ADMOB_APP_ID,
        @Expose @SerializedName("check_id")
        var checkId: String = AdIdCenter.ADMOB_APP_ID_CHECK,
        @Expose @SerializedName("disable")
        var disable: Boolean = false,
        @Expose @SerializedName("mediation")
        var mediation: List<AdSourceConfigMediation> = arrayListOf()
) : BaseBean(), Parcelable