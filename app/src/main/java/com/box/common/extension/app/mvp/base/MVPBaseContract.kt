package com.box.common.extension.app.mvp.base

import android.app.Activity
import android.os.Bundle
import com.box.common.core.app.fragment.CoreBaseFragment
import me.yokeyword.fragmentation.ISupportFragment
import me.yokeyword.fragmentation.anim.FragmentAnimator

interface MVPBaseContract {

    interface View {

        /**
         * 返回上一层视图
         */
        fun back()

        fun backToPrev()

        fun backToRoot()

        fun setSwipeBackEnable(enable: Boolean)

        fun <T : CoreBaseFragment> backTo(clazz: Class<T>, include: Boolean = false, afterPopTransactionRunnable: Runnable? = null)

        fun <T : CoreBaseFragment> go(clazz: Class<T>, bundle: Bundle? = null, launchMode: Int = ISupportFragment.SINGLETOP)

        fun <T : CoreBaseFragment> goFromParent(clazz: Class<T>, bundle: Bundle? = null, launchMode: Int = ISupportFragment.SINGLETOP)

        fun <T : CoreBaseFragment> goFromRoot(clazz: Class<T>,
                                              bundle: Bundle? = null,
                                              launchMode: Int = ISupportFragment.SINGLETOP,
                                              fragmentAnimator: FragmentAnimator? = null,
                                              hideSelf: Boolean = true)

        fun setFragmentResult(resultCode: Int, bundle: Bundle?)

        /**
         * 将当前页面替换为另一页面，这将销毁当前页面并加载另一页面
         * @param addToBackStack 是否加进回退栈，即新页面是否允许back()
         */
        fun <T : CoreBaseFragment> replaceTo(clazz: Class<T>, addToBackStack: Boolean = false)

        fun showToast(text: String, isLong: Boolean = true)

        fun showToast(textResource: Int, isLong: Boolean = true)

        /**
         * 过期，防止可能引发的内存泄漏问题，除非极端情况,否则建议使用CoreApp.getInstance()
         */
        @Deprecated("尽量使用CoreApp.getInstance()获取全局上下文")
        fun getActivityContext(): Activity // 获取上下文

    }

    interface Presenter<in V : View> {

        /*--------------------*/
        /* Attach/Detach View */
        /*--------------------*/

        /**
         * 绑定视图
         * @param view 视图
         */
        fun attachView(view: V)

        /**
         * 解绑视图
         */
        fun detachView()

        /*------------*/
        /* Life Cycle */
        /*------------*/

        /**
         * Presenter创建
         * 自动化传参已完成，这里可以直接使用Auto字段
         * @param savedState 保存的状态
         */
        fun onCreate(savedState: Bundle?)

        /**
         * Presenter懒加载创建
         * 目前建议只应用在Viewpager中的Fragment的创建回调，其它场景稳定性待验证
         * 自动化传参已完成，这里可以直接使用Auto字段
         * 该方法只在视图初次可见时，或者视图从保存状态恢复后初次可见时调用
         * 调用时进入动画还未开始
         * onActivityCreated() -> onResume() -> onSupportVisible -> onLazyInitView() => onSupportInvisible() -> onPause()
         * @param savedState 保存的状态
         */
        fun onLazyCreate(savedState: Bundle?)

        /**
         * 进入动画结束，用户可以开始控制页面
         */
        fun onEnterEnd()

        /**
         * 进返回动画开始，用户失去页面控制
         */
        fun onBackBegin()

        /**
         * Presenter销毁
         */
        fun onDestroy()

        /*------------*/
        /* State Save */
        /*------------*/

        /**
         * 状态保存
         * @param outState 状态包
         */
        fun onSave(outState: Bundle)

        /**
         * 状态恢复
         * 只有确实是从保存状态中恢复才会调用
         * @param saveState 状态包
         */
        fun onRestore(saveState: Bundle)

        /*-----------*/
        /* Arguments */
        /*-----------*/

        /**
         * 创建(Add)时传参（第一次接到参数回调，之后不回调）
         * @param bundle 参数包
         */
        fun onBundle(bundle: Bundle?)

        /**
         * 运行(show)时传参（第二次接到参数之后开始回调）
         * @see CoreBaseFragment.putNewBundle
         * @param bundle 参数包
         */
        fun onNewBundle(bundle: Bundle?)

        /*---------*/
        /* Visible */
        /*---------*/

        fun onViewVisible()

        fun onViewInvisible()

        /*---------*/
        /* Nesting */
        /*---------*/

//        fun onParentEnterEnd()

        /*-----------*/
        /* Analytics */
        /*-----------*/

        fun onAnalyticsPage(pageName: String) : String

    }

}