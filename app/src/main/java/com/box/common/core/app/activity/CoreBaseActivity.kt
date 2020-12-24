package com.box.common.core.app.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment;
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import com.box.common.core.app.fragment.CoreBaseFragment
import com.box.common.core.util.StatusBarUtils
import com.box.common.core.util.TouchEventUtils
import com.bumptech.glide.manager.SupportRequestManagerFragment
import com.umeng.analytics.MobclickAgent
import com.yatatsu.autobundle.AutoBundle
import me.yokeyword.fragmentation.SupportActivity
import me.yokeyword.fragmentation.SupportFragment
import me.yokeyword.fragmentation.anim.FragmentAnimator
import org.jetbrains.anko.inputMethodManager

@Suppress("RedundantOverride")
abstract class CoreBaseActivity : SupportActivity() {
    protected var isUmengEnable: Boolean = true

    private var mIsRestore: Boolean = false
    protected fun isRestore() = mIsRestore
    protected fun isNotRestore() = !mIsRestore
    var autoHideSoftInput = true

    /*------------*/
    /* LIFE CYCLE */
    /*------------*/

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mIsRestore = savedInstanceState != null
        AutoBundle.bind(this)
    }

    override fun onRestart() {
        super.onRestart()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        if (isUmengEnable) {
            MobclickAgent.onResume(this)
        }
    }

    override fun onPause() {
        super.onPause()
        if (isUmengEnable) {
            MobclickAgent.onPause(this)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        AutoBundle.pack(this, outState ?: return)
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    /*-----------*/
    /* ON ACTION */
    /*-----------*/

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        AutoBundle.bind(this, intent ?: return)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    /*--------*/
    /* ACTION */
    /*--------*/

    override fun finish() {
        super.finish()
    }

    fun hideNavigationBar() {
        window.decorView.systemUiVisibility =
                window.decorView.systemUiVisibility or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }

    fun setDecorViewBackgroundColor(color: Int) {
        window.decorView.setBackgroundColor(color)
    }

    /*---------------------*/
    /* ACTION - STATUS BAR */
    /*---------------------*/

    fun useTransparentStatusBar() {
        StatusBarUtils.setStatusBarTransparent(this)
    }

    fun notifyStatusBarIsLight() {
        StatusBarUtils.notifyStatusBarIsLight(this)
    }

    fun notifyStatusBarIsDark() {
        StatusBarUtils.notifyStatusBarIsDark(this)
    }

    /*-----------------*/
    /* FRAGMENT ACTION */
    /*-----------------*/

    fun callRootFragmentStart(toFragment: SupportFragment) {
        callRootFragmentStart(toFragment, SupportFragment.SINGLETOP)
    }

    fun callRootFragmentStart(toFragment: SupportFragment, launchMode: Int, fragmentAnimator: FragmentAnimator? = null, hideSelf: Boolean = true) {
        val fragment = supportFragmentManager.fragments.lastOrNull { it is SupportFragment }
                ?: return
        (fragment as? CoreBaseFragment)?.start(toFragment, launchMode, fragmentAnimator, hideSelf)
    }

    /*----------------------*/
    /* AUTO HIDE SOFT INPUT */
    /*----------------------*/

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (!autoHideSoftInput) {
            return super.dispatchTouchEvent(ev)
        }
        try {
            if (ev.action == MotionEvent.ACTION_DOWN) {
                val currentFocusView = currentFocus
                if (currentFocusView is EditText &&
                        TouchEventUtils.isTouchInViewLocation(currentFocusView, ev)) {
                    inputMethodManager.hideSoftInputFromWindow(currentFocusView.windowToken, 0)
                }
                return super.dispatchTouchEvent(ev)
            }
            return window.superDispatchTouchEvent(ev) || onTouchEvent(ev)
        } catch (e: Exception) {
            return super.dispatchTouchEvent(ev)
        }
    }
}