package com.mynews.app.news.page.mvp.layer.main.dialog.updateChannel

import android.os.Bundle
import android.view.View
import com.mynews.app.news.R
import com.mynews.common.extension.app.mvp.dialog.MVPDialogFragment
import kotlinx.android.synthetic.main.fragment_dialog_update_channel.*

/**
 *
 */
class UpdateChannelDialogFragment : MVPDialogFragment<UpdateChannelDialogContract.View,
        UpdateChannelDialogContract.Presenter<UpdateChannelDialogContract.View>>(),
        UpdateChannelDialogContract.View {

    override val mPresenter = UpdateChannelDialogPresenter()
    override val mLayoutRes = R.layout.fragment_dialog_update_channel
    override var mEnterType = MVPDialogFragment.Companion.EnterType.CENTER

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        update_channel_cancel.setOnClickListener { mPresenter.onClickSkip() }
        update_channel_ok.setOnClickListener { mPresenter.onClickConfirm() }
    }
}
