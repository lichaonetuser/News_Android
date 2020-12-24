package com.box.app.news.util

import com.box.app.news.bean.Video
import com.shuyu.gsyvideoplayer.GSYVideoManager

object GSYVideoTransferUtils {

    val INFO_PREPARE_TRANSFER = -111233

    var transferFlag = false
    var video: Video? = null
    var firstStartTimestamp = 0L
    var allPlayDuration = 0L

    fun prepareTransfer() {
        if (GSYVideoManager.instance().listener() != null) {
            GSYVideoManager.instance().listener().onInfo(INFO_PREPARE_TRANSFER, 0)
        }
    }

}