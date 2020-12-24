package com.box.app.news.page.mvp.layer.main.article.search

import android.os.Build
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import com.box.app.news.R
import com.box.app.news.analytics.AnalyticsKey
import com.box.app.news.data.source.remote.http.url.WebNewsUrls
import com.box.app.news.page.mvp.layer.main.web.news.SearchWebBrowserFragment
import com.box.app.news.page.mvp.layer.main.web.news.SearchWebBrowserPresenterAutoBundle
import com.box.common.core.analytics.AnalyticsManager
import com.box.common.core.app.fragment.CoreBaseFragment
import com.box.common.core.util.ResUtils
import com.box.common.extension.app.mvp.loading.list.MVPListFragment
import kotlinx.android.synthetic.main.fragment_search.*

open class SearchFragment : MVPListFragment<SearchContract.View,
        SearchContract.Presenter<SearchContract.View>>(),
        SearchContract.View {

    @Suppress("UNCHECKED_CAST")
    override val mPresenter = SearchPresenter()
    override val mLayoutRes = R.layout.fragment_search
    override var mDispatchBack = true
    override val mAttachSwipeBack = true
    override var mCommonLayoutManage: RecyclerView.LayoutManager = GridLayoutManager(activity, 2)

    private var mResultFragment: SearchWebBrowserFragment? = null

    override fun setSearchHint(hint: String) {
        search_input_et.hint = hint
    }

    override fun initCommonContentRecyclerView() {
        super.initCommonContentRecyclerView()
        if (mCommonLayoutManage is GridLayoutManager) {
            val mGridLayout = mCommonLayoutManage as GridLayoutManager
            mGridLayout.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    if (position < mPresenter.mSearchItemList.size) {
                        return mPresenter.getSpanSize(position)
                    }
                    return 2
                }
            }
        }

    }

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            status_bar.setBackgroundColor(ResUtils.getColor(R.color.color_1))
        }

        cancel_txt.setOnClickListener {
            AnalyticsManager.logEvent(AnalyticsKey.Event.SEARCH, AnalyticsKey.Parameter.CLICK_SEARCH_CANCEL)
            back()
        }

        search_input_clear_img.setOnClickListener {
            search_input_et.setText("")
            showSoftInput(search_input_et)
        }

        search_input_et.setOnFocusChangeListener { _, focus ->
            // 获取焦点时候，如果显示了搜索结果页面，退回到之前页面
            if (focus) {
                goBack()
            }
        }

        search_input_et.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (text.isNullOrEmpty()) {
                    search_input_clear_img.visibility = View.GONE
                } else {
                    search_input_clear_img.visibility = View.VISIBLE
                }
            }

        })

        search_input_et.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                var text = search_input_et.text?.toString()
                if (text.isNullOrEmpty()) {
                    text = search_input_et.hint?.toString()
                }
                if (text == null || text.isBlank() || text == ResUtils.getString(R.string.Query_TypeInSearchKeyword)) {
                    return@setOnEditorActionListener false
                }

                mPresenter.setSearchHistory(text)
                hideSoftInput()
                doSearch(text)
                AnalyticsManager.logEvent(AnalyticsKey.Event.SEARCH, AnalyticsKey.Parameter.CLICK_SEARCH_BUTTON)
            }
            false
        }

        showSoftInput(search_input_et)
    }

    override fun doSearch(keyword: String) {
        // 关键字显示到输入框里，更新光标位置
        search_input_et?.setText(keyword)
        search_input_et?.setSelection(keyword.length)
        // 清除输入框的焦点，焦点交给外层布局，使输入框去掉光标。
        search_input_et?.clearFocus()
        search_bar_layout.requestFocus()

        var init = false
        // 未初始化过搜索结果页面，则初始化
        if (mResultFragment == null) {
            mResultFragment = CoreBaseFragment.instantiate(
                    clazz = SearchWebBrowserFragment::class.java)
            init = true
        }
        // 设置arguments参数，主要是url
        mResultFragment?.arguments = SearchWebBrowserPresenterAutoBundle.builder()
                .mUrl(WebNewsUrls.getNewsSearchResultUrl(keyword = keyword))
                .mTitle("")
                .bundle()
        search_result_webview_layout.visibility = View.VISIBLE

        // 首次加载Fragment调用loadRootFragment
        if (init) {
            loadRootFragment(R.id.search_result_webview_layout, mResultFragment, false, false)
        } else {
            // 载入新的url
            mResultFragment?.loadURL(WebNewsUrls.getNewsSearchResultUrl(keyword = keyword))
        }
    }

    override fun onBackPressedSupport(): Boolean {
        // 如果搜索结果页面可见，return true拦截返回事件并隐藏页面。否则执行返回键操作
        if (!goBack()) {
            super.onBackPressedSupport()
        }
        return true
    }

    override fun goBack(): Boolean {
        // 如果搜索结果页面可见，则隐藏
        when (search_result_webview_layout.visibility) {
            View.VISIBLE -> {
                search_result_webview_layout.visibility = View.GONE
                return true
            }
        }
        return false
    }
}