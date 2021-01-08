package com.mynews.app.news.page.mvp.layer.main.channel

import com.mynews.common.extension.app.mvp.loading.MVPLoadingContract

interface ChannelEditContract {

    interface View : MVPLoadingContract.View

    interface Presenter<in V : View> : MVPLoadingContract.Presenter<V>

}