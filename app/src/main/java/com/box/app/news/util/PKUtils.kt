package com.box.app.news.util

import com.box.app.news.account.AccountManager
import com.box.app.news.data.DataManager

object PKUtils {

    fun getPk(): String {
        return if (AccountManager.isLogin()) {
            AccountManager.account?.uid ?: ""
        } else {
            DataManager.Local.getUniqueDeviceId()
        }
    }

}