package com.box.app.news.page.mvp.layer.main.list.comment.my

import android.os.Bundle
import android.view.View
import com.box.app.news.R
import com.box.common.extension.app.mvp.loading.list.MVPListFragment
import com.box.common.extension.widget.recycler.decoration.CommonItemDecoration
import kotlinx.android.synthetic.main.fragment_related.*
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import me.yokeyword.fragmentation.anim.FragmentAnimator

class MyCommentFragment : MVPListFragment<MyCommentContract.View,
        MyCommentContract.Presenter<MyCommentContract.View>>(),
        MyCommentContract.View {

    override val mAttachSwipeBack = true
    override var mDispatchBack = true
    override val mPresenter = MyCommentPresenter()
    override val mLayoutRes = R.layout.fragment_my_comment
    override var mCommonItemDecorations = CommonItemDecoration()
            .withDivider(R.drawable.divider_location_list)
            .withDrawDividerOnLastItem(true)

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        mCommonAdapter.setEnableLoadMore(false)
        common_content_rv.removeItemDecoration(CommonItemDecoration.DEFAULT)
        OverScrollDecoratorHelper.setUpOverScroll(common_content_rv, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
    }

    override fun onSupportVisible() {
        super.onSupportVisible()
        notifyStatusBarIsLight()
    }

    override fun onCreateFragmentAnimator(): FragmentAnimator {
        return FragmentAnimator(
                R.anim.core_swipe_in_right,
                R.anim.core_swipe_out_right,
                R.anim.no_anim,
                R.anim.pop_exit_no_anim)
    }
}


