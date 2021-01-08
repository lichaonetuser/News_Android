package com.mynews.app.news.event

import com.mynews.app.news.BuildConfig
import com.google.firebase.perf.metrics.AddTrace
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.meta.SubscriberInfoIndex

object EventManager {

    private var sSubscriberInfoIndex: SubscriberInfoIndex? = null

    @AddTrace(name = "InitEventManager", enabled = true)
    fun init(subscriberInfoIndex: SubscriberInfoIndex) {
        sSubscriberInfoIndex = subscriberInfoIndex
    }

    private val mDefaultBus by lazy {
        EventBus.builder()
                .addIndex(sSubscriberInfoIndex)
                .logNoSubscriberMessages(BuildConfig.DEBUG)
                .logSubscriberExceptions(BuildConfig.DEBUG)
                .throwSubscriberException(BuildConfig.DEBUG)
                .build()
    }

    fun register(any: Any) {
        if (!mDefaultBus.isRegistered(any)) {
            mDefaultBus.register(any)
        }
    }

    fun unregister(any: Any) {
        if (mDefaultBus.isRegistered(any)) {
            mDefaultBus.unregister(any)
        }
    }

    fun isRegistered(any: Any): Boolean {
        return mDefaultBus.isRegistered(any)
    }

    fun post(any: Any) {
        mDefaultBus.post(any)
    }

    fun postSticky(any: Any) {
        mDefaultBus.postSticky(any)
    }

    fun <T> getStickyEvent(eventType: Class<T>): T {
        return mDefaultBus.getStickyEvent(eventType)
    }
}
