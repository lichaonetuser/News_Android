package com.box.app.news.page.mvp.layer.main.setting.push

import android.os.Bundle
import com.box.app.news.R
import com.box.app.news.analytics.AnalyticsKey
import com.box.app.news.data.DataManager
import com.box.app.news.event.EventManager
import com.box.app.news.event.change.ClarityChangeEvent
import com.box.app.news.item.factory.ItemFactory
import com.box.app.news.widget.NewsVideoView
import com.box.common.core.analytics.AnalyticsManager
import com.box.common.core.log.Logger
import com.box.common.core.util.ResUtils
import com.box.common.extension.app.mvp.base.MVPBasePresenter
import com.box.common.extension.app.mvp.bindToLifecycle
import com.box.common.extension.app.mvp.loading.list.MVPListPresent
import com.box.common.extension.widget.recycler.item.BaseItem
import com.box.common.extension.widget.recycler.util.convertBeansToItems
import eu.davidea.flexibleadapter.SelectableAdapter
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy

class PushSettingPresenter : MVPBasePresenter<PushSettingContract.View>(),
        PushSettingContract.Presenter<PushSettingContract.View> {

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
    }

    override fun onPushUseSoundCheckedChange(checked: Boolean) {
        DataManager.Local.savePushUseSound(checked)
        if (checked) {
            AnalyticsManager.logEvent(AnalyticsKey.Event.SETTING, AnalyticsKey.Parameter.PUSH_SOUND_ON)
        } else {
            AnalyticsManager.logEvent(AnalyticsKey.Event.SETTING, AnalyticsKey.Parameter.PUSH_SOUND_OFF)
        }
    }

    override fun onPushShowDialogWhenLockCheckedChange(checked: Boolean) {
        DataManager.Local.saveShowPushDialogWhenLock(checked)
        if (checked) {
            AnalyticsManager.logEvent(AnalyticsKey.Event.SETTING, AnalyticsKey.Parameter.PUSH_LOCK_DIALOG_ON)
        } else {
            AnalyticsManager.logEvent(AnalyticsKey.Event.SETTING, AnalyticsKey.Parameter.PUSH_LOCK_DIALOG_OFF)
        }
    }

}

