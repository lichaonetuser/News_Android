package com.box.app.news.page.mvp.layer.main.debug

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.box.app.news.R
import com.box.app.news.data.DataManager
import com.box.app.news.data.source.remote.http.url.HttpBaseUrls
import com.box.app.news.debug.DebugTool
import com.box.app.news.page.mvp.layer.main.feedback.FeedbackFragment
import com.box.common.core.CoreApp
import com.box.common.core.net.http.HttpManager
import com.box.common.extension.app.mvp.base.MVPBaseFragment
import kotlinx.android.synthetic.main.fragment_debug.*

class DebugFragment : MVPBaseFragment<DebugContract.View,
        DebugContract.Presenter<DebugContract.View>>(),
        DebugContract.View {

    override val mPresenter = DebugPresenter()
    override val mLayoutRes = R.layout.fragment_debug
    override var mDispatchBack = true
    override val mAttachSwipeBack = true

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        initBars()
    }

    fun initBars() {
        debug_setting_translate.isChecked = DebugTool.mTranslateContent

        debug_setting_translate.setOnClickListener {
            debug_setting_translate.toggle()
            DebugTool.mTranslateContent = debug_setting_translate.isChecked
            DebugTool.updateConfig()
            DebugTool.saveConfig()
        }

        debug_setting_show_refer.isChecked = DebugTool.mShowRefer

        debug_setting_show_refer.setOnClickListener {
            debug_setting_show_refer.toggle()
            DebugTool.mShowRefer = debug_setting_show_refer.isChecked
            DebugTool.updateConfig()
            DebugTool.saveConfig()
        }

        debug_setting_switch_base_url.isChecked = DataManager.Local.getBaseUrl() == HttpBaseUrls.getReleaseUrl()

        debug_setting_switch_base_url.setOnClickListener {
            debug_setting_switch_base_url.isChecked = !debug_setting_switch_base_url.isChecked
            val url = if (debug_setting_switch_base_url.isChecked) {
                HttpBaseUrls.getReleaseUrl()
            } else {
                HttpBaseUrls.TEST
            }
            DataManager.Local.saveUniqueDeviceId("")
            DataManager.Local.saveBaseUrl(url)
            HttpManager.setBaseUrl(url)
            Toast.makeText(CoreApp.getInstance(), "Current baseUrl ：$url", Toast.LENGTH_LONG).show()
        }

        debug_setting_ad_test.isChecked = DebugTool.mAdTestDevice
        debug_setting_ad_test.setOnClickListener {
            debug_setting_ad_test.toggle()
            DebugTool.mAdTestDevice = debug_setting_ad_test.isChecked
            DebugTool.updateConfig()
            DebugTool.saveConfig()
        }

        debug_setting_ad_test_2.isChecked = DebugTool.mAdShowId
        debug_setting_ad_test_2.setOnClickListener {
            debug_setting_ad_test_2.toggle()
            DebugTool.mAdShowId = debug_setting_ad_test_2.isChecked
            DebugTool.updateConfig()
            DebugTool.saveConfig()
        }

        debug_setting_list_ad_test.isChecked = DebugTool.mListAdTest
        debug_setting_list_ad_test.setOnClickListener {
            debug_setting_list_ad_test.toggle()
            DebugTool.mListAdTest = debug_setting_list_ad_test.isChecked
            DebugTool.updateConfig()
            DebugTool.saveConfig()
        }

        debug_setting_feedback.setOnClickListener {
            goFromRoot(FeedbackFragment::class.java)
        }
    }

}


