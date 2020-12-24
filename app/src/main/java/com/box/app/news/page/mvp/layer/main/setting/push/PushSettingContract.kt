package com.box.app.news.page.mvp.layer.main.setting.push

import com.box.common.extension.app.mvp.base.MVPBaseContract
import com.box.common.extension.app.mvp.loading.list.MVPListContract

interface PushSettingContract {

    interface View : MVPBaseContract.View

    interface Presenter<in V : View> : MVPBaseContract.Presenter<V> {
        fun onPushUseSoundCheckedChange(checked: Boolean)
        fun onPushShowDialogWhenLockCheckedChange(checked: Boolean)
    }

}