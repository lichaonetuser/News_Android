package com.box.common.core.environment

import android.provider.Settings
import com.box.common.core.CoreApp

object EnvSecure {
    val ANDROID_ID = Settings.Secure.getString(CoreApp.getInstance().contentResolver, Settings.Secure.ANDROID_ID)
}