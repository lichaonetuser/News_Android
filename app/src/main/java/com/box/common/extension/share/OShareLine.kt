package com.box.common.extension.share

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import java.net.URLEncoder

internal object OShareLine : IShare {

    private var mIShareListener: IShareListener? = null

    override fun shareLink(activity: Activity, content: ContentLink, listener: IShareListener) {
        try {
            mIShareListener = listener
            val shareBuilder = StringBuilder("line://msg/")
            shareBuilder.append("text/")
            shareBuilder.append(URLEncoder.encode(content.text, "UTF-8"))
            val uri = Uri.parse(shareBuilder.toString())
            val intent = Intent(Intent.ACTION_VIEW, uri)
            activity.startActivityForResult(intent, SharePlatform.LINE.requestCode)
        } catch (e: Exception) {
            oldShareLink(activity, content, listener)
        }
    }

    private fun oldShareLink(activity: Activity, content: ContentLink, listener: IShareListener) {
        mIShareListener = listener
        val cn = ComponentName("jp.naver.line.android", "jp.naver.line.android.activity.selectchat.SelectChatActivity")
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain" // 纯文本
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, content.title)
        shareIntent.putExtra(Intent.EXTRA_TEXT, content.text)
        shareIntent.component = cn
        activity.startActivityForResult(shareIntent, SharePlatform.LINE.requestCode)
    }

    override fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        mIShareListener?.onSuccess(SharePlatform.LINE, false)
    }
}
