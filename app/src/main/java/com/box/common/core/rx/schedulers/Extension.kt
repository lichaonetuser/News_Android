package com.box.common.core.rx.schedulers

import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

fun <R> Observable<R>.io(): Observable<R> = subscribeOn(Schedulers.io())

fun <R> Observable<R>.newThread(): Observable<R> = subscribeOn(Schedulers.newThread())

fun <R> Observable<R>.computation(): Observable<R> = subscribeOn(Schedulers.computation())

fun <R> Observable<R>.main(): Observable<R> = subscribeOn(AndroidSchedulers.mainThread())

fun <R> Observable<R>.trampoline(): Observable<R> = subscribeOn(Schedulers.trampoline())

fun <R> Observable<R>.obOnIo(): Observable<R> = observeOn(Schedulers.io())

fun <R> Observable<R>.obOnNewThread(): Observable<R> = observeOn(Schedulers.newThread())

fun <R> Observable<R>.obOnComputation(): Observable<R> = observeOn(Schedulers.computation())

fun <R> Observable<R>.obOnMain(): Observable<R> = observeOn(AndroidSchedulers.mainThread())

fun <R> Observable<R>.obOnTrampoline(): Observable<R> = observeOn(Schedulers.trampoline())

fun <R> Observable<R>.ioToMain(): Observable<R> = compose(RxSchedulersUtils.io_main())

fun <R> Observable<R>.computationToMain(): Observable<R> = compose(RxSchedulersUtils.computation_main())

fun <R> Observable<R>.newThreadToMain(): Observable<R> = compose(RxSchedulersUtils.newThread_main())


fun <R> Single<R>.io(): Single<R> = subscribeOn(Schedulers.io())

fun <R> Single<R>.newThread(): Single<R> = subscribeOn(Schedulers.newThread())

fun <R> Single<R>.computation(): Single<R> = subscribeOn(Schedulers.computation())

fun <R> Single<R>.main(): Single<R> = subscribeOn(AndroidSchedulers.mainThread())

fun <R> Single<R>.obOnIo(): Single<R> = observeOn(Schedulers.io())

fun <R> Single<R>.obOnNewThread(): Single<R> = observeOn(Schedulers.newThread())

fun <R> Single<R>.obOnComputation(): Single<R> = observeOn(Schedulers.computation())

fun <R> Single<R>.obOnMain(): Single<R> = observeOn(AndroidSchedulers.mainThread())

fun <R> Single<R>.ioToMain(): Single<R> = compose(RxSchedulersUtils.io_main())

fun <R> Single<R>.computationToMain(): Single<R> = compose(RxSchedulersUtils.computation_main())

fun <R> Single<R>.newThreadToMain(): Single<R> = compose(RxSchedulersUtils.newThread_main())


fun <R> Flowable<R>.io(): Flowable<R> = subscribeOn(Schedulers.io())

fun <R> Flowable<R>.newThread(): Flowable<R> = subscribeOn(Schedulers.newThread())

fun <R> Flowable<R>.computation(): Flowable<R> = subscribeOn(Schedulers.computation())

fun <R> Flowable<R>.main(): Flowable<R> = subscribeOn(AndroidSchedulers.mainThread())

fun <R> Flowable<R>.obOnIo(): Flowable<R> = observeOn(Schedulers.io())

fun <R> Flowable<R>.obOnNewThread(): Flowable<R> = observeOn(Schedulers.newThread())

fun <R> Flowable<R>.obOnComputation(): Flowable<R> = observeOn(Schedulers.computation())

fun <R> Flowable<R>.obOnMain(): Flowable<R> = observeOn(AndroidSchedulers.mainThread())

fun <R> Flowable<R>.ioToMain(): Flowable<R> = compose(RxSchedulersUtils.io_main())

fun <R> Flowable<R>.computationToMain(): Flowable<R> = compose(RxSchedulersUtils.computation_main())

fun <R> Flowable<R>.newThreadToMain(): Flowable<R> = compose(RxSchedulersUtils.newThread_main())


fun <R> Maybe<R>.io(): Maybe<R> = subscribeOn(Schedulers.io())

fun <R> Maybe<R>.newThread(): Maybe<R> = subscribeOn(Schedulers.newThread())

fun <R> Maybe<R>.computation(): Maybe<R> = subscribeOn(Schedulers.computation())

fun <R> Maybe<R>.main(): Maybe<R> = subscribeOn(AndroidSchedulers.mainThread())

fun <R> Maybe<R>.obOnIo(): Maybe<R> = observeOn(Schedulers.io())

fun <R> Maybe<R>.obOnNewThread(): Maybe<R> = observeOn(Schedulers.newThread())

fun <R> Maybe<R>.obOnComputation(): Maybe<R> = observeOn(Schedulers.computation())

fun <R> Maybe<R>.obOnMain(): Maybe<R> = observeOn(AndroidSchedulers.mainThread())

fun <R> Maybe<R>.ioToMain(): Maybe<R> = compose(RxSchedulersUtils.io_main())

fun <R> Maybe<R>.computationToMain(): Maybe<R> = compose(RxSchedulersUtils.computation_main())

fun <R> Maybe<R>.newThreadToMain(): Maybe<R> = compose(RxSchedulersUtils.newThread_main())


fun Completable.io(): Completable = subscribeOn(Schedulers.io())

fun Completable.newThread(): Completable = subscribeOn(Schedulers.newThread())

fun Completable.computation(): Completable = subscribeOn(Schedulers.computation())

fun Completable.main(): Completable = subscribeOn(AndroidSchedulers.mainThread())

fun Completable.obOnIo(): Completable = observeOn(Schedulers.io())

fun Completable.obOnNewThread(): Completable = observeOn(Schedulers.newThread())

fun Completable.obOnComputation(): Completable = observeOn(Schedulers.computation())

fun Completable.obOnMain(): Completable = observeOn(AndroidSchedulers.mainThread())

fun <R> Completable.ioToMain(): Completable = compose(RxSchedulersUtils.io_main<R>())

fun <R> Completable.computationToMain(): Completable = compose(RxSchedulersUtils.computation_main<R>())

fun <R> Completable.newThreadToMain(): Completable = compose(RxSchedulersUtils.newThread_main<R>())
