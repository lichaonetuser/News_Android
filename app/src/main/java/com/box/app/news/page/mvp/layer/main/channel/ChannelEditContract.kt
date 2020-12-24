package com.box.app.news.page.mvp.layer.main.channel

import com.box.common.extension.app.mvp.loading.MVPLoadingContract

interface ChannelEditContract {

    interface View : MVPLoadingContract.View

    interface Presenter<in V : View> : MVPLoadingContract.Presenter<V>

}