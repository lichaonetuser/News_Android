package com.mynews.app.news.page.mvp.layer.main.me.profile.edit.name

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.bean.Account
import com.mynews.common.core.CoreApp
import com.mynews.common.core.util.ResUtils
import com.mynews.common.extension.app.mvp.base.MVPBaseFragment
import com.kongzue.dialog.v2.WaitDialog
import kotlinx.android.synthetic.main.fragment_me_profile_edit_name.*

class ProfileEditNameFragment : MVPBaseFragment<ProfileEditNameContract.View,
        ProfileEditNameContract.Presenter<ProfileEditNameContract.View>>(),
        ProfileEditNameContract.View {

    override val mPresenter = ProfileEditNamePresenter()
    override val mLayoutRes = R.layout.fragment_me_profile_edit_name
    override var mDispatchBack = true
    override val mAttachSwipeBack = true

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        done_btn.setOnClickListener {
            mPresenter.onClickDone(user_name_etxt.text.toString())
        }
        user_name_etxt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s == null) {
                    return
                }
                done_btn.isEnabled = s.length in 1..20
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        clean_btn.setOnClickListener {
            user_name_etxt.setText("")
        }
    }

    override fun onSupportVisible() {
        super.onSupportVisible()
        notifyStatusBarIsLight()
    }

    override fun setAccount(account: Account) {
        user_name_etxt.setText(account.screenName)
        done_btn.isEnabled = user_name_etxt.text?.length in 1..20
    }

    override fun showProgress() {
        WaitDialog.show(CoreApp.getLastBaseActivityInstance()
                ?: return, ResUtils.getString(R.string.Tip_Loading))
    }

    override fun hideProgress() {
        WaitDialog.dismiss()
    }
}


