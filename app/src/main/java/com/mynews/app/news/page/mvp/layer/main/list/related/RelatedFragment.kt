package com.mynews.app.news.page.mvp.layer.main.list.related

import android.os.Bundle
import android.view.View
import com.mynews.app.news.R
import com.mynews.common.extension.app.mvp.loading.list.MVPListFragment
import com.mynews.common.extension.widget.recycler.decoration.CommonItemDecoration
import com.github.magiepooh.recycleritemdecoration.ItemDecorations
import kotlinx.android.synthetic.main.fragment_related.*
import me.yokeyword.fragmentation.anim.FragmentAnimator

class RelatedFragment : MVPListFragment<RelatedContract.View,
        RelatedContract.Presenter<RelatedContract.View>>(),
        RelatedContract.View {

    override val mAttachSwipeBack = false
    override var mDispatchBack = false
    override val mPresenter = RelatedPresenter()
    override val mLayoutRes = R.layout.fragment_related
    override var mCommonItemDecorations = CommonItemDecoration()

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        mCommonAdapter.setEnableLoadMore(false)
        common_content_rv.isNestedScrollingEnabled = false
        common_content_rv.addItemDecoration(ItemDecorations.vertical(_mActivity)
                .type(R.layout.item_related_article_left_img, R.drawable.divider_news_list)
                .type(R.layout.item_related_article_right_img, R.drawable.divider_news_list)
                .type(R.layout.item_related_article_no_img, R.drawable.divider_news_list)
                .type(R.layout.item_related_video, R.drawable.divider_news_list)
                .type(R.layout.item_related_gif, R.drawable.divider_news_list)
                .type(R.layout.item_related_joke, R.drawable.divider_news_list)
                .create())
    }

    override fun onCreateFragmentAnimator(): FragmentAnimator {
        return FragmentAnimator(
                R.anim.no_anim,
                R.anim.no_anim,
                R.anim.no_anim,
                R.anim.no_anim)
    }

    override fun back() {}
}


