package com.mynews.common.extension.login

import android.app.Activity
import android.content.Intent

import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult

import java.util.Arrays

object OLoginFacebook : ILogin {

    const val KEY_TOKEN = "token"
    const val KEY_LOGIN_RESULT = "loginResult"

    private val mCallbackManager by lazy { CallbackManager.Factory.create() }

    override fun login(activity: Activity, l: ILoginListener) {
        LoginManager.getInstance().registerCallback(mCallbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        try {
                            val map = hashMapOf(
                                    KEY_TOKEN to loginResult.accessToken.token,
                                    KEY_LOGIN_RESULT to loginResult)
                            l.onSuccess(LoginPlatform.FACEBOOK, map)
                        } catch (e: Exception) {
                            l.onFailure(LoginPlatform.FACEBOOK, e)
                        }
                    }

                    override fun onCancel() {
                        l.onCancel(LoginPlatform.FACEBOOK)
                    }

                    override fun onError(exception: FacebookException) {
                        l.onFailure(LoginPlatform.FACEBOOK, exception)
                    }
                })
        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile"))
    }

    override fun logout(activity: Activity, l: ILoginListener?) {
        LoginManager.getInstance().logOut()
    }

    override fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data)
    }

}
