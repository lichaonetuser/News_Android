package com.box.app.news.data.source.remote.http.url

import android.os.Build
import com.box.common.core.log.Logger

object HttpBaseUrls {

//    const val TEST = "http://test.api.bigappinfo.com/"
    const val TEST = "http://c.test.bigappinfo.com/"
    const val RELEASE = "https://api.bigappinfo.com/"
    const val RELEASE_FOR_BELOW_KITKAT = "http://api.bigappinfo.com/"

    fun getReleaseUrl(): String {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            RELEASE
        } else {
            RELEASE_FOR_BELOW_KITKAT
        }
    }

}
