package com.mynews.common.core

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.multidex.MultiDexApplication
import com.mynews.common.core.app.activity.CoreBaseActivity
import com.mynews.common.core.util.ProcessUtils
//import com.crashlytics.android.Crashlytics
//import io.fabric.sdk.android.Fabric
import java.util.*

abstract class CoreApp : MultiDexApplication() {

    companion object {
        private var inited: Boolean = false
        private var singleInstance: CoreApp? = null
        @JvmStatic
        fun getInstance() = singleInstance!!

        @JvmStatic
        fun isApplicationAvailability() = (singleInstance != null)

        fun getLastBaseActivityInstance() = _coreBaseActivities.lastOrNull()
        fun getLastActivityInstance() = _activities.lastOrNull()
        fun getLastActivityInstance(predicate: (Activity) -> Boolean) = _activities.lastOrNull(predicate)

        private val _activities = ArrayList<Activity>()
        val activities: ArrayList<Activity>
            get() {
                return ArrayList(_activities)
            }
        var activatedActivityCount = 0
            private set

        private val _coreBaseActivities = ArrayList<CoreBaseActivity>()
        val coreBaseActivities: ArrayList<CoreBaseActivity>
            get() {
                return ArrayList(_coreBaseActivities)
            }
        var coreBaseActivatedActivityCount = 0
            private set

        val isBackground: Boolean
            get() = activatedActivityCount <= 0

        val isCoreBaseBackground: Boolean
            get() = coreBaseActivatedActivityCount <= 0

        val isMainProcess: Boolean by lazy(LazyThreadSafetyMode.NONE) {
            ProcessUtils.isMainProcess(getInstance())
        }

        val mainProcessName: String by lazy {
            getInstance().packageName
        }

        val currentProcessName: String by lazy {
            ProcessUtils.getCurrentProcessName(getInstance())
        }

        private class LifecycleCallback : Application.ActivityLifecycleCallbacks {

            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
                if (activity == null) {
                    return
                }
                if (activity is CoreBaseActivity) {
                    _coreBaseActivities.add(activity)
                }
                _activities.add(activity)
            }

            override fun onActivityStarted(activity: Activity?) {
                if (activity == null) {
                    return
                }
                if (activity is CoreBaseActivity) {
                    coreBaseActivatedActivityCount++
                }
                activatedActivityCount++
            }

            override fun onActivityResumed(activity: Activity?) {
            }

            override fun onActivityPaused(activity: Activity?) {

            }

            override fun onActivityStopped(activity: Activity?) {
                if (activity == null) {
                    return
                }
                if (activity is CoreBaseActivity) {
                    coreBaseActivatedActivityCount--
                }
                activatedActivityCount--
            }

            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {

            }

            override fun onActivityDestroyed(activity: Activity?) {
                if (activity == null) {
                    return
                }
                if (activity is CoreBaseActivity) {
                    _coreBaseActivities.remove(activity)
                }
                _activities.remove(activity)
            }
        }
    }

    @CallSuper
    override fun onCreate() {
        super.onCreate()
        init()
    }

    @CallSuper
    open fun init() {
        if (inited) {
//            Fabric.with(this, Crashlytics())
//            Crashlytics.logException(Exception("App init more than once!"))
        }
        inited = true
        CoreApp.singleInstance = this
        registerActivityLifecycleCallbacks(LifecycleCallback())
    }

}