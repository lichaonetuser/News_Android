package com.box.app.news.page.mvp.layer.main.location.sub

import android.os.Bundle
import android.view.View
import com.box.app.news.R
import com.box.common.extension.app.mvp.loading.list.MVPListFragment
import com.box.common.extension.widget.recycler.decoration.CommonItemDecoration
import kotlinx.android.synthetic.main.core_widget_title_bar.*
import kotlinx.android.synthetic.main.fragment_location.*
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper

class LocationSubFragment : MVPListFragment<LocationSubContract.View,
        LocationSubContract.Presenter<LocationSubContract.View>>(),
        LocationSubContract.View {

    override val mPresenter = LocationSubPresenter()
    override val mLayoutRes = R.layout.fragment_location_sub
    override val mAttachSwipeBack = true
    override var mCommonItemDecorations = CommonItemDecoration()
            .withDivider(R.drawable.divider_location_list)
            .withDrawDividerOnLastItem(true)

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        mCommonAdapter.setEnableLoadMore(false)
        OverScrollDecoratorHelper.setUpOverScroll(common_content_rv, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
    }

    override fun setTitle(title: String) {
        title_txt.text = title
    }

}


