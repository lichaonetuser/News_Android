package com.mynews.app.news.page.mvp.layer.main.me.profile.edit.name

import android.os.Bundle
import com.mynews.app.news.R
import com.mynews.app.news.account.AccountManager
import com.mynews.app.news.analytics.AnalyticsKey
import com.mynews.app.news.bean.Account
import com.mynews.app.news.bean.UpdateProfile
import com.mynews.app.news.data.DataManager
import com.mynews.common.core.CoreApp
import com.mynews.common.core.analytics.AnalyticsManager
import com.mynews.common.core.rx.schedulers.ioToMain
import com.mynews.common.core.util.ResUtils
import com.mynews.common.extension.app.mvp.base.MVPBasePresenter
import com.mynews.common.extension.app.mvp.bindToLifecycle
import com.kongzue.dialog.v2.TipDialog
import io.reactivex.rxkotlin.subscribeBy

class ProfileEditNamePresenter : MVPBasePresenter<ProfileEditNameContract.View>(),
        ProfileEditNameContract.Presenter<ProfileEditNameContract.View> {

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        mView?.setAccount(AccountManager.account ?: Account())
    }

    override fun onClickDone(name: String) {
        AnalyticsManager.logEvent(AnalyticsKey.Event.PROFILE_EDIT, AnalyticsKey.Parameter.CLICK_SUBMIT)
        if (name == AccountManager.account?.screenName) {
            mView?.back()
            return
        }

        updateAccount(UpdateProfile(screenName = name))
    }

    private fun updateAccount(profile: UpdateProfile) {
        mView?.showProgress()
        DataManager.Remote.updateProfile(profile)
                .ioToMain()
                .bindToLifecycle(this)
                .subscribeBy(onNext = { data ->
                    AnalyticsManager.logEvent(AnalyticsKey.Event.PROFILE_EDIT, AnalyticsKey.Parameter.NAME_CHANGED)
                    AccountManager.updateAccount(data)
                    mView?.hideProgress()
                    TipDialog.show(CoreApp.getLastBaseActivityInstance()
                            ?: return@subscribeBy,
                            ResUtils.getString(R.string.Common_Done),
                            TipDialog.SHOW_TIME_SHORT, TipDialog.TYPE_FINISH)
                    mView?.back()
                }, onError = {
                    mView?.hideProgress()
                    TipDialog.show(CoreApp.getLastBaseActivityInstance()
                            ?: return@subscribeBy,
                            ResUtils.getString(R.string.Tip_ServerError),
                            TipDialog.SHOW_TIME_SHORT, TipDialog.TYPE_ERROR)
                })
    }

}

