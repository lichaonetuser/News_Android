package com.box.app.news.page.mvp.layer.main.dialog.selectSex

import android.os.Bundle
import android.view.View
import com.box.app.news.R
import com.box.common.extension.app.mvp.dialog.MVPDialogFragment
import kotlinx.android.synthetic.main.fragment_dialog_select_sex.*

/**
 *
 */
class SelectSexDialogFragment : MVPDialogFragment<SelectSexDialogContract.View,
        SelectSexDialogContract.Presenter<SelectSexDialogContract.View>>(),
        SelectSexDialogContract.View {

    override val mPresenter = SelectSexDialogPresenter()
    override val mLayoutRes = R.layout.fragment_dialog_select_sex
    override var mEnterType = MVPDialogFragment.Companion.EnterType.BOTTOM

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        select_sex_male_text.setOnClickListener { mPresenter.onMaleSelect() }
        select_sex_female_text.setOnClickListener { mPresenter.onFemaleSelect() }
    }
}

