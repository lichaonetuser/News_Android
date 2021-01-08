package com.mynews.common.extension.share

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent

internal object OShareClipboard : IShare {

    override fun shareLink(activity: Activity, content: ContentLink, listener: IShareListener) {
        val clip = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clip.primaryClip = ClipData.newPlainText(null, content.text)
        listener.onSuccess(SharePlatform.CLIPBOARD)
    }

    override fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
    }

}
