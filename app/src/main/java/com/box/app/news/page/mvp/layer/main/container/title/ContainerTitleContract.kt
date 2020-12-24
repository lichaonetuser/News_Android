package com.box.app.news.page.mvp.layer.main.container.title

import android.os.Bundle
import androidx.fragment.app.Fragment;
import com.box.common.core.app.fragment.CoreBaseFragment
import com.box.common.extension.app.mvp.base.MVPBaseContract
import com.box.common.extension.app.mvp.loading.list.MVPListContract

interface ContainerTitleContract {

    interface View : MVPBaseContract.View {
        fun loadContainer(fragmentClassName: String, bundle: Bundle)
        fun setTitle(title: String)
        fun setStatusBarIsLight(isLight: Boolean)
    }

    interface Presenter<in V : View> : MVPBaseContract.Presenter<V>

}