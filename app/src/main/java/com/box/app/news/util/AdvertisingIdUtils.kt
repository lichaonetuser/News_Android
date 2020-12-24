package com.box.app.news.util

import com.box.app.news.data.DataManager
import com.box.common.core.CoreApp
import com.box.common.core.log.Logger
import com.box.common.core.net.http.HttpManager
import com.box.common.core.rx.schedulers.io
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy

object AdvertisingIdUtils {

    const val HTTP_PARAMS_KEY_ADVERTISING_ID = "atd"

    fun updateAdvertisingIdToHttpParams() {
        Single.fromCallable {
            val advertisingId = AdvertisingIdClient.getAdvertisingIdInfo(CoreApp.getInstance()).id
            DataManager.Local.saveAdvertisingId(advertisingId)
            HttpManager.putCommonParams(HTTP_PARAMS_KEY_ADVERTISING_ID to advertisingId)
        }.io().subscribeBy(
                onSuccess = {
                    Logger.d("update atd success.")
                },
                onError = {
                    Logger.d("update atd fail.")
                }
        )
    }

    /**
     * 获取通用参数用的Pair
     */
    fun getAdvertisingIdPair(): Pair<String, String> {
        return HTTP_PARAMS_KEY_ADVERTISING_ID to DataManager.Local.getAdvertisingId()
    }

    /**
     * 目前等价于从本地获取
     */
    fun getAdvertisingIdFromLocal() = DataManager.Local.getAdvertisingId()

}