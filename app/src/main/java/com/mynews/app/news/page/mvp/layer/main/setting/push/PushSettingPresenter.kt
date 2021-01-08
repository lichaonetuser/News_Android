package com.mynews.app.news.page.mvp.layer.main.setting.push

import android.os.Bundle
import com.mynews.app.news.analytics.AnalyticsKey
import com.mynews.app.news.data.DataManager
import com.mynews.common.core.analytics.AnalyticsManager
import com.mynews.common.extension.app.mvp.base.MVPBasePresenter

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

