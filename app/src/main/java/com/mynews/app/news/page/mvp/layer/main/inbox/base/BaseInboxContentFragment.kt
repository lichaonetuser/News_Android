package com.mynews.app.news.page.mvp.layer.main.inbox.base

import android.os.Bundle
import android.view.View
import com.mynews.app.news.R
import com.mynews.common.extension.app.mvp.loading.list.MVPListFragment
import com.mynews.common.extension.widget.recycler.decoration.CommonItemDecoration
import com.github.magiepooh.recycleritemdecoration.ItemDecorations
import kotlinx.android.synthetic.main.fragment_common_list_refresh.*
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import me.yokeyword.fragmentation.anim.FragmentAnimator

abstract class BaseInboxContentFragment<
        V : BaseInboxContentContract.View,
        P : BaseInboxContentContract.Presenter<V>>
    : MVPListFragment<V, P>(), BaseInboxContentContract.View {

    override val mAttachSwipeBack = false
    override var mDispatchBack = false
    override val mLayoutRes = R.layout.fragment_common_list
    override var mCommonItemDecorations = CommonItemDecoration()

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        common_content_rv.addItemDecoration(ItemDecorations.vertical(_mActivity)
                .type(R.layout.item_inbox_comment, R.drawable.divider_inbox_list)
                .type(R.layout.item_inbox_news, R.drawable.divider_inbox_list)
                .last(R.drawable.divider_inbox_list)
                .create())
        OverScrollDecoratorHelper.setUpOverScroll(common_content_rv, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
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


