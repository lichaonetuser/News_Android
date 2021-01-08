package com.mynews.app.news.page.mvp.layer.main.location.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.mynews.app.news.R
import com.mynews.common.extension.app.mvp.loading.list.MVPListFragment
import kotlinx.android.synthetic.main.fragment_location_search.*
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper

class LocationSearchFragment : MVPListFragment<LocationSearchContract.View,
        LocationSearchContract.Presenter<LocationSearchContract.View>>(),
        LocationSearchContract.View {
    override val mPresenter = LocationSearchPresenter()
    override val mLayoutRes = R.layout.fragment_location_search

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        mCommonAdapter.setEnableLoadMore(false)
        OverScrollDecoratorHelper.setUpOverScroll(common_content_rv, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        search_city_etxt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(text: Editable?) {
                val newText = text?.toString() ?: ""
                if (mCommonAdapter.hasNewSearchText(newText)) {
                    mCommonAdapter.searchText = newText
                    mCommonAdapter.filterItems()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        showSoftInputImmediate(search_city_etxt)
    }

}


