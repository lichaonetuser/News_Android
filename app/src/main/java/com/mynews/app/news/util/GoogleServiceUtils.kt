package com.mynews.app.news.util

import com.mynews.app.news.analytics.AnalyticsKey
import com.mynews.common.core.CoreApp
import com.mynews.common.core.analytics.AnalyticsManager
//import com.crashlytics.android.Crashlytics
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

object GoogleServiceUtils {

    const val SUCCESS = "SUCCESS"
    const val SERVICE_MISSING = "SERVICE_MISSING"
    const val SERVICE_VERSION_UPDATE_REQUIRED = "SERVICE_VERSION_UPDATE_REQUIRED"
    const val SERVICE_DISABLED = "SERVICE_DISABLED"
    const val SIGN_IN_REQUIRED = "SIGN_IN_REQUIRED"
    const val INVALID_ACCOUNT = "INVALID_ACCOUNT"
    const val RESOLUTION_REQUIRED = "RESOLUTION_REQUIRED"
    const val NETWORK_ERROR = "NETWORK_ERROR"
    const val INTERNAL_ERROR = "INTERNAL_ERROR"
    const val SERVICE_INVALID = "SERVICE_INVALID"
    const val DEVELOPER_ERROR = "DEVELOPER_ERROR"
    const val LICENSE_CHECK_FAILED = "LICENSE_CHECK_FAILED"
    const val CANCELED = "CANCELED"
    const val TIMEOUT = "TIMEOUT"
    const val INTERRUPTED = "INTERRUPTED"
    const val API_UNAVAILABLE = "API_UNAVAILABLE"
    const val SIGN_IN_FAILED = "SIGN_IN_FAILED"
    const val SERVICE_UPDATING = "SERVICE_UPDATING"
    const val SERVICE_MISSING_PERMISSION = "SERVICE_MISSING_PERMISSION"
    const val RESTRICTED_PROFILE = "RESTRICTED_PROFILE"

    fun analyticsGoogleServiceMissing() {
        try {
            val connectionResult = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(CoreApp.getInstance())
            val parameter = when (connectionResult) {
                ConnectionResult.SUCCESS -> SUCCESS
                ConnectionResult.SERVICE_MISSING -> SERVICE_MISSING
                ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED -> SERVICE_VERSION_UPDATE_REQUIRED
                ConnectionResult.SERVICE_DISABLED -> SERVICE_DISABLED
                ConnectionResult.SIGN_IN_REQUIRED -> SIGN_IN_REQUIRED
                ConnectionResult.INVALID_ACCOUNT -> INVALID_ACCOUNT
                ConnectionResult.RESOLUTION_REQUIRED -> RESOLUTION_REQUIRED
                ConnectionResult.NETWORK_ERROR -> NETWORK_ERROR
                ConnectionResult.INTERNAL_ERROR -> INTERNAL_ERROR
                ConnectionResult.SERVICE_INVALID -> SERVICE_INVALID
                ConnectionResult.DEVELOPER_ERROR -> DEVELOPER_ERROR
                ConnectionResult.LICENSE_CHECK_FAILED -> LICENSE_CHECK_FAILED
                ConnectionResult.CANCELED -> CANCELED
                ConnectionResult.TIMEOUT -> TIMEOUT
                ConnectionResult.INTERRUPTED -> INTERRUPTED
                ConnectionResult.API_UNAVAILABLE -> API_UNAVAILABLE
                ConnectionResult.SIGN_IN_FAILED -> SIGN_IN_FAILED
                ConnectionResult.SERVICE_UPDATING -> SERVICE_UPDATING
                ConnectionResult.SERVICE_MISSING_PERMISSION -> SERVICE_MISSING_PERMISSION
                ConnectionResult.RESTRICTED_PROFILE -> RESTRICTED_PROFILE
                else -> connectionResult.toString()
            }
            AnalyticsManager.logEvent(AnalyticsKey.Event.GOOGLE_SERVICE_AVAILABLE, parameter)
        } catch (e: Exception) {
//            Crashlytics.logException(e)
        }
    }

}