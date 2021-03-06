package com.mynews.app.news.bean

import android.os.Parcelable
import com.mynews.app.news.ad.AdIdCenter
import com.mynews.app.news.ad.AdSourceEna
import com.mynews.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdSourceConfigMediation(
        @Expose @SerializedName("source")
        var source: String = AdSourceEna.MOPUB.value,
        @Expose @SerializedName("id")
        var id: String = AdIdCenter.MOPUB_TEST_ID_NATIVE,
        @Expose @SerializedName("check_id")
        var checkId: String = AdIdCenter.MOPUB_TEST_ID_NATIVE_CHECK,
        @Expose @SerializedName("disable")
        var disable: Boolean = false
) : BaseBean(), Parcelable