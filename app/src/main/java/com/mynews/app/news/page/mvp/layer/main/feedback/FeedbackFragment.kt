package com.mynews.app.news.page.mvp.layer.main.feedback

import android.os.Bundle
import android.view.View
import com.mynews.app.news.R
import com.mynews.common.core.util.StatusBarUtils
import com.mynews.common.extension.app.mvp.loading.list.MVPListFragment
import com.mynews.common.extension.widget.recycler.decoration.CommonItemDecoration
import kotlinx.android.synthetic.main.fragment_feedback.*
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper.ORIENTATION_VERTICAL
import me.yokeyword.fragmentation.anim.FragmentAnimator

class FeedbackFragment : MVPListFragment<FeedbackContract.View,
        FeedbackContract.Presenter<FeedbackContract.View>>(),
        FeedbackContract.View {

    override val mAttachSwipeBack = true
    override val mPresenter = FeedbackPresenter()
    override val mLayoutRes = R.layout.fragment_feedback

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        common_content_rv.removeItemDecoration(CommonItemDecoration.DEFAULT)
        OverScrollDecoratorHelper.setUpOverScroll(common_content_rv, ORIENTATION_VERTICAL)
        feedback_btn.setOnClickListener { mPresenter.onClickFeedback() }
    }

    override fun onSupportVisible() {
        super.onSupportVisible()
        StatusBarUtils.notifyStatusBarIsLight(_mActivity)
    }

    override fun onCreateFragmentAnimator(): FragmentAnimator {
        return FragmentAnimator(
                R.anim.core_swipe_in_right,
                R.anim.core_swipe_out_right,
                R.anim.no_anim,
                R.anim.pop_exit_no_anim)
    }

    override fun scrollTo(position: Int, smooth: Boolean) {
        if (smooth) {
            common_content_rv.smoothScrollToPosition(position)
        } else {
            common_content_rv.scrollToPosition(position)
        }
    }
}


