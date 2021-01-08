package com.mynews.app.news.page.mvp.layer.main.setting.push

import android.os.Bundle
import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.data.DataManager
import com.mynews.common.core.util.StatusBarUtils
import com.mynews.common.extension.app.mvp.base.MVPBaseFragment
import kotlinx.android.synthetic.main.fragment_push_setting.*

class PushSettingFragment : MVPBaseFragment<PushSettingContract.View,
        PushSettingContract.Presenter<PushSettingContract.View>>(),
        PushSettingContract.View {

    override val mAttachSwipeBack = true
    override val mPresenter = PushSettingPresenter()
    override val mLayoutRes = R.layout.fragment_push_setting

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        push_use_sound_check.isChecked = DataManager.Local.getPushUseSound()
        push_use_sound_bar.setOnClickListener {
            push_use_sound_check.toggle()
        }
        push_use_sound_check.setOnCheckedChangeListener { buttonView, isChecked ->
            mPresenter.onPushUseSoundCheckedChange(isChecked)
        }
        push_show_dialog_when_lock_check.isChecked = DataManager.Local.getShowPushDialogWhenLock()
        push_show_dialog_when_lock_bar.setOnClickListener {
            push_show_dialog_when_lock_check.toggle()
        }
        push_show_dialog_when_lock_check.setOnCheckedChangeListener { buttonView, isChecked ->
            mPresenter.onPushShowDialogWhenLockCheckedChange(isChecked)
        }

    }

    override fun onSupportVisible() {
        super.onSupportVisible()
        push_show_dialog_when_lock_check.isChecked = DataManager.Local.getShowPushDialogWhenLock()
        push_use_sound_check.isChecked = DataManager.Local.getPushUseSound()
        StatusBarUtils.notifyStatusBarIsLight(_mActivity)
    }
}


