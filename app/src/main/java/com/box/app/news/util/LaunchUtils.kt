package com.box.app.news.util

import com.box.app.news.data.source.local.preference.PreferenceAPI
import com.box.common.core.environment.EnvPackage

object LaunchUtils {

    var isVersionFirstLaunch = PreferenceAPI.isVersionFirstRun(EnvPackage.VERSION_CODE)

    /**
     * 进程存在时多次启动需要更新状态
     */
    fun forceRefresh() {
        isVersionFirstLaunch = PreferenceAPI.isVersionFirstRun(EnvPackage.VERSION_CODE)
    }

}