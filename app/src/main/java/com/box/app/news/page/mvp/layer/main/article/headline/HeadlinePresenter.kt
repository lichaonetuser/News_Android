package com.box.app.news.page.mvp.layer.main.article.headline

import android.os.Bundle
import com.box.app.news.analytics.AnalyticsKey
import com.box.app.news.bean.Headline
import com.box.app.news.bean.base.BaseNewsBean
import com.box.app.news.data.source.local.preference.PreferenceAPI
import com.box.app.news.item.base.BaseArticleItem
import com.box.app.news.page.mvp.layer.main.article.detail.ArticleDetailFragment
import com.box.app.news.page.mvp.layer.main.article.detail.ArticleDetailPresenterAutoBundle
import com.box.app.news.page.mvp.layer.main.list.news.NewsListPresenter
import com.box.app.news.util.TimeUtils
import com.box.common.core.analytics.AnalyticsManager
import com.box.common.core.json.gson.util.CoreGsonUtils
import com.box.common.core.util.extension.format2DateString
import com.box.common.extension.widget.recycler.item.BaseItem
import com.google.gson.reflect.TypeToken
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer


/**
 *
 */
open class HeadlinePresenter : NewsListPresenter<HeadlineContract.View>(),
        HeadlineContract.Presenter<HeadlineContract.View> {

    override val mShowTimeHeader: Boolean = true
    override val mLoadCache: Boolean = false

    private var mReadHistory = mutableMapOf<String, Int>()
    private val mJsonType = object : TypeToken<Map<String, Int>>() {}.type

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        AnalyticsManager.logEvent(AnalyticsKey.Event.NEWS, AnalyticsKey.Parameter.SWITCH_CHANNEL_HEADLINE)

        val readCache = PreferenceAPI.getArticleReadCache(mChannel.chid)
        if (readCache.isNotEmpty()) {
            mReadHistory = CoreGsonUtils.fromJson(readCache, mJsonType) ?: mutableMapOf()
        }
    }

    //由于友盟统计特殊所以重写了父类方法
    override fun onArticleItemClick(item: BaseItem<*, *>) {
        if (item is BaseArticleItem) {
            GSYVideoManager.releaseAllVideos()
            AnalyticsManager.logEvent(AnalyticsKey.Event.NEWS, AnalyticsKey.Parameter.CLICK_ARTICLE_HEADLINE)
            val article = item.getModel()

            if (!mReadHistory.contains(article.aid)) {
                mReadHistory.put(article.aid, 1)
                PreferenceAPI.saveArticleReadCache(mChannel.chid, CoreGsonUtils.toJson(mReadHistory))
            }

            mView?.goFromRoot(ArticleDetailFragment::class.java, ArticleDetailPresenterAutoBundle
                    .builder(article, false, mItemRefer)
                    .mChannel(mChannel)
                    .bundle())
        }
    }

    override fun createTimeTitle(list: MutableList<BaseNewsBean>, lastData: BaseNewsBean?) {
        val map = mutableMapOf<Int, Headline>()
        var k = 0
        while (k < list.size) {
            if (list[k] is Headline) {
                list.removeAt(k)
            } else {
                if (mReadHistory.containsKey(list[k].aid)) {
                    list[k].isRead = true
                }
                k++
            }
        }
        var lastItem: BaseNewsBean? = lastData
        for (i in list.indices) {
            val curr = list[i]
            curr.showTimerTitle = true
            if (i == 0 && lastData == null
                    && !TimeUtils.isSameDay(curr.emitTime, System.currentTimeMillis()).first) {
                map.put(0, createHeadlineTimeBean(list[0]))
            }
            if (lastItem != null && lastItem !is Headline && curr !is Headline) {
                if (getDayFromFormatTime(createTimeData(curr)) != getDayFromFormatTime(createTimeData(lastItem))) {
                    map.put(i, createHeadlineTimeBean(curr))
                }
            }
            lastItem = curr
        }

        var index = 0
        for (entry in map) {
            list.add(entry.key + index, entry.value)
            index++
        }
    }

    private fun createHeadlineTimeBean(data: BaseNewsBean): Headline {
        val timeContent = createTimeData(data)
        val headlineTime = Headline()
        headlineTime.dateContent = getSubDateFromFormatTime(timeContent, 5, 10)
        return headlineTime
    }

    private fun createTimeData(data: BaseNewsBean?): String {
        val temp = data?.emitTime
        if (temp != null) {
            return temp.format2DateString("yyyy-MM-dd HH:mm:ss")
        }
        return ""
    }

    private fun getDayFromFormatTime(formatTime: String): String = getSubDateFromFormatTime(formatTime, 0, 10)

    private fun getSubDateFromFormatTime(formatTime: String, startIndex: Int, endIndex: Int): String {
        return formatTime.substring(startIndex, endIndex)
    }

    override fun getTitleTime(position: Int): String {
        if (position < 0 || position >= mListNews.news.size){
            return ""
        }
        val currentItem = mListNews.news[position]
        return when (currentItem) {
            is Headline -> {
                currentItem.dateContent
            }
            else -> {
                getSubDateFromFormatTime(createTimeData(currentItem), 5, 10)
            }
        }
    }
}