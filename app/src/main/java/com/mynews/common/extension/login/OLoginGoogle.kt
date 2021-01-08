package com.mynews.common.extension.login

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.mynews.app.news.R
import com.mynews.common.core.CoreApp
import com.mynews.common.core.app.fragment.CoreBaseFragment
import com.mynews.common.core.util.ResUtils
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient

object OLoginGoogle : ILogin {

    const val KEY_ID_TOKEN = "idToken"
    const val KEY_SERVER_AUTH_CODE = "serverAuthCode"
    const val KEY_RESULT = "result"

    private var sGoogleApiClient: GoogleApiClient? = null
    private var mListener: ILoginListener? = null

    fun getGoogleApiClient(activity: Activity): GoogleApiClient? {
        synchronized(this) {
            if (sGoogleApiClient == null) {
                val clientId = ResUtils.getString(R.string.default_web_client_id)
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(clientId)
                        .requestServerAuthCode(clientId)
                        .requestEmail()
                        .build()
                sGoogleApiClient = GoogleApiClient.Builder(activity)
                        .enableAutoManage(activity as FragmentActivity) { connectionResult ->
                            mListener?.onFailure(LoginPlatform.GOOGLE,
                                    Exception(connectionResult.errorMessage))
                        }
                        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                        .build()
            }
            return sGoogleApiClient
        }
    }

    override fun login(activity: Activity, l: ILoginListener) {
        val googleApiClient = getGoogleApiClient(activity)
        if (googleApiClient == null) {
            l.onFailure(LoginPlatform.GOOGLE, Exception("googleApiClient is null"))
            return
        }
        mListener = l
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
        activity.startActivityForResult(signInIntent, LoginPlatform.GOOGLE.requestCode)
    }

    override fun logout(activity: Activity, l: ILoginListener?) {
//        val googleApiClient = getGoogleApiClient(activity)
//        if (googleApiClient != null) {
//            Auth.GoogleSignInApi.signOut(googleApiClient)
//        }
//        clearGoogleView()
    }

    override fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != LoginPlatform.GOOGLE.requestCode) {
            mListener?.onFailure(LoginPlatform.GOOGLE, Exception("error requestCode"))
            return
        }

        if (resultCode == Activity.RESULT_CANCELED) {
            clearGoogleView()
            return
        }

        val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
        if (result == null || !result.isSuccess) {
            mListener?.onFailure(LoginPlatform.GOOGLE, Exception("result is null"))
            return
        }

        val acct = result.signInAccount
        if (acct == null) {
            mListener?.onFailure(LoginPlatform.GOOGLE, Exception("signInAccount is null"))
            return
        }

        val idToken = acct.idToken ?: ""
        val serverAuthCode = acct.serverAuthCode ?: ""

        if (idToken.isBlank()) {
            mListener?.onFailure(LoginPlatform.GOOGLE, Exception("idToken is null"))
            return
        }

        if (serverAuthCode.isBlank()) {
            mListener?.onFailure(LoginPlatform.GOOGLE, Exception("serverAuthCode is null"))
            return
        }

        clearGoogleView()

        mListener?.onSuccess(LoginPlatform.GOOGLE, hashMapOf(
                KEY_ID_TOKEN to idToken,
                KEY_SERVER_AUTH_CODE to serverAuthCode,
                KEY_RESULT to result
        ))
    }

    fun clearGoogleView() {
        CoreApp.activities.forEach { activity ->
            if (activity.packageName.contains("google")) {
                activity.finish()
            }
        }
        CoreApp.coreBaseActivities.forEach { activity ->
            activity.supportFragmentManager.fragments.forEach { fragment ->
                if (fragment !is CoreBaseFragment) {
                    activity.supportFragmentManager.beginTransaction()
                            .remove(fragment)
                            .commitNowAllowingStateLoss()
                }
            }
        }
    }

}
