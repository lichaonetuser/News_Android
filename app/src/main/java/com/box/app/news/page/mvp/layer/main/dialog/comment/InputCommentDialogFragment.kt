package com.box.app.news.page.mvp.layer.main.dialog.comment

import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.EditText
import com.box.app.news.R
import com.box.common.core.app.activity.CoreBaseActivity
import com.box.common.core.util.ResUtils
import com.box.common.core.widget.CoreEditText
import com.box.common.extension.app.mvp.dialog.MVPDialogFragment
import kotlinx.android.synthetic.main.fragment_dialog_input_comment.*
import org.jetbrains.anko.hintTextColor


class InputCommentDialogFragment : MVPDialogFragment<InputCommentDialogContract.View,
        InputCommentDialogContract.Presenter<InputCommentDialogContract.View>>(),
        InputCommentDialogContract.View {

    companion object {
        private val MAX_COMMENT_LIMIT = 140
    }

    override val mPresenter = InputCommentDialogPresenter()
    override val mLayoutRes = R.layout.fragment_dialog_input_comment
    override var mEnterType = MVPDialogFragment.Companion.EnterType.CENTER
    override var maskColorRes = R.color.black_20

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        (_mActivity as? CoreBaseActivity)?.autoHideSoftInput = false

        limit_txt.text = MAX_COMMENT_LIMIT.toString()
        comment_input_etxt.setMaxLength(MAX_COMMENT_LIMIT)

        comment_input_etxt.setOnBackListener(object : CoreEditText.OnBackListener {
            override fun back(editText: EditText): Boolean {
                hideSoftInput()
                back()
                return true
            }
        })
        anonymous_check.setOnClickListener {
            if (activity != null && anonymous_check != null) {
                mPresenter.onClickAnonymousCheck(anonymous_check.isChecked)
            }
        }
//        world_cup_check.setOnClickListener {
//            mPresenter.onClickWorldCupCheck(world_cup_check.isChecked)
//        }
        submit_btn.setOnClickListener {
            mPresenter.onClickSubmit(comment_input_etxt.text.toString(), anonymous_check.isChecked, world_cup_check.isChecked)
        }

        comment_input_etxt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s == null) {
                    return
                }

                submit_btn?.isEnabled = s.isNotBlank()
                limit_txt?.text = (MAX_COMMENT_LIMIT - s.length).toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        comment_input_etxt.getViewTreeObserver()
            .addOnGlobalLayoutListener(OnGlobalLayoutListener
            {
                val rect = Rect()
                activity!!.window.decorView.getWindowVisibleDisplayFrame(rect)
                val screenHeight =
                    activity!!.window.decorView.rootView.height
                val heightDifference: Int = screenHeight - rect.bottom
                val visible = heightDifference > screenHeight / 3
                if (!visible) {

                }
            })
    }

    override fun toggleAnonymousCheck() {
        anonymous_check.toggle()
    }

    override fun toggleWorldCupCheck() {
//        world_cup_check.toggle()
    }

    override fun setWorldCupCheckVisible(showForwardBoard: Boolean) {
        world_cup_check.visibility = View.GONE
        world_cup_check.isChecked = false
    }

    /**
     * 当键盘弹起时设置提示字体的颜色内容
     */
    override fun onShowInput() {
        super.onShowInput()
        comment_input_etxt.hintTextColor = ResUtils.getColor(R.color.color_5)
        comment_input_etxt.hint = mPresenter.getHint()?:ResUtils.getString(R.string.Tip_CommentPlaceHolder)
    }

    /**
     * 当键盘收起时设置提示字体的颜色内容
     */
    override fun onHideInput() {
        super.onHideInput()
        comment_input_etxt.hintTextColor = ResUtils.getColor(R.color.color_1)
        comment_input_etxt.hint = ResUtils.getString(R.string.Tip_WriteAComment)
    }

    override fun onResume() {
        super.onResume()
        /**
         * 之前的页面如果含有webview的话这里会遗失焦点，原因不明
         * 推测为webview重新抢占了焦点
         * 这里重新获取
         */
        showSoftInputImmediate(comment_input_etxt)
    }

    override fun onDestroy() {
        super.onDestroy()
        (_mActivity as? CoreBaseActivity)?.autoHideSoftInput = true
    }

    override fun back() {
        hideSoftInput()
        super.back()
    }

    override fun onEnterAnimationEnd(savedInstanceState: Bundle?, flag: Int) {
        super.onEnterAnimationEnd(savedInstanceState, flag)
    }

    override fun setInputHint(hint: String) {
        comment_input_etxt.hint = hint
    }


}


