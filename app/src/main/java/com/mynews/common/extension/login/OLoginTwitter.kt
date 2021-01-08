package com.mynews.common.extension.login

import android.app.Activity
import android.content.Intent
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterAuthClient

object OLoginTwitter : ILogin {

    const val KEY_TOKEN = "token"
    const val KEY_SECRET = "secret"
    const val KEY_RESULT = "result"

    private val mTwitterAuthClient by lazy { TwitterAuthClient() }

    override fun login(activity: Activity, l: ILoginListener) {
        mTwitterAuthClient.cancelAuthorize()
        mTwitterAuthClient.authorize(activity, object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>) {
                try {
                    val token = result.data.authToken
                    l.onSuccess(LoginPlatform.TWITTER, hashMapOf(
                            KEY_RESULT to result,
                            KEY_TOKEN to token.token,
                            KEY_SECRET to token.secret
                    ))
                } catch (e: Exception) {
                    l.onFailure(LoginPlatform.TWITTER, e)
                }
            }

            override fun failure(e: TwitterException) {
                l.onFailure(LoginPlatform.TWITTER, e)
            }
        })
    }

    override fun logout(activity: Activity, l: ILoginListener?) {
        TwitterCore.getInstance().sessionManager.clearActiveSession()
    }

    override fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mTwitterAuthClient.onActivityResult(requestCode, resultCode, data)
    }
}
