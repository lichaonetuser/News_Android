package com.mynews.common.extension.app.mvp.loading

import com.mynews.common.extension.app.mvp.base.MVPBasePresenter

abstract class MVPLoadingPresent<V : MVPLoadingContract.View>
    : MVPBasePresenter<V>(), MVPLoadingContract.Presenter<V> {

    override fun onLoadingLayoutRetryClicked(id: Int) {

    }

}