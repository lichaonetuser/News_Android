package com.box.app.news.page.mvp.layer.main.browser

import android.os.Bundle
import com.box.app.news.bean.Share
import com.box.common.core.app.fragment.CoreBaseFragment
import com.box.common.extension.app.mvp.base.MVPBaseContract

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