package com.mynews.common.core.environment

import com.mynews.common.core.CoreApp
import java.io.File

object EnvOther {
    val IS_ROOT by lazy { File("/system/bin/su").exists() && File("/system/xbin/su").exists() }
    val IS_TABLET: Boolean by lazy {
        val configuration = CoreApp.getInstance().resources.configuration
        try {
            val isLayoutSizeAtLeast = configuration.javaClass.getMethod("isLayoutSizeAtLeast", Int::class.javaPrimitiveType)
            isLayoutSizeAtLeast.invoke(configuration, 0x00000003) as Boolean
        } catch (e: Exception) {
            e.printStackTrace()
        }
        false
    }
}