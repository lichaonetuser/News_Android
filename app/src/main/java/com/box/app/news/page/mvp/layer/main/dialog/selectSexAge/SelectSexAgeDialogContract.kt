package com.box.app.news.page.mvp.layer.main.dialog.selectSexAge

import com.box.app.news.R
import com.box.common.extension.app.mvp.dialog.MVPDialogContract

/**
 *
 */
interface SelectSexAgeDialogContract {

    companion object {
        const val SEX_NONE = 0
        const val SEX_FEMALE = 1
        const val SEX_MALE = 2

        const val AGE_NONE = 4
        const val AGE_BELOW_18 = 5
        const val AGE_BETWEEN_18_24 = 6
        const val AGE_BETWEEN_25_29 = 7
        const val AGE_BETWEEN_30_35 = 8
        const val AGE_BETWEEN_36_45 = 9
        const val AGE_OVER_45 = 10

        const val FEMALE_SELECT_COLOR = R.color.select_sex_female_selected
        const val MALE_SELECT_COLOR = R.color.select_sex_male_selected
        const val AGE_DEFAULT_COLOR = R.color.select_age_unselected
        const val BUTTON_DEFAULT_COLOR = R.color.color_2
        const val AGE_DEFAULT_TEXT_COLOR = R.color.color_1
        const val AGE_SELECT_TEXT_COLOR = R.color.color_9
    }

    interface View: MVPDialogContract.View{
        fun getSex():Int
        fun getAge():Int
    }

    interface Presenter<in V : SelectSexAgeDialogContract.View> : MVPDialogContract.Presenter<V> {
        fun onClickSkip()
        fun onClickConfirm()
    }
}