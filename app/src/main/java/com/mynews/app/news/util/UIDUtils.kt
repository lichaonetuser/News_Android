package com.mynews.app.news.util

import com.mynews.app.news.account.AccountManager
import com.mynews.app.news.data.DataManager
import com.mynews.common.core.net.http.HttpManager
//import com.crashlytics.android.Crashlytics

object UIDUtils {

    const val HTTP_PARAMS_KEY_UID = "uid"

    /**
     * 获取通用参数用的Pair
     */
    fun getUIdPair(): Pair<String, String> {
        return HTTP_PARAMS_KEY_UID to (AccountManager.account?.uid ?: "")
    }

    fun updateUIdToHttpParams() {
        try {
            val isLogin = DataManager.Local.getAccountIsLogin()
            if (isLogin) {
                HttpManager.putCommonParams(getUIdPair())
            } else {
                HttpManager.removeCommonParams(HTTP_PARAMS_KEY_UID)
            }
        } catch (e: Exception) {
//            Crashlytics.logException(e)
        }
    }

}
