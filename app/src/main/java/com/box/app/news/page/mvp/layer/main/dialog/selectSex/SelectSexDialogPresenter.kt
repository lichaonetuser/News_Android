package com.box.app.news.page.mvp.layer.main.dialog.selectSex

import com.box.app.news.R
import com.box.app.news.account.AccountManager
import com.box.app.news.analytics.AnalyticsKey
import com.box.app.news.bean.Account
import com.box.app.news.bean.SelectInfo
import com.box.app.news.data.DataDictionary
import com.box.app.news.data.DataManager
import com.box.common.core.CoreApp
import com.box.common.core.analytics.AnalyticsManager
import com.box.common.core.rx.schedulers.ioToMain
import com.box.common.core.util.ResUtils
import com.box.common.extension.app.mvp.dialog.MVPDialogPresenter
import com.kongzue.dialog.v2.TipDialog
import io.reactivex.rxkotlin.subscribeBy

/**
 *
 */
class SelectSexDialogPresenter : MVPDialogPresenter<SelectSexDialogContract.View>(),
        SelectSexDialogContract.Presenter<SelectSexDialogContract.View> {

    private var originSex = DataDictionary.SelectInfoObject.UNSET
    private var update = false

    init {
        originSex = AccountManager.account?.gender ?: DataDictionary.SelectInfoObject.UNSET
    }

    override fun onMaleSelect() {
        onSelect(false)
    }

    override fun onFemaleSelect() {
        onSelect(true)
    }

    private fun onSelect(isFemale: Boolean) {
        //防止重复点击
        if (update) {
            return
        }
        update = true

        val sex = if (isFemale) DataDictionary.SelectInfoObject.FEMALE else DataDictionary.SelectInfoObject.MALE

        //发送友盟统计
        val parameters = if (isFemale) {
            AnalyticsKey.Parameter.SET_GENDER_FEMALE
        } else {
            AnalyticsKey.Parameter.SET_GENDER_MALE
        }
        AnalyticsManager.logEvent(AnalyticsKey.Event.PROFILE_EDIT, parameters)

        if (sex == originSex) {
            mView?.back()
            return
        }
        DataManager.Remote.selectInfo(
                SelectInfo(sex, DataDictionary.SelectInfoObject.UNSET))
                .ioToMain()
                .subscribeBy(
                        onNext = {
                            AccountManager.account?.gender = it.gender
                            AccountManager.updateAccount(AccountManager.account ?: Account(gender = it.gender))
                            mView?.back()
                            TipDialog.show(CoreApp.getLastBaseActivityInstance()
                                    ?: return@subscribeBy,
                                    ResUtils.getString(R.string.Common_Done),
                                    TipDialog.SHOW_TIME_SHORT, TipDialog.TYPE_FINISH)
                        },
                        onError = {
                            update = false

                            TipDialog.show(CoreApp.getLastBaseActivityInstance()
                                    ?: return@subscribeBy,
                                    ResUtils.getString(R.string.Tip_ServerError),
                                    TipDialog.SHOW_TIME_SHORT, TipDialog.TYPE_ERROR)
                        }
                )
    }
}

