package com.mynews.app.news.bean

import android.os.Parcelable
import com.mynews.app.news.ad.AdIdCenter
import com.mynews.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdNativeFeedMoPubConfig(
        @Expose @SerializedName("check_id")
        var checkId: String = AdIdCenter.MOPUB_TEST_ID_NATIVE,
        @Expose @SerializedName("id")
        var id: String = AdIdCenter.MOPUB_TEST_ID_NATIVE
) : BaseBean(), Parcelable