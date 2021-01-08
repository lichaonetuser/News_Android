package com.mynews.common.core.environment

import android.content.Context
import android.telephony.TelephonyManager
import com.mynews.common.core.CoreApp

object EnvTelephony {
    private val TELEPHONY_MANAGER = CoreApp.getInstance().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val NETWORK_OPERATOR = TELEPHONY_MANAGER.networkOperator
    val NETWORK_OPERATOR_NAME = TELEPHONY_MANAGER.networkOperatorName
}