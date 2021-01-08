package com.mynews.app.news.page.mvp.layer.main.article.headline


import com.mynews.app.news.R
import com.mynews.app.news.page.mvp.layer.main.list.news.NewsListFragment
import com.mynews.common.extension.widget.recycler.decoration.CommonItemDecoration

/**
 * 7.1 24小时新闻改为频道，这个类是为了替换通用的NewsListFragment
 */
class HeadlineChannelFragment : NewsListFragment<HeadlineContract.View,
        HeadlineContract.Presenter<HeadlineContract.View>>(),
        HeadlineContract.View {

    @Suppress("UNCHECKED_CAST")
    override val mPresenter = HeadlinePresenter()
    override val mLayoutRes = R.layout.fragment_headline_channel
    override var mDispatchBack = true
    override val mAttachSwipeBack = true
    override var mCommonItemDecorations: CommonItemDecoration = CommonItemDecoration().hide()
}