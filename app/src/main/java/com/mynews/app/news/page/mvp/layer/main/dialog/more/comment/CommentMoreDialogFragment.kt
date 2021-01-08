package com.mynews.app.news.page.mvp.layer.main.dialog.more.comment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.page.mvp.layer.main.dialog.comment.InputCommentDialogFragment
import com.mynews.app.news.page.mvp.layer.main.dialog.report.ReportDialogFragment
import com.mynews.common.extension.app.mvp.dialog.MVPDialogFragment
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.mynews.common.extension.widget.recycler.decoration.CommonItemDecoration
import kotlinx.android.synthetic.main.fragment_dialog_more_comment.*
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import org.jetbrains.anko.noButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.yesButton

class CommentMoreDialogFragment : MVPDialogFragment<CommentMoreDialogContract.View,
        CommentMoreDialogContract.Presenter<CommentMoreDialogContract.View>>(),
        CommentMoreDialogContract.View {

    override val mPresenter = CommentMoreDialogPresenter()
    override val mLayoutRes = R.layout.fragment_dialog_more_comment
    override var mEnterType = MVPDialogFragment.Companion.EnterType.BOTTOM

    private val mAdapter: CommonRecyclerAdapter = CommonRecyclerAdapter()

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        initRV()
        initAdapter()
    }

    private fun initRV() {
        option_rv.addItemDecoration(CommonItemDecoration().withDivider(R.drawable.divider_common))
        option_rv.layoutManager = LinearLayoutManager(null, LinearLayoutManager.VERTICAL, false)
        option_rv.setHasFixedSize(true)
        option_rv.adapter = mAdapter
        OverScrollDecoratorHelper.setUpOverScroll(option_rv, OverScrollDecoratorHelper.ORIENTATION_VERTICAL)
    }

    private fun initAdapter() {
        mAdapter.setEnableLoadMore(false)
        mAdapter.addListener(object : CommonRecyclerAdapter.FlexibleListener() {
            override fun onItemClick(position: Int): Boolean {
                val item = mAdapter.getItem(position) ?: return false
                return mPresenter.onItemClick(position, item)
            }
        })
    }

    override fun showDeleteDialog() {
        alert(R.string.Tip_ConfirmDeleteComment) {
            yesButton { mPresenter.onClickDeleteConfirm(true) }
            noButton { mPresenter.onClickDeleteConfirm(false) }
            onCancelled { mPresenter.onClickDeleteConfirm(false) }
        }.show()
    }

    override fun goReply(bundle: Bundle) {
        pop()
        goFromRoot(InputCommentDialogFragment::class.java, bundle,hideSelf = false)
    }

    override fun goReport(bundle: Bundle) {
        pop()
        goFromRoot(ReportDialogFragment::class.java, bundle,hideSelf = false)
    }

    override fun getAdapter(): CommonRecyclerAdapter {
        return mAdapter
    }
}
