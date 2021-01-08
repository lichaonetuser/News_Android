package com.mynews.common.core.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.mynews.common.core.log.Logger

object GooglePlayUtils {

    fun goMarketDetail(context: Context) {
        try {
            var marketFound = false
            val appPackageName = context.packageName
            val PACKAGE_GOOGLE_PLAY = "com.android.vending"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("market://details?id=" + appPackageName)
            val infos = context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            for (ri in infos) {
                if (ri.activityInfo.packageName == PACKAGE_GOOGLE_PLAY) {
                    marketFound = true
                    val launchIntent = context.packageManager.getLaunchIntentForPackage(PACKAGE_GOOGLE_PLAY)
                    val comp = ComponentName(ri.activityInfo.packageName, ri.activityInfo.name)
                    launchIntent!!.component = comp
                    launchIntent.data = Uri.parse("market://details?id=" + appPackageName)
                    launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                    context.startActivity(launchIntent)
                    break
                }
            }
            if (!marketFound) {
                val goIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName))
                goIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(goIntent)
            }
        } catch (e: Exception) {
            Logger.e("Go to GP error : %s", e.toString())
        }

    }

}