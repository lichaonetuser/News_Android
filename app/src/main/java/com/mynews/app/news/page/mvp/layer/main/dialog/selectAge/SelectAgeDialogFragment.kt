package com.mynews.app.news.page.mvp.layer.main.dialog.selectAge

import android.os.Bundle
import android.view.View
import com.mynews.app.news.R
import com.mynews.common.extension.app.mvp.dialog.MVPDialogFragment
import kotlinx.android.synthetic.main.fragment_dialog_select_age.*

/**
 *
 */
class SelectAgeDialogFragment : MVPDialogFragment<SelectAgeDialogContract.View,
        SelectAgeDialogContract.Presenter<SelectAgeDialogContract.View>>(),
        SelectAgeDialogContract.View {

    override val mPresenter = SelectAgeDialogPresenter()
    override val mLayoutRes = R.layout.fragment_dialog_select_age
    override var mEnterType = MVPDialogFragment.Companion.EnterType.BOTTOM

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        confirm_btn.setOnClickListener { mPresenter.onClickConfirm() }
    }

    override fun setAge(ages:ArrayList<String>) {
        select_age_view.setAges(ages)
    }

    override fun getAge() = select_age_view.getSelectIndex()
}
