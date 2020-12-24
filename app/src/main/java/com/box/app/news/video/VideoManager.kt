package com.box.app.news.video

import android.os.Build
import com.box.common.core.CoreApp
import com.google.android.exoplayer2.ExoPlayer
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.utils.GSYVideoType
import com.shuyu.gsyvideoplayer.model.VideoOptionModel
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager
import tv.danmaku.ijk.media.player.IjkMediaPlayer

object VideoManager {

    fun init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            PlayerFactory.setPlayManager(Exo2PlayerManager::class.java)
        } else {
            PlayerFactory.setPlayManager(IjkPlayerManager::class.java)
        }
        tryFixKeyFrame()
    }

    private fun tryFixKeyFrame() {
        val videoOptionModel = VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1)
        val list = arrayListOf(videoOptionModel)
        GSYVideoManager.instance().optionModelList = list
    }

}