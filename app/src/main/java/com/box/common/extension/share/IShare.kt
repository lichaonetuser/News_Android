package com.box.common.extension.share

import android.app.Activity
import android.content.Intent

internal interface IShare {

    fun shareLink(activity: Activity, content: ContentLink, listener: IShareListener)

    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent)

}
