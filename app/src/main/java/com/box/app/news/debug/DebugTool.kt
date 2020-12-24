package com.box.app.news.debug

import android.app.Activity
import android.hardware.SensorManager
import com.box.app.news.App
import com.box.app.news.data.DataManager
import com.box.app.news.page.activity.MainActivity
import com.box.app.news.page.mvp.layer.main.debug.DebugFragment
import com.box.common.core.CoreApp
import com.box.common.core.app.fragment.CoreBaseFragment
import com.box.common.core.net.http.HttpManager
import com.squareup.seismic.ShakeDetector
import me.yokeyword.fragmentation.ISupportFragment

object DebugTool {

    private var mShakeDetector: ShakeDetector? = null

    var mTranslateContent = DataManager.Local.getDebugTranslateContent()
    var mShowRefer = DataManager.Local.getDebugShowRefer()
    var mAdTestDevice = DataManager.Local.getDebugAdTestDevice()
    var mAdShowId = DataManager.Local.getDebugAdShowId()
    var mListAdTest = DataManager.Local.getDebugListAdTest()

    fun init() {
        if (!App.isDebug()) {
            return
        }
        initConfig()
    }

    private fun initConfig() {
        updateConfig()
    }

    fun updateConfig() {
        if (mTranslateContent) {
            HttpManager.putCommonParams("translate" to "true")
        } else {
            HttpManager.removeCommonParams("translate")
        }
    }

    fun saveConfig() {
        DataManager.Local.saveDebugTranslateContent(mTranslateContent)
        DataManager.Local.saveDebugShowRefer(mShowRefer)
        DataManager.Local.saveDebugAdTestDevice(mAdTestDevice)
        DataManager.Local.saveDebugAdShowId(mAdShowId)
        DataManager.Local.saveDebugListAdTest(mListAdTest)
    }

    private fun startDebugFragment() {
        if (!App.isDebug()) {
            return
        }

        val mainActivity = CoreApp.coreBaseActivities.find {
            it::class.java == MainActivity::class.java
        } ?: return
        mainActivity.callRootFragmentStart(
                CoreBaseFragment.instantiate(DebugFragment::class.java),
                ISupportFragment.SINGLETASK)
    }

    fun startDetector() {
        if (!App.isDebug()) {
            return
        }

        synchronized(DebugTool::class.java) {
            if (mShakeDetector == null) {
                mShakeDetector = ShakeDetector(ShakeDetector.Listener { startDebugFragment() })
            }
            val sensorManager = CoreApp.getInstance().getSystemService(Activity.SENSOR_SERVICE) as SensorManager
            mShakeDetector!!.start(sensorManager)
        }
    }

    fun stopDetector() {
        if (!App.isDebug()) {
            return
        }

        synchronized(DebugTool::class.java) {
            if (mShakeDetector == null) {
                return
            }
            mShakeDetector!!.stop()
        }
    }
}