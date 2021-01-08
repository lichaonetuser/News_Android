package com.mynews.app.news.page.mvp.layer.main.dialog.font

import com.mynews.app.news.analytics.AnalyticsKey
import com.mynews.app.news.data.DataDictionary
import com.mynews.app.news.data.DataManager
import com.mynews.app.news.event.EventManager
import com.mynews.app.news.event.change.FontSizeChangeEvent
import com.mynews.app.news.util.TextSizeUtils
import com.mynews.common.core.analytics.AnalyticsManager
import com.mynews.common.extension.app.mvp.dialog.MVPDialogPresenter

class FontSizeDialogPresenter : MVPDialogPresenter<FontSizeDialogContract.View>(),
        FontSizeDialogContract.Presenter<FontSizeDialogContract.View> {

    private val mEnterFontSize = DataManager.Local.getFontSize()

    override fun onFontSizeChange(size: DataDictionary.FontSize) {
        if (size == DataManager.Local.getFontSize()) {
            return
        }
        DataManager.Local.saveFontSize(size)
        EventManager.post(FontSizeChangeEvent(size))
        TextSizeUtils.updateTextSizeParams(size.toCommonParams())
    }

    override fun onBackBegin() {
        super.onBackBegin()
        if (mEnterFontSize != DataManager.Local.getFontSize()) {
            AnalyticsManager.logEvent(AnalyticsKey.Event.NEWS_DETAIL, String.format(AnalyticsKey.Parameter.SET_FRONT_SIZE__ID_, DataManager.Local.getFontSize()))
        }
    }
}

