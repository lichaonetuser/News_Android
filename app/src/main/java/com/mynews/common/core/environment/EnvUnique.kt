package com.mynews.common.core.environment

import com.mynews.common.core.CoreApp
import com.ta.utdid2.device.UTDevice
import org.jetbrains.anko.defaultSharedPreferences
import java.util.*

object EnvUnique {

    val UTDID by lazy {
        UTDevice.getUtdid(CoreApp.getInstance())
    }
    val CLIENT_ID by lazy {
        var clientId = CoreApp.getInstance().defaultSharedPreferences
                .getString("client_id", null)
        if (clientId == null) {
            clientId = "CU_${UUID.randomUUID()}"
            CoreApp.getInstance().defaultSharedPreferences.edit()
                    .putString("client_id", clientId).apply()
        }
        clientId
    }
    val SERIAL = EnvBuild.SERIAL
    val ANDROID_ID = EnvSecure.ANDROID_ID

    fun toPairArray(): Array<Pair<String, String>> {
        return arrayOf(
                "client_id" to CLIENT_ID,
                "android_id" to ANDROID_ID,
                "android_utdid" to UTDID,
                "android_serial" to SERIAL)
    }

}