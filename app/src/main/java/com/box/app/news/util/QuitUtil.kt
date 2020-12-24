package com.box.app.news.util

import android.app.Activity
import com.box.app.news.R
import com.box.common.core.util.ResUtils

object QuitUtil {

    private var mLastBackPressedTime = 0L

    fun quit(activity: Activity) {
        if (System.currentTimeMillis() - mLastBackPressedTime <= 2 * 1000) {
            ToastUtils.cancel()
            activity.finish()
            mLastBackPressedTime = 0
            LaunchUtils.forceRefresh()
            return
        }
        mLastBackPressedTime = System.currentTimeMillis()
        ToastUtils.showToast(ResUtils.getString(R.string.Tip_AndroidPhysicalEsc))
    }

}
