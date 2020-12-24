package com.box.common.extension.login

interface ILoginListener {

    fun onSuccess(platform: LoginPlatform, result: Map<String, Any>)

    fun onCancel(platform: LoginPlatform)

    fun onFailure(platform: LoginPlatform, error: Exception)

}
