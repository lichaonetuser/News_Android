package com.mynews.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.mynews.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class SyncProfileStatus(
        @Expose @SerializedName("profile_status") var profileStatus: ProfileStatus = ProfileStatus(),
        @Expose @SerializedName("user") var account: Account = Account()
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelSyncProfileStatus.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelSyncProfileStatus.writeToParcel(this, dest, flags)
    }
}