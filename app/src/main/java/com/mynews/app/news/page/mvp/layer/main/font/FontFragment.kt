package com.mynews.app.news.page.mvp.layer.main.font

import android.os.Bundle
import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.data.DataDictionary
import com.mynews.app.news.data.DataManager
import com.mynews.common.core.util.StatusBarUtils
import com.mynews.common.extension.app.mvp.loading.list.MVPListFragment
import kotlinx.android.synthetic.main.fragment_font.*

class FontFragment : MVPListFragment<FontContract.View,
        FontContract.Presenter<FontContract.View>>(),
        FontContract.View {
    override val mAttachSwipeBack = true
    override val mPresenter = FontPresenter()
    override val mLayoutRes = R.layout.fragment_font

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        val fontSize = DataManager.Local.getFontSize()
        val barIndex = when (fontSize) {
            DataDictionary.FontSize.S -> 0
            DataDictionary.FontSize.M -> 1
            else -> 2
        }
        font_size_bar.index = barIndex
        font_size_bar.onIndexChanged {
            val size = when (it) {
                0 -> DataDictionary.FontSize.S
                1 -> DataDictionary.FontSize.M
                else -> DataDictionary.FontSize.L
            }
            mPresenter.onFontSizeChange(size)
        }
        common_content_rv.itemAnimator = null
        common_content_rv.removeItemDecoration(mCommonItemDecorations)
        common_content_rv.overScrollMode = View.OVER_SCROLL_NEVER
        mCommonAdapter.setEnableLoadMore(false)
    }

    override fun onSupportVisible() {
        super.onSupportVisible()
        StatusBarUtils.notifyStatusBarIsLight(_mActivity)
    }
}


