package com.box.app.news.ab

import com.box.app.news.bean.AbInfo
import com.box.app.news.bean.AppConfig
import com.box.app.news.data.DataManager
import com.box.common.core.net.http.HttpManager
import java.net.URLEncoder

object ABManager {

    const val KEY = "e_flag"

    var info: AbInfo = AbInfo()

    fun init(info: AbInfo) {
        this.info = info
        syncEFlagToHttpCommonParams(info.eFlag)
    }

    fun updateABInfo(info: AbInfo) {
        this.info = info
        syncEFlagToHttpCommonParams(info.eFlag)
    }

    fun updateEFlag(eFlag: String?) {
        val config = DataManager.Local.getAppConfig() ?: AppConfig()
        config.abInfo.eFlag = eFlag
        info.eFlag = eFlag
        DataManager.Local.saveAppConfig(config)
        syncEFlagToHttpCommonParams(info.eFlag)
    }

    private fun syncEFlagToHttpCommonParams(eFlag: String?) {
        if (!eFlag.isNullOrBlank()) {
            val str = URLEncoder.encode(eFlag, "utf-8")
            HttpManager.putCommonParams(KEY to (str ?: ""))
        } else {
            HttpManager.removeCommonParams(KEY)
        }
    }

}