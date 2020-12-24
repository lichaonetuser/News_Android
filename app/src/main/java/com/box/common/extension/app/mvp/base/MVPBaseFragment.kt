package com.box.common.extension.app.mvp.base

import android.app.Activity
import android.os.Bundle
import androidx.annotation.CallSuper
import android.view.View
import com.box.app.news.R
import com.box.common.core.app.fragment.CoreBaseFragment
import me.yokeyword.fragmentation.SupportFragment
import me.yokeyword.fragmentation.anim.FragmentAnimator
import org.jetbrains.anko.support.v4.findOptional
import org.jetbrains.anko.support.v4.longToast
import org.jetbrains.anko.support.v4.toast

abstract class MVPBaseFragment<in V : MVPBaseContract.View,
        out P : MVPBaseContract.Presenter<V>>
    : CoreBaseFragment(), MVPBaseContract.View {

    protected abstract val mPresenter: P
    protected open var mDispatchBack = true

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        findOptional<View>(R.id.back_btn)?.setOnClickListener {
            back()
        }
        super.onViewCreated(view, savedInstanceState)
        initView(view, savedInstanceState)
        @Suppress("UNCHECKED_CAST")
        mPresenter.attachView(this as V)
        mPresenter.onBundle(arguments)
        if (savedInstanceState != null) {
            mPresenter.onRestore(savedInstanceState)
        }
        mPresenter.onCreate(savedInstanceState)
    }

    override fun onLazyInitView(savedInstanceState: Bundle?) {
        super.onLazyInitView(savedInstanceState)
        mPresenter.onLazyCreate(savedInstanceState)
    }

    /**
     * 初始化视图相关操作重写此方法实现，不要在onViewCreated回调操作(虽然我没加final)
     */
    abstract fun initView(view: View?, savedInstanceState: Bundle?)

    override fun onEnterAnimationEnd(savedInstanceState: Bundle?) {
        super.onEnterAnimationEnd(savedInstanceState)
        mPresenter.onEnterEnd()
    }

    override fun onResume() {
        mAnalyticsPageName = mPresenter.onAnalyticsPage(mAnalyticsPageName)
        super.onResume()
    }

    @CallSuper
    override fun onDestroyView() {
        mPresenter.onDestroy()
        mPresenter.detachView()
        super.onDestroyView()
    }

    @CallSuper
    override fun onBackPressedSupport(): Boolean {
        if (mDispatchBack) {
            back()
        }
        return mDispatchBack
    }

    protected fun onBackBegin() {
        mPresenter.onBackBegin()
    }

    override fun onCreateFragmentAnimator(): FragmentAnimator {
        return FragmentAnimator(
                R.anim.core_swipe_in_right,
                R.anim.core_swipe_out_right,
                R.anim.core_swipe_pop_in_right,
                R.anim.core_swipe_pop_out_right)
    }

    /*--------*/
    /* Bundle */
    /*--------*/

    override fun onNewBundle(args: Bundle?) {
        super.onNewBundle(args)
        mPresenter.onNewBundle(args)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mPresenter.onSave(outState)
    }

    /*----------------*/
    /* View Interface */
    /*----------------*/

    override fun <T : CoreBaseFragment> go(clazz: Class<T>, bundle: Bundle?, launchMode: Int) {
        start(CoreBaseFragment.instantiate(clazz, bundle), launchMode)
    }

    override fun <T : CoreBaseFragment> goFromParent(clazz: Class<T>, bundle: Bundle?, launchMode: Int) {
        if (parentFragment is CoreBaseFragment) {
            (parentFragment as CoreBaseFragment).start(CoreBaseFragment.instantiate(clazz, bundle), launchMode)
        }
    }

    override fun <T : CoreBaseFragment> goFromRoot(clazz: Class<T>, bundle: Bundle?, launchMode: Int, fragmentAnimator: FragmentAnimator?, hideSelf: Boolean) {
        callActivityRootFragmentStart(CoreBaseFragment.instantiate(clazz, bundle), launchMode, fragmentAnimator, hideSelf)
    }

    override fun back() {
        onBackBegin()
        pop()
    }

    override fun backToPrev() {
        onBackBegin()
        popTo(this::class.java, true)
    }

    override fun backToRoot() {
        onBackBegin()
        val fragments = _mActivity.supportFragmentManager.fragments
        if (fragments == null || fragments.size < 2) {
            return
        }
        val fragment = fragments.firstOrNull { it is SupportFragment } ?: return
        popTo(fragment::class.java, false)
    }

    override fun <T : CoreBaseFragment> backTo(clazz: Class<T>, include: Boolean, afterPopTransactionRunnable: Runnable?) {
        onBackBegin()
        if (afterPopTransactionRunnable == null) {
            popTo(clazz, include)
        } else {
            popTo(clazz, include, afterPopTransactionRunnable)
        }
    }

    override fun <T : CoreBaseFragment> replaceTo(clazz: Class<T>, addToBackStack: Boolean) {
        replaceFragment(CoreBaseFragment.instantiate(clazz), addToBackStack)
    }

    override fun showToast(text: String, isLong: Boolean) {
        if (isLong) longToast(text) else toast(text)
    }

    override fun showToast(textResource: Int, isLong: Boolean) {
        if (isLong) longToast(textResource) else toast(textResource)
    }

    @Suppress("OverridingDeprecatedMember")
    override fun getActivityContext(): Activity {
        return _mActivity
    }

    /*---------*/
    /* Visible */
    /*---------*/

    override fun onSupportVisible() {
        super.onSupportVisible()
        mPresenter.onViewVisible()
    }

    override fun onSupportInvisible() {
        super.onSupportInvisible()
        mPresenter.onViewInvisible()
    }

    /*---------*/
    /* Nesting */
    /*---------*/

    override fun onParentEnterAnimationEnd(savedInstanceState: Bundle?) {
        super.onParentEnterAnimationEnd(savedInstanceState)
//        mPresenter.onParentEnterEnd()
    }

}