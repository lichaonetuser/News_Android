package com.mynews.app.news.page.mvp.layer.main.me.profile

import android.os.Bundle
import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.account.AccountManager
import com.mynews.app.news.bean.Account
import com.mynews.app.news.data.DataDictionary
import com.mynews.common.core.image.fresco.ImageManager
import com.mynews.common.extension.app.mvp.base.MVPBaseFragment
import kotlinx.android.synthetic.main.fragment_me_profile.*
import org.jetbrains.anko.imageResource

class ProfileFragment : MVPBaseFragment<ProfileContract.View,
        ProfileContract.Presenter<ProfileContract.View>>(),
        ProfileContract.View {

    override val mPresenter = ProfilePresenter()
    override val mLayoutRes = R.layout.fragment_me_profile
    override var mDispatchBack = false

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        profile_layout.setOnClickListener {
            mPresenter.onClickProfile()
        }
    }

    override fun setAccount(account: Account) {
        ImageManager.with(user_cover_img).load(account.avatarUrl)
        val platformImgRes = when (AccountManager.getAccountPlatform()) {
            DataDictionary.AccountPlatform.FACEBOOK -> R.drawable.profile_platform_facebook
            DataDictionary.AccountPlatform.TWITTER -> R.drawable.profile_platform_twitter
            DataDictionary.AccountPlatform.GOOGLE -> R.drawable.profile_platform_google
            else -> -1
        }
        if (platformImgRes < 0) {
            platform_img.visibility = View.GONE
        } else {
            platform_img.visibility = View.VISIBLE
        }

        platform_img.imageResource = platformImgRes
        user_name_txt.text = account.screenName
    }

}


