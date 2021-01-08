package com.mynews.common.core.util

import android.app.ActivityManager
import android.content.Context
import android.os.Process
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader

internal object ProcessUtils {
    fun isMainProcess(context: Context): Boolean {
        val processName = getCurrentProcessName(context)
        return processName == context.packageName
    }

    fun getCurrentProcessName(context: Context): String {
        try {
            val pid = android.os.Process.myPid()
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            activityManager.runningAppProcesses
                    .filter { it.pid == pid }
                    .forEach { return it.processName }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return getCurProcessNameFromProc()
    }

    fun getCurProcessNameFromProc(): String {
        var cmdlineReader: BufferedReader? = null
        try {
            cmdlineReader = BufferedReader(InputStreamReader(
                    FileInputStream("/proc/" + Process.myPid() + "/cmdline"),
                    "iso-8859-1"))
            val processName = StringBuilder()
            var c: Int = cmdlineReader.read()
            while (c > 0) {
                processName.append(c.toChar())
                c = cmdlineReader.read()
            }
            return processName.toString()
        } catch (e: Throwable) {
            return ""
        } finally {
            cmdlineReader?.close()
        }
    }
}