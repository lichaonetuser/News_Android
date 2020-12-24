package com.box.app.news.page.mvp.layer.main.dialog.selectAge

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
class SelectAgeDialogPresenter : MVPDialogPresenter<SelectAgeDialogContract.View>(),
        SelectAgeDialogContract.Presenter<SelectAgeDialogContract.View> {

    private val ageStringList = DataDictionary.SelectInfoObject.AGE_STRING_LIST
    private var originAgeStage = DataDictionary.SelectInfoObject.UNSET
    private var update = false

    init {
        mView?.setAge(ageStringList)
        originAgeStage = AccountManager.account?.ageStage?: DataDictionary.SelectInfoObject.UNSET
    }

    override fun onClickConfirm() {
        //防止重复点击
        if (update) {
            return
        }
        update = true

        val ageString = ageStringList[mView?.getAge() ?: 0] //获取年龄段的字符串
        //获取年龄段的int表示 11～16
        val ageStage = DataDictionary.SelectInfoObject.AGE_STAGE_MAP[ageString] ?: DataDictionary.SelectInfoObject.UNSET

        //发送友盟统计
        val parameters = when (ageStage) {
            DataDictionary.SelectInfoObject.AGE_STAGE_LIST[0]-> AnalyticsKey.Parameter.AGE_SELECT_ONE_LEVEL
            DataDictionary.SelectInfoObject.AGE_STAGE_LIST[1]-> AnalyticsKey.Parameter.AGE_SELECT_TWO_LEVEL
            DataDictionary.SelectInfoObject.AGE_STAGE_LIST[2]-> AnalyticsKey.Parameter.AGE_SELECT_THREE_LEVEL
            DataDictionary.SelectInfoObject.AGE_STAGE_LIST[3]-> AnalyticsKey.Parameter.AGE_SELECT_FOUR_LEVEL
            DataDictionary.SelectInfoObject.AGE_STAGE_LIST[4]-> AnalyticsKey.Parameter.AGE_SELECT_FIVE_LEVEL
            DataDictionary.SelectInfoObject.AGE_STAGE_LIST[5]-> AnalyticsKey.Parameter.AGE_SELECT_SIX_LEVEL
            else -> AnalyticsKey.Parameter.AGE_SELECT_ONE_LEVEL  //不可能出现，为了使编译通过
        }
        AnalyticsManager.logEvent(AnalyticsKey.Event.PROFILE_EDIT, parameters)

        if (originAgeStage == ageStage) {
            mView?.back()
            return
        }
        DataManager.Remote.selectInfo(SelectInfo(DataDictionary.SelectInfoObject.UNSET, ageStage))
                .ioToMain()
                .subscribeBy(
                        onNext = {
                            AccountManager.account?.ageStage = it.ageStage
                            AccountManager.updateAccount(AccountManager.account ?: Account(ageStage = it.ageStage))
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