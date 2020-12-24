package com.box.common.core.rx.schedulers

import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Publisher

object RxSchedulersUtils {

    fun <T> io_io(): SchedulersTransformer<T> {
        return SchedulersTransformer(Schedulers.io(), Schedulers.io())
    }

    fun <T> io_main(): SchedulersTransformer<T> {
        return SchedulersTransformer(Schedulers.io(), AndroidSchedulers.mainThread())
    }

    fun <T> newThread_main(): SchedulersTransformer<T> {
        return SchedulersTransformer(Schedulers.newThread(), AndroidSchedulers.mainThread())
    }

    fun <T> computation_main(): SchedulersTransformer<T> {
        return SchedulersTransformer(Schedulers.computation(), AndroidSchedulers.mainThread())
    }

    class SchedulersTransformer<T> constructor(private val subscribeSchedulers: Scheduler,
                                               private val observeSchedulers: Scheduler)
        : ObservableTransformer<T, T>,
            FlowableTransformer<T, T>,
            SingleTransformer<T, T>,
            MaybeTransformer<T, T>,
            CompletableTransformer {

        override fun apply(upstream: Flowable<T>): Publisher<T> {
            return upstream
                    .subscribeOn(subscribeSchedulers)
                    .observeOn(observeSchedulers)
        }

        override fun apply(upstream: Maybe<T>): MaybeSource<T> {
            return upstream
                    .subscribeOn(subscribeSchedulers)
                    .observeOn(observeSchedulers)
        }

        override fun apply(upstream: Observable<T>): ObservableSource<T> {
            return upstream
                    .subscribeOn(subscribeSchedulers)
                    .observeOn(observeSchedulers)
        }

        override fun apply(upstream: Single<T>): SingleSource<T> {
            return upstream
                    .subscribeOn(subscribeSchedulers)
                    .observeOn(observeSchedulers)
        }

        override fun apply(upstream: Completable): CompletableSource {
            return upstream
                    .subscribeOn(subscribeSchedulers)
                    .observeOn(observeSchedulers)
        }

    }

}
