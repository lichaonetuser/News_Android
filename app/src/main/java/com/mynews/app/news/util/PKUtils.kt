package com.mynews.app.news.util

import com.mynews.app.news.account.AccountManager
import com.mynews.app.news.data.DataManager

object PKUtils {

    fun getPk(): String {
        return if (AccountManager.isLogin()) {
            AccountManager.account?.uid ?: ""
        } else {
            DataManager.Local.getUniqueDeviceId()
        }
    }

}