package com.mynews.app.news.page.mvp.layer.main.setting.push

import com.mynews.common.extension.app.mvp.base.MVPBaseContract

interface PushSettingContract {

    interface View : MVPBaseContract.View

    interface Presenter<in V : View> : MVPBaseContract.Presenter<V> {
        fun onPushUseSoundCheckedChange(checked: Boolean)
        fun onPushShowDialogWhenLockCheckedChange(checked: Boolean)
    }

}