package com.box.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.base.BaseBean
import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.PrimaryKey
import com.github.gfx.android.orma.annotation.Setter
import com.github.gfx.android.orma.annotation.Table
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class WorldcupPlayer(
        @Expose @SerializedName("position") var position: Int = 0,
        @Expose @SerializedName("sub_name") var sub_name: String = "",
        @Expose @SerializedName("name") var name: String = "",
        @Expose @SerializedName("is_player") var is_player: Boolean = true,
        @Expose @SerializedName("channels") var playerChannels: ArrayList<Channel> = arrayListOf(),
        @Expose @SerializedName("player_id") var player_id: String = "",
        @Expose @SerializedName("avatar_url") var avatar_url: String = "",
        @Expose @SerializedName("kit_number") var kit_number: String = "",
        @Expose @SerializedName("team") var team: WorldcupTeam = WorldcupTeam(),
        @Expose @SerializedName("goals") var goals: Int = 0,
        @Expose @SerializedName("is_star") var is_star: Int = 0,
        @Expose @SerializedName("desc") var desc: String = "",
        @Expose @SerializedName("share") var share: Share? = null
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelWorldcupPlayer.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelWorldcupPlayer.writeToParcel(this, dest, flags)
    }

}