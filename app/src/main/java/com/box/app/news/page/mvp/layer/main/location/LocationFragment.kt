package com.box.app.news.page.mvp.layer.main.location

import android.os.Bundle
import android.view.View
import com.box.app.news.R
import com.box.common.extension.app.mvp.loading.list.MVPListFragment
import com.box.common.extension.widget.recycler.decoration.CommonItemDecoration
import kotlinx.android.synthetic.main.fragment_location.*
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper

class LocationFragment : MVPListFragment<LocationContract.View,
        LocationContract.Presenter<LocationContract.View>>(),
        LocationContract.View {

    override val mAttachSwipeBack = true
    override val mPresenter = LocationPresenter()
    override val mLayoutRes = R.layout.fragment_location
    override var mCommonItemDecorations = CommonItemDecoration()
            .withDivider(R.drawable.divider_location_list)
            .withDrawDividerOnLastItem(true)

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        mCommonAdapter.setEnableLoadMore(false)
        OverScrollDecoratorHelper.setUpOverScroll(common_content_rv, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        location_btn.setOnClickListener { mPresenter.onClickCurrentLocation() }
        search_btn.setOnClickListener { mPresenter.onClickSearch() }
    }

    override fun showLocationProgress() {
        location_pb.visibility = View.VISIBLE
    }

    override fun hideLocationProgress() {
        location_pb.visibility = View.GONE
    }

    override fun showArrow() {
        location_arrow_img.visibility = View.VISIBLE
    }

    override fun hideArrow() {
        location_arrow_img.visibility = View.GONE
    }

    override fun setLocationResult(result: String) {
        city_txt.text = result
    }

    override fun setTitle(title: String) {
        title_bar.setTitle(title)
    }

}


