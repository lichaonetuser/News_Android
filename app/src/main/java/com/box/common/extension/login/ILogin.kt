package com.box.common.extension.login

import android.app.Activity
import android.content.Intent

interface ILogin {

    fun login(activity: Activity, l: ILoginListener)

    fun logout(activity: Activity, l: ILoginListener? = null)

    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?)

}
