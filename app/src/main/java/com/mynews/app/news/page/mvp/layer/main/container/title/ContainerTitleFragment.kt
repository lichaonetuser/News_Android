package com.mynews.app.news.page.mvp.layer.main.container.title

import android.os.Bundle
import android.view.View
import com.mynews.app.news.R
import com.mynews.common.core.app.fragment.CoreBaseFragment
import com.mynews.common.core.util.StatusBarUtils
import com.mynews.common.extension.app.mvp.base.MVPBaseFragment
import kotlinx.android.synthetic.main.core_widget_title_bar.view.*
import kotlinx.android.synthetic.main.fragment_container_title.*

class ContainerTitleFragment : MVPBaseFragment<ContainerTitleContract.View,
        ContainerTitleContract.Presenter<ContainerTitleContract.View>>(),
        ContainerTitleContract.View {

    override val mAttachSwipeBack = true
    override val mPresenter = ContainerTitlePresenter()
    override val mLayoutRes = R.layout.fragment_container_title

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        title_bar.back_btn.setOnClickListener {
            back()
        }
    }

    override fun loadContainer(fragmentClassName: String, bundle: Bundle) {
        val fragment: CoreBaseFragment = Class.forName(fragmentClassName).newInstance() as? CoreBaseFragment
                ?: return
        fragment.arguments = bundle
        loadRootFragment(R.id.container_layout, fragment)
    }

    override fun setTitle(title: String) {
        title_bar.setTitle(title)
    }

    override fun setStatusBarIsLight(isLight: Boolean) {
        if (isLight) {
            StatusBarUtils.notifyStatusBarIsLight(_mActivity)
        } else {
            StatusBarUtils.notifyStatusBarIsDark(_mActivity)
        }
    }
}


