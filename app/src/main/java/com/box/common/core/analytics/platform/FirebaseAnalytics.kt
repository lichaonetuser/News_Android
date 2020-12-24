package com.box.common.core.analytics.platform

import android.os.Bundle
import com.box.common.core.CoreApp
import com.box.common.core.analytics.IAnalytics
import com.google.firebase.analytics.FirebaseAnalytics

internal object FirebaseAnalytics : IAnalytics {

    private val mFirebaseAnalytics by lazy {
        FirebaseAnalytics.getInstance(CoreApp.getInstance())
    }

    override fun logEvent(event: String, vararg parameters: String) {
        val bundle = Bundle()
        parameters.forEach { bundle.putString(it, it) }
        mFirebaseAnalytics.logEvent(event, bundle)
    }

    override fun logEvent(event: String, parameter: String, value: String) {
        val bundle = Bundle()
        bundle.putString(parameter, value)
        mFirebaseAnalytics.logEvent(event, bundle)
    }

    override fun logEvent(event: String, vararg pairs: Pair<String, String>) {
        val bundle = Bundle()
        pairs.forEach {
            bundle.putString(it.first, it.second)
        }
        mFirebaseAnalytics.logEvent(event, bundle)
    }

}