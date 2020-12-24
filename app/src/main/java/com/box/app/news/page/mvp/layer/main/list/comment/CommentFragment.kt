package com.box.app.news.page.mvp.layer.main.list.comment

import android.os.Bundle
import android.view.View
import com.box.app.news.R
import com.box.common.core.util.ResUtils
import com.box.common.extension.app.mvp.loading.list.MVPListFragment
import com.box.common.extension.widget.recycler.decoration.CommonItemDecoration
import com.github.magiepooh.recycleritemdecoration.ItemDecorations
import kotlinx.android.synthetic.main.fragment_comment.*
import kotlinx.android.synthetic.main.layout_comment_empty.*
import me.yokeyword.fragmentation.anim.FragmentAnimator

class CommentFragment : MVPListFragment<CommentContract.View,
        CommentContract.Presenter<CommentContract.View>>(),
        CommentContract.View {

    override val mAttachSwipeBack = false
    override var mDispatchBack = false
    override val mPresenter = CommentPresenter()
    override val mLayoutRes = R.layout.fragment_comment
    override var mCommonItemDecorations = CommonItemDecoration()

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        common_content_rv.isNestedScrollingEnabled = true
        common_content_rv.addItemDecoration(ItemDecorations.vertical(_mActivity)
                .type(R.layout.item_comment, R.drawable.divider_news_list)
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

    override fun showEmpty(message: String, imgRes: Int) {
        super.showEmpty(message)
        if (message.isEmpty()) {
            comment_empty_img.setImageResource(R.drawable.comment_loading_empty_ic)
            comment_empty_txt.text = ResUtils.getString(R.string.Comment_NoCommentClickPost)
        } else {
            comment_empty_img.setImageResource(imgRes)
            comment_empty_txt.text = message
        }
    }

    fun updateShowForwardBoard(showForwardBoard: Boolean) {
        mPresenter.onUpdateShowForwardBoard(showForwardBoard)
    }

}


