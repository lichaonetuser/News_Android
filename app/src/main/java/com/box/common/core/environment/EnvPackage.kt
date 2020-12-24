package com.box.common.core.environment

import com.box.common.core.CoreApp


object EnvPackage {
    private val PACKAGE_MANAGER = CoreApp.getInstance().packageManager
    val PACKAGE_INFO = PACKAGE_MANAGER.getPackageInfo(CoreApp.getInstance().packageName, 0)
    val PACKAGE_NAME = PACKAGE_INFO.packageName
    val VERSION_NAME = PACKAGE_INFO.versionName
    val VERSION_CODE = PACKAGE_INFO.versionCode

    val APPLICATION_INFO = PACKAGE_INFO.applicationInfo
    val APP_NAME = APPLICATION_INFO.loadLabel(PACKAGE_MANAGER).toString()

    val META_DATA = APPLICATION_INFO.metaData
    val UMENG_CHANNEL = META_DATA?.getString("UMENG_CHANNEL") ?: ""
}