package com.mynews.app.news.page.mvp.layer.main.browser

import android.os.Bundle
import com.mynews.app.news.bean.Share
import com.mynews.common.core.app.fragment.CoreBaseFragment
import com.mynews.common.extension.app.mvp.base.MVPBaseContract

interface BrowserContract {

    interface View : MVPBaseContract.View {

        fun <F : CoreBaseFragment> loadContainer(clazz: Class<F>, bundle: Bundle)
        fun setTitle(title: String)
        fun setShare(share: Share?)

    }

    interface Presenter<in V : View> : MVPBaseContract.Presenter<V> {
        fun onClickShare(share: Share)
    }
}