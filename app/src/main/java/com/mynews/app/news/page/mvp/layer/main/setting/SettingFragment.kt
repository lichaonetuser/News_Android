package com.mynews.app.news.page.mvp.layer.main.setting

import android.os.Bundle
import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.widget.NewsVideoView
import com.mynews.common.core.json.gson.util.CoreGsonUtils
import com.mynews.common.core.net.http.HttpManager
import com.mynews.common.core.util.*
import com.mynews.common.extension.app.mvp.base.MVPBaseFragment
import kotlinx.android.synthetic.main.fragment_setting.*
import me.yokeyword.fragmentation.anim.FragmentAnimator
import org.jetbrains.anko.noButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.yesButton

class SettingFragment : MVPBaseFragment<SettingContract.View,
        SettingContract.Presenter<SettingContract.View>>(),
        SettingContract.View {

    override val mPresenter = SettingPresenter()
    override val mLayoutRes = R.layout.fragment_setting
    override var mDispatchBack = false

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        initBars()
    }

    private fun initBars() {
        setting_information_bar.setOnClickListener { mPresenter.onClickBar(it.id) }
        setting_feedback_bar.setOnClickListener { mPresenter.onClickBar(it.id) }
        setting_font_bar.setOnClickListener { mPresenter.onClickBar(it.id) }
        setting_term_bar.setOnClickListener { mPresenter.onClickBar(it.id) }
        setting_version_bar.setOnClickListener { mPresenter.onClickBar(it.id) }
        setting_video_clarity_bar.setOnClickListener { mPresenter.onClickBar(it.id) }
        setting_push_bar.setOnClickListener { mPresenter.onClickBar(it.id) }
        logout_btn.setOnClickListener { mPresenter.onClickLogout() }
    }

    override fun setVersionName(name: String) {
        setting_version_bar.setValue(name)
    }

    override fun setFontSize(size: String) {
        setting_font_bar.setValue(size)
    }

    override fun setVideoClarity(clarity: NewsVideoView.Clarity) {
        setting_video_clarity_bar.setValue(clarity.text)
    }

    override fun setHasUnreadFeedback(hasUnread: Boolean) {
        setting_feedback_bar.setRemindVisibility(if (hasUnread) View.VISIBLE else View.GONE)
    }

    override fun onCreateFragmentAnimator(): FragmentAnimator {
        return FragmentAnimator(
                R.anim.no_anim,
                R.anim.no_anim,
                R.anim.no_anim,
                R.anim.no_anim)
    }

    override fun showLogout() {
        logout_btn.visibility = View.VISIBLE
    }

    override fun hideLogout() {
        logout_btn.visibility = View.GONE
    }

    override fun showLogoutConfirmDialog() {
        alert(R.string.Tip_LogOutAlert) {
            yesButton {
                mPresenter.onClickLogoutConfirm(true)
                it.dismiss()
            }
            noButton {
                mPresenter.onClickLogoutConfirm(false)
                it.dismiss()
            }
        }.show()
    }

    override fun gotoSendEmail() {
        context?.run {
            sendEmail(this, arrayOf(FEEDBACK_EMAIL), "フィードバック", getFeedbackAppender(), "")
        }
    }

    private fun getFeedbackAppender(): String {
        val content = ResUtils.getString(R.string.Feedback_Param_Tip) + "\n"
        val param = HttpManager.getCommonParams()
        context?.run {
            val memInfo = getMemoryInfo(this)
            if (memInfo != null && memInfo.size > 1) {
                param["totalMemory"] = memInfo[0]
                param["usageMemory"] = memInfo[1]
            }
            val diskInfo = getDiskInfo(this)
            if (diskInfo.size > 1) {
                param["totalStorage"] = diskInfo[0]
                param["usageStorage"] = diskInfo[1]
            }
        }

        val str = CoreGsonUtils.toJson(param)
        return "\n\n\n\n\n\n\n\n-------------------------------------------------------------\n$content$str"
    }
}