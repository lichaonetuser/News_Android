package com.mynews.common.core.environment


object EnvCoreHttpCommon {

    val VERSION_CODE = EnvPackage.VERSION_CODE.toString()

    fun toPairArray(): Array<Pair<String, String>> {
        return arrayOf("version_code" to VERSION_CODE)
    }

}