package com.mynews.common.core.environment

import android.os.Build


object EnvBuild {
    val CPU_ABI: String = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            var abi: String = ""
            for (info in Build.SUPPORTED_ABIS) {
                abi += "_"
                abi += info
            }
            if (abi.length > 1) {
                abi = abi.substring(1)
            }
            abi
        } else {
            Build.CPU_ABI
        }
    }.invoke()
    val PRODUCT = Build.PRODUCT
    val RELEASE_VERSION = Build.VERSION.RELEASE
    val MODEL = Build.MODEL
    val SERIAL = Build.SERIAL
    val HARDWARE = Build.HARDWARE
    val SDK_INT = Build.VERSION.SDK_INT
    val INCREMENTAL = Build.VERSION.INCREMENTAL

}