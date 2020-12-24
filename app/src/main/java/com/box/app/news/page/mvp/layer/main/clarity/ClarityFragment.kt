package com.box.app.news.page.mvp.layer.main.clarity

import android.os.Bundle
import android.view.View
import com.box.app.news.R
import com.box.common.core.util.StatusBarUtils
import com.box.common.extension.app.mvp.loading.list.MVPListFragment

class ClarityFragment : MVPListFragment<ClarityContract.View,
        ClarityContract.Presenter<ClarityContract.View>>(),
        ClarityContract.View {
    override val mAttachSwipeBack = true
    override val mPresenter = ClarityPresenter()
    override val mLayoutRes = R.layout.fragment_clarity

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        mCommonAdapter.setEnableLoadMore(false)
    }

    override fun onSupportVisible() {
        super.onSupportVisible()
        StatusBarUtils.notifyStatusBarIsLight(_mActivity)
    }
}


