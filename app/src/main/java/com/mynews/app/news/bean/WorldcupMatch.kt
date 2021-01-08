package com.mynews.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.mynews.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class WorldcupMatch(
        @Expose @SerializedName("channels") var channels: ArrayList<Channel> = arrayListOf(),
        @Expose @SerializedName("home_team") var home_team: WorldcupTeam = WorldcupTeam(),
        @Expose @SerializedName("away_team") var away_team: WorldcupTeam = WorldcupTeam(),
        @Expose @SerializedName("start_time") var start_time: String = "",
        @Expose @SerializedName("sport_season_round") var sport_season_round: String = "",
        @Expose @SerializedName("game_status") var game_status: Int = 0,
        @Expose @SerializedName("match_id") var match_id: String = "",
        @Expose @SerializedName("is_subscribed") var is_subscribed: Boolean = true,
        @Expose @SerializedName("date_week") var date_week: String = "",
        @Expose @SerializedName("desc") var desc: String = "",
        @Expose @SerializedName("share") var share: Share? = null
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelWorldcupMatch.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelWorldcupMatch.writeToParcel(this, dest, flags)
    }

}