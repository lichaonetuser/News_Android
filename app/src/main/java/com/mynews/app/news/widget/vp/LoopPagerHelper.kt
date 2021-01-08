package com.mynews.app.news.widget.vp

import androidx.viewpager.widget.ViewPager
import android.view.MotionEvent
import com.mynews.common.core.rx.schedulers.computationToMain
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import java.util.concurrent.TimeUnit

class LoopPagerHelper(private val viewPager: ViewPager,
                      var initialDelay: Long, //单位：毫秒
                      var period: Long) {

    var mLoopDisposable: Disposable? = null
    var size: Int = 0

    init {
        @Suppress("ImplicitThis")
        viewPager.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    stopInfiniteScroll()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    startInfiniteScroll()
                }
            }
            return@setOnTouchListener false
        }
    }

    fun startInfiniteScroll() {
        if (mLoopDisposable != null || mLoopDisposable?.isDisposed == false) {
            return
        }
        mLoopDisposable = Observable.interval(initialDelay, period, TimeUnit.MILLISECONDS)
                .onErrorReturn { -1 }
                .computationToMain()
                .subscribeBy(onNext = {
                    viewPager.currentItem++
                }, onError = {
                    stopInfiniteScroll()
                })
    }

    fun stopInfiniteScroll() {
        mLoopDisposable?.dispose()
        mLoopDisposable = null
    }
}