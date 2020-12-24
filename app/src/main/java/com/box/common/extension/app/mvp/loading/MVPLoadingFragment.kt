package com.box.common.extension.app.mvp.loading

import android.os.Bundle
import android.view.View
import com.box.app.news.R
import com.box.common.extension.app.mvp.base.MVPBaseFragment
import com.box.common.extension.widget.layout.loading.LoadingLayout
import org.jetbrains.anko.support.v4.findOptional

abstract class MVPLoadingFragment<in V : MVPLoadingContract.View,
        out P : MVPLoadingContract.Presenter<V>>
    : MVPBaseFragment<V, P>(), MVPLoadingContract.View {

    protected val mLoadingLayout: LoadingLayout? by lazy {
        findOptional<LoadingLayout>(R.id.loading_layout)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initLoadingLayout()
        super.onViewCreated(view, savedInstanceState)
    }

    protected fun initLoadingLayout() {
        mLoadingLayout?.onRetryClick { id -> mPresenter.onLoadingLayoutRetryClicked(id) }
    }

    override fun showEmpty(message: String) {
        mLoadingLayout?.showEmpty()
    }

    override fun showLoading(message: String) {
        mLoadingLayout?.showLoading()
    }

    override fun showFail(message: String) {
        mLoadingLayout?.showFail()
    }

    override fun showContent(message: String) {
        mLoadingLayout?.showContent()
    }
}