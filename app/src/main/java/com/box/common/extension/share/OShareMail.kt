package com.box.common.extension.share

import android.app.Activity
import android.content.Intent
import com.box.app.news.R
import com.box.common.core.util.ResUtils

internal object OShareMail : IShare {

    private var mIShareListener: IShareListener? = null

    override fun shareLink(activity: Activity, content: ContentLink, listener: IShareListener) {
        mIShareListener = listener
        val email = Intent(Intent.ACTION_SEND)
        email.type = "plain/text"
        email.putExtra(Intent.EXTRA_SUBJECT, content.title)
        email.putExtra(Intent.EXTRA_TEXT, content.text)
        activity.startActivityForResult(Intent.createChooser(email, ResUtils.getString(R.string.Tip_SelectMailApp))
                , SharePlatform.MAIL.requestCode)
    }

    override fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (resultCode == Activity.RESULT_OK) {
            mIShareListener?.onSuccess(SharePlatform.MAIL, false, resultCode)
        } else if (resultCode == Activity.RESULT_CANCELED) {
            mIShareListener?.onCancel(SharePlatform.MAIL)
        } else {
            mIShareListener?.onError(SharePlatform.MAIL, Exception("error"))
        }
    }

}
