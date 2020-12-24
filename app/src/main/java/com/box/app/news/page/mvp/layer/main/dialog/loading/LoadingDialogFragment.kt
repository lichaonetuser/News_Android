package com.box.app.news.page.mvp.layer.main.dialog.loading

import android.graphics.Paint
import android.os.Bundle
import android.view.View
import com.box.app.news.R
import com.box.common.extension.app.mvp.dialog.MVPDialogFragment
import kotlinx.android.synthetic.main.fragment_dialog_login.*

class LoadingDialogFragment : MVPDialogFragment<LoadingDialogContract.View,
        LoadingDialogContract.Presenter<LoadingDialogContract.View>>(),
        LoadingDialogContract.View {

    override val mPresenter = LoadingDialogPresenter()
    override val mLayoutRes = R.layout.fragment_dialog_login
    override var mEnterType = MVPDialogFragment.Companion.EnterType.CENTER

    override fun initView(view: View?, savedInstanceState: Bundle?) {
    }

}


