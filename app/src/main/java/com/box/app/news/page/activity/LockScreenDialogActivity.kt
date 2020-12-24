package com.box.app.news.page.activity

import android.app.KeyguardManager
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.*
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SwitchCompat
import android.view.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import com.box.app.news.R
import com.box.app.news.analytics.AnalyticsKey
import com.box.app.news.bean.PushMessage
import com.box.app.news.bean.PushMessageLockScreenItem
import com.box.app.news.bean.PushMessageLockScreenItem.Companion.LOCK_SCREEN_ITEM_STYLE_LARGE_IMAGE
import com.box.app.news.bean.PushMessageLockScreenItem.Companion.LOCK_SCREEN_ITEM_STYLE_NORMAL
import com.box.app.news.data.DataDictionary
import com.box.app.news.data.DataManager
import com.box.app.news.util.ScreenUtils
import com.box.common.core.CoreApp
import com.box.common.core.analytics.AnalyticsManager
import com.box.common.core.app.activity.CoreBaseActivity
import com.box.common.core.image.fresco.ImageManager
import com.box.common.core.log.Logger
import com.box.common.core.rx.schedulers.ioToMain
import com.box.common.core.rx.schedulers.obOnMain
import com.box.common.core.util.ResUtils
import com.box.common.core.util.extension.format2DateString
import com.appsflyer.AppsFlyerLib
import com.crashlytics.android.Crashlytics
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.postprocessors.IterativeBoxBlurPostProcessor
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.yatatsu.autobundle.AutoBundleField
import io.fabric.sdk.android.Fabric
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_lock_screen.*
import kotlinx.android.synthetic.main.activity_lock_screen_include_large_img.*
import kotlinx.android.synthetic.main.activity_lock_screen_include_large_img.view.*
import kotlinx.android.synthetic.main.item_lock_screen_multi.view.*
import org.jetbrains.anko.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit

class LockScreenDialogActivity : CoreBaseActivity() {
    private var tempDir: String? = null

    companion object {
        private const val WALLPAPER_TEMP_FILE_NAME = "wallpaper_temp.png"

        fun tryStartIfInKeyguardRestrictedInputMode(context: Context,
                                                    pushMessage: PushMessage,
                                                    openUrl: String,
                                                    notifyId: Int,
                                                    cancelNotifyIfOpen: Boolean = true) {
            try {
                //是否锁屏
                val keyguardManager = CoreApp.getInstance().keyguardManager
                val isKeyLock = keyguardManager.inKeyguardRestrictedInputMode()

                //是否息屏
                val isScreenOff = !ScreenUtils.isScreenOn()

                //锁屏弹窗正在展示
                val isLocked = (CoreApp.coreBaseActivities.find {
                    it::class.java == LockScreenDialogActivity::class.java
                }) != null

                when {
                    isKeyLock -> AnalyticsManager.logEvent(AnalyticsKey.Event.LOCK_SCREEN, AnalyticsKey.Parameter.STATUS_KEY_LOCK)
                    isScreenOff -> AnalyticsManager.logEvent(AnalyticsKey.Event.LOCK_SCREEN, AnalyticsKey.Parameter.STATUS_SCREEN_OFF)
                    isLocked -> AnalyticsManager.logEvent(AnalyticsKey.Event.LOCK_SCREEN, AnalyticsKey.Parameter.STATUS_SHOW_LOCKED)
                }

                if (isKeyLock || isScreenOff || isLocked) {
                    // 处于锁屏或息屏
                    context.startActivity(LockScreenDialogActivityAutoBundle.builder()
                            .mPushMessage(pushMessage)
                            .mPushOpenUrl(openUrl)
                            .mNotifyId(notifyId)
                            .mCancelNotifyIfOpen(cancelNotifyIfOpen)
                            .build(context)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED))
                } else {
                    AnalyticsManager.logEvent(AnalyticsKey.Event.LOCK_SCREEN, AnalyticsKey.Parameter.STATUS_OTHER)
                }
            } catch (e: Exception) {
                Fabric.with(context, Crashlytics())
                Crashlytics.logException(e)
            }
        }
    }

    @AutoBundleField(required = false)
    var mPushMessage: PushMessage? = null
    @AutoBundleField(required = false)
    var mPushOpenUrl: String? = null
    @AutoBundleField(required = false)
    var mNotifyId: Int = -1
    @AutoBundleField(required = false)
    var mCancelNotifyIfOpen: Boolean = true

    private var mTimerDisposable: Disposable? = null
    private var mWakeLock: PowerManager.WakeLock? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.activity_lock_screen)

            AppsFlyerLib.getInstance().stopTracking(true, CoreApp.getInstance())

            tempDir = externalCacheDir.absolutePath

            if (mPushMessage == null || mPushOpenUrl.isNullOrBlank()) {
                finish()
                return
            }
            isUmengEnable = false
            initUI()
            resetWindow()
            resetUIAndData()
            AnalyticsManager.logEvent(AnalyticsKey.Event.LOCK_SCREEN, AnalyticsKey.Parameter.ENTER)
        } catch (e: Exception) {
            Fabric.with(this, Crashlytics())
            Crashlytics.logException(e)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        updateTimeText()
        startTimer()
    }

    override fun onStop() {
        super.onStop()
        stopTimer()
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseWakeLock()
        if (AppsFlyerLib.getInstance().isTrackingStopped) {
            AppsFlyerLib.getInstance().stopTracking(false, CoreApp.getInstance())
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        resetWindow()
        resetUIAndData()
        AnalyticsManager.logEvent(AnalyticsKey.Event.LOCK_SCREEN, AnalyticsKey.Parameter.UPDATE)
    }

    private fun initUI() {
        close_btn.setOnClickListener {
            AnalyticsManager.logEvent(AnalyticsKey.Event.LOCK_SCREEN, AnalyticsKey.Parameter.CLICK_CLOSE)
            finish()
            MainActivity.shouldSendLogResume = false
            MainActivity.shouldSendLogPause = false
        }
        setting_btn.setOnClickListener {
            AnalyticsManager.logEvent(AnalyticsKey.Event.LOCK_SCREEN, AnalyticsKey.Parameter.CLICK_SETTING)
            showPushSettingDialog()
        }
        try {
            blurBackground()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun gotoMain() {
        if (AppsFlyerLib.getInstance().isTrackingStopped) {
            AppsFlyerLib.getInstance().stopTracking(false, CoreApp.getInstance())
        }
        if (mCancelNotifyIfOpen) {
            notificationManager.cancel(mNotifyId)
        }
        val intent = Intent(this, SplashActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                .putExtras(SplashActivityAutoBundle.builder()
                        .mPushOpenUrl(mPushOpenUrl ?: "")
                        .bundle())
        startActivity(intent)
        finish()
    }

    private fun blurBackground() {
        val wallPowerManager = WallpaperManager.getInstance(this)
        val drawable = wallPowerManager.drawable as BitmapDrawable
        val originBmp = drawable.bitmap

        val matrix = Matrix()
        matrix.postScale(0.1f, 0.1f)

        val startX: Int
        val width: Int
        if (originBmp.width < resources.displayMetrics.widthPixels) {
            startX = 0
            width = originBmp.width
        } else {
            startX = (originBmp.width - resources.displayMetrics.widthPixels) / 2
            width = resources.displayMetrics.widthPixels
        }
//        val width = Math.min(resources.displayMetrics.widthPixels, originBmp.width)
        val height = Math.min(resources.displayMetrics.heightPixels - dip(24), originBmp.height)
        val resizeBmp = Bitmap.createBitmap(originBmp, startX, 0, width, height, matrix, true)

        Completable.fromCallable {
            saveBitmap(resizeBmp)
        }.ioToMain<Void>().subscribeBy(
                onComplete = {
                    val uri = "file://$tempDir/$WALLPAPER_TEMP_FILE_NAME"
                    showUriBlur(lock_screen_bg, uri, 3, 3)
                    resizeBmp.recycle()
                },
                onError = {
                    resizeBmp.recycle()
                }
        )
    }

    /** 保存方法 */
    private fun saveBitmap(bitmap: Bitmap) {
        val dir = File(tempDir)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val file = File(tempDir, WALLPAPER_TEMP_FILE_NAME)
        if (file.exists()) {
            file.delete()
        }
        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
            out.flush()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            out?.close()
        }
    }

    /**
     * 以高斯模糊显示。
     *
     * @param draweeView View。
     * @param url        url.
     * @param iterations 迭代次数，越大越模糊。
     * @param blurRadius 模糊图半径，必须大于0，越大越模糊。
     */
    private fun showUriBlur(draweeView: SimpleDraweeView, url: String, iterations: Int, blurRadius: Int) {
        try {
            val uri = Uri.parse(url)
            val request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setPostprocessor(IterativeBoxBlurPostProcessor(iterations, blurRadius))
                    .build()
            val controller = Fresco.newDraweeControllerBuilder()
                    .setOldController(draweeView.controller)
                    .setImageRequest(request)
                    .build()
            draweeView.controller = controller
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun isSecured(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            return keyguardManager.isKeyguardSecure
        }
        return false
    }

    /**
     * 重置窗口属性
     */
    private fun resetWindow() {
        //置于锁屏界面之上
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        //window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
        }

        //是否唤醒屏幕
        val lockScreen = mPushMessage?.lockScreen ?: return
        val wakeType = DataDictionary.PushMessageWakeType.intValueOf(lockScreen.wakeType)
        val wakeTime = lockScreen.wakeTime
        when (wakeType) {
            DataDictionary.PushMessageWakeType.NONE -> {
                setTurnScreenOnCompat(false)
            }
            DataDictionary.PushMessageWakeType.WAKE_ONCE -> {
                setTurnScreenOnCompat(true)
                if (wakeTime < 0L) {
                    acquireWakeLock(50)
                } else {
                    acquireWakeLock(wakeTime)
                }
            }
        }
    }

    private fun setTurnScreenOnCompat(show: Boolean) {
        if (show) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                setTurnScreenOn(true)
            }

        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                setTurnScreenOn(false)
            }
        }
    }

    private fun acquireWakeLock(timeout: Long = 5 * 1000) {
        try {
            if (mWakeLock == null) {
                mWakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "wake")
            }
            mWakeLock?.acquire(timeout)
        } catch (e: Exception) {
            Fabric.with(this, Crashlytics())
            Crashlytics.logException(e)
        }
    }

    private fun releaseWakeLock(delayMS: Long = 0) {
        try {
            if (mWakeLock?.isHeld != true) {
                return
            }

            if (delayMS == 0L) {
                mWakeLock?.release()
                return
            }

            Single.just(1)
                    .delay(delayMS, TimeUnit.MILLISECONDS)
                    .subscribeBy(
                            onSuccess = {
                                if (mWakeLock?.isHeld != true) {
                                    return@subscribeBy
                                }
                                mWakeLock?.release()
                            },
                            onError = {
                                Fabric.with(this, Crashlytics())
                                Crashlytics.logException(it)
                            }
                    )
        } catch (e: Exception) {
            Fabric.with(this, Crashlytics())
            Crashlytics.logException(e)
        }
    }

    /**
     * 重置UI和数据
     */
    private fun resetUIAndData() {
        val lockScreen = mPushMessage?.lockScreen ?: return
        val style = DataDictionary.PushMessageDialogStyle.intValueOf(lockScreen.style)
        val title = if (lockScreen.title == null) mPushMessage?.title else lockScreen.title
        val content = if (lockScreen.content == null) mPushMessage?.content else lockScreen.content
        val images = lockScreen.images

        when (style) {
            DataDictionary.PushMessageDialogStyle.UNKNOWN -> return
            DataDictionary.PushMessageDialogStyle.TEXT -> {
                showContentLayout(no_img_layout)
                no_img_layout.push_content.text = content

                content_layout.setOnClickListener {
                    AnalyticsManager.logEvent(AnalyticsKey.Event.LOCK_SCREEN, AnalyticsKey.Parameter.CLICK_CONTENT)
                    gotoMain()
                }
                detail_btn.setOnClickListener {
                    AnalyticsManager.logEvent(AnalyticsKey.Event.LOCK_SCREEN, AnalyticsKey.Parameter.CLICK_CONTENT)
                    gotoMain()
                }
            }
            DataDictionary.PushMessageDialogStyle.RIGHT_IMAGE -> {
                showContentLayout(right_img_layout)
                right_img_layout.push_content.text = content
                ImageManager.with(right_img_layout.push_img).load(images?.firstOrNull())

                content_layout.setOnClickListener {
                    AnalyticsManager.logEvent(AnalyticsKey.Event.LOCK_SCREEN, AnalyticsKey.Parameter.CLICK_CONTENT)
                    gotoMain()
                }
                detail_btn.setOnClickListener {
                    AnalyticsManager.logEvent(AnalyticsKey.Event.LOCK_SCREEN, AnalyticsKey.Parameter.CLICK_CONTENT)
                    gotoMain()
                }
            }
            DataDictionary.PushMessageDialogStyle.LARGE_IMAGE -> {
                showContentLayout(large_img_layout)
                large_img_layout.push_content.text = content
                ImageManager.with(large_img_layout.push_img).load(images?.firstOrNull())

                large_img_container.setOnClickListener {
                    AnalyticsManager.logEvent(AnalyticsKey.Event.LOCK_SCREEN, AnalyticsKey.Parameter.CLICK_CONTENT)
                    gotoMain()
                }
                detail_btn.setOnClickListener {
                    AnalyticsManager.logEvent(AnalyticsKey.Event.LOCK_SCREEN, AnalyticsKey.Parameter.CLICK_CONTENT)
                    gotoMain()
                }
            }
            DataDictionary.PushMessageDialogStyle.MULTI_ITEM -> {
                showContentLayout(multi_item_layout)
                showMultiItemLayout()

                mPushMessage?.lockScreen?.items?.run {
                    if (size > 1) {
                        detail_btn.setText(R.string.Notification_List_ReadAll)
                        detail_btn.setOnClickListener {
                            AnalyticsManager.logEvent(AnalyticsKey.Event.LOCK_SCREEN, AnalyticsKey.Parameter.CLICK_CONTENT)
                            mPushOpenUrl = mPushMessage?.lockScreen?.moreUrl
                            gotoMain()
                        }
                        content_root.setPadding(0, 0, 0, 0)
                    } else {
                        detail_btn.setText(R.string.Mine_Notification_ReadList)
                        detail_btn.setOnClickListener {
                            AnalyticsManager.logEvent(AnalyticsKey.Event.LOCK_SCREEN, AnalyticsKey.Parameter.CLICK_CONTENT)
                            if (isNotEmpty()) {
                                mPushOpenUrl = get(0).openUrl
                                gotoMain()
                            }
                        }
                        content_root.setPadding(0, dip(48), 0, 0)
                    }
                }
            }
        }
    }

    //锁屏推送多条item
    private fun showMultiItemLayout() {
        val items = mPushMessage?.lockScreen?.items
        if (items == null || items.isEmpty()) return

        val rv = multi_item_layout as RecyclerView
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                return when (viewType) {
                    LOCK_SCREEN_ITEM_STYLE_LARGE_IMAGE -> {
                        val view = LayoutInflater.from(this@LockScreenDialogActivity)
                                .inflate(R.layout.activity_lock_screen_include_large_img, parent, false)
                        LargeItemHolder(view)
                    }
                    else -> {
                        val view = LayoutInflater.from(this@LockScreenDialogActivity)
                                .inflate(R.layout.item_lock_screen_multi, parent, false)
                        MultiItemHolder(view)
                    }

                }
            }

            override fun getItemCount(): Int {
                return items.size
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                when (holder) {
                    is MultiItemHolder -> holder.bind(items[position], itemCount)
                    is LargeItemHolder -> holder.bind(items[position], itemCount)
                }
            }

            override fun getItemViewType(position: Int): Int {
                return when (items[position].style) {
                    1 -> LOCK_SCREEN_ITEM_STYLE_LARGE_IMAGE
                    else -> LOCK_SCREEN_ITEM_STYLE_NORMAL
                }
            }
        }
    }

    inner class MultiItemHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: PushMessageLockScreenItem, totalCount: Int) = with(view) {
            if (item.images == null || item.images!!.isEmpty()) {
                lock_screen_item_cover.visibility = View.GONE
                lock_screen_item_content.maxLines = 4
            } else {
                lock_screen_item_cover.visibility = View.VISIBLE
                ImageManager.with(lock_screen_item_cover).load(item.images?.firstOrNull())
                lock_screen_item_content.maxLines = 3
            }
            lock_screen_item_content.text = item.content
            lock_screen_item.setOnClickListener {
                mPushOpenUrl = item.openUrl
                gotoMain()
            }

            if (totalCount <= 1) {
                lock_screen_item_more.visibility = View.GONE
            } else {
                lock_screen_item_more.visibility = View.VISIBLE
            }
        }
    }

    inner class LargeItemHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: PushMessageLockScreenItem, totalCount: Int) = with(view) {
            if (item.images == null || item.images!!.isEmpty()) {
                push_img.visibility = View.GONE
            } else {
                push_img.visibility = View.VISIBLE
                ImageManager.with(push_img).load(item.images?.firstOrNull())
            }
            push_content.text = item.content
            large_img_container.setOnClickListener {
                mPushOpenUrl = item.openUrl
                gotoMain()
            }
        }
    }

    private fun showContentLayout(showView: View) {
        right_img_layout.visibility = View.GONE
        large_img_layout.visibility = View.GONE
        no_img_layout.visibility = View.GONE
        multi_item_layout.visibility = View.GONE
        showView.visibility = View.VISIBLE
    }

    private fun showPushSettingDialog() {
        if (isFinishing || (Build.VERSION.SDK_INT >= 17 && isDestroyed)) {
            return
        }
        val switchView = SwitchCompat(this)
        switchView.showText = false
        switchView.text = ResUtils.getString(R.string.Setting_SwitchOfPushOnLockedScreen)
        switchView.isChecked = DataManager.Local.getShowPushDialogWhenLock()
        switchView.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AnalyticsManager.logEvent(AnalyticsKey.Event.LOCK_SCREEN, AnalyticsKey.Parameter.PUSH_LOCK_DIALOG_ON)
            } else {
                AnalyticsManager.logEvent(AnalyticsKey.Event.LOCK_SCREEN, AnalyticsKey.Parameter.PUSH_LOCK_DIALOG_OFF)
            }
            DataManager.Local.saveShowPushDialogWhenLock(isChecked)
        }

        val frameLayout = FrameLayout(this)
        val layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        layoutParams.gravity = Gravity.CENTER
        frameLayout.setPadding(dip(20), dip(10), dip(20), dip(10))
        frameLayout.addView(switchView, layoutParams)

        val dialog = AlertDialog.Builder(this)
                .setView(frameLayout)
                .create()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && isDestroyed) {
            return
        }
        dialog.show()
    }

    private fun startTimer() {
        mTimerDisposable = Observable.interval(1, TimeUnit.SECONDS)
                .map { System.currentTimeMillis() }
                .onErrorResumeNext(Observable.just(System.currentTimeMillis()))
                .obOnMain()
                .subscribeBy(
                        onNext = {
                            updateTimeText(it)
                        },
                        onError = {
                            Fabric.with(this, Crashlytics())
                            Crashlytics.logException(it)
                        }
                )
    }

    private fun stopTimer() {
        mTimerDisposable?.dispose()
    }

    private fun updateTimeText(timeMillis: Long = System.currentTimeMillis()) {
        date_txt.text = timeMillis.format2DateString("MM / dd")
        time_txt.text = timeMillis.format2DateString("HH:mm")
    }
}