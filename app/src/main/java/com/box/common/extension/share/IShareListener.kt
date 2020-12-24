package com.box.common.extension.share

interface IShareListener {

    fun onSuccess(sharePlatform: SharePlatform, confirm: Boolean = true, extra: Any? = null)

    fun onCancel(sharePlatform: SharePlatform)

    fun onError(sharePlatform: SharePlatform, e: Exception)

}
