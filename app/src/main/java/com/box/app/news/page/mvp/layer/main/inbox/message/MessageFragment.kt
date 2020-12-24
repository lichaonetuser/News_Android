package com.box.app.news.page.mvp.layer.main.inbox.message

import com.box.app.news.R
import com.box.app.news.page.mvp.layer.main.inbox.base.BaseInboxContentFragment
import com.box.common.core.util.ResUtils

class MessageFragment : BaseInboxContentFragment<
        MessageContract.View,
        MessageContract.Presenter<MessageContract.View>>(),
        MessageContract.View {

    override val mPresenter = MessagePresenter()

    override fun getPageTitle(): String {
        return ResUtils.getString(R.string.Mine_Activity)
    }

}


