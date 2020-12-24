package com.box.common.extension.widget.recycler.util

import com.box.common.extension.widget.recycler.item.BaseItem
import io.reactivex.*
import org.reactivestreams.Publisher

fun <Bean> Observable<Bean>.convertBeanToItems(itemFactory: ((Bean) -> List<BaseItem<*, *>>))
        : Observable<List<BaseItem<*, *>>> = compose(RxItemUtils.convertBeanToItems(itemFactory))

fun <Bean> Single<Bean>.convertBeanToItems(itemFactory: ((Bean) -> List<BaseItem<*, *>>))
        : Single<List<BaseItem<*, *>>> = compose(RxItemUtils.convertBeanToItems(itemFactory))

fun <Bean, Beans : List<Bean>> Observable<Beans>.convertBeansToItems(itemFactory: ((Bean) -> BaseItem<*, *>))
        : Observable<List<BaseItem<*, *>>> = compose(RxItemUtils.convertBeansToItems(itemFactory))

fun <Bean, Beans : List<Bean>> Single<Beans>.convertBeansToItems(itemFactory: ((Bean) -> BaseItem<*, *>))
        : Single<List<BaseItem<*, *>>> = compose(RxItemUtils.convertBeansToItems(itemFactory))

object RxItemUtils {

    fun <Bean> convertBeanToItems(itemFactory: ((Bean) -> List<BaseItem<*, *>>)): BeanToItemsTransformer<Bean> {
        return BeanToItemsTransformer(itemFactory)
    }

    class BeanToItemsTransformer<Bean> constructor(private val itemFactory: ((Bean) -> List<BaseItem<*, *>>)) :
            ObservableTransformer<Bean, List<BaseItem<*, *>>>,
            FlowableTransformer<Bean, List<BaseItem<*, *>>>,
            SingleTransformer<Bean, List<BaseItem<*, *>>>,
            MaybeTransformer<Bean, List<BaseItem<*, *>>> {

        override fun apply(upstream: Observable<Bean>): ObservableSource<List<BaseItem<*, *>>> {
            return upstream.map { bean -> itemFactory.invoke(bean) } // 构建Items
        }

        override fun apply(upstream: Flowable<Bean>): Publisher<List<BaseItem<*, *>>> {
            return upstream.map { bean -> itemFactory.invoke(bean) } // 构建Item
        }

        override fun apply(upstream: Single<Bean>): SingleSource<List<BaseItem<*, *>>> {
            return upstream.map { bean -> itemFactory.invoke(bean) } // 构建Item
        }

        override fun apply(upstream: Maybe<Bean>): MaybeSource<List<BaseItem<*, *>>> {
            return upstream.map { bean -> itemFactory.invoke(bean) } // 构建Item
        }
    }


    fun <Bean, Beans : List<Bean>> convertBeansToItems(itemFactory: ((Bean) -> BaseItem<*, *>)): BeansToItemsTransformer<Bean, Beans> {
        return BeansToItemsTransformer(itemFactory)
    }

    class BeansToItemsTransformer<out Bean, Beans : List<Bean>> constructor(private val itemFactory: ((Bean) -> BaseItem<*, *>)) :
            ObservableTransformer<Beans, List<BaseItem<*, *>>>,
            FlowableTransformer<Beans, List<BaseItem<*, *>>>,
            SingleTransformer<Beans, List<BaseItem<*, *>>>,
            MaybeTransformer<Beans, List<BaseItem<*, *>>> {

        override fun apply(upstream: Observable<Beans>): ObservableSource<List<BaseItem<*, *>>> {
            return upstream
                    .concatMapIterable { beans -> beans } // 迭代列表
                    .map { bean -> itemFactory.invoke(bean) } // 构建Item
                    .distinct() // 去重
                    .toList() // 合并为列表
                    .toObservable()
        }

        override fun apply(upstream: Flowable<Beans>): Publisher<List<BaseItem<*, *>>> {
            return upstream
                    .concatMapIterable { beans -> beans } // 迭代列表
                    .map { bean -> itemFactory.invoke(bean) } // 构建Item
                    .distinct() // 去重
                    .toList() // 合并为列表
                    .toFlowable()
        }

        override fun apply(upstream: Single<Beans>): SingleSource<List<BaseItem<*, *>>> {
            return upstream
                    .toObservable()
                    .concatMapIterable { beans -> beans } // 迭代列表
                    .map { bean -> itemFactory.invoke(bean) } // 构建Item
                    .distinct() // 去重
                    .toList() // 合并为列表
        }

        override fun apply(upstream: Maybe<Beans>): MaybeSource<List<BaseItem<*, *>>> {
            return upstream
                    .toObservable()
                    .concatMapIterable { beans -> beans } // 迭代列表
                    .map { bean -> itemFactory.invoke(bean) } // 构建Item
                    .distinct() // 去重
                    .toList() // 合并为列表
                    .toMaybe()
        }
    }
}