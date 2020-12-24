package com.box.app.news.page.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.box.app.news.R
import com.box.app.news.applog.AppLogManager
import com.box.app.news.bean.ExtraInfo
import com.box.app.news.bean.Interest
import com.box.app.news.data.DataManager
import com.box.app.news.data.source.local.LocalKeys
import com.box.app.news.page.presenter.InterestPresenter
import com.box.app.news.page.view.InterestView
import com.box.app.news.widget.CustomFlowLayout
import com.box.common.core.CoreApp
import com.box.common.core.analytics.AnalyticsManager
import com.box.common.core.app.activity.CoreBaseActivity
import com.box.common.core.image.fresco.ImageLoader
import com.box.common.core.util.ResUtils
import com.facebook.drawee.view.SimpleDraweeView
import com.kongzue.dialog.v2.TipDialog
import com.kongzue.dialog.v2.WaitDialog
import com.pixplicity.easyprefs.library.Prefs
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer
import com.yatatsu.autobundle.AutoBundle
import mehdi.sakout.fancybuttons.FancyButton


class InterestActivity : CoreBaseActivity(), InterestView {

    private val mFlowLayoutList = arrayListOf<CustomFlowLayout>()
    private var mMaxNumber: Int? = Int.MAX_VALUE
    private var mMinNumber: Int? = 0
    private lateinit var mLinearLayout: LinearLayout
    private lateinit var mButton: FancyButton
    private lateinit var mHintTextView: TextView
    private lateinit var mSkipButton: ImageButton
    private lateinit var mLinearLayoutBottomContainer: LinearLayout

    private val mInterestPresenter: InterestPresenter = InterestPresenter(this)
    private var mTagNumberSelected: Int = 0
    private var mTagTotalCount: Int = 0
    private var mStartTime = System.currentTimeMillis()
    private var hasSwitchBackground = false
    private var hasGotoMain = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommdation_screen)
        mSkipButton = findViewById(R.id.skip_button)
        mSkipButton.bringToFront()
        mLinearLayout = findViewById(R.id.linear_layout)
        mButton = findViewById(R.id.finish_button)
        mHintTextView = findViewById(R.id.recommand_hint_tv)
        mLinearLayoutBottomContainer = findViewById(R.id.bottom_container)
        mInterestPresenter.getUserInterests()
        AnalyticsManager.logEvent(LocalKeys.INTEREST_EVENT, "show", onFirebase = false)

    }

    override fun onResume() {
        super.onResume()
        if (hasSwitchBackground) {
            val extraInfo = ExtraInfo()
            extraInfo.cL = LocalKeys.ENTER_FOREGROUND
            extraInfo.cE = LocalKeys.INTEREST_EVENT
            extraInfo.cRN = LocalKeys.LAUNCH
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
        extraInfoDuration.cL = LocalKeys.STAY_PAGE
        extraInfoDuration.cE = LocalKeys.INTEREST_EVENT
        extraInfoDuration.cRN = LocalKeys.LAUNCH
        extraInfoDuration.cRI = ""
        extraInfoDuration.enterTime = mStartTime
        extraInfoDuration.duration = System.currentTimeMillis() - mStartTime
        AppLogManager.logEvent(extraInfoDuration)
    }


    override fun onStop() {
        super.onStop()
        Prefs.putBoolean(LocalKeys.IS_SELECTED_INTEREST, true)
        if (!hasGotoMain) {
            hasSwitchBackground = true
            val extraInfo = ExtraInfo()
            extraInfo.cL = LocalKeys.ENTER_BACKGROUND
            extraInfo.cE = LocalKeys.INTEREST_EVENT
            extraInfo.cRN = LocalKeys.LAUNCH
            extraInfo.cRI = ""
            AppLogManager.logEvent(extraInfo)

        }

        AnalyticsManager.logEvent(LocalKeys.INTEREST_EVENT, "jump_out_to_background", onFirebase = false)

        AppLogManager.sendAppLog()

    }

    override fun onStart() {
        super.onStart()
        mSkipButton.setOnClickListener {
            Prefs.putBoolean(LocalKeys.IS_SELECTED_INTEREST, true)

            DataManager.Init.initNewsChannelListFromRemote()

            startMain()
            finish()
            AnalyticsManager.logEvent(LocalKeys.INTEREST_EVENT, "click_skip_button", onFirebase = false)
            val extraInfo = ExtraInfo()
            extraInfo.cL = "skip"
            extraInfo.cE = LocalKeys.INTEREST_EVENT
            extraInfo.cRN = LocalKeys.LAUNCH
            extraInfo.cRI = ""
            AppLogManager.logEvent(extraInfo)
        }

        mButton.setOnClickListener(View.OnClickListener { v ->
            val list = arrayListOf<String>()
            for (item in mFlowLayoutList) {
                list.addAll(item.mSelectedTag)
            }

            if(list.isEmpty()){
                return@OnClickListener
            }

            AnalyticsManager.logEvent(LocalKeys.INTEREST_EVENT, "click_done_button", onFirebase = false)
            mInterestPresenter.sendDataInterestDataToServer(list)
            (v as FancyButton).isEnabled = false
            v.isClickable = false
            val extraInfo = ExtraInfo()
            extraInfo.cL = "ok"
            extraInfo.cE = LocalKeys.INTEREST_EVENT
            extraInfo.cRN = LocalKeys.LAUNCH
            extraInfo.cRI = ""
            extraInfo.count = list.size
            AppLogManager.logEvent(extraInfo)
            AnalyticsManager.logEvent(LocalKeys.INTEREST_EVENT, "select_tag_done_[${list.size}]", onFirebase = false)
        })
    }


    @SuppressLint("SetTextI18n")
    override fun updateInteretTag(interest: Interest?, max: Int?, min: Int?) {

        if (max as Int != -1) {
            mMaxNumber = max
        }
        mMinNumber = min

        for (item in interest?.items.orEmpty()) {
            mTagTotalCount += item.tags.size
            mLinearLayout.addView(inflateTagLable(item.icon, item.category, item.color))
            val flowLayout = inflateFlowlayout()
            flowLayout.setTagListToFlowview(item)
            flowLayout.setTagChooseEvent(object : TagChooseEvent {
                override fun addOrRemove(number: Int) {
                    mTagNumberSelected += number

                    if (mTagNumberSelected > 0) {

                        if ((mMaxNumber as Int) >= mTagNumberSelected && mTagNumberSelected >= (mMinNumber as Int)) {
                            mButton.isEnabled = true
                            mButton.isClickable = true
                        } else {
                            mButton.isEnabled = false
                            mButton.isClickable = false
                        }
                    } else {
                        mButton.isClickable = false
                        mButton.isEnabled = false
                    }

                    // for logging
                    if (number > 0) {
                        AnalyticsManager.logEvent(LocalKeys.INTEREST_EVENT, "click_tag_select", onFirebase = false)
                    } else {
                        AnalyticsManager.logEvent(LocalKeys.INTEREST_EVENT, "click_tag_cancel", onFirebase = false)
                    }

                }
            })
            Log.d("update_ui", item.toString())
            mLinearLayout.addView(flowLayout)
            mFlowLayoutList.add(flowLayout)

        }
        val extraInfo = ExtraInfo()
        extraInfo.cL = "show"
        extraInfo.cE = LocalKeys.INTEREST_EVENT
        extraInfo.cRN = LocalKeys.LAUNCH
        extraInfo.cRI = ""
        AppLogManager.logEvent(extraInfo)
        // control hint if visiable
        if (max == -1 && min == 0) {
            mHintTextView.visibility = View.GONE
        } else {
            mHintTextView.visibility = View.VISIBLE
            if (max == -1) {
                mHintTextView.text = "$min~$mTagTotalCount" + mHintTextView.text
            } else {
                mHintTextView.text = "$min~$max" + mHintTextView.text
            }
        }
    }

    override fun updateUserInterest(isUpdated: Boolean) {
        if (isUpdated) {
            startMain()
        }
    }


    @SuppressLint("InflateParams")
    private fun inflateTagLable(iconUrl: String, category: String, textColor: String): View {
        val view = LayoutInflater.from(this).inflate(R.layout.recommand_image_view, null)
        val categoryLabel = view.findViewById<SimpleDraweeView>(R.id.recommand_iv)
        val categoryText = view.findViewById<TextView>(R.id.recommand_tv)
        ImageLoader.loadImage(categoryLabel, iconUrl)
        categoryText.text = category.trim()
        categoryText.setTextColor(Color.parseColor(textColor))
        return view
    }

    @SuppressLint("InflateParams")
    private fun inflateFlowlayout(): CustomFlowLayout {
        return LayoutInflater.from(this).inflate(R.layout.recommend_flow_layout, null) as CustomFlowLayout
    }

    private fun startMain() {
        hasGotoMain = true
        GSYVideoManager.releaseAllVideos() //释放所有正在播放的视频
        val bundle = Bundle()
        AutoBundle.pack(this, bundle)
        val intent = Intent(this@InterestActivity, MainActivity::class.java)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        startActivity(intent)
        finish()
    }

    override fun startAnim() {
        WaitDialog.show(CoreApp.getLastBaseActivityInstance()
                ?: return, ResUtils.getString(R.string.Tip_Loading))
    }

    override fun stopAnim() {
        WaitDialog.dismiss()
    }

    override fun onRestart() {
        super.onRestart()
        AnalyticsManager.logEvent(LocalKeys.INTEREST_EVENT, "jump_in_from_background", onFirebase = false)
    }


    override fun reEnableButtonAndShowToast() {
        mButton.isEnabled = true
        TipDialog.show(CoreApp.getLastBaseActivityInstance() ?: return,
                ResUtils.getString(R.string.Tip_ServerError),
                TipDialog.SHOW_TIME_SHORT, TipDialog.TYPE_ERROR)
    }
}

interface TagChooseEvent {
    fun addOrRemove(number: Int)
}