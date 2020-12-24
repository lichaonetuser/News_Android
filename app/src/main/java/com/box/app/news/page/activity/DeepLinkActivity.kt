package com.box.app.news.page.activity

import android.content.Intent
import android.os.Bundle
import com.box.common.core.CoreApp
import com.box.common.core.app.activity.CoreBaseActivity
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer


class DeepLinkActivity : CoreBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startMain()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        startMain()
    }

    fun startMain() {
        val uri = intent.data
        GSYVideoManager.releaseAllVideos() //释放所有正在播放的视频
        val intent = Intent(this@DeepLinkActivity, MainActivity::class.java)
        intent.data = uri
        intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        startActivity(intent)
        finish()
    }

}

