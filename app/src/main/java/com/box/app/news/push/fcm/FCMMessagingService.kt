package com.box.app.news.push.fcm

import android.annotation.TargetApi
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.text.TextUtils
import com.box.app.news.R
import com.box.app.news.analytics.AnalyticsKey
import com.box.app.news.applog.AppLogKey
import com.box.app.news.applog.AppLogManager
import com.box.app.news.bean.PushArrival
import com.box.app.news.bean.PushArrivalLog
import com.box.app.news.bean.PushMessage
import com.box.app.news.data.DataDictionary
import com.box.app.news.data.DataManager
import com.box.app.news.data.source.local.preference.PreferenceAPI
import com.box.app.news.page.activity.LockScreenDialogActivity
import com.box.app.news.page.activity.SplashActivity
import com.box.app.news.page.activity.SplashActivityAutoBundle
import com.box.app.news.proto.AppLog
import com.box.common.core.analytics.AnalyticsManager
import com.box.common.core.image.fresco.ImageManager
import com.box.common.core.image.fresco.config.ImageLoaderConfig
import com.box.common.core.image.fresco.listener.IResult
import com.box.common.core.json.gson.util.CoreGsonUtils
import com.box.common.core.log.Logger
import com.crashlytics.android.Crashlytics
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.fabric.sdk.android.Fabric
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class FCMMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String?) {
        super.onNewToken(token)
        try {
            Logger.d("onNewToken Thread : ${Thread.currentThread().name}")
            Logger.d("onNewToken : $token")
            DataManager.Local.savePushTokenSubmit(false)
            DataManager.submitPushToken(token)
        } catch (e: Exception) {
            Logger.e("onNewToken error: $e")
            Fabric.with(this, Crashlytics())
            Crashlytics.logException(e)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        try {
            sendNotification(remoteMessage ?: return)
            Logger.d("onMessageReceived")
        } catch (e: Exception) {
            Fabric.with(this, Crashlytics())
            Crashlytics.logException(e)
        }
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {
        try {
            val message = remoteMessage.data["message"]
            val openUrl = remoteMessage.data["open_url"]
            val pushId = remoteMessage.messageId?: ""
            val sentTime = remoteMessage.sentTime

            AnalyticsManager.logEvent(AnalyticsKey.Event.APNS, AnalyticsKey.Parameter.RECEIVE)
            AppLogManager.logEvent(
                    name = AppLog.EventName.EVENT_APNS,
                    label = AppLogKey.Label.RECEIVE,
                    body = AppLog.EventBody.newBuilder()
                            .setOpenUrl(openUrl)
                            .build())

            if (!isNotificationChannelEnabled(this, CHANNEL_ID)) {
                // Logger.d("Notifications not enabled.")
                return
            }

            if (openUrl == null || message == null) {
                return
            }

            val pushMessage = CoreGsonUtils.fromJson(message, PushMessage::class.java) ?: return
            val pushArrivalLog = PushArrivalLog(openUrl = openUrl, pushId = pushId)
            val pushArrival = DataManager.Local.getPushArrival()
            pushArrival.logs.add(pushArrivalLog)
            DataManager.Remote.pushArrival(pushArrival)
                    .subscribeOn(Schedulers.single())
                    .subscribeBy(
                            onNext = {
                                DataManager.Local.savePushArrival(PushArrival())
                            },
                            onError = {
                                DataManager.Local.savePushArrival(pushArrival)
                            }
                    )

            val clear = pushMessage.clear
            if (clear == 1) {
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                notificationManager?.cancelAll()
            }

            val id = System.currentTimeMillis().toInt()
            val style = pushMessage.style
            when(style) {
                0 -> notifyDefault(id, pushMessage, openUrl)
                1 -> notifyMultiple(pushMessage, sentTime)
                2 -> notifyImage(id, pushMessage, openUrl, sentTime)
            }

            val showDialogWhenLock = DataManager.Local.getShowPushDialogWhenLock()
            if (!showDialogWhenLock) {
                AnalyticsManager.logEvent(AnalyticsKey.Event.LOCK_SCREEN, AnalyticsKey.Parameter.STATUS_DISABLE)
                return
            }
            notifyLockScreenStyle(id, pushMessage, openUrl)

        } catch (e: Exception) {
            Fabric.with(this, Crashlytics())
            Crashlytics.logException(e)
        }
    }

    private fun notifyDefault(id: Int, pushMessage: PushMessage, openUrl: String) {
        val pendingIntent = getPendingIntent(pushMessage, openUrl)
        val needSound = DataManager.Local.getPushUseSound()

        val notificationBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !needSound) {  //适配8.0声音问题
            NotificationCompat.Builder(this, CHANNEL_LOW_ID)
        } else {
            NotificationCompat.Builder(this, CHANNEL_ID)
        }
        .setDefaults(if (needSound) {  //8.0以下声音控制
            NotificationCompat.DEFAULT_SOUND
        } else {
            NotificationCompat.DEFAULT_LIGHTS
        })
        .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
        .setColorized(false)
        .setGroupSummary(true)
        .setGroup(System.currentTimeMillis().toString())
        .setContentTitle(pushMessage.title)
        .setContentText(pushMessage.content)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setContentIntent(pendingIntent)
        .setSmallIcon(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            R.drawable.ic_status_notifications
        } else {
            applicationInfo.icon
        })
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !needSound) {  //适配8.0声音问题
            notifyLow(id, notificationBuilder.build())
        } else {
            notify(id, notificationBuilder.build())
        }
    }

    private fun notifyMultiple(pushMessage: PushMessage, sendTime: Long) {
        val smallIcon = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            R.drawable.ic_status_notifications
        } else {
            applicationInfo.icon
        }

        val needSound = DataManager.Local.getPushUseSound()
        val items = pushMessage.items
        var groupStartId = System.currentTimeMillis()

        val idList = PreferenceAPI.getPushIds()
        val iterator = idList.iterator()
        while (iterator.hasNext()) {
            val preLongId = iterator.next()
            if (groupStartId - preLongId > MAX_CLEAN_INTERVAL) {
                val notifyId = (preLongId % Int.MAX_VALUE).toInt()
                cancelNotify(notifyId)
                iterator.remove()
            }
        }
        var index = 0
        items?.forEach {
            val imageUrl = it.images?.firstOrNull()
            val url = it.openUrl?: ""
            val pendingIntent = getPendingIntent(pushMessage, url)

            val builder = if (index == 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !needSound) {  //适配8.0声音问题
                    NotificationCompat.Builder(this, CHANNEL_LOW_ID)
                } else {
                    NotificationCompat.Builder(this, CHANNEL_ID)
                }
            } else {
                NotificationCompat.Builder(this, CHANNEL_LOW_ID)
            }
            val priority = when(index) {
                0 -> NotificationCompat.PRIORITY_MAX
                1 -> NotificationCompat.PRIORITY_HIGH
                2 -> NotificationCompat.PRIORITY_DEFAULT
                3 -> NotificationCompat.PRIORITY_LOW
                else -> NotificationCompat.PRIORITY_MIN
            }

            builder.setSmallIcon(smallIcon)
                    .setPriority(priority)
                    .setShowWhen(true)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setWhen(sendTime)
                    .setOnlyAlertOnce(true)

            if (index == 0) { //8.0以下第一个声音控制
                builder.setDefaults(if (needSound) {
                    NotificationCompat.DEFAULT_SOUND
                } else {
                    NotificationCompat.DEFAULT_LIGHTS
                })
            } else {
                builder.setDefaults(NotificationCompat.DEFAULT_LIGHTS)
            }
            val style = NotificationCompat.BigTextStyle()
            when(it.flag) {
                0 -> {
                    builder.setContentTitle(it.title)

                    style.setBigContentTitle(it.title)
                    builder.setStyle(style)
                }
                1 -> {
                    builder.setContentText(it.content)

                    style.bigText(it.content)
                    builder.setStyle(style)
                }
                2 -> {
                    builder.setContentTitle(it.title)
                    builder.setContentText(it.content)

                    style.setBigContentTitle(it.title)
                    style.bigText(it.content)

                    builder.setStyle(style)
                }
                else -> {
                    builder.setContentText(it.content)

                    style.bigText(it.content)
                    builder.setStyle(style)
                }
            }

            val notifyId = (groupStartId % Int.MAX_VALUE).toInt()
            if (TextUtils.isEmpty(imageUrl)) {
                if (index == 0 ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !needSound) {  //适配8.0声音问题
                        notifyLow(notifyId, builder.build())
                    } else {
                        notify(notifyId, builder.build())
                    }
                } else {
                    notifyLow(notifyId, builder.build())
                }
            } else {
                ImageManager.with(this)
                        .setUrl(imageUrl)
                        .setResult(IResult<Bitmap> { result ->
                            if (result == null) {
                                return@IResult
                            }
                            ImageManager.prefetchToBitmapCache(imageUrl)
                            builder.setLargeIcon(result)
                            if (index == 0 ) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !needSound) {  //适配8.0声音问题
                                    notifyLow(notifyId, builder.build())
                                } else {
                                    notify(notifyId, builder.build())
                                }
                            } else {
                                notifyLow(notifyId, builder.build())
                            }
                        })
                        .load()
            }
            idList.add(groupStartId)
            groupStartId++
            index++
        }
        PreferenceAPI.savePushIds(idList)
    }

    private fun notifyImage(id: Int, pushMessage: PushMessage, openUrl: String, sendTime: Long) {
        val imageUrl = pushMessage.image
        val needSound = DataManager.Local.getPushUseSound()
        ImageManager.with(this)
                .setUrl(imageUrl)
                .setResult(IResult<Bitmap> { result ->
                    if (result == null) {
                        return@IResult
                    }
                    val smallIcon = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        R.drawable.ic_status_notifications
                    } else {
                        applicationInfo.icon
                    }
                    val intent = getPendingIntent(pushMessage, openUrl)

                    val style = NotificationCompat.BigPictureStyle().bigPicture(result)
//                    style.setBigContentTitle(pushMessage.title)
//                    style.setSummaryText(pushMessage.content)
//                    style.bigLargeIcon(largeIcon)

                    val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !needSound) {  //适配8.0声音问题
                        NotificationCompat.Builder(this, CHANNEL_LOW_ID)
                    } else {
                        NotificationCompat.Builder(this, CHANNEL_ID)
                    }
                    .setDefaults(if (needSound) {
                        NotificationCompat.DEFAULT_SOUND
                    } else {
                        NotificationCompat.DEFAULT_LIGHTS
                    })
                    .setSmallIcon(smallIcon)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setAutoCancel(true)
                    .setContentIntent(intent)
                    .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                    .setStyle(style)
                    .setWhen(sendTime)
                    .setOnlyAlertOnce(true)

                    if (TextUtils.isEmpty(pushMessage.title)) {
                        builder.setContentTitle(pushMessage.content)
                    } else {
                        builder.setContentTitle(pushMessage.title)
                                .setContentText(pushMessage.content)
                    }
                    ImageManager.prefetchToBitmapCache(imageUrl)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !needSound) {  //适配8.0声音问题
                        notifyLow(id, builder.build())
                    } else {
                        notify(id, builder.build())
                    }
                })
                .load()
    }

    private fun notifyLockScreenStyle(id: Int, pushMessage: PushMessage, openUrl: String) {
        val lockScreen = pushMessage.lockScreen
        if (lockScreen == null || !lockScreen.show) {
            return
        }
        val style = DataDictionary.PushMessageDialogStyle.intValueOf(lockScreen.style)
        when (style) {
            DataDictionary.PushMessageDialogStyle.UNKNOWN,
            DataDictionary.PushMessageDialogStyle.TEXT,
            DataDictionary.PushMessageDialogStyle.RIGHT_IMAGE -> return
//                DataDictionary.PushMessageDialogStyle.TEXT -> {
//                    LockScreenDialogActivity.tryStartIfInKeyguardRestrictedInputMode(
//                            context = this,
//                            pushMessage = pushMessage,
//                            openUrl = openUrl,
//                            notifyId = id,
//                            cancelNotifyIfOpen = true)
//                }

            DataDictionary.PushMessageDialogStyle.LARGE_IMAGE -> {
                val images = lockScreen.images ?: return
                if (images.isEmpty()) {
                    return
                }
                ImageManager.init(this, ImageLoaderConfig.getInstance(this).imagePipelineConfigBuilder)
                ImageManager.with(this)
                        .setUrl(images.firstOrNull())
                        .setResult(IResult<Bitmap> { result ->
                            if (result == null) {
                                return@IResult
                            }
                            ImageManager.prefetchToBitmapCache(images.firstOrNull())
                            LockScreenDialogActivity.tryStartIfInKeyguardRestrictedInputMode(
                                    context = this@FCMMessagingService,
                                    pushMessage = pushMessage,
                                    openUrl = openUrl,
                                    notifyId = id,
                                    cancelNotifyIfOpen = true)
                        })
                        .load()
            }
            DataDictionary.PushMessageDialogStyle.MULTI_ITEM -> {
                LockScreenDialogActivity.tryStartIfInKeyguardRestrictedInputMode(
                        context = this@FCMMessagingService,
                        pushMessage = pushMessage,
                        openUrl = openUrl,
                        notifyId = id,
                        cancelNotifyIfOpen = true)
            }
        }

    }

    private fun notify(id: Int, notification: Notification) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        @TargetApi(Build.VERSION_CODES.O)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan1 = NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT)
            chan1.lightColor = Color.GREEN
            chan1.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManager?.createNotificationChannel(chan1)
        }
        notificationManager?.notify(id, notification)
    }

    private fun notifyLow(id: Int, notification: Notification) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        @TargetApi(Build.VERSION_CODES.O)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan1 = NotificationChannel(CHANNEL_LOW_ID, CHANNEL_LOW_ID, NotificationManager.IMPORTANCE_LOW)
            chan1.lightColor = Color.GREEN
            chan1.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManager?.createNotificationChannel(chan1)
        }
        notificationManager?.notify(id, notification)
    }

    private fun cancelNotify(id: Int) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.cancel(id)
    }

    private fun getPendingIntent(pushMessage: PushMessage, openUrl: String): PendingIntent {
        val intent = Intent(this, SplashActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                .putExtras(SplashActivityAutoBundle.builder().mPushOpenUrl(openUrl).bundle())
        val requestCode = System.currentTimeMillis().toInt()
        return PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    companion object {
        const val CHANNEL_ID = "News"
        const val CHANNEL_LOW_ID = "News_Low"
        const val MAX_CLEAN_INTERVAL = 20 * 60 * 60 * 1000 //20小时

        fun isNotificationChannelEnabled(context: Context, @NonNull channelId: String): Boolean {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (NotificationManagerCompat.from(context).areNotificationsEnabled() && channelId.isNotBlank()) {
                        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        val channel = manager.getNotificationChannel(channelId)
                        return channel.importance != NotificationManager.IMPORTANCE_NONE
                    }
                    return false
                } else {
                    return NotificationManagerCompat.from(context).areNotificationsEnabled()
                }
            } catch (e: Exception) {
                return true
            }
        }
    }
}