package com.mynews.common.core.crash

//import com.mynews.app.news.BuildConfig
//import com.mynews.app.news.util.UDIDUtils
//import com.mynews.common.core.CoreApp
//import com.crashlytics.android.Crashlytics
//import com.crashlytics.android.core.CrashlyticsCore
//import com.google.firebase.perf.metrics.AddTrace
//import io.fabric.sdk.android.Fabric
//import io.reactivex.plugins.RxJavaPlugins

object CrashManager {

//    @AddTrace(name = "InitCrashManager", enabled = true)
    fun init(enable: Boolean = false) {
//        val crashlyticsCore = CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()
//        val crashlytics = Crashlytics.Builder().core(crashlyticsCore).build()
//        Fabric.with(CoreApp.getInstance(), crashlytics)
//        RxJavaPlugins.setErrorHandler { error ->
//            if (error != null) {
//                Crashlytics.logException(error)
//            }
//        }
//        Crashlytics.setString("UniqueDeviceId", UDIDUtils.getUniqueDeviceId())
    }

}