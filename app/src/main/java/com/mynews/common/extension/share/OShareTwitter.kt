package com.mynews.common.extension.share

import android.app.Activity
import android.content.Intent
import com.twitter.sdk.android.tweetcomposer.TweetComposer
import java.net.URL

internal object OShareTwitter : IShare {

    private var mIShareListener: IShareListener? = null

    override fun shareLink(activity: Activity, content: ContentLink, listener: IShareListener) {
        mIShareListener = listener
        val builder = TweetComposer.Builder(activity)
        builder.url(URL(content.linkUrl))
        if (!content.text.isNullOrBlank()) {
            builder.text(content.text)
        }
//        if (!content.imageUrl.isNullOrBlank()) {
//            val file: File? = ImageManager.getCachedImageOnDisk(UriUtil.parseUriOrNull(content.imageUrl))
//            if (true == file?.exists()) {
//                val uri = FileProvider.getUriForFile(activity, activity.applicationContext.packageName + ".provider", file)
//                builder.image(uri)
//            }
//        }
        val intent = builder.createIntent()
        activity.startActivityForResult(intent, SharePlatform.TWITTER.requestCode)
    }

    override fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        mIShareListener?.onSuccess(SharePlatform.TWITTER, false)
    }

}
