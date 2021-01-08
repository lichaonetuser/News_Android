package com.mynews.common.extension.app.mvp.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import android.widget.RelativeLayout
import com.mynews.app.news.R
import com.mynews.common.core.CoreApp
import com.mynews.common.extension.app.mvp.base.MVPBaseFragment
import com.mynews.common.extension.app.mvp.dialog.MVPDialogFragment.Companion.EnterType.*
import me.yokeyword.fragmentation.anim.FragmentAnimator

abstract class MVPDialogFragment<in V : MVPDialogContract.View,
        out P : MVPDialogContract.Presenter<V>>
    : MVPBaseFragment<V, P>(), MVPDialogContract.View {

    override var mDispatchBack = true

    companion object {
        enum class EnterType { CENTER, BOTTOM, TOP }

        private val MASK_DEFAULT_COLOR_RES = android.R.color.black
        private val MASK_DEFAULT_DURATION = 230
        private val MASK_DEFAULT_INTERPOLATOR: Interpolator = LinearInterpolator()
        private val CONTENT_DEFAULT_DURATION = 250
        private val CONTENT_DEFAULT_INTERPOLATOR: Interpolator = DecelerateInterpolator(2.5f)
    }

    protected open val mEnterType: EnterType = CENTER
    protected open var maskColorRes: Int = MASK_DEFAULT_COLOR_RES
    protected open var maskEnterDuration: Int = MASK_DEFAULT_DURATION
    protected open var maskEnterInterpolator: Interpolator = MASK_DEFAULT_INTERPOLATOR
    protected open val contentEnterDuration: Int = CONTENT_DEFAULT_DURATION
    protected open val contentEnterInterpolator: Interpolator = CONTENT_DEFAULT_INTERPOLATOR

    private lateinit var mMaskView: View
    private lateinit var mContentView: View
    private var mIsAnim: Boolean = false
    private var mFinishListener: Animation.AnimationListener? = null
    private lateinit var mPreFragmentAnimator: FragmentAnimator

    /*------------*/
    /* LIFE CYCLE */
    /*------------*/

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mPreFragmentAnimator = preFragment.fragmentAnimator
        preFragment.fragmentAnimator = FragmentAnimator(
                R.anim.no_anim,
                R.anim.no_anim,
                R.anim.no_anim,
                R.anim.no_anim)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return attachMaskView(super.onCreateView(inflater, container, savedInstanceState))
    }

    private fun attachMaskView(contentView: View): ViewGroup {
        mContentView = contentView
        mMaskView = View(_mActivity)
        mMaskView.isClickable = true

        val layout = RelativeLayout(_mActivity)
        layout.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layout.addView(mMaskView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layout.addView(mContentView)
        val params = mContentView.layoutParams as RelativeLayout.LayoutParams
        when (mEnterType) {
            CENTER -> params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
            BOTTOM -> params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            TOP -> params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)
        }
        mContentView.layoutParams = params
        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startContentAnimation(savedInstanceState, false)
        startMaskAnimation(false)
    }

    final override fun onEnterAnimationEnd(savedInstanceState: Bundle?) {} //转为动画结束时调用

    protected open fun onEnterAnimationEnd(savedInstanceState: Bundle?, flag: Int) {
        mPresenter.onEnterEnd()
    }

    override fun onResume() {
        super.onResume()
        showPreFragment()
    }

    /*------*/
    /* MASK */
    /*------*/

    protected fun showMask(): Boolean {
        return true
    }

    protected fun canClickMask(): Boolean {
        return true
    }

    private fun startMaskAnimation(reverse: Boolean) {
        if (showMask()) {
            val animation: AlphaAnimation
            if (reverse) {
                animation = AlphaAnimation(0.60f, 0f)
            } else {
                animation = AlphaAnimation(0f, 0.60f)
            }
            animation.fillAfter = true
            animation.duration = maskEnterDuration.toLong()
            animation.interpolator = maskEnterInterpolator
            mMaskView.setBackgroundColor(CoreApp.getInstance().resources.getColor(maskColorRes))
            mMaskView.startAnimation(animation)
        } else {
            mMaskView.setBackgroundColor(CoreApp.getInstance().resources.getColor(R.color.transparent))
            mMaskView.isClickable = canClickMask()
        }
    }

    /*---------*/
    /* CONTENT */
    /*---------*/

    private fun startContentAnimation(savedInstanceState: Bundle?, reverse: Boolean) {
        if (mIsAnim) {
            return
        }
        mIsAnim = true
        when (mEnterType) {
            CENTER -> {
                mContentView.visibility = View.INVISIBLE
                val defaultAnim: Animation
                if (reverse) {
                    defaultAnim = AlphaAnimation(1.0f, 0.0f)
                } else {
                    defaultAnim = AlphaAnimation(0.0f, 1.0f)
                }
                runContentAnimation(defaultAnim, savedInstanceState, reverse)
            }
            BOTTOM -> {
                val bottomAnim: Animation
                if (reverse) {
                    bottomAnim = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f)
                } else {
                    bottomAnim = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                            Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0f)
                }
                runContentAnimation(bottomAnim, savedInstanceState, reverse)
            }
            TOP -> {
                val topAnim: Animation
                if (reverse) {
                    topAnim = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                            Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f)
                } else {
                    topAnim = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f)
                }
                runContentAnimation(topAnim, savedInstanceState, reverse)
            }
            else -> throw AssertionError("a unknown mode")
        }
    }

    private fun runContentAnimation(animation: Animation, savedInstanceState: Bundle?, reverse: Boolean) {
        animation.interpolator = contentEnterInterpolator
        animation.duration = contentEnterDuration.toLong()
        animation.fillAfter = true
        if (reverse) {
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {

                }

                override fun onAnimationEnd(animation: Animation) {
                    if (mFinishListener != null) {
                        mFinishListener!!.onAnimationEnd(animation)
                        return
                    }

                    pop()
                    preFragment?.fragmentAnimator = mPreFragmentAnimator
                    mIsAnim = false
                }

                override fun onAnimationRepeat(animation: Animation) {

                }
            })
        } else {
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    mContentView.visibility = View.VISIBLE
                }

                override fun onAnimationEnd(animation: Animation) {
                    mMaskView.setOnClickListener {
                        back()
                    }
                    mContentView.clearAnimation()
                    onEnterAnimationEnd(savedInstanceState, 0)
                    mIsAnim = false
                }

                override fun onAnimationRepeat(animation: Animation) {

                }
            })
        }
        mContentView.startAnimation(animation)
    }

    /*-----------------*/
    /* TRANSITION ANIM */
    /*-----------------*/

    override fun onCreateFragmentAnimator(): FragmentAnimator {
        return FragmentAnimator(R.anim.no_anim, R.anim.no_anim)
    }

    /*------------*/
    /* BACK LOGIC */
    /*------------*/

    override fun back() {
        if (mIsAnim) {
            return
        }
        onBackBegin()
        startMaskAnimation(true)
        startContentAnimation(null, true)
    }

//    fun startFromRootWithFinish(toFragment: CoreBaseFragment) {
//        startFromRootWithFinish(null, toFragment)
//    }
//
//    fun startFromRootWithFinish(fragmentClass: Class<CoreBaseFragment>?, toFragment: CoreBaseFragment) {
//        mFinishListener = object : Animation.AnimationListener {
//            override fun onAnimationStart(animation: Animation) {
//
//            }
//
//            override fun onAnimationEnd(animation: Animation) {
//                pop()
//                if (fragmentClass == null) {
//                    callActivityRootFragmentStart(toFragment)
//                } else {
//                    callTargetFragmentStart(fragmentClass, toFragment)
//                }
//                mIsAnim = false
//            }
//
//            override fun onAnimationRepeat(animation: Animation) {
//
//            }
//        }
//        pop()
//    }

}
