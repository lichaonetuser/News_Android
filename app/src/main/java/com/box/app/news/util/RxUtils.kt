package com.box.app.news.util

import com.box.app.news.bean.base.BaseListBean
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import io.reactivex.*
import org.reactivestreams.Publisher


fun <ListBean : BaseListBean<*>> Observable<ListBean>.checkHasMore(adapter: CommonRecyclerAdapter?)
        : Observable<ListBean> = compose(RxUtils.CheckHasMoreTransformer(adapter))

//fun <T> Observable<T>.assign(any: T?)
//        : Observable<T> = compose(RxUtils.AssignTransformer(any))

object RxUtils {

    fun <ListBean : BaseListBean<*>> checkHasMore(adapter: CommonRecyclerAdapter?): CheckHasMoreTransformer<ListBean> {
        return CheckHasMoreTransformer(adapter)
    }

    class CheckHasMoreTransformer<ListBean : BaseListBean<*>>(private val mAdapter: CommonRecyclerAdapter?) :
            ObservableTransformer<ListBean, ListBean>,
            FlowableTransformer<ListBean, ListBean>,
            SingleTransformer<ListBean, ListBean>,
            MaybeTransformer<ListBean, ListBean> {

        override fun apply(upstream: Observable<ListBean>): ObservableSource<ListBean> {
            return upstream.doOnNext({ listBean ->
                mAdapter?.setEnableLoadMore(listBean.hasMore)
            })
        }

        override fun apply(upstream: Flowable<ListBean>): Publisher<ListBean> {
            return upstream.doOnNext({ listBean ->
                mAdapter?.setEnableLoadMore(listBean.hasMore)
            })
        }

        override fun apply(upstream: Single<ListBean>): SingleSource<ListBean> {
            return upstream.doOnSuccess({ listBean ->
                mAdapter?.setEnableLoadMore(listBean.hasMore)
            })
        }

        override fun apply(upstream: Maybe<ListBean>): MaybeSource<ListBean> {
            return upstream.doOnSuccess({ listBean ->
                mAdapter?.setEnableLoadMore(listBean.hasMore)
            })
        }
    }

//    fun <T> assign(any: T?): AssignTransformer<T> {
//        return AssignTransformer(any)
//    }
//
//    class AssignTransformer<T>(private var any: T?) :
//            ObservableTransformer<T, T>,
//            FlowableTransformer<T, T>,
//            SingleTransformer<T, T>,
//            MaybeTransformer<T, T> {
//
//        override fun apply(upstream: Observable<T>): ObservableSource<T> {
//            return upstream.map { it ->
//                any = it
//                it
//            }
//        }
//
//        override fun apply(upstream: Flowable<T>): Publisher<T> {
//            return upstream.map { it ->
//                any = it
//                it
//            }
//        }
//
//        override fun apply(upstream: Single<T>): SingleSource<T> {
//            return upstream.map { it ->
//                any = it
//                it
//            }
//        }
//
//        override fun apply(upstream: Maybe<T>): MaybeSource<T> {
//            return upstream.map { it ->
//                any = it
//                it
//            }
//        }
//    }

}