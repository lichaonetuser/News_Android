package com.mynews.app.news.page.mvp.layer.main.me.lgoin

import android.os.Bundle
import android.view.View
import com.mynews.app.news.R
import com.mynews.common.core.util.ResUtils
import com.mynews.common.extension.app.mvp.base.MVPBaseFragment
import kotlinx.android.synthetic.main.fragment_me_login.*
import lt.neworld.spanner.Spanner
import lt.neworld.spanner.Spans

class LoginFragment : MVPBaseFragment<LoginContract.View,
        LoginContract.Presenter<LoginContract.View>>(),
        LoginContract.View {
    override val mPresenter = LoginPresenter()
    override val mLayoutRes = R.layout.fragment_me_login
    override var mDispatchBack = false

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        agreement_txt.text = Spanner()
                .append("${ResUtils.getString(R.string.Setting_LoginEnjoyRecom)}")
                .append("${ResUtils.getString(R.string.Setting_LoginAgreementPartOne)}-->")
                .append(ResUtils.getString(R.string.Setting_LoginAgreementPartTwo),
                        Spans.foreground(ResUtils.getColor(R.color.color_11)))
        agreement_txt.setOnClickListener {
            mPresenter.onClickAgreement()
        }
        login_facebook_btn.setOnClickListener({
            mPresenter.onClickFacebook()
        })
        login_twitter_btn.setOnClickListener({
            mPresenter.onClickTwitter()
        })
        login_google_btn.setOnClickListener({
            mPresenter.onClickGoogle()
        })
    }

}


