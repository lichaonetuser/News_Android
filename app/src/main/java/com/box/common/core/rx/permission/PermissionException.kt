package com.box.common.core.rx.permission

class PermissionException(private val mDeniedPermissions: MutableList<String>) : RuntimeException() {

    override fun toString(): String {
        return "Get $mDeniedPermissions fail."
    }

}