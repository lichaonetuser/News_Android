package com.box.app.news.page.mvp.layer.main.me.profile.edit

import com.box.app.news.bean.Account
import com.box.common.extension.app.mvp.base.MVPBaseContract
import com.luck.picture.lib.entity.LocalMedia

interface ProfileEditContract {

    interface View : MVPBaseContract.View {
        fun setAccount(account: Account)
        fun goPictureSelector()
        fun showProgress()
        fun hideProgress()
    }

    interface Presenter<in V : View> : MVPBaseContract.Presenter<V> {
        fun onClickBar(id: Int)
        fun onSelectPicture(localMedia: LocalMedia)
    }

}