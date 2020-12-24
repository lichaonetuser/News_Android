package com.box.app.news.page.mvp.layer.main.dialog.more

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.box.app.news.R
import com.box.app.news.bean.base.BaseNewsBean
import com.box.app.news.page.mvp.layer.main.dialog.report.ReportDialogFragment
import com.box.common.core.util.ResUtils
import com.box.common.extension.app.mvp.dialog.MVPDialogFragment
import com.box.common.extension.share.ContentLink
import com.box.common.extension.share.IShareListener
import com.box.common.extension.share.ShareManager
import com.box.common.extension.share.SharePlatform
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_dialog_more.*
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import org.jetbrains.anko.noButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.yesButton

class MoreDialogFragment : MVPDialogFragment<MoreDialogContract.View,
        MoreDialogContract.Presenter<MoreDialogContract.View>>(),
        MoreDialogContract.View {

    override val mPresenter = MoreDialogPresenter()
    override val mLayoutRes = R.layout.fragment_dialog_more
    override var mEnterType = MVPDialogFragment.Companion.EnterType.BOTTOM

    private val mShareAdapter: CommonRecyclerAdapter = CommonRecyclerAdapter()
    private val mActionAdapter: CommonRecyclerAdapter = CommonRecyclerAdapter()

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        share_rv.adapter = mShareAdapter
        action_rv.adapter = mActionAdapter
        initRV(share_rv, action_rv)
        initAdapter(mShareAdapter, mActionAdapter)
    }

    private fun initRV(vararg rvs: RecyclerView) {
        rvs.forEach {
            it.layoutManager = LinearLayoutManager(null, LinearLayoutManager.HORIZONTAL, false)
            OverScrollDecoratorHelper.setUpOverScroll(it, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL)
        }
    }

    private fun initAdapter(vararg adapters: CommonRecyclerAdapter) {
        adapters.forEach {
            it.setEnableLoadMore(false)
            it.addListener(object : CommonRecyclerAdapter.FlexibleListener() {
                override fun onItemClick(position: Int): Boolean {
                    val item = it.getItem(position) ?: return false
                    return mPresenter.onItemClick(position, item)
                }
            })
        }
    }

    override fun getShareAdapter(): CommonRecyclerAdapter {
        return mShareAdapter
    }

    override fun getActionAdapter(): CommonRecyclerAdapter {
        return mActionAdapter
    }

    override fun showDeleteDialog(news: BaseNewsBean) {
        alert(R.string.Tip_ContentTrashDescription) {
            yesButton { mPresenter.onClickDeleteConfirm(true) }
            noButton { mPresenter.onClickDeleteConfirm(false) }
            onCancelled { mPresenter.onClickDeleteConfirm(false) }
        }.show()
    }

    override fun reportByEmail(content: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "plain/text"
        intent.putExtra(Intent.EXTRA_EMAIL, Array(1) { ResUtils.getString(R.string.report_email) })
        intent.putExtra(Intent.EXTRA_SUBJECT, ResUtils.getString(R.string.Report_MailReportTitle))
        intent.putExtra(Intent.EXTRA_TEXT, ResUtils.getString(R.string.Report_ImproperMailReportContent))
        _mActivity.startActivity(Intent.createChooser(intent, ResUtils.getString(R.string.Tip_SelectMailApp)))
    }

    override fun goReport(bundle: Bundle) {
        pop()
        goFromRoot(ReportDialogFragment::class.java, bundle, hideSelf = false)
    }

    override fun shareLink(platform: SharePlatform, content: ContentLink, listener: IShareListener) {
        ShareManager.shareLink(_mActivity, platform, content, listener)
    }

}
