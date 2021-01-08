package com.mynews.app.news.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * 监听网络状态变化广播
 */
class NetworkConnectChangedReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        NetWorkAccessUtils.updateNetWorkAccessParams()
    }
}