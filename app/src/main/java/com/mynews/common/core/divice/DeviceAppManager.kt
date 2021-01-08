//package com.box.common.core.divice
//
//import android.content.pm.ApplicationInfo
//import android.content.pm.PackageInfo
//import com.box.app.news.bean.DeviceAppRequestInfo
//import com.box.app.news.data.DataDictionary
//import com.box.common.core.CoreApp
//import com.box.common.core.divice.processes.AndroidProcesses
//import com.box.common.core.divice.processes.models.AndroidAppProcess
//import com.crashlytics.android.Crashlytics
//
//
//object DeviceAppManager {
//
//    private val mContext = CoreApp.getInstance()
//    private val mPackageManager = CoreApp.getInstance().packageManager
//
//    fun getInstalledPackages(): MutableList<PackageInfo> {
//        return try {
//            mPackageManager.getInstalledPackages(0)
//        } catch (e: Exception) {
//            Crashlytics.logException(e)
//            mutableListOf()
//        }
//    }
//
//    fun getInstalledDeviceAppInfo(findLabel: Boolean): List<DeviceAppInfo> {
//        val result = mutableListOf<DeviceAppInfo>()
//        try {
//            val packages = getInstalledPackages()
//            for (packageInfo in packages) {
//                result.add(newDeviceAppInfo(packageInfo, findLabel))
//            }
//        } catch (e: Exception) {
//            Crashlytics.logException(e)
//        }
//        return result
//    }
//
//    fun getRunningAppProcesses(): MutableList<AndroidAppProcess> {
//        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.M) {
//            return mutableListOf()
//        }
//        return try {
//            AndroidProcesses.getRunningAppProcesses()
//        } catch (e: Exception) {
//            Crashlytics.logException(e)
//            mutableListOf()
//        }
//    }
//
//    fun getRunningDeviceAppInfo(findLabel: Boolean): List<DeviceAppInfo> {
//        val result = mutableListOf<DeviceAppInfo>()
//        try {
//            val processes = getRunningAppProcesses()
//            for (process in processes) {
//                /*
//                val processName = process.name
//                val stat = process.stat()
//                val pid = stat.pid
//                val parentProcessId = stat.ppid()
//                val startTime = stat.stime()
//                val policy = stat.policy()
//                val state = stat.state()
//                val statm = process.statm()
//                val totalSizeOfProcess = statm.size
//                val residentSetSize = statm.residentSetSize
//                 */
//                val packageInfo = process.getPackageInfo(mContext, 0)
//                result.add(newDeviceAppInfo(packageInfo, findLabel))
//            }
//        } catch (e: Exception) {
//            Crashlytics.logException(e)
//        }
//        return result
//    }
//
//    fun getDeviceAppRequestInfo(findLabel: Boolean): DeviceAppRequestInfo {
//        val info = DeviceAppRequestInfo()
//        try {
//            with(info) {
//                installedApp = getInstalledDeviceAppInfo(findLabel)
//                runningProcesses = getRunningDeviceAppInfo(findLabel)
//                type =
//                        if (findLabel) DataDictionary.DeviceAppRequestInfoType.ALL.value
//                        else DataDictionary.DeviceAppRequestInfoType.FAST.value
//            }
//        } catch (e: Exception) {
//            Crashlytics.logException(e)
//        }
//        return info
//    }
//
//    private fun newDeviceAppInfo(packageInfo: PackageInfo, findLabel: Boolean): DeviceAppInfo {
//        val label = if (findLabel) {
//            mPackageManager.getApplicationLabel(packageInfo.applicationInfo)
//                    ?: packageInfo.packageName ?: ""
//        } else {
//            ""
//        }
//        val applicationInfo: ApplicationInfo? = packageInfo.applicationInfo
//        return DeviceAppInfo(
//                appName = label.toString(),
//                packageName = packageInfo.packageName ?: "",
//                processName = applicationInfo?.processName ?: "",
//                firstInstallTime = packageInfo.firstInstallTime,
//                lastUpdateTime = packageInfo.lastUpdateTime,
//                versionCode = packageInfo.versionCode,
//                versionName = packageInfo.versionName ?: "",
//                applicationFlags = applicationInfo?.flags ?: -1
//        )
//    }
//}