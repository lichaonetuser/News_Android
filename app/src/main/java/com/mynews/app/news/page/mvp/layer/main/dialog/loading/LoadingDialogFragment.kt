package com.mynews.app.news.page.mvp.layer.main.dialog.loading

import android.os.Bundle
import android.view.View
import com.mynews.app.news.R
import com.mynews.common.extension.app.mvp.dialog.MVPDialogFragment

class LoadingDialogFragment : MVPDialogFragment<LoadingDialogContract.View,
        LoadingDialogContract.Presenter<LoadingDialogContract.View>>(),
        LoadingDialogContract.View {

    override val mPresenter = LoadingDialogPresenter()
    override val mLayoutRes = R.layout.fragment_dialog_login
    override var mEnterType = MVPDialogFragment.Companion.EnterType.CENTER

    override fun initView(view: View?, savedInstanceState: Bundle?) {
    }

}


