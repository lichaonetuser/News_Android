package com.mynews.app.news.page.mvp.layer.main.browser

import android.os.Bundle
import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.bean.Share
import com.mynews.common.core.app.fragment.CoreBaseFragment
import com.mynews.common.extension.app.mvp.base.MVPBaseFragment
import kotlinx.android.synthetic.main.fragment_common_browser_web_title.*

class BrowserFragment : MVPBaseFragment<BrowserContract.View,
        BrowserContract.Presenter<BrowserContract.View>>(),
        BrowserContract.View {

    override val mPresenter = BrowserPresenter()
    override val mLayoutRes = R.layout.fragment_browser
    override val mAttachSwipeBack = false

    override fun initView(view: View?, savedInstanceState: Bundle?) {

    }

    override fun <F : CoreBaseFragment> loadContainer(clazz: Class<F>, bundle: Bundle) {
        loadRootFragment(R.id.container_layout,
                CoreBaseFragment.instantiate(clazz, bundle),
                false, false)
    }

    override fun setTitle(title: String) {
        title_bar.setTitle(title)
    }

    override fun setShare(share: Share?) {
        if (share == null || share.url.isNullOrBlank()) {
            share_btn.visibility = View.GONE
        } else {
            share_btn.visibility = View.VISIBLE
            share_btn.setOnClickListener {
                mPresenter.onClickShare(share)
            }
        }
    }

}


