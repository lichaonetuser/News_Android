package com.box.common.extension.login

import android.app.Activity
import android.content.Intent
import com.box.common.core.log.Logger

object LoginManager {

    private fun getILogin(platform: LoginPlatform): ILogin {
        return when (platform) {
            LoginPlatform.FACEBOOK -> OLoginFacebook
            LoginPlatform.TWITTER -> OLoginTwitter
            LoginPlatform.GOOGLE -> OLoginGoogle
        }
    }

    private fun getILogin(requestCode: Int): ILogin? {
        return when (requestCode) {
            LoginPlatform.FACEBOOK.requestCode -> OLoginFacebook
            LoginPlatform.TWITTER.requestCode -> OLoginTwitter
            LoginPlatform.GOOGLE.requestCode -> OLoginGoogle
            else -> null
        }
    }

    private var mLogin: ILogin? = null

    fun login(activity: Activity, platform: LoginPlatform, l: ILoginListener) {
        try {
            mLogin = getILogin(platform)
            mLogin?.login(activity, l)
        } catch (e: Exception) {
            l.onFailure(platform, e)
        }
    }

    fun logout(activity: Activity, platform: LoginPlatform, l: ILoginListener? = null) {
        try {
            mLogin = getILogin(platform)
            mLogin?.logout(activity, l)
        } catch (e: Exception) {
            l?.onFailure(platform, e)
        }
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val login = getILogin(requestCode) ?: mLogin
        login?.handleActivityResult(requestCode, resultCode, data)
        mLogin = null
    }
}
