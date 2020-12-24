package com.box.common.extension.share

import android.app.Activity
import android.content.Intent
import com.box.common.core.log.Logger
import com.box.common.extension.share.SharePlatform.*

@Suppress("unused")
object ShareManager {

    var mIShareExtraListener: IShareExtraListener? = null
    var mIsSharing: Boolean = false
    private var mHasShareError: Boolean = false

    private fun getIShare(platform: SharePlatform): IShare {
        return when (platform) {
            FACEBOOK -> OShareFacebook
            TWITTER -> OShareTwitter
            LINE -> OShareLine
            MAIL -> OShareMail
            SMS -> OShareSMS
            CLIPBOARD -> OShareClipboard
            SYSTEM -> OShareSystem
        }
    }

    private fun getIShare(requestCode: Int): IShare? {
        return when (requestCode) {
            FACEBOOK.requestCode -> OShareFacebook
            TWITTER.requestCode -> OShareTwitter
            LINE.requestCode -> OShareLine
            MAIL.requestCode -> OShareMail
            SMS.requestCode -> OShareSMS
            CLIPBOARD.requestCode -> OShareClipboard
            SYSTEM.requestCode -> OShareSystem
            else -> null
        }
    }

    private var mShare: IShare? = null

    fun shareLink(activity: Activity, platform: SharePlatform, content: ContentLink, listener: IShareListener) {
        try {
            mIShareExtraListener?.onPreShare()
            mIsSharing = true
            mShare = getIShare(platform)
            mShare?.shareLink(activity, content, listener)
        } catch (e: Exception) {
            listener.onError(platform, e)
            mHasShareError = true
            mIsSharing = false
            mIShareExtraListener?.onFinishShare()
        }
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        try {
            if (!mHasShareError) {
                val share = getIShare(requestCode) ?: mShare
                share?.handleActivityResult(requestCode, resultCode, data)
                mShare = null
            }
            mHasShareError = false
            mIsSharing = false
            mIShareExtraListener?.onFinishShare()
        } catch (e: Exception) {
            Logger.e(e)
            mIsSharing = false
            mIShareExtraListener?.onFinishShare()
        }
    }

}
