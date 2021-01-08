package com.mynews.app.news.page.mvp.layer.main.dialog.updateChannel

import com.mynews.app.news.R
import com.mynews.app.news.bean.Channel
import com.mynews.app.news.data.DataDictionary
import com.mynews.app.news.data.DataManager
import com.mynews.app.news.event.EventManager
import com.mynews.app.news.event.change.HideOrShowNewChannelTipEvent
import com.mynews.app.news.event.refresh.UpdateArticleVideoChannelEvent
import com.mynews.app.news.util.ReddotUtils
import com.mynews.common.core.CoreApp
import com.mynews.common.core.rx.schedulers.ioToMain
import com.mynews.common.core.util.ResUtils
import com.mynews.common.extension.app.mvp.dialog.MVPDialogPresenter
import com.kongzue.dialog.v2.TipDialog
import com.mynews.common.core.log.Logger
import com.mynews.common.core.rx.schedulers.io
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy

/**
 *
 */
class UpdateChannelDialogPresenter : MVPDialogPresenter<UpdateChannelDialogContract.View>(),
        UpdateChannelDialogContract.Presenter<UpdateChannelDialogContract.View> {

    override fun onClickSkip() {
        mView?.back()
    }


    override fun onClickConfirm() {
        DataManager.Remote.getServerChannelList()
            .onErrorResumeNext(Observable.empty())
            .doOnNext subscribeBy@{ data ->

                //发送广播是否展示文章首页频道编辑按钮右上角的新频道提醒
                //如果推荐频道里有没有存在本地的reddot，则发送广播显示提醒
                var show = false
                data.recommendChannels.articleChannels.forEach {
                    if (!ReddotUtils.containRedDot(it.redDot.toString())) {
                        show = true
                    }
                }
                if (show) {
                    EventManager.post(HideOrShowNewChannelTipEvent(true))
                }

                //推荐频道由客户端自行添加
                val articleRecommendChannel = Channel(DataDictionary.CHANNEL_ID_RECOMMEND_ARTICLE, ResUtils.getString(R.string.Channel_ForYou), index = 0)
                val videoRecommendChannel = Channel(DataDictionary.CHANNEL_ID_RECOMMEND_VIDEO, ResUtils.getString(R.string.Channel_ForYou), index = 0)

                if (data.selectedChannels.articleChannels.isEmpty() && data.selectedChannels.videoChannels.isEmpty()) {
                    return@subscribeBy
                }

                //过滤、添加推荐、为index赋值，添加memory缓存、文件缓存
                val articleRemoteChannels = data.selectedChannels.articleChannels
                val articleFilterRemoteChannels = articleRemoteChannels.filter {
                    it.channelType == DataDictionary.ChannelType.ARTICLE.value ||
                            it.channelType == DataDictionary.ChannelType.VIDEO.value ||
                            it.channelType == DataDictionary.ChannelType.IMAGE.value ||
                            it.channelType == DataDictionary.ChannelType.GIF.value ||
                            it.channelType == DataDictionary.ChannelType.PUBLIC_FEED.value ||
                            it.channelType == DataDictionary.ChannelType.TWITTER_VIDEO.value
                }.toMutableList()
                articleFilterRemoteChannels.removeAll { it.chid == DataDictionary.CHANNEL_ID_RECOMMEND_ARTICLE }
                articleFilterRemoteChannels.add(0, articleRecommendChannel)
                for (i in articleFilterRemoteChannels.indices) {
                    articleFilterRemoteChannels[i].index = i
                }
                data.selectedChannels.articleChannels = ArrayList(articleFilterRemoteChannels)

                val videoRemoteChannels = data.selectedChannels.videoChannels
                val videoFilterRemoteChannels = videoRemoteChannels.filter {
                    it.channelType == DataDictionary.ChannelType.ARTICLE.value ||
                            it.channelType == DataDictionary.ChannelType.VIDEO.value ||
                            it.channelType == DataDictionary.ChannelType.IMAGE.value ||
                            it.channelType == DataDictionary.ChannelType.GIF.value ||
                            it.channelType == DataDictionary.ChannelType.TWITTER_VIDEO.value
                }.toMutableList()
                videoFilterRemoteChannels.removeAll { it.chid == DataDictionary.CHANNEL_ID_RECOMMEND_VIDEO }
                videoFilterRemoteChannels.add(0, videoRecommendChannel)
                for (i in videoFilterRemoteChannels.indices) {
                    videoFilterRemoteChannels[i].index = i
                }
                data.selectedChannels.videoChannels = ArrayList(videoFilterRemoteChannels)

                //更新内存
                DataManager.Memory.putRecommendChannelList(data.recommendChannels)
                DataManager.Memory.putChannelList(data.selectedChannels)
                //更新文件
                DataManager.Local.saveFileChannelList(data.selectedChannels)
                //更新数据库
                val saveChannels = arrayListOf<Channel>()
                saveChannels.addAll(data.selectedChannels.articleChannels)
                saveChannels.addAll(data.selectedChannels.videoChannels)
                DataManager.Local.saveChannelList(saveChannels)


                //发送更新文章/视频频道的广播
                EventManager.post(UpdateArticleVideoChannelEvent(data))

            }.io().subscribeBy(onError = {
                TipDialog.show(CoreApp.getLastBaseActivityInstance()
                    ?: return@subscribeBy,
                    ResUtils.getString(R.string.Tip_ServerError),
                    TipDialog.SHOW_TIME_SHORT, TipDialog.TYPE_ERROR)
            })
        mView?.back()
    }

//    override fun onClickConfirm() {
//        DataManager.Remote.getServerChannelList()
//                .ioToMain()
//                .subscribeBy(
//                        onNext = { data ->
//
//                            //发送广播是否展示文章首页频道编辑按钮右上角的新频道提醒
//                            //如果推荐频道里有没有存在本地的reddot，则发送广播显示提醒
//                            var show = false
//                            data.recommendChannels.articleChannels.forEach {
//                                if (!ReddotUtils.containRedDot(it.redDot.toString())) {
//                                    show = true
//                                }
//                            }
//                            if (show) {
//                                EventManager.post(HideOrShowNewChannelTipEvent(true))
//                            }
//
//                            //推荐频道由客户端自行添加
//                            val articleRecommendChannel = Channel(DataDictionary.CHANNEL_ID_RECOMMEND_ARTICLE, ResUtils.getString(R.string.Channel_ForYou), index = 0)
//                            val videoRecommendChannel = Channel(DataDictionary.CHANNEL_ID_RECOMMEND_VIDEO, ResUtils.getString(R.string.Channel_ForYou), index = 0)
//
//                            if (data.selectedChannels.articleChannels.isEmpty() && data.selectedChannels.videoChannels.isEmpty()) {
//                                return@subscribeBy
//                            }
//
//                            //过滤、添加推荐、为index赋值，添加memory缓存、文件缓存
//                            val articleRemoteChannels = data.selectedChannels.articleChannels
//                            val articleFilterRemoteChannels = articleRemoteChannels.filter {
//                                it.channelType == DataDictionary.ChannelType.ARTICLE.value ||
//                                        it.channelType == DataDictionary.ChannelType.VIDEO.value ||
//                                        it.channelType == DataDictionary.ChannelType.IMAGE.value ||
//                                        it.channelType == DataDictionary.ChannelType.GIF.value ||
//                                        it.channelType == DataDictionary.ChannelType.PUBLIC_FEED.value ||
//                                        it.channelType == DataDictionary.ChannelType.TWITTER_VIDEO.value
//                            }.toMutableList()
//                            articleFilterRemoteChannels.removeAll { it.chid == DataDictionary.CHANNEL_ID_RECOMMEND_ARTICLE }
//                            articleFilterRemoteChannels.add(0, articleRecommendChannel)
//                            for (i in articleFilterRemoteChannels.indices) {
//                                articleFilterRemoteChannels[i].index = i
//                            }
//                            data.selectedChannels.articleChannels = ArrayList(articleFilterRemoteChannels)
//
//                            val videoRemoteChannels = data.selectedChannels.videoChannels
//                            val videoFilterRemoteChannels = videoRemoteChannels.filter {
//                                it.channelType == DataDictionary.ChannelType.ARTICLE.value ||
//                                        it.channelType == DataDictionary.ChannelType.VIDEO.value ||
//                                        it.channelType == DataDictionary.ChannelType.IMAGE.value ||
//                                        it.channelType == DataDictionary.ChannelType.GIF.value ||
//                                        it.channelType == DataDictionary.ChannelType.TWITTER_VIDEO.value
//                            }.toMutableList()
//                            videoFilterRemoteChannels.removeAll { it.chid == DataDictionary.CHANNEL_ID_RECOMMEND_VIDEO }
//                            videoFilterRemoteChannels.add(0, videoRecommendChannel)
//                            for (i in videoFilterRemoteChannels.indices) {
//                                videoFilterRemoteChannels[i].index = i
//                            }
//                            data.selectedChannels.videoChannels = ArrayList(videoFilterRemoteChannels)
//
//                            //更新内存
//                            DataManager.Memory.putRecommendChannelList(data.recommendChannels)
//                            DataManager.Memory.putChannelList(data.selectedChannels)
//                            //更新文件
//                            DataManager.Local.saveFileChannelList(data.selectedChannels)
//                            //更新数据库
//                            val saveChannels = arrayListOf<Channel>()
//                            saveChannels.addAll(data.selectedChannels.articleChannels)
//                            saveChannels.addAll(data.selectedChannels.videoChannels)
//                            DataManager.Local.saveChannelList(saveChannels)
//
//
//                            //发送更新文章/视频频道的广播
//                            EventManager.post(UpdateArticleVideoChannelEvent(data))
//                        },
//
//                        onError = {
//                            TipDialog.show(CoreApp.getLastBaseActivityInstance()
//                                    ?: return@subscribeBy,
//                                    ResUtils.getString(R.string.Tip_ServerError),
//                                    TipDialog.SHOW_TIME_SHORT, TipDialog.TYPE_ERROR)
//                        }
//                )
//        mView?.back()
//    }
}