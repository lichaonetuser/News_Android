package com.box.app.news.page.mvp.layer.main.setting

import android.os.Bundle
import com.box.app.news.App
import com.box.app.news.BuildConfig
import com.box.app.news.R
import com.box.app.news.account.AccountManager
import com.box.app.news.analytics.AnalyticsKey
import com.box.app.news.data.DataManager
import com.box.app.news.data.source.remote.http.url.WebNewsUrls
import com.box.app.news.event.EventManager
import com.box.app.news.event.OpenUpdateChannelDialogEvent
import com.box.app.news.event.change.ClarityChangeEvent
import com.box.app.news.event.change.FeedbackHasUnreadChangeEvent
import com.box.app.news.event.change.FontSizeChangeEvent
import com.box.app.news.event.login.LoginEvent
import com.box.app.news.event.login.LogoutEvent
import com.box.app.news.page.mvp.layer.main.clarity.ClarityFragment
import com.box.app.news.page.mvp.layer.main.dialog.updateChannel.UpdateChannelDialogFragment
import com.box.app.news.page.mvp.layer.main.feedback.FeedbackFragment
import com.box.app.news.page.mvp.layer.main.font.FontFragment
import com.box.app.news.page.mvp.layer.main.me.profile.edit.ProfileEditFragment
import com.box.app.news.page.mvp.layer.main.setting.push.PushSettingFragment
import com.box.app.news.page.mvp.layer.main.web.WebBrowserFragment
import com.box.app.news.page.mvp.layer.main.web.WebBrowserPresenterAutoBundle
import com.box.common.core.CoreApp
import com.box.common.core.analytics.AnalyticsManager
import com.box.common.core.environment.EnvPackage
import com.box.common.core.util.ResUtils
import com.box.common.extension.app.mvp.base.MVPBasePresenter
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SettingPresenter : MVPBasePresenter<SettingContract.View>(),
        SettingContract.Presenter<SettingContract.View> {

    private var openUpdateChannelDialog = false

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        mView?.setFontSize(DataManager.Local.getFontSize().name)
        mView?.setVideoClarity(DataManager.Local.getVideoClarity())
        if (BuildConfig.DEBUG) {
            mView?.setVersionName("${EnvPackage.VERSION_NAME}(${BuildConfig.BUILD_TYPE})")
        } else {
            mView?.setVersionName(EnvPackage.VERSION_NAME)
        }
        EventManager.register(this)
        if (AccountManager.isLogin()) {
            mView?.showLogout()
        } else {
            mView?.hideLogout()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventManager.unregister(this)
    }

    override fun onClickBar(id: Int) {
        when (id) {
            R.id.setting_information_bar -> {
                AnalyticsManager.logEvent(AnalyticsKey.Event.SETTING, AnalyticsKey.Parameter.CLICK_FEEDBACK)
                mView?.goFromRoot(ProfileEditFragment::class.java)
            }
            R.id.setting_feedback_bar -> {
                AnalyticsManager.logEvent(AnalyticsKey.Event.SETTING, AnalyticsKey.Parameter.CLICK_FEEDBACK)
                if (App.isDebug() || BuildConfig.BUILD_TYPE == "beta") {
                    // mView?.setHasUnreadFeedback(false)
                    mView?.goFromRoot(FeedbackFragment::class.java)
                } else {
                    mView?.gotoSendEmail()
                }
            }
            R.id.setting_font_bar -> mView?.goFromRoot(FontFragment::class.java)
            R.id.setting_term_bar -> {
                AnalyticsManager.logEvent(AnalyticsKey.Event.SETTING, AnalyticsKey.Parameter.CLICK_TERM_OF_USE)
                mView?.goFromRoot(WebBrowserFragment::class.java,
                        WebBrowserPresenterAutoBundle.builder(WebNewsUrls.getUserTermUrl(),
                                ResUtils.getString(R.string.Setting_UserTerms)
                        ).bundle())
            }
            R.id.setting_version_bar -> {
                /**
                 * 6.9修改  将所有的调试功能入口放在  调试功能页面
                 * {@link com.box.app.news.page.mvp.layer.main.debug.DebugFragment}
                 */
//                if (BuildConfig.DEBUG) {
//                    val url = if (HttpManager.getBaseUrl() == HttpBaseUrls.getReleaseUrl()) {
//                        HttpBaseUrls.TEST
//                    } else {
//                        HttpBaseUrls.getReleaseUrl()
//                    }
//                    DataManager.Local.saveUniqueDeviceId("")
//                    DataManager.Local.saveBaseUrl(url)
//                    HttpManager.setBaseUrl(url)
//                    Toast.makeText(CoreApp.getInstance(), "Current baseUrl ：$url", Toast.LENGTH_LONG).show()
//                }
            }
            R.id.setting_video_clarity_bar -> {
                AnalyticsManager.logEvent(AnalyticsKey.Event.SETTING, AnalyticsKey.Parameter.CLICK_VIDEO_QUALITY)
                mView?.goFromRoot(ClarityFragment::class.java)
            }
            R.id.setting_push_bar -> {
                AnalyticsManager.logEvent(AnalyticsKey.Event.SETTING, AnalyticsKey.Parameter.CLICK_PUSH_SETTING)
                mView?.goFromRoot(PushSettingFragment::class.java)
            }
        }
    }

    override fun onClickLogout() {
        AnalyticsManager.logEvent(AnalyticsKey.Event.SETTING, AnalyticsKey.Parameter.CLICK_LOG_OUT)
        mView?.showLogoutConfirmDialog()
    }

    override fun onClickLogoutConfirm(isYes: Boolean) {
        if (isYes) {
            AccountManager.logout(activity = CoreApp.coreBaseActivities.last(),
                    analyticsEventKey = AnalyticsKey.Event.SETTING)
        }
    }

    override fun onViewVisible() {
        super.onViewVisible()
        if (openUpdateChannelDialog) {
            openUpdateChannelDialog = false
            mView?.goFromRoot(UpdateChannelDialogFragment::class.java)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOpenUpdateChannelDialogEvent(event: OpenUpdateChannelDialogEvent) {
        openUpdateChannelDialog = event.open
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFeedbackHasUnreadChangeEvent(event: FeedbackHasUnreadChangeEvent) {
        mView?.setHasUnreadFeedback(event.hasUnread)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFontSizeChangeEvent(event: FontSizeChangeEvent) {
        mView?.setFontSize(event.fontSize.name)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onClarityChangeEvent(event: ClarityChangeEvent) {
        mView?.setVideoClarity(event.newClarity)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoginEvent(event: LoginEvent) {
        if (event.success) {
            mView?.showLogout()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLogoutEvent(event: LogoutEvent) {
        if (event.success) {
            mView?.hideLogout()
        }
    }
}

