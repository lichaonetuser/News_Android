package com.mynews.app.news.page.mvp.layer.main.feedback.submit

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.mynews.app.news.R
import com.mynews.common.core.image.fresco.ImageManager
import com.mynews.common.core.util.StatusBarUtils
import com.mynews.common.extension.app.mvp.base.MVPBaseFragment
import com.mynews.common.extension.picture.PictureSelectorHelper
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import kotlinx.android.synthetic.main.fragment_feedback_submit.*
import me.yokeyword.fragmentation.ISupportFragment
import me.yokeyword.fragmentation.anim.FragmentAnimator

class FeedbackSubmitFragment : MVPBaseFragment<FeedbackSubmitContract.View,
        FeedbackSubmitContract.Presenter<FeedbackSubmitContract.View>>(),
        FeedbackSubmitContract.View {

    companion object {
        const val DESCRIPTION_MAX_LENGTH = 200
    }

    override val mPresenter = FeedbackSubmitPresenter()
    override val mLayoutRes = R.layout.fragment_feedback_submit

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        submit_btn.setOnClickListener {
            mPresenter.onClickSubmit(
                    feedback_description_etxt.text.toString(),
                    feedback_contact_etxt.text.toString())
        }
        upload_picture_img.setOnClickListener { mPresenter.onClickUploadPicture() }
        feedback_description_etxt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val inputLength = feedback_description_etxt.text?.length ?: 0
                val inputTrimLength = feedback_description_etxt.text?.trim()?.length ?: 0
                val remainLength = DESCRIPTION_MAX_LENGTH - inputLength
                submit_btn.isEnabled = inputTrimLength > 0
                feedback_description_limit_txt.text = remainLength.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    override fun onSupportVisible() {
        super.onSupportVisible()
        StatusBarUtils.notifyStatusBarIsLight(_mActivity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == ISupportFragment.RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    val result = PictureSelector.obtainMultipleResult(data)
                    if (result.isNotEmpty()) {
                        mPresenter.onSelectPicture(result[0])
                    }
                }
            }
        }
    }

    override fun setUploadPicture(mPicturePath: String?) {
        ImageManager.with(upload_picture_img).load(mPicturePath)
    }

    override fun setDescription(description: String) {
        feedback_description_etxt.setText(description)
    }

    override fun goPictureSelector() {
        PictureSelectorHelper.startSelectSinglePicture(this)
    }

    override fun onCreateFragmentAnimator(): FragmentAnimator {
        return FragmentAnimator(
                R.anim.core_slide_in_bottom,
                R.anim.core_slide_out_bottom,
                R.anim.no_anim,
                R.anim.pop_exit_no_anim)
    }

    override fun showProgress() {
        submit_btn.visibility = View.INVISIBLE
        progress_bar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progress_bar.visibility = View.GONE
        submit_btn.visibility = View.VISIBLE
    }

}


