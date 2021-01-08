package com.mynews.common.extension.app.mvp.loading.browser

import com.mynews.common.extension.app.mvp.loading.MVPLoadingPresent

abstract class MVPBrowserPresent<V : MVPBrowserContract.View>
    : MVPLoadingPresent<V>(), MVPBrowserContract.Presenter<V> {

    override fun onLoadingLayoutRetryClicked(id: Int) {
        mView?.reloadURL()
    }

    override fun onWebPageStarted(url: String?) {
    }

    override fun onWebPageFinished(url: String?) {
    }

    override fun onWebReceivedError(errorCode: Int, description: CharSequence?, failingUrl: String?) : Boolean {
        return false
    }

    override fun onProgressChanged(newProgress: Int) {
    }
}