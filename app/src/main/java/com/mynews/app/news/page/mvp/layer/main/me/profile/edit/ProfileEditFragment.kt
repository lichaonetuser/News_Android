package com.mynews.app.news.page.mvp.layer.main.me.profile.edit

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.bean.Account
import com.mynews.app.news.data.DataDictionary
import com.mynews.common.core.CoreApp
import com.mynews.common.core.image.fresco.ImageManager
import com.mynews.common.core.util.ResUtils
import com.mynews.common.extension.app.mvp.base.MVPBaseFragment
import com.mynews.common.extension.picture.PictureSelectorHelper
import com.kongzue.dialog.v2.WaitDialog
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import kotlinx.android.synthetic.main.fragment_me_profile_edit.*
import me.yokeyword.fragmentation.ISupportFragment

class ProfileEditFragment : MVPBaseFragment<ProfileEditContract.View,
        ProfileEditContract.Presenter<ProfileEditContract.View>>(),
        ProfileEditContract.View {

    override val mPresenter = ProfileEditPresenter()
    override val mLayoutRes = R.layout.fragment_me_profile_edit
    override var mDispatchBack = true
    override val mAttachSwipeBack = true

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        profile_avatar_bar.setOnClickListener { mPresenter.onClickBar(it.id) }
        profile_name_bar.setOnClickListener { mPresenter.onClickBar(it.id) }
        profile_sex_bar.setOnClickListener { mPresenter.onClickBar(it.id) }
        profile_age_bar.setOnClickListener { mPresenter.onClickBar(it.id) }
    }

    override fun onSupportVisible() {
        super.onSupportVisible()
        notifyStatusBarIsLight()
    }

    override fun setAccount(account: Account) {
        ImageManager.with(user_cover_img).load(account.avatarUrl)
        profile_name_bar.setValue(account.screenName)
        val gender = when (account.gender) {
            DataDictionary.SelectInfoObject.FEMALE -> DataDictionary.SelectInfoObject.FEMALE_STRING
            DataDictionary.SelectInfoObject.MALE -> DataDictionary.SelectInfoObject.MALE_STRING
            else -> ""
        }
        profile_sex_bar.setValue(gender)
        val ageStage = when (account.ageStage) {
             DataDictionary.SelectInfoObject.AGE_STAGE_LIST[0] -> DataDictionary.SelectInfoObject.AGE_STRING_LIST[0]
             DataDictionary.SelectInfoObject.AGE_STAGE_LIST[1] -> DataDictionary.SelectInfoObject.AGE_STRING_LIST[1]
             DataDictionary.SelectInfoObject.AGE_STAGE_LIST[2] -> DataDictionary.SelectInfoObject.AGE_STRING_LIST[2]
             DataDictionary.SelectInfoObject.AGE_STAGE_LIST[3] -> DataDictionary.SelectInfoObject.AGE_STRING_LIST[3]
             DataDictionary.SelectInfoObject.AGE_STAGE_LIST[4] -> DataDictionary.SelectInfoObject.AGE_STRING_LIST[4]
             DataDictionary.SelectInfoObject.AGE_STAGE_LIST[5] -> DataDictionary.SelectInfoObject.AGE_STRING_LIST[5]
            else -> ""
        }
        profile_age_bar.setValue(ageStage)
    }

    override fun goPictureSelector() {
        PictureSelectorHelper
                .getDefaultSinglePictureSelectionModel(this)
                .withAspectRatio(1, 1)
                .forResult(PictureSelectorHelper.REQUEST_CODE_SELECT_SINGLE_PICTURE)
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

    override fun showProgress() {
        WaitDialog.show(CoreApp.getLastBaseActivityInstance()
                ?: return, ResUtils.getString(R.string.Tip_Loading))
    }

    override fun hideProgress() {
        WaitDialog.dismiss()
    }

}


