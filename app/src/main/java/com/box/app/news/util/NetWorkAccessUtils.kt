package com.box.app.news.util

import com.box.app.news.data.DataDictionary
import com.box.common.core.net.http.HttpManager
import com.box.common.core.util.NetworkUtils

/**
 * 监听网络状态的工具，并修改网络通参的工具
 */
object NetWorkAccessUtils {

    private const val KEY = "access"

    fun getNetWorkAccess(): Pair<String, String> {
        return KEY to when (NetworkUtils.getNetworkType().value){
            4 -> DataDictionary.NetworkAccess._4G
            3 -> DataDictionary.NetworkAccess._3G
            2 -> DataDictionary.NetworkAccess._2G
            1 -> DataDictionary.NetworkAccess.WiFi
            else -> DataDictionary.NetworkAccess.unknown
        }
    }

    fun updateNetWorkAccessParams() {
        HttpManager.putCommonParams(KEY to when (NetworkUtils.getNetworkType().value){
            4 -> DataDictionary.NetworkAccess._4G
            3 -> DataDictionary.NetworkAccess._3G
            2 -> DataDictionary.NetworkAccess._2G
            1 -> DataDictionary.NetworkAccess.WiFi
            else -> DataDictionary.NetworkAccess.unknown })
    }
}