package com.box.app.news.page.mvp.layer.main.video

import com.box.app.news.analytics.AnalyticsKey
import com.box.app.news.bean.Channel
import com.box.app.news.bean.list.ListChannel
import com.box.app.news.data.DataManager
import com.box.app.news.event.refresh.UpdateArticleVideoChannelEvent
import com.box.app.news.page.mvp.layer.main.article.ArticlePresenter
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer

class VideoPresenter : ArticlePresenter<VideoContract.View>(),
        VideoContract.Presenter<VideoContract.View> {

    override val mAnalyticsKey = AnalyticsKey.Event.VIDEO

    override fun initCurrentClickedChannel(channel: Channel) {
        DataManager.Memory.putCurrentVideoChannel(channel)
    }

    override fun getListChannels(listChannel: ListChannel): List<Channel> {
        return listChannel.videoChannels.distinct()
    }

    override fun onClickTab(currentIndex: Int, newIndex: Int) {
        super.onClickTab(currentIndex, newIndex)
        GSYVideoManager.releaseAllVideos()
    }

    override fun onPageSelected(position: Int) {
        super.onPageSelected(position)
        GSYVideoManager.releaseAllVideos()
    }

    //注意：子类父类完全不一样，修改前三思（因为mview对象和其他对象在子类父类里不一致，所以要分开写）
    //跟父类文章的方法类似，父类更新文章频道，该类更新视频频道
    override fun onUpdateArticleVideoChannelEvent(event: UpdateArticleVideoChannelEvent) {
        if (javaClass != VideoPresenter::class.java) {
            return
        }
        //更新mlist里的数据
        mListChannel.videoChannels = arrayListOf()
        mListChannel.videoChannels.addAll(event.recommendChannel.selectedChannels.videoChannels)

        val channels = event.recommendChannel.selectedChannels.videoChannels
        val beforeChid = DataManager.Memory.getCurrentVideoChannel()?.chid
        val pos = channels.indices.lastOrNull { beforeChid == channels[it].chid } ?: -1
        if (pos == -1) {
            mView?.updateChannels(channels, 0,true)
        } else {
            mView?.updateChannels(channels, pos, false)
        }
    }
}

