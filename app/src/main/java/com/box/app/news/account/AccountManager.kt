package com.box.app.news.account

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import com.box.app.news.R
import com.box.app.news.analytics.AnalyticsHelper
import com.box.app.news.analytics.AnalyticsKey
import com.box.app.news.bean.Account
import com.box.app.news.bean.Channel
import com.box.app.news.bean.LoginRequestInfo
import com.box.app.news.data.DataDictionary
import com.box.app.news.data.DataDictionary.AccountPlatform.*
import com.box.app.news.data.DataManager
import com.box.app.news.event.EventManager
import com.box.app.news.event.OpenUpdateChannelDialogEvent
import com.box.app.news.event.change.AccountChangeEvent
import com.box.app.news.event.change.HideOrShowNewChannelTipEvent
import com.box.app.news.event.login.LoginEvent
import com.box.app.news.event.login.LogoutEvent
import com.box.app.news.event.refresh.UpdateArticleVideoChannelEvent
import com.box.app.news.util.ReddotUtils
import com.box.app.news.util.UIDUtils
import com.box.common.core.CoreApp
import com.box.common.core.log.Logger
import com.box.common.core.rx.schedulers.io
import com.box.common.core.rx.schedulers.ioToMain
import com.box.common.core.rx.schedulers.main
import com.box.common.core.util.ResUtils
import com.box.common.extension.login.*
import com.kongzue.dialog.v2.TipDialog
import com.kongzue.dialog.v2.WaitDialog
import io.reactivex.Completable
import io.reactivex.rxkotlin.subscribeBy
import me.yokeyword.fragmentation.SupportActivity

object AccountManager {

    const val LOGGER_TAG = "ACCOUNT"

    var mIAccountExtraListener: IAccountExtraListener? = null
    var mIsDoLogin = false

    var account: Account? = DataManager.Local.getAccountInfo()
        private set

    fun getAccountPlatform(): DataDictionary.AccountPlatform? {
        return DataDictionary.AccountPlatform.intValueOf(DataManager.Local.getAccountPlatform())
    }

    fun isLogin(): Boolean {
        return DataManager.Local.getAccountIsLogin()
    }

    fun login(activity: Activity, accountPlatform: DataDictionary.AccountPlatform, showTip: Boolean = true, analyticsEventKey: String) {
        if (activity?.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        mIAccountExtraListener?.onPreLogin()
        mIsDoLogin = true
        LoginManager.login(activity, accountPlatform.loginPlatform, object : ILoginListener {
            override fun onSuccess(platform: LoginPlatform, result: Map<String, Any>) {
                Logger.tag(LOGGER_TAG).d("login platform pass")
                val loginRequestInfo = LoginRequestInfo(platform = accountPlatform.value)
                when (accountPlatform) {
                    FACEBOOK -> {
                        val token = result[OLoginFacebook.KEY_TOKEN] as? String
                        if (token.isNullOrBlank()) {
                            Logger.tag(LOGGER_TAG).i("facebook token is null or blank")
                            showFailTip(showTip)
                            return
                        }
                        loginRequestInfo.token = token!!
                    }
                    TWITTER -> {
                        val token = result[OLoginTwitter.KEY_TOKEN] as? String
                        val secret = result[OLoginTwitter.KEY_SECRET] as? String
                        if (token.isNullOrBlank()) {
                            Logger.tag(LOGGER_TAG).i("twitter token is null or blank")
                            showFailTip(showTip)
                            return
                        }
                        if (secret.isNullOrBlank()) {
                            Logger.tag(LOGGER_TAG).i("twitter secret is null or blank")
                            showFailTip(showTip)
                            return
                        }
                        loginRequestInfo.token = token!!
                        loginRequestInfo.secret = secret!!
                    }
                    GOOGLE -> {
                        val idToken = result[OLoginGoogle.KEY_ID_TOKEN] as? String
                        val serverAuthCode = result[OLoginGoogle.KEY_SERVER_AUTH_CODE] as? String
                        if (idToken.isNullOrBlank()) {
                            Logger.tag(LOGGER_TAG).i("google idToken is null or blank")
                            showFailTip(showTip)
                            return
                        }
                        if (serverAuthCode.isNullOrBlank()) {
                            Logger.tag(LOGGER_TAG).i("google serverAuthCode is null or blank")
                            showFailTip(showTip)
                            return
                        }
                        loginRequestInfo.idToken = idToken!!
                        loginRequestInfo.serverAuthCode = serverAuthCode!!
                    }
                }

                showLoading(showTip)

                DataManager.Remote.login(loginRequestInfo)
                        .ioToMain()
                        .subscribeBy(
                                onNext = {
                                    dismissLoading()
                                    if (it.code != DataDictionary.LoginCode.SUCCESS.value) {
                                        Logger.tag(LOGGER_TAG).d("login api already login")
                                        showSuccessTip(showTip)
                                        EventManager.post(LoginEvent(success = true, account = it.account))
                                        mIsDoLogin = false
                                        mIAccountExtraListener?.onFinishLogin()
                                        return@subscribeBy
                                    }

                                    if (it.login_status != DataDictionary.LoginStatus.SUCCESS.value) {
                                        Logger.tag(LOGGER_TAG).d("login api fail : ${it.msg}")
                                        if (it.msg.isBlank()) {
                                            showFailTip(showTip)
                                        } else {
                                            showFailTip(showTip, it.msg)
                                        }
                                        EventManager.post(LoginEvent(success = false, account = null))
                                        mIsDoLogin = false
                                        mIAccountExtraListener?.onFinishLogin()
                                        return@subscribeBy
                                    }
                                    showSuccessTip(showTip)
                                    account = it.account
                                    DataManager.Local.saveAccountInfo(account)
                                    DataManager.Local.saveAccountPlatform(accountPlatform.value)
                                    DataManager.Local.saveAccountIsLogin(true)
                                    UIDUtils.updateUIdToHttpParams()
                                    Logger.tag(LOGGER_TAG).d("login api success : $it")
                                    EventManager.post(LoginEvent(success = true, account = it.account))
                                    when (accountPlatform) {
                                        FACEBOOK -> AnalyticsHelper.logLoginEvent(analyticsEventKey, AnalyticsKey.Parameter.LOGIN_SUCCESS_FACEBOOK)
                                        TWITTER -> AnalyticsHelper.logLoginEvent(analyticsEventKey, AnalyticsKey.Parameter.LOGIN_SUCCESS_TWITTER)
                                        GOOGLE -> AnalyticsHelper.logLoginEvent(analyticsEventKey, AnalyticsKey.Parameter.LOGIN_SUCCESS_GOOGLE)
                                    }
                                    mIsDoLogin = false
                                    mIAccountExtraListener?.onFinishLogin()
                                    dismissLoading()
                                },
                                onError = {
                                    dismissLoading()
                                    showFailTip(showTip)
                                    EventManager.post(LoginEvent(success = false, account = null))
                                    when (accountPlatform) {
                                        FACEBOOK -> AnalyticsHelper.logLoginEvent(analyticsEventKey, AnalyticsKey.Parameter.LOGIN_FAIL_FACEBOOK)
                                        TWITTER -> AnalyticsHelper.logLoginEvent(analyticsEventKey, AnalyticsKey.Parameter.LOGIN_FAIL_TWITTER)
                                        GOOGLE -> AnalyticsHelper.logLoginEvent(analyticsEventKey, AnalyticsKey.Parameter.LOGIN_FAIL_GOOGLE)
                                    }
                                    mIsDoLogin = false
                                    mIAccountExtraListener?.onFinishLogin()
                                    dismissLoading()
                                })
            }

            override fun onCancel(platform: LoginPlatform) {
                mIsDoLogin = false
                mIAccountExtraListener?.onFinishLogin()
            }

            override fun onFailure(platform: LoginPlatform, error: Exception) {
                Logger.tag(LOGGER_TAG).d("login fail : $error")
                showFailTip(showTip)
                EventManager.post(LoginEvent(success = false, account = null))
                when (accountPlatform) {
                    FACEBOOK -> AnalyticsHelper.logLoginEvent(analyticsEventKey, AnalyticsKey.Parameter.LOGIN_FAIL_FACEBOOK)
                    TWITTER -> AnalyticsHelper.logLoginEvent(analyticsEventKey, AnalyticsKey.Parameter.LOGIN_FAIL_TWITTER)
                    GOOGLE -> AnalyticsHelper.logLoginEvent(analyticsEventKey, AnalyticsKey.Parameter.LOGIN_FAIL_GOOGLE)
                }
                mIsDoLogin = false
                mIAccountExtraListener?.onFinishLogin()
            }
        })
    }

    fun logoutFromLastActivity(showTip: Boolean = true, analyticsEventKey: String, force: Boolean = false) {
        val lastActivity = CoreApp.getLastActivityInstance()
        if (lastActivity != null) {
            logout(lastActivity, showTip = showTip, analyticsEventKey = analyticsEventKey, force = force)
        }
    }

    fun logout(activity: Activity, showTip: Boolean = true, analyticsEventKey: String, force: Boolean = false) {
        if (force) {
            EventManager.post(LogoutEvent(success = true, account = null))
            DataManager.Local.saveAccountIsLogin(false)
        }
        showLoading(showTip)
        val loginAcccountPlatform = getAccountPlatform()
        DataManager.Remote.logout()
                .ioToMain()
                .subscribeBy(
                        onNext = {
                            try {
                                if (it.code != DataDictionary.LogoutCode.SUCCESS.value) {
                                    showFailTip(showTip, ResUtils.getString(R.string.Tip_LogOutFail))
                                    Logger.tag(LOGGER_TAG).d("logout fail code : ${it.code}")
                                    EventManager.post(LogoutEvent(success = false, account = account))
                                    return@subscribeBy
                                }
                                DataManager.Local.saveAccountIsLogin(false)
                                val accountPlatform = getAccountPlatform() ?: return@subscribeBy
                                LoginManager.logout(activity, accountPlatform.loginPlatform)
                                Logger.tag(LOGGER_TAG).d("logout success")
                                showSuccessTip(showTip, ResUtils.getString(R.string.Tip_LogOutSuccess))
                                EventManager.post(LogoutEvent(success = true, account = account))
                                account = null
                                UIDUtils.updateUIdToHttpParams()
                                dismissLoading()
                                when (loginAcccountPlatform) {
                                    FACEBOOK -> AnalyticsHelper.logLoginEvent(analyticsEventKey, AnalyticsKey.Parameter.LOGOUT_SUCCESS_FACEBOOK)
                                    TWITTER -> AnalyticsHelper.logLoginEvent(analyticsEventKey, AnalyticsKey.Parameter.LOGOUT_SUCCESS_TWITTER)
                                    GOOGLE -> AnalyticsHelper.logLoginEvent(analyticsEventKey, AnalyticsKey.Parameter.LOGOUT_SUCCESS_GOOGLE)
                                }

                                //7.1 增加需求，当登陆成功后拉取profile接口，并更新channel
                                getProfile()
                                updateChannel()
                            } catch (e: Exception) {
                                showFailTip(showTip, ResUtils.getString(R.string.Tip_LogOutFail))
                                Logger.tag(LOGGER_TAG).d("logout Exception : $e")
                                EventManager.post(LogoutEvent(success = false, account = account))
                                when (loginAcccountPlatform) {
                                    FACEBOOK -> AnalyticsHelper.logLoginEvent(analyticsEventKey, AnalyticsKey.Parameter.LOGOUT_FAIL_FACEBOOK)
                                    TWITTER -> AnalyticsHelper.logLoginEvent(analyticsEventKey, AnalyticsKey.Parameter.LOGOUT_FAIL_TWITTER)
                                    GOOGLE -> AnalyticsHelper.logLoginEvent(analyticsEventKey, AnalyticsKey.Parameter.LOGOUT_FAIL_GOOGLE)
                                }
                            }
                        },
                        onError = {
                            showFailTip(showTip, ResUtils.getString(R.string.Tip_LogOutFail))
                            Logger.tag(LOGGER_TAG).d("logout fail : $it")
                            EventManager.post(LogoutEvent(success = false, account = account))
                            dismissLoading()
                            when (loginAcccountPlatform) {
                                FACEBOOK -> AnalyticsHelper.logLoginEvent(analyticsEventKey, AnalyticsKey.Parameter.LOGOUT_FAIL_FACEBOOK)
                                TWITTER -> AnalyticsHelper.logLoginEvent(analyticsEventKey, AnalyticsKey.Parameter.LOGOUT_FAIL_TWITTER)
                                GOOGLE -> AnalyticsHelper.logLoginEvent(analyticsEventKey, AnalyticsKey.Parameter.LOGOUT_FAIL_GOOGLE)
                            }
                        }
                )
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            LoginManager.handleActivityResult(requestCode, resultCode, data)
        } catch (e: Exception) {
            Logger.tag(LOGGER_TAG).d("handleActivityResult Exception : $e")
        }
    }

    fun showLoading(show: Boolean) {
        if (show) {
            Completable.fromCallable {
                WaitDialog.show(CoreApp.getLastActivityInstance { it is SupportActivity }
                        ?: return@fromCallable 1,
                        ResUtils.getString(R.string.Tip_Loading))
            }.main().subscribe()
        }
    }

    fun dismissLoading() {
        WaitDialog.dismiss()
    }

    fun showSuccessTip(show: Boolean, tip: String = ResUtils.getString(R.string.Tip_LogInSuccess)) {
        if (show) {
            TipDialog.show(CoreApp.getLastActivityInstance { it is SupportActivity } ?: return,
                    tip,
                    TipDialog.SHOW_TIME_SHORT, TipDialog.TYPE_FINISH)

        }
    }

    fun showFailTip(show: Boolean, tip: String = ResUtils.getString(R.string.Tip_LogInFail)) {
        if (show) {
            TipDialog.show(CoreApp.getLastActivityInstance { it is SupportActivity } ?: return,
                    tip,
                    TipDialog.SHOW_TIME_SHORT, TipDialog.TYPE_ERROR)
        }
    }

    //fromProfile参数用来指示是不是/user/profile/导致的账户更新
    fun updateAccount(account: Account, fromProfile: Boolean = false) {
        this.account = account
        DataManager.Local.saveAccountInfo(account)
        EventManager.post(AccountChangeEvent(account, fromProfile))
    }

    private fun getProfile() {
        DataManager.Remote.getProfile()
                .io()
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

                            if (accout == null) {

                                val newAccount = Account(screenName = data.screenName,
                                        avatarUrl = data.avatarUrl, gender = gender, ageStage = ageStage)
                                AccountManager.updateAccount(newAccount, true)

                                EventManager.post(OpenUpdateChannelDialogEvent(false))
                            } else if (data.screenName != accout.screenName ||
                                    data.ageStage != accout.ageStage ||
                                    data.avatarUrl != accout.avatarUrl ||
                                    data.gender != accout.gender) {

                                AccountManager.account?.screenName = data.screenName
                                AccountManager.account?.avatarUrl = data.avatarUrl
                                AccountManager.account?.gender = gender
                                AccountManager.account?.ageStage = ageStage
                                AccountManager.updateAccount(AccountManager.account
                                        ?: Account(), true)

                                EventManager.post(OpenUpdateChannelDialogEvent(false))
                            }
                        },
                        onError = {
                            //7.1 登陆成功自动调用user/profile接口，失败无提示
//                            TipDialog.show(CoreApp.getLastActivityInstance()
//                                    ?: return@subscribeBy,
//                                    ResUtils.getString(R.string.Tip_ServerError),
//                                    TipDialog.SHOW_TIME_SHORT, TipDialog.TYPE_ERROR)
                        }
                )
    }

    //7.1 登陆成功更新文章/视频频道
    private fun updateChannel() {
        DataManager.Remote.getChannelList()
                .io()
                .subscribeBy(
                        onNext = { data ->

                            //发送广播是否展示文章首页频道编辑按钮右上角的新频道提醒
                            //如果推荐频道里有没有存在本地的reddot，则发送广播显示提醒
                            var show = false
                            data.recommendChannels.articleChannels.forEach {
                                if (!ReddotUtils.containRedDot(it.redDot.toString())) {
                                    show = true
                                }
                            }
                            if (show) {
                                EventManager.post(HideOrShowNewChannelTipEvent(true))
                            }

                            //推荐频道由客户端自行添加
                            val articleRecommendChannel = Channel(DataDictionary.CHANNEL_ID_RECOMMEND_ARTICLE, ResUtils.getString(R.string.Channel_ForYou), index = 0)
                            val videoRecommendChannel = Channel(DataDictionary.CHANNEL_ID_RECOMMEND_VIDEO, ResUtils.getString(R.string.Channel_ForYou), index = 0)

                            if (data.selectedChannels.articleChannels.isEmpty() && data.selectedChannels.videoChannels.isEmpty()) {
                                return@subscribeBy
                            }

                            //过滤、添加推荐、为index赋值，添加memory缓存、文件缓存
                            val articleRemoteChannels = data.selectedChannels.articleChannels
                            val articleFilterRemoteChannels = articleRemoteChannels.filter {
                                it.channelType == DataDictionary.ChannelType.ARTICLE.value ||
                                        it.channelType == DataDictionary.ChannelType.VIDEO.value ||
                                        it.channelType == DataDictionary.ChannelType.IMAGE.value ||
                                        it.channelType == DataDictionary.ChannelType.GIF.value ||
                                        it.channelType == DataDictionary.ChannelType.PUBLIC_FEED.value ||
                                        it.channelType == DataDictionary.ChannelType.TWITTER_VIDEO.value
                            }.toMutableList()
                            articleFilterRemoteChannels.removeAll { it.chid == DataDictionary.CHANNEL_ID_RECOMMEND_ARTICLE }
                            articleFilterRemoteChannels.add(0, articleRecommendChannel)
                            for (i in articleFilterRemoteChannels.indices) {
                                articleFilterRemoteChannels[i].index = i
                            }
                            data.selectedChannels.articleChannels = ArrayList(articleFilterRemoteChannels)

                            val videoRemoteChannels = data.selectedChannels.videoChannels
                            val videoFilterRemoteChannels = videoRemoteChannels.filter {
                                it.channelType == DataDictionary.ChannelType.ARTICLE.value ||
                                        it.channelType == DataDictionary.ChannelType.VIDEO.value ||
                                        it.channelType == DataDictionary.ChannelType.IMAGE.value ||
                                        it.channelType == DataDictionary.ChannelType.GIF.value ||
                                        it.channelType == DataDictionary.ChannelType.TWITTER_VIDEO.value
                            }.toMutableList()
                            videoFilterRemoteChannels.removeAll { it.chid == DataDictionary.CHANNEL_ID_RECOMMEND_VIDEO }
                            videoFilterRemoteChannels.add(0, videoRecommendChannel)
                            for (i in videoFilterRemoteChannels.indices) {
                                videoFilterRemoteChannels[i].index = i
                            }
                            data.selectedChannels.videoChannels = ArrayList(videoFilterRemoteChannels)

                            //更新内存
                            DataManager.Memory.putRecommendChannelList(data.recommendChannels)
                            DataManager.Memory.putChannelList(data.selectedChannels)
                            //更新文件
                            DataManager.Local.saveFileChannelList(data.selectedChannels)
                            //更新数据库
                            val saveChannels = arrayListOf<Channel>()
                            saveChannels.addAll(data.selectedChannels.articleChannels)
                            saveChannels.addAll(data.selectedChannels.videoChannels)
                            DataManager.Local.saveChannelList(saveChannels)


                            //更新文章/视频channel
                            EventManager.post(UpdateArticleVideoChannelEvent(data))
                        }
                )
    }
}