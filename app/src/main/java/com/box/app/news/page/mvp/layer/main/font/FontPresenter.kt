package com.box.app.news.page.mvp.layer.main.font

import android.os.Bundle
import com.box.app.news.R
import com.box.app.news.analytics.AnalyticsKey
import com.box.app.news.bean.Article
import com.box.app.news.data.DataDictionary
import com.box.app.news.data.DataManager
import com.box.app.news.event.EventManager
import com.box.app.news.event.change.FontSizeChangeEvent
import com.box.app.news.item.ArticleTestItem
import com.box.app.news.util.TextSizeUtils
import com.box.common.core.analytics.AnalyticsManager
import com.box.common.core.util.ResUtils
import com.box.common.extension.app.mvp.loading.list.MVPListPresent
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class FontPresenter : MVPListPresent<FontContract.View>(),
        FontContract.Presenter<FontContract.View> {

    private val mEnterFontSize = DataManager.Local.getFontSize()

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        loadData(0)
        EventManager.register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventManager.unregister(this)
    }

    override fun loadData(pageNum: Int, isRefresh: Boolean) {
        val article = Article(aid = "preview",
                title = ResUtils.getString(R.string.Setting_FontSizeSampleBigNews),
                sourceName = ResUtils.getString(R.string.app_name),
                emitTime = System.currentTimeMillis(),
                displayTime = System.currentTimeMillis(),
                listImageUrls = listOf(""))
        getAdapter()?.updateDataSet(listOf(ArticleTestItem(article)))
    }

    override fun onFontSizeChange(size: DataDictionary.FontSize) {
        if (size == DataManager.Local.getFontSize()) {
            return
        }
        TextSizeUtils.updateTextSizeParams(size.toCommonParams())
        DataManager.Local.saveFontSize(size)
        EventManager.post(FontSizeChangeEvent(size))
    }

    override fun onBackBegin() {
        super.onBackBegin()
        if (mEnterFontSize != DataManager.Local.getFontSize()) {
            AnalyticsManager.logEvent(AnalyticsKey.Event.SETTING, String.format(AnalyticsKey.Parameter.SET_FRONT_SIZE__ID_, DataManager.Local.getFontSize()))
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onFontSizeChangeEvent(event: FontSizeChangeEvent) {
        getAdapter()?.notifyDataSetChanged()
    }

}

