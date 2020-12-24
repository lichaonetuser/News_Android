package com.box.app.news.page.mvp.layer.main.dialog.selectSexAge

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.box.app.news.R
import com.box.app.news.page.mvp.layer.main.dialog.selectSexAge.SelectSexAgeDialogContract.Companion.AGE_BELOW_18
import com.box.app.news.page.mvp.layer.main.dialog.selectSexAge.SelectSexAgeDialogContract.Companion.AGE_BETWEEN_18_24
import com.box.app.news.page.mvp.layer.main.dialog.selectSexAge.SelectSexAgeDialogContract.Companion.AGE_BETWEEN_25_29
import com.box.app.news.page.mvp.layer.main.dialog.selectSexAge.SelectSexAgeDialogContract.Companion.AGE_BETWEEN_30_35
import com.box.app.news.page.mvp.layer.main.dialog.selectSexAge.SelectSexAgeDialogContract.Companion.AGE_BETWEEN_36_45
import com.box.app.news.page.mvp.layer.main.dialog.selectSexAge.SelectSexAgeDialogContract.Companion.AGE_DEFAULT_COLOR
import com.box.app.news.page.mvp.layer.main.dialog.selectSexAge.SelectSexAgeDialogContract.Companion.AGE_DEFAULT_TEXT_COLOR
import com.box.app.news.page.mvp.layer.main.dialog.selectSexAge.SelectSexAgeDialogContract.Companion.AGE_NONE
import com.box.app.news.page.mvp.layer.main.dialog.selectSexAge.SelectSexAgeDialogContract.Companion.AGE_OVER_45
import com.box.app.news.page.mvp.layer.main.dialog.selectSexAge.SelectSexAgeDialogContract.Companion.AGE_SELECT_TEXT_COLOR
import com.box.app.news.page.mvp.layer.main.dialog.selectSexAge.SelectSexAgeDialogContract.Companion.BUTTON_DEFAULT_COLOR
import com.box.app.news.page.mvp.layer.main.dialog.selectSexAge.SelectSexAgeDialogContract.Companion.FEMALE_SELECT_COLOR
import com.box.app.news.page.mvp.layer.main.dialog.selectSexAge.SelectSexAgeDialogContract.Companion.MALE_SELECT_COLOR
import com.box.app.news.page.mvp.layer.main.dialog.selectSexAge.SelectSexAgeDialogContract.Companion.SEX_FEMALE
import com.box.app.news.page.mvp.layer.main.dialog.selectSexAge.SelectSexAgeDialogContract.Companion.SEX_MALE
import com.box.app.news.page.mvp.layer.main.dialog.selectSexAge.SelectSexAgeDialogContract.Companion.SEX_NONE
import com.box.common.extension.app.mvp.dialog.MVPDialogFragment
import kotlinx.android.synthetic.main.dialog_select_sex_age.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.textColor

/**
 *
 */
class SelectSexAgeDialogFragment : MVPDialogFragment<SelectSexAgeDialogContract.View,
        SelectSexAgeDialogContract.Presenter<SelectSexAgeDialogContract.View>>(),
        SelectSexAgeDialogContract.View {

    override val mPresenter = SelectSexAgeDialogPresenter()
    override val mLayoutRes = R.layout.dialog_select_sex_age

    private var lastSex:View? = null
    private var lastAge:TextView? = null

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        sex_female_selected.setOnClickListener{ onSexClick(it) }
        sex_male_selected.setOnClickListener{ onSexClick(it) }

        age_below_18.setOnClickListener { onAgeClick(it as TextView) }
        age_between_18_24.setOnClickListener { onAgeClick(it as TextView) }
        age_between_25_29.setOnClickListener { onAgeClick(it as TextView) }
        age_between_30_35.setOnClickListener { onAgeClick(it as TextView) }
        age_between_36_45.setOnClickListener { onAgeClick(it as TextView) }
        age_over_45.setOnClickListener { onAgeClick(it as TextView) }

        select_sex_age_skip.setOnClickListener { mPresenter.onClickSkip() }
        select_sex_age_confirm.setOnClickListener { mPresenter.onClickConfirm() }
    }

    private fun onSexClick(view: View) {
        lastSex?.alpha = 0F
        if (lastSex != view) {
            view.alpha = 1F
            lastSex = view
        } else {
            lastSex = null
        }
    }

    private fun onAgeClick(view: TextView) {
        lastAge?.backgroundColor = AGE_DEFAULT_COLOR
        lastAge?.textColor = AGE_DEFAULT_TEXT_COLOR
        if (lastAge != view) {
            val confirmColor = when (lastSex) {
                null -> BUTTON_DEFAULT_COLOR
                sex_female_selected -> FEMALE_SELECT_COLOR
                else -> MALE_SELECT_COLOR
            }
            view.backgroundColor = confirmColor
            view.textColor = AGE_SELECT_TEXT_COLOR
            lastAge = view
        } else {
            lastAge = null
        }
    }

    override fun getSex():Int {
        return when (lastSex) {
            null -> SEX_NONE
            sex_female_selected -> SEX_FEMALE
            else -> SEX_MALE
        }
    }

    override fun getAge():Int {
        return when (lastAge) {
            null -> AGE_NONE
            age_below_18 -> AGE_BELOW_18
            age_between_18_24 -> AGE_BETWEEN_18_24
            age_between_25_29 -> AGE_BETWEEN_25_29
            age_between_30_35 -> AGE_BETWEEN_30_35
            age_between_36_45 -> AGE_BETWEEN_36_45
            else -> AGE_OVER_45
        }
    }
}