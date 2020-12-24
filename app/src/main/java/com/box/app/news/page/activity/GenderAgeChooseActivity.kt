package com.box.app.news.page.activity

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.TextView
import com.box.app.news.R
import com.box.app.news.account.AccountManager
import com.box.app.news.analytics.AnalyticsKey
import com.box.app.news.applog.AppLogKey
import com.box.app.news.applog.AppLogManager
import com.box.app.news.bean.Account
import com.box.app.news.bean.ExtraInfo
import com.box.app.news.bean.SelectInfo
import com.box.app.news.data.DataDictionary
import com.box.app.news.data.DataManager
import com.box.common.core.CoreApp
import com.box.common.core.analytics.AnalyticsManager
import com.box.common.core.app.activity.CoreBaseActivity
import com.box.common.core.environment.EnvDisplayMetrics
import com.box.common.core.rx.schedulers.ioToMain
import com.box.common.core.util.ResUtils
import com.kongzue.dialog.v2.TipDialog
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer
import com.yatatsu.autobundle.AutoBundle
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.dialog_select_sex_age.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.dip
import org.jetbrains.anko.textColor

/**
 *
 */
class GenderAgeChooseActivity : CoreBaseActivity() {

    companion object {
        val FEMALE_SELECT_COLOR = ResUtils.getColor(R.color.select_sex_female_selected)
        val MALE_SELECT_COLOR = ResUtils.getColor(R.color.select_sex_male_selected)
        val AGE_DEFAULT_COLOR = ResUtils.getColor(R.color.select_age_unselected)
        val BUTTON_DEFAULT_COLOR = ResUtils.getColor(R.color.color_2)
        val AGE_DEFAULT_TEXT_COLOR = ResUtils.getColor(R.color.color_1)
        val AGE_SELECT_TEXT_COLOR = ResUtils.getColor(R.color.color_9)
    }

    private var lastSex: View? = null
    private var lastAge: TextView? = null
    private var clickDone = false
    private var clickSkip = false

    //服务端统计
    private var mStartTime = 0L
    private var hasSwitchBackground = false
    private var hasGotoMain = false

    private var layoutSlideOutAnimator = ValueAnimator.ofInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_select_sex_age)
        //友盟统计
        AnalyticsManager.logEvent(AnalyticsKey.Event.GENDER_INTEREST, AnalyticsKey.Parameter.ENTER)
        AnalyticsManager.logEvent(AnalyticsKey.Event.GENDER_INTEREST, AnalyticsKey.Parameter.SHOW)
        //初始化布局
        initView()
    }

    private fun initView() {

        //UI要求的比例公式，主题弹框与上部距离
        val params = main_container.layoutParams as ConstraintLayout.LayoutParams
        params.topMargin = (EnvDisplayMetrics.HEIGHT_PIXELS - dip(417)) * 105 / 225

        age_below_18.backgroundColor = AGE_DEFAULT_COLOR
        age_between_18_24.backgroundColor = AGE_DEFAULT_COLOR
        age_between_25_29.backgroundColor = AGE_DEFAULT_COLOR
        age_between_30_35.backgroundColor = AGE_DEFAULT_COLOR
        age_between_36_45.backgroundColor = AGE_DEFAULT_COLOR
        age_over_45.backgroundColor = AGE_DEFAULT_COLOR

        sex_female_selected.setOnClickListener { onSexClick(it) }
        sex_male_selected.setOnClickListener { onSexClick(it) }

        age_below_18.setOnClickListener { onAgeClick(it as TextView) }
        age_between_18_24.setOnClickListener { onAgeClick(it as TextView) }
        age_between_25_29.setOnClickListener { onAgeClick(it as TextView) }
        age_between_30_35.setOnClickListener { onAgeClick(it as TextView) }
        age_between_36_45.setOnClickListener { onAgeClick(it as TextView) }
        age_over_45.setOnClickListener { onAgeClick(it as TextView) }

        select_sex_age_skip.setOnClickListener { onSkipClick() }
        select_sex_age_confirm.setOnClickListener { onConfirmClick() }
    }

    private fun onSexClick(view: View) {
        if (lastSex == view || clickSkip || clickDone) {
            return
        }

        //发送友盟统计
        val parameters = if (sex_female_selected == view) {
            AnalyticsKey.Parameter.GENDER_SELECT_FEMALE
        } else {
            AnalyticsKey.Parameter.GENDER_SELECT_MALE
        }
        AnalyticsManager.logEvent(AnalyticsKey.Event.GENDER_INTEREST, parameters)

        //改变显示效果
        lastSex?.alpha = 0F
        view.alpha = 1F
        lastSex = view

        val confirmColor = when (lastSex) {
            null -> BUTTON_DEFAULT_COLOR
            sex_female_selected -> FEMALE_SELECT_COLOR
            else -> MALE_SELECT_COLOR
        }
        //改变年龄选择的颜色
        lastAge?.backgroundColor = confirmColor
        lastAge?.textColor = AGE_SELECT_TEXT_COLOR

        select_sex_age_confirm.setBackgroundColor(confirmColor)
    }

    private fun onAgeClick(view: TextView) {
        if (lastAge == view || clickSkip || clickDone) {
            return
        }

        //发送友盟统计
        val parameters = when (view) {
            age_below_18 -> AnalyticsKey.Parameter.AGE_SELECT_18
            age_between_18_24 -> AnalyticsKey.Parameter.AGE_SELECT_24
            age_between_25_29 -> AnalyticsKey.Parameter.AGE_SELECT_29
            age_between_30_35 -> AnalyticsKey.Parameter.AGE_SELECT_35
            age_between_36_45 -> AnalyticsKey.Parameter.AGE_SELECT_45
            age_over_45 -> AnalyticsKey.Parameter.AGE_SELECT_OVER_45
            else -> AnalyticsKey.Parameter.AGE_SELECT_18  //不可能出现，为了使编译通过
        }
        AnalyticsManager.logEvent(AnalyticsKey.Event.GENDER_INTEREST, parameters)

        //改变显示效果
        lastAge?.backgroundColor = AGE_DEFAULT_COLOR
        lastAge?.textColor = AGE_DEFAULT_TEXT_COLOR

        val confirmColor = when (lastSex) {
            null -> BUTTON_DEFAULT_COLOR
            sex_female_selected -> FEMALE_SELECT_COLOR
            else -> MALE_SELECT_COLOR
        }
        view.backgroundColor = confirmColor
        view.textColor = AGE_SELECT_TEXT_COLOR
        lastAge = view
    }

    private fun onSkipClick() {
        //防止重复点击
        if (clickDone || clickSkip) {
            return
        }
        clickSkip = true

        DataManager.Init.initNewsChannelListFromRemote()

        startAnimation()
        AnalyticsManager.logEvent(AnalyticsKey.Event.GENDER_INTEREST, AnalyticsKey.Parameter.CLICK_SKIP_BUTTON)
    }

    private fun onConfirmClick() {
        //防止重复点击
        if (clickDone || clickSkip) {
            select_sex_age_confirm.isClickable = false
            select_sex_age_confirm.postDelayed({
                select_sex_age_confirm.isClickable = true
            }, 2000)
            return
        }
        clickDone = true

        AnalyticsManager.logEvent(AnalyticsKey.Event.GENDER_INTEREST, AnalyticsKey.Parameter.CLICK_DONE_BUTTON)
        val gender: Int = when (lastSex) {
            sex_female_selected -> DataDictionary.SelectInfoObject.FEMALE
            sex_male_selected -> DataDictionary.SelectInfoObject.MALE
            else -> DataDictionary.SelectInfoObject.UNSET
        }
        val age: Int = when (lastAge) {
            age_below_18 -> DataDictionary.SelectInfoObject.AGE_STAGE_LIST[0]
            age_between_18_24 -> DataDictionary.SelectInfoObject.AGE_STAGE_LIST[1]
            age_between_25_29 -> DataDictionary.SelectInfoObject.AGE_STAGE_LIST[2]
            age_between_30_35 -> DataDictionary.SelectInfoObject.AGE_STAGE_LIST[3]
            age_between_36_45 -> DataDictionary.SelectInfoObject.AGE_STAGE_LIST[4]
            age_over_45 -> DataDictionary.SelectInfoObject.AGE_STAGE_LIST[5]
            else -> DataDictionary.SelectInfoObject.UNSET
        }
        if (gender == DataDictionary.SelectInfoObject.UNSET) {
            TipDialog.show(this@GenderAgeChooseActivity,
                    ResUtils.getString(R.string.Tip_ProfileSelectGender),
                    TipDialog.SHOW_TIME_SHORT, TipDialog.TYPE_WARNING).setCanCancel(true)
            clickDone = false
            return
        }

        if (age == DataDictionary.SelectInfoObject.UNSET) {
            TipDialog.show(this@GenderAgeChooseActivity,
                    ResUtils.getString(R.string.Tip_ProfileSelectAge),
                    TipDialog.SHOW_TIME_SHORT, TipDialog.TYPE_WARNING).setCanCancel(true)
            clickDone = false
            return
        }

        DataManager.Remote.selectInfo(
                SelectInfo(gender, age))
                .ioToMain()
                .subscribeBy(
                        onNext = {
                            AccountManager.account?.gender = it.gender
                            AccountManager.account?.ageStage = it.ageStage
                            AccountManager.updateAccount(AccountManager.account ?: Account(gender = it.gender, ageStage = it.ageStage))

                            DataManager.Init.initNewsChannelListFromRemote()

                            startAnimation()
                        },
                        onError = {
                            clickDone = false

                            TipDialog.show(CoreApp.getLastBaseActivityInstance()
                                    ?: return@subscribeBy,
                                    ResUtils.getString(R.string.Tip_ServerError),
                                    TipDialog.SHOW_TIME_SHORT, TipDialog.TYPE_ERROR)
                        }
                )
    }

    private fun startMain() {
        hasGotoMain = true
        GSYVideoManager.releaseAllVideos() //释放所有正在播放的视频
        val bundle = Bundle()
        AutoBundle.pack(this, bundle)
        val intent = Intent(this@GenderAgeChooseActivity, MainActivity::class.java)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }

    //UI需求：点击取消/确定按钮时，主界面滑出屏幕
    private fun startAnimation() {
        val screenHeight = EnvDisplayMetrics.HEIGHT_PIXELS
        layoutSlideOutAnimator.setIntValues(0, EnvDisplayMetrics.HEIGHT_PIXELS)
        layoutSlideOutAnimator.duration = 500
        layoutSlideOutAnimator.interpolator = AccelerateInterpolator()
        layoutSlideOutAnimator.addUpdateListener {
            val scrollY = it.animatedValue as? Int ?: 0
            main_container.translationY = scrollY.toFloat()
            if (it.animatedValue as? Int == screenHeight) {
                startMain()
            }
        }
        layoutSlideOutAnimator.start()
    }

    override fun onRestart() {
        super.onRestart()
        AnalyticsManager.logEvent(AppLogKey.Refer.GENDER_AGE_EVENT, AnalyticsKey.Parameter.JUMP_IN_FROM_BACKGROUND)
    }

    override fun onResume() {
        super.onResume()
        if (hasSwitchBackground) {
            val extraInfo = ExtraInfo()
            extraInfo.cL = AppLogKey.Label.ENTER_FOREGROUND
            extraInfo.cE = AppLogKey.CE.GENDER_AGE_EVENT
            extraInfo.cRN = AppLogKey.CRN.LAUNCH
            extraInfo.cRI = ""
            AppLogManager.logEvent(extraInfo)
        }
        mStartTime = System.currentTimeMillis()
        hasSwitchBackground = false
    }

    override fun onPause() {
        super.onPause()
        //stay page
        val extraInfoDuration = ExtraInfo()
        extraInfoDuration.cL = AppLogKey.CL.STAY_PAGE
        extraInfoDuration.cE = AppLogKey.CE.GENDER_AGE_EVENT
        extraInfoDuration.cRN = AppLogKey.CRN.LAUNCH
        extraInfoDuration.cRI = ""
        extraInfoDuration.enterTime = mStartTime
        extraInfoDuration.duration = System.currentTimeMillis() - mStartTime
        AppLogManager.logEvent(extraInfoDuration)
    }

    override fun onStop() {
        super.onStop()
        if (!hasGotoMain) {
            hasSwitchBackground = true
            val extraInfo = ExtraInfo()
            extraInfo.cL = AppLogKey.Label.ENTER_BACKGROUND
            extraInfo.cE = AppLogKey.CE.GENDER_AGE_EVENT
            extraInfo.cRN = AppLogKey.CRN.LAUNCH
            extraInfo.cRI = ""
            AppLogManager.logEvent(extraInfo)
            AnalyticsManager.logEvent(AppLogKey.Refer.GENDER_AGE_EVENT, AnalyticsKey.Parameter.JUMP_OUT_TO_BACKGROUND)
            AppLogManager.sendAppLog()
        }
    }
}