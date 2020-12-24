package com.box.common.core.app.fragment

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment;
import androidx.customview.widget.ViewDragHelper.STATE_IDLE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.box.common.core.app.activity.CoreBaseActivity
import com.box.common.core.util.StatusBarUtils
import me.yokeyword.fragmentation.ISupportFragment
import me.yokeyword.fragmentation.SupportActivity
import me.yokeyword.fragmentation.SupportFragment
import me.yokeyword.fragmentation.SwipeBackLayout
import me.yokeyword.fragmentation.anim.FragmentAnimator
import me.yokeyword.fragmentation_swipeback.core.ISwipeBackFragment
import me.yokeyword.fragmentation_swipeback.core.SwipeBackFragmentDelegate

abstract class CoreBaseFragment : SupportFragment(), ISwipeBackFragment {

    protected open var mAnalyticsPageName = javaClass.simpleName

    companion object {
        @JvmOverloads
        fun <F : CoreBaseFragment> instantiate(clazz: Class<F>, bundle: Bundle? = null): F {
            try {
                val fragment = clazz.newInstance()
                fragment.arguments = bundle
                return fragment
            } catch (e: Exception) {
                throw Fragment.InstantiationException("Unable to instantiate fragment " + clazz.name
                        + ": make sure class name exists, is public, and has an"
                        + " empty constructor that is public", e)
            }
        }
    }

    private var mIsBacking = false
    protected abstract val mLayoutRes: Int
    protected open val mAttachSwipeBack: Boolean = false

    /*------------*/
    /* LIFE CYCLE */
    /*------------*/

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (mAttachSwipeBack && mSwipeBackDelegate == null) {
            mSwipeBackDelegate = SwipeBackFragmentDelegate(this)
        }
        mSwipeBackDelegate?.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return attachToSwipeBack(inflater.inflate(mLayoutRes, container, false))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mSwipeBackDelegate?.onViewCreated(view, savedInstanceState)
        mSwipeBackDelegate?.setEdgeLevel(SwipeBackLayout.EdgeLevel.MAX)
        mSwipeBackDelegate?.swipeBackLayout?.setScrollThresHold(0.45F)
        mSwipeBackDelegate?.swipeBackLayout?.addSwipeListener(object : SwipeBackLayout.OnSwipeListener {

            private var mScrollPercent: Float = 0F

            override fun onEdgeTouch(oritentationEdgeFlag: Int) {
            }

            override fun onDragScrolled(scrollPercent: Float) {
                mScrollPercent = scrollPercent
            }

            override fun onDragStateChange(state: Int) {
                if (state == STATE_IDLE && mScrollPercent < 1) {
                    mSwipeBackDelegate?.swipeBackLayout?.hiddenFragment()
                }
            }

        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
//        MobclickAgent.onPageStart(mAnalyticsPageName)
    }

    override fun onPause() {
        super.onPause()
//        MobclickAgent.onPageEnd(mAnalyticsPageName)
        if (mNeedHideSoft) {
            hideSoftInput()
        }
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroyView() {
        mSwipeBackDelegate?.onDestroyView()
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        mSwipeBackDelegate?.onDestroyView()
    }

    override fun onDetach() {
        super.onDetach()
    }

    /*-----------*/
    /* ON ACTION */
    /*-----------*/

    override fun onNewBundle(args: Bundle?) {
        super.onNewBundle(args)
    }

    override fun onEnterAnimationEnd(savedInstanceState: Bundle?) {
        super.onEnterAnimationEnd(savedInstanceState)
//        childFragmentManager.fragments
//                .filterIsInstance<CoreBaseFragment>()
//                .forEach { it.onParentEnterAnimationEnd(savedInstanceState) }
    }

    open fun onParentEnterAnimationEnd(savedInstanceState: Bundle?) {

    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        mSwipeBackDelegate?.onHiddenChanged(hidden)
    }

    override fun onSupportVisible() {
        super.onSupportVisible()
    }

    override fun onSupportInvisible() {
        super.onSupportInvisible()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onFragmentResult(requestCode: Int, resultCode: Int, data: Bundle?) {
        super.onFragmentResult(requestCode, resultCode, data)
    }

    /*--------*/
    /* ACTION */
    /*--------*/

    protected open fun back() {
        if (!mIsBacking) {
            mIsBacking = true
            hideSoftInput()
            pop()
        }
    }

    protected fun backTo(classOfTargetFragment: Class<*>, includeTargetFragment: Boolean) {
        if (!mIsBacking) {
            mIsBacking = true
            hideSoftInput()
            popTo(classOfTargetFragment, includeTargetFragment)
        }
    }

    fun callLastChildFragmentBack() {
        popChild()
    }

    override fun start(toFragment: ISupportFragment) {
        start(toFragment, ISupportFragment.SINGLETOP)
    }

    override fun start(toFragment: ISupportFragment, launchMode: Int) {
        super.start(toFragment, launchMode)
    }

    fun start(toFragment: ISupportFragment, launchMode: Int, fragmentAnimator: FragmentAnimator?, hideSelf: Boolean) {
        val transaction = extraTransaction()

        if (fragmentAnimator != null) {
            transaction.setCustomAnimations(fragmentAnimator.enter,
                    fragmentAnimator.popExit,
                    fragmentAnimator.popEnter,
                    fragmentAnimator.exit)
        }

        if (hideSelf) {
            transaction.start(toFragment, launchMode)
        } else {
            transaction.startDontHideSelf(toFragment)
        }
    }

    @JvmOverloads
    fun callActivityRootFragmentStart(toFragment: CoreBaseFragment, launchMode: Int = SINGLETOP, fragmentAnimator: FragmentAnimator? = null, hideSelf: Boolean) {
        if (_mActivity is CoreBaseActivity) {
            (_mActivity as CoreBaseActivity).callRootFragmentStart(toFragment, launchMode, fragmentAnimator, hideSelf)
        }
        //兼容方案
        else if (_mActivity is SupportActivity) {
            val transaction = extraTransaction()
            if (fragmentAnimator != null) {
                transaction.setCustomAnimations(fragmentAnimator.enter,
                        fragmentAnimator.popExit,
                        fragmentAnimator.popEnter,
                        fragmentAnimator.exit)
            }
            if (hideSelf) {
                transaction.start(toFragment, launchMode)
            } else {
                transaction.startDontHideSelf(toFragment)
            }
        }
    }

    @JvmOverloads
    fun callTargetFragmentStart(classOfTargetFragment: Class<CoreBaseFragment>, toFragment: CoreBaseFragment, launchMode: Int = SINGLETOP) {
        findFragment(classOfTargetFragment)?.start(toFragment, launchMode)
    }

    @JvmOverloads
    fun callChildFragmentStart(classOfChildFragment: Class<CoreBaseFragment>, toFragment: CoreBaseFragment, launchMode: Int = SINGLETOP) {
        findChildFragment(classOfChildFragment)?.start(toFragment, launchMode)
    }

    protected fun showPreFragment() {
        val preFragment = preFragment
        if (preFragment is CoreBaseFragment) {
            fragmentManager?.beginTransaction()
                    ?.show(preFragment)
                    ?.commit()
        }
    }

    protected fun hidePreFragment() {
        val preFragment = preFragment
        if (preFragment is CoreBaseFragment) {
            fragmentManager?.beginTransaction()
                    ?.hide(preFragment)
                    ?.commit()
        }
    }

    /*---------------------*/
    /* ACTION - STATUS BAR */
    /*---------------------*/

    fun useTransparentStatusBar() {
        StatusBarUtils.setStatusBarTransparent(_mActivity)
    }

    fun notifyStatusBarIsLight() {
        StatusBarUtils.notifyStatusBarIsLight(_mActivity)
    }

    fun notifyStatusBarIsDark() {
        StatusBarUtils.notifyStatusBarIsDark(_mActivity)
    }

    /*---------------------*/
    /* ACTION - SOFT INPUT */
    /*---------------------*/

    private var mIMM: InputMethodManager? = null
    private var mNeedHideSoft: Boolean = false
    private var mSoftInputRunnable: Runnable? = null

    override fun showSoftInput(targetView: View) {
        showSoftInput(targetView, 120L)
    }

    fun showSoftInput(targetView: View, delay: Long) {
        if (view == null) {
            return
        }
        targetView.requestFocus()
        val focusView = view?.findFocus() ?: return
        initImm()
        mNeedHideSoft = true
        if (mSoftInputRunnable != null) {
            focusView.removeCallbacks(mSoftInputRunnable)
            mSoftInputRunnable = null
        }
        mSoftInputRunnable = Runnable {
            mIMM!!.showSoftInput(targetView, InputMethodManager.SHOW_FORCED)
        }
        targetView.postDelayed(mSoftInputRunnable, delay)
    }

    protected fun showSoftInputImmediate(targetView: View) {
        if (view == null) {
            return
        }
        initImm()
        targetView.requestFocus()
        targetView.requestFocusFromTouch()
        mNeedHideSoft = true
        mIMM!!.showSoftInput(targetView, InputMethodManager.SHOW_FORCED)
        onShowInput()
    }

    /**
     * 当键盘弹起时的回调
     */
    open fun onShowInput() {

    }

    override fun hideSoftInput() {
        if (view == null) {
            return
        }
        val focusView = view?.findFocus() ?: return
        initImm()
        if (mSoftInputRunnable != null) {
            focusView.removeCallbacks(mSoftInputRunnable)
            mSoftInputRunnable = null
        }
        mIMM!!.hideSoftInputFromWindow(focusView.windowToken, 0)
        onHideInput()
    }

    /**
     * 当键盘收起时的回调
     */
    open fun onHideInput() {

    }

    private fun initImm() {
        if (mIMM == null) {
            mIMM = _mActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        }
    }

    /*------------*/
    /* SWIPE BACK */
    /*------------*/

    private var mSwipeBackDelegate: SwipeBackFragmentDelegate? = null

    override fun getSwipeBackLayout(): SwipeBackLayout? {
        return mSwipeBackDelegate?.swipeBackLayout
    }

    override fun attachToSwipeBack(view: View): View {
        return mSwipeBackDelegate?.attachToSwipeBack(view) ?: view
    }

    override fun setEdgeLevel(edgeLevel: SwipeBackLayout.EdgeLevel?) {
        mSwipeBackDelegate?.setEdgeLevel(edgeLevel)
    }

    override fun setEdgeLevel(widthPixel: Int) {
        mSwipeBackDelegate?.setEdgeLevel(widthPixel)
    }

    override fun setParallaxOffset(offset: Float) {
        mSwipeBackDelegate?.setParallaxOffset(offset)
    }

    override fun setSwipeBackEnable(enable: Boolean) {
        mSwipeBackDelegate?.setSwipeBackEnable(enable)
    }

    /*-------*/
    /* TITLE */
    /*-------*/

    open fun getPageTitle(): String {
        return ""
    }

}
