package com.mynews.app.news.page.mvp.layer.main.dialog.report.content

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import com.mynews.app.news.R
import com.mynews.common.core.app.activity.CoreBaseActivity
import com.mynews.common.core.widget.CoreEditText
import com.mynews.common.extension.app.mvp.dialog.MVPDialogFragment
import kotlinx.android.synthetic.main.fragment_dialog_report_content.*

class ReportContentDialogFragment : MVPDialogFragment<ReportContentDialogContract.View,
        ReportContentDialogContract.Presenter<ReportContentDialogContract.View>>(),
        ReportContentDialogContract.View {

    override val mPresenter = ReportContentDialogPresenter()
    override val mLayoutRes = R.layout.fragment_dialog_report_content
    override var mEnterType = MVPDialogFragment.Companion.EnterType.BOTTOM

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        (_mActivity as? CoreBaseActivity)?.autoHideSoftInput = false
        back_btn.setOnClickListener {
            back()
        }
        submit_btn.setOnClickListener {
            mPresenter.onClickSubmit(report_input_etxt.text.toString())
        }
        report_input_etxt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s == null) {
                    return
                }
                submit_btn.isEnabled = s.isNotBlank()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        report_input_etxt.setOnBackListener(object : CoreEditText.OnBackListener {
            override fun back(editText: EditText): Boolean {
                hideSoftInput()
                back()
                return true
            }
        })
    }

    override fun back() {
        hideSoftInput()
        super.back()
    }

    override fun onResume() {
        super.onResume()
        showSoftInputImmediate(report_input_etxt)
    }

}
