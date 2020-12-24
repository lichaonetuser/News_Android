package com.box.app.news.page.mvp.layer.main.dialog.report

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import com.box.app.news.R
import com.box.app.news.page.mvp.layer.main.dialog.report.content.ReportContentDialogFragment
import com.box.common.core.util.ResUtils
import com.box.common.extension.app.mvp.dialog.MVPDialogFragment
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.github.magiepooh.recycleritemdecoration.ItemDecorations
import kotlinx.android.synthetic.main.fragment_dialog_report.*

class ReportDialogFragment : MVPDialogFragment<ReportDialogContract.View,
        ReportDialogContract.Presenter<ReportDialogContract.View>>(),
        ReportDialogContract.View {

    override val mPresenter = ReportDialogPresenter()
    override val mLayoutRes = R.layout.fragment_dialog_report
    override var mEnterType = MVPDialogFragment.Companion.EnterType.BOTTOM

    private val mAdapter: CommonRecyclerAdapter = CommonRecyclerAdapter()

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        initRV()
        initAdapter()
        cancel_btn.setOnClickListener { back() }
    }

    private fun initRV() {
        report_rv.addItemDecoration(ItemDecorations.vertical(_mActivity)
                .first(R.drawable.divider_common)
                .type(R.layout.item_report, R.drawable.divider_common)
                .last(R.drawable.divider_common)
                .create())
        report_rv.layoutManager = LinearLayoutManager(null, LinearLayoutManager.VERTICAL, false)
        report_rv.setHasFixedSize(true)
        report_rv.adapter = mAdapter
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

    override fun reportByEmail(content: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "plain/text"
        intent.putExtra(Intent.EXTRA_EMAIL, Array(1) { ResUtils.getString(R.string.report_email) })
        intent.putExtra(Intent.EXTRA_SUBJECT, ResUtils.getString(R.string.Report_MailReportTitle))
        intent.putExtra(Intent.EXTRA_TEXT, ResUtils.getString(R.string.Report_ImproperMailReportContent))
        _mActivity.startActivity(Intent.createChooser(intent, ResUtils.getString(R.string.Tip_SelectMailApp)))
    }


    override fun goReportContent(bundle: Bundle) {
        pop()
        goFromRoot(ReportContentDialogFragment::class.java, bundle, hideSelf = false)
    }

    override fun getAdapter(): CommonRecyclerAdapter {
        return mAdapter
    }
}
