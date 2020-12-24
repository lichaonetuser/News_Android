package com.box.common.extension.share

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent

internal object OShareSystem : IShare {

    private var mIShareListener: IShareListener? = null

    override fun shareLink(activity: Activity, content: ContentLink, listener: IShareListener) {
        mIShareListener = listener
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, content.title)
        intent.putExtra(Intent.EXTRA_TEXT, content.text)
        activity.startActivity(Intent.createChooser(intent, null))
    }

    override fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (resultCode == Activity.RESULT_OK) {
            mIShareListener?.onSuccess(SharePlatform.SYSTEM, false)
        } else if (resultCode == Activity.RESULT_CANCELED) {
            mIShareListener?.onCancel(SharePlatform.SYSTEM)
        }  else {
            mIShareListener?.onError(SharePlatform.MAIL, Exception("error"))
        }
    }

}
