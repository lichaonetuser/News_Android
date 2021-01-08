package com.mynews.app.news.page.mvp.layer.main.dialog.login

import android.graphics.Paint
import android.os.Bundle
import android.view.View
import com.mynews.app.news.R
import com.mynews.common.extension.app.mvp.dialog.MVPDialogFragment
import kotlinx.android.synthetic.main.fragment_dialog_login.*

class LoginDialogFragment : MVPDialogFragment<LoginDialogContract.View,
        LoginDialogContract.Presenter<LoginDialogContract.View>>(),
        LoginDialogContract.View {

    override val mPresenter = LoginDialogPresenter()
    override val mLayoutRes = R.layout.fragment_dialog_login
    override var mEnterType = MVPDialogFragment.Companion.EnterType.CENTER

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        agreement_two_txt.paint.flags = Paint.UNDERLINE_TEXT_FLAG
        agreement_two_txt.paint.isAntiAlias = true
        login_facebook_btn.setOnClickListener {
            mPresenter.onClickLoginFacebook()
        }
        login_twitter_btn.setOnClickListener {
            mPresenter.onClickLoginTwitter()
        }
        login_google_btn.setOnClickListener {
            mPresenter.onClickLoginGoogle()
        }
        agreement_one_txt.setOnClickListener {
            mPresenter.onClickAgreement()
        }
        agreement_two_txt.setOnClickListener {
            mPresenter.onClickAgreement()
        }
        close_btn.setOnClickListener {
            mPresenter.onClickClose()
        }
    }

}


