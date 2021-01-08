package com.mynews.common.extension.app.mvp

import com.trello.rxlifecycle2.LifecycleProvider
import io.reactivex.Observable
import io.reactivex.Single


fun <R> Observable<R>.bindToLifecycle(provider: LifecycleProvider<MVPPresenterEvent>)
        : Observable<R> = compose(provider.bindToLifecycle())

fun <R> Observable<R>.bindUntilEvent(provider: LifecycleProvider<MVPPresenterEvent>, event: MVPPresenterEvent)
        : Observable<R> = compose(provider.bindUntilEvent(event))

fun <R> Single<R>.bindToLifecycle(provider: LifecycleProvider<MVPPresenterEvent>)
        : Single<R> = compose(provider.bindToLifecycle())

fun <R> Single<R>.bindUntilEvent(provider: LifecycleProvider<MVPPresenterEvent>, event: MVPPresenterEvent)
        : Single<R> = compose(provider.bindUntilEvent(event))