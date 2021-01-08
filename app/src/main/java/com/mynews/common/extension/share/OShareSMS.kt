package com.mynews.common.extension.share

import android.app.Activity
import android.content.Intent
import android.net.Uri

internal object OShareSMS : IShare {

    private var mIShareListener: IShareListener? = null

    override fun shareLink(activity: Activity, content: ContentLink, listener: IShareListener) {
        mIShareListener = listener
        val sendIntent = Intent(Intent.ACTION_VIEW, Uri.parse("smsto:"))
        sendIntent.putExtra("sms_body", content.text)
        sendIntent.type = "vnd.android-dir/mms-sms"
        activity.startActivityForResult(sendIntent
                , SharePlatform.SMS.requestCode)
    }

    override fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (resultCode == Activity.RESULT_OK) {
            mIShareListener?.onSuccess(SharePlatform.SMS, false)
        } else if (resultCode == Activity.RESULT_CANCELED) {
            mIShareListener?.onCancel(SharePlatform.SMS)
        }  else {
            mIShareListener?.onError(SharePlatform.MAIL, Exception("error"))
        }
    }
}
