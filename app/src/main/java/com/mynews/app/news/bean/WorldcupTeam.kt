package com.mynews.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.mynews.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class WorldcupTeam(
        @Expose @SerializedName("sub_name") var sub_name: String = "",
        @Expose @SerializedName("name") var name: String = "",
        @Expose @SerializedName("is_subscribed") var is_subscribed: Boolean = true,
        @Expose @SerializedName("channels") var teamChannels: ArrayList<Channel> = arrayListOf(),
        @Expose @SerializedName("team_id") var team_id: String = "",
        @Expose @SerializedName("avatar_url") var avatar_url: String = "",
        @Expose @SerializedName("is_digged") var is_digged: Boolean = false,
        @Expose @SerializedName("dig_count") var dig_count: Int = 0,
        @Expose @SerializedName("item_id") var item_id: Int = 0,
        @Expose @SerializedName("aid") var aid: String = "",
        @Expose @SerializedName("score") var score: Int = 0,
        @Expose @SerializedName("id") var id: Int = 0,
        @Expose @SerializedName("desc") var desc: String = "",
        @Expose @SerializedName("share") var share: Share? = null,
        @Expose @SerializedName("point_sphere") var pointSphere: Int = 0
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelWorldcupTeam.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelWorldcupTeam.writeToParcel(this, dest, flags)
    }

}