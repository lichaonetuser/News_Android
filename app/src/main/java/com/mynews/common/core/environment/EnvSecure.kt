package com.mynews.common.core.environment

import android.provider.Settings
import com.mynews.common.core.CoreApp

object EnvSecure {
    val ANDROID_ID = Settings.Secure.getString(CoreApp.getInstance().contentResolver, Settings.Secure.ANDROID_ID)
}