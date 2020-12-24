package com.box.app.news.page.mvp.layer.main.me.profile.edit

import android.os.Bundle
import com.box.app.news.R
import com.box.app.news.account.AccountManager
import com.box.app.news.analytics.AnalyticsKey
import com.box.app.news.bean.Account
import com.box.app.news.bean.UpdateProfile
import com.box.app.news.data.DataDictionary
import com.box.app.news.data.DataManager
import com.box.app.news.event.EventManager
import com.box.app.news.event.OpenUpdateChannelDialogEvent
import com.box.app.news.event.change.AccountChangeEvent
import com.box.app.news.page.mvp.layer.main.dialog.login.LoginDialogFragment
import com.box.app.news.page.mvp.layer.main.dialog.selectAge.SelectAgeDialogFragment
import com.box.app.news.page.mvp.layer.main.dialog.selectSex.SelectSexDialogFragment
import com.box.app.news.page.mvp.layer.main.me.profile.edit.name.ProfileEditNameFragment
import com.box.common.core.CoreApp
import com.box.common.core.analytics.AnalyticsManager
import com.box.common.core.rx.permission.RxPermission
import com.box.common.core.rx.schedulers.ioToMain
import com.box.common.core.util.ResUtils
import com.box.common.extension.app.mvp.base.MVPBasePresenter
import com.box.common.extension.app.mvp.bindToLifecycle
import com.box.common.extension.picture.PictureSelectorHelper
import com.kongzue.dialog.v2.TipDialog
import com.luck.picture.lib.entity.LocalMedia
import com.yanzhenjie.permission.Permission
import io.reactivex.rxkotlin.subscribeBy
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File

@Suppress("unused")

class ProfileEditPresenter : MVPBasePresenter<ProfileEditContract.View>(),
        ProfileEditContract.Presenter<ProfileEditContract.View> {

    private var originAgeState = -2
    private var originGender = -2

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        AnalyticsManager.logEvent(AnalyticsKey.Event.PROFILE_EDIT, AnalyticsKey.Parameter.ENTER)
        val account = AccountManager.account ?: Account()
        originAgeState = account.ageStage
        originGender = account.gender
        mView?.setAccount(account)
        EventManager.register(this)
        getProgileRemote()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventManager.unregister(this)
    }

    override fun onClickBar(id: Int) {
        when (id) {
            R.id.profile_avatar_bar -> {
                AnalyticsManager.logEvent(AnalyticsKey.Event.PROFILE_EDIT, AnalyticsKey.Parameter.SET_AVATAR)
                if (AccountManager.isLogin()) {
                    RxPermission
                            .request(*Permission.STORAGE)
                            .singleOrError()
                            .subscribeBy(
                                    onSuccess = {
                                        mView?.goPictureSelector()
                                    },
                                    onError = {

                                    }
                            )
                } else {
                    mView?.goFromRoot(LoginDialogFragment::class.java)
                    AnalyticsManager.logEvent(AnalyticsKey.Event.PROFILE_EDIT, AnalyticsKey.Parameter.AVATAR_LOGIN_SHOW)
                }
            }
            R.id.profile_name_bar -> {
                AnalyticsManager.logEvent(AnalyticsKey.Event.PROFILE_EDIT, AnalyticsKey.Parameter.SET_NICK_NAME)
                if (AccountManager.isLogin()) {
                    mView?.goFromRoot(ProfileEditNameFragment::class.java)
                } else {
                    mView?.goFromRoot(LoginDialogFragment::class.java)
                    AnalyticsManager.logEvent(AnalyticsKey.Event.PROFILE_EDIT, AnalyticsKey.Parameter.NAME_LOGIN_SHOW)
                }
            }
            R.id.profile_sex_bar -> {
                mView?.goFromRoot(SelectSexDialogFragment::class.java)
            }
            R.id.profile_age_bar -> {
                mView?.goFromRoot(SelectAgeDialogFragment::class.java)
            }
        }
    }

    override fun onSelectPicture(localMedia: LocalMedia) {
        updatePicture(localMedia)
    }

    private fun updatePicture(localMedia: LocalMedia) {
        mView?.showProgress()
        DataManager.Remote.uploadImageFile(File(PictureSelectorHelper.getPathFromLocalMedia(localMedia)))
                .ioToMain()
                .subscribeBy(onNext = { data ->
                    updateAccount(UpdateProfile(imageUri = data.imageUri))
                }, onError = {
                    mView?.hideProgress()
                    TipDialog.show(CoreApp.getLastBaseActivityInstance()
                            ?: return@subscribeBy,
                            ResUtils.getString(R.string.Tip_ServerError),
                            TipDialog.SHOW_TIME_SHORT, TipDialog.TYPE_ERROR)
                })
    }

    private fun updateAccount(profile: UpdateProfile) {
        DataManager.Remote.updateProfile(profile)
                .ioToMain()
                .bindToLifecycle(this)
                .subscribeBy(onNext = { data ->
                    AnalyticsManager.logEvent(AnalyticsKey.Event.PROFILE_EDIT, AnalyticsKey.Parameter.AVATAR_CHANGED)
                    AccountManager.updateAccount(data)
                    mView?.hideProgress()
                    TipDialog.show(CoreApp.getLastBaseActivityInstance()
                            ?: return@subscribeBy,
                            ResUtils.getString(R.string.Common_Done),
                            TipDialog.SHOW_TIME_SHORT, TipDialog.TYPE_FINISH)
                }, onError = {
                    mView?.hideProgress()
                    TipDialog.show(CoreApp.getLastBaseActivityInstance()
                            ?: return@subscribeBy,
                            ResUtils.getString(R.string.Tip_ServerError),
                            TipDialog.SHOW_TIME_SHORT, TipDialog.TYPE_ERROR)
                })
    }

    private fun getProgileRemote() {
        mView?.showProgress()
        DataManager.Remote.getProfile()
                .ioToMain()
                .subscribeBy(
                        onNext = { user ->
                            val data = user.user
                            val accout = AccountManager.account

                            //后端如果返回的年龄性别不再合法范围则清空
                            val gender = if (data.gender == DataDictionary.SelectInfoObject.MALE ||
                                    data.gender == DataDictionary.SelectInfoObject.FEMALE) {
                                data.gender
                            } else {
                                DataDictionary.SelectInfoObject.UNSET
                            }
                            val ageStage = if (DataDictionary.SelectInfoObject.AGE_STAGE_LIST.contains(data.ageStage)) {
                                data.ageStage
                            } else {
                                DataDictionary.SelectInfoObject.UNSET
                            }

                            originAgeState = ageStage
                            originGender = gender
                            if (accout == null) {

                                val newAccount = Account(screenName = data.screenName,
                                        avatarUrl = data.avatarUrl, gender = gender, ageStage = ageStage)
                                AccountManager.updateAccount(newAccount, true)
                                mView?.setAccount(newAccount)

                                EventManager.post(OpenUpdateChannelDialogEvent(false))
                            } else if (data.screenName != accout.screenName ||
                                    data.ageStage != accout.ageStage ||
                                    data.avatarUrl != accout.avatarUrl ||
                                    data.gender != accout.gender) {

                                AccountManager.account?.screenName = data.screenName
                                AccountManager.account?.avatarUrl = data.avatarUrl
                                AccountManager.account?.gender = gender
                                AccountManager.account?.ageStage = ageStage
                                AccountManager.updateAccount(AccountManager.account ?: Account(), true)
                                mView?.setAccount(AccountManager.account ?: Account())

                                EventManager.post(OpenUpdateChannelDialogEvent(false))
                            }
                            mView?.hideProgress()
                        },
                        onError = {
                            mView?.hideProgress()
                            TipDialog.show(CoreApp.getLastBaseActivityInstance()
                                    ?: return@subscribeBy,
                                    ResUtils.getString(R.string.Tip_ServerError),
                                    TipDialog.SHOW_TIME_SHORT, TipDialog.TYPE_ERROR)
                        }
                )
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAccountChangeEvent(event: AccountChangeEvent) {
        mView?.setAccount(event.account)

        if (event.fromProfile) {
            EventManager.post(OpenUpdateChannelDialogEvent(false))
            originAgeState = event.account.ageStage
            originGender = event.account.gender
        } else {
            if (event.account.gender != originGender || event.account.ageStage != originAgeState) {
                EventManager.post(OpenUpdateChannelDialogEvent(true))
            }
        }
    }
}

