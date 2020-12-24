package com.box.common.extension.share

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.share.Sharer
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog

internal object OShareFacebook : IShare {

    private val mFacebookCallbackMgr: CallbackManager by lazy {
        CallbackManager.Factory.create()
    }

    private fun createShareDialog(activity: Activity, shareListener: IShareListener): ShareDialog {
        val shareDialog = ShareDialog(activity)
        if (FacebookSdk.isFacebookRequestCode(SharePlatform.FACEBOOK.requestCode)) {
            shareDialog.registerCallback(mFacebookCallbackMgr, object : FacebookCallback<Sharer.Result> {
                override fun onSuccess(result: Sharer.Result) {
                    shareListener.onSuccess(SharePlatform.FACEBOOK, true, result)
                }

                override fun onCancel() {
                    shareListener.onCancel(SharePlatform.FACEBOOK)
                }

                override fun onError(error: FacebookException) {
                    shareListener.onError(SharePlatform.FACEBOOK, error)
                }
            })
        } else {
            shareDialog.registerCallback(mFacebookCallbackMgr, object : FacebookCallback<Sharer.Result> {
                override fun onSuccess(result: Sharer.Result) {
                    shareListener.onSuccess(SharePlatform.FACEBOOK, true, result)
                }

                override fun onCancel() {
                    shareListener.onCancel(SharePlatform.FACEBOOK)
                }

                override fun onError(error: FacebookException) {
                    shareListener.onError(SharePlatform.FACEBOOK,error)
                }
            }, SharePlatform.FACEBOOK.requestCode)
        }
        return shareDialog
    }

    override fun shareLink(activity: Activity, content: ContentLink, listener: IShareListener) {
        val shareDialog = createShareDialog(activity, listener)
        if (ShareDialog.canShow(ShareLinkContent::class.java)) {
            val linkContent = ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(content.linkUrl))
                    .build()
            shareDialog.show(linkContent)
        } else {
            listener.onError(SharePlatform.FACEBOOK, Exception("ShareDialog can not show"))
        }
    }

    override fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        mFacebookCallbackMgr.onActivityResult(requestCode, resultCode, data)
    }

}
