package com.mynews.common.core.environment

object EnvIOSApp {

    val appid = EnvPackage.PACKAGE_NAME
    val appname = EnvPackage.APP_NAME
    val carrier = EnvTelephony.NETWORK_OPERATOR_NAME
    val device_type = EnvBuild.MODEL
    val jb = EnvOther.IS_ROOT
    val lang = EnvLocale.LANGUAGE
    val model = EnvBuild.CPU_ABI
    val osn = EnvBuild.PRODUCT
    val osv = EnvBuild.RELEASE_VERSION
    val phonetype = "android"
    val v = EnvPackage.VERSION_NAME

    fun toPairArray(): Array<Pair<String, String>> {
        return arrayOf("appid" to appid,
                "appname" to appname,
                "carrier" to carrier,
                "device_type" to device_type,
                "jb" to jb.toString(),
                "lang" to lang,
                "model" to model,
                "channel_lang" to "ja",
                "osn" to osn,
                "osv" to osv,
                "phonetype" to phonetype,
                "v" to v.toString())
    }
}
