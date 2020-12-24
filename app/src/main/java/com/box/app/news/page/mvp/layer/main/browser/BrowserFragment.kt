package com.box.app.news.page.mvp.layer.main.browser

import android.os.Bundle
import android.view.View
import com.box.app.news.R
import com.box.app.news.bean.Share
import com.box.common.core.app.fragment.CoreBaseFragment
import com.box.common.extension.app.mvp.base.MVPBaseFragment
import kotlinx.android.synthetic.main.fragment_common_browser_web_title.*
import org.jetbrains.anko.support.v4.findOptional

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


