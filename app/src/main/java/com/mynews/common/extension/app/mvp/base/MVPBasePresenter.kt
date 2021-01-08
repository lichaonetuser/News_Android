package com.mynews.common.extension.app.mvp.base

import android.os.Bundle
import androidx.annotation.CallSuper
import com.mynews.common.core.auto.bundle.bridge.Bridge
import com.mynews.common.extension.app.mvp.MVPPresenterEvent
import com.mynews.common.extension.app.mvp.MVPRxLifecycle
import com.trello.rxlifecycle2.LifecycleProvider
import com.trello.rxlifecycle2.LifecycleTransformer
import com.yatatsu.autobundle.AutoBundle
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

abstract class MVPBasePresenter<V : MVPBaseContract.View>
    : MVPBaseContract.Presenter<V>, LifecycleProvider<MVPPresenterEvent> {

    @JvmField
    protected var mView: V? = null
    private var mIsRestore: Boolean = false
    protected fun isRestore() = mIsRestore
    protected fun isNotRestore() = !mIsRestore
    private var mIsVisible: Boolean = false
    protected fun isVisible() = mIsVisible
    protected fun isNotVisible() = !mIsVisible
    protected var mVisibleTime: Long = System.currentTimeMillis()

    /*--------------------*/
    /* Attach/Detach View */
    /*--------------------*/

    override final fun attachView(view: V) {
        this.mView = view
    }

    override final fun detachView() {
        mView = null
    }

    /*------------*/
    /* Life Cycle */
    /*------------*/

    @CallSuper
    override fun onCreate(savedState: Bundle?) {
        mIsRestore = savedState != null
        mLifecycleSubject.onNext(MVPPresenterEvent.CREATE)
    }

    @CallSuper
    override fun onLazyCreate(savedState: Bundle?) {
        mLifecycleSubject.onNext(MVPPresenterEvent.LAZY_CREATE)
    }

    @CallSuper
    override fun onEnterEnd() {
        mLifecycleSubject.onNext(MVPPresenterEvent.ENTER_END)
    }

    @CallSuper
    override fun onBackBegin() {
        mLifecycleSubject.onNext(MVPPresenterEvent.BACK_BEGIN)
    }

    @CallSuper
    override fun onDestroy() {
        mLifecycleSubject.onNext(MVPPresenterEvent.DESTROY)

    }

    /*--------------*/
    /* Rx Lifecycle */
    /*--------------*/

    private val mLifecycleSubject = BehaviorSubject.create<MVPPresenterEvent>()

    override final fun lifecycle(): Observable<MVPPresenterEvent> {
        return mLifecycleSubject.hide()
    }

    override final fun <T> bindUntilEvent(event: MVPPresenterEvent): LifecycleTransformer<T> {
        return MVPRxLifecycle.bindUntilEvent<T, MVPPresenterEvent>(mLifecycleSubject, event)
    }

    override final fun <T> bindToLifecycle(): LifecycleTransformer<T> {
        return MVPRxLifecycle.bindPresenter(mLifecycleSubject)
    }

    /*------------*/
    /* State Save */
    /*------------*/

    override fun onSave(outState: Bundle) {
        Bridge.saveInstanceState(this, outState)
    }

    override fun onRestore(saveState: Bundle) {
        Bridge.restoreInstanceState(this, saveState)
    }

    /*-----------*/
    /* Arguments */
    /*-----------*/

    override fun onBundle(bundle: Bundle?) {
        if (bundle != null) {
            AutoBundle.bind(this, bundle)
        }
    }

    override fun onNewBundle(bundle: Bundle?) {
        if (bundle != null) {
            AutoBundle.bind(this, bundle)
        }
    }

    /*---------*/
    /* Visible */
    /*---------*/

    override fun onViewVisible() {
        mIsVisible = true
        mVisibleTime = System.currentTimeMillis()
    }

    override fun onViewInvisible() {
        mIsVisible = false
    }

    /*---------*/
    /* Nesting */
    /*---------*/

//    override fun onParentEnterEnd() {
//
//    }

    /*-----------*/
    /* Analytics */
    /*-----------*/

    override fun onAnalyticsPage(pageName: String): String {
        return pageName
    }

}