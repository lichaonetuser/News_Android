package com.mynews.app.news.data.source.remote.http.url

import android.os.Build

object HttpBaseUrls {

//    const val TEST = "http://api.bigappinfo.com/"
    const val TEST = "http://test.api.bigappinfo.com/"
//    const val TEST = "http://152.32.146.81/"
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
