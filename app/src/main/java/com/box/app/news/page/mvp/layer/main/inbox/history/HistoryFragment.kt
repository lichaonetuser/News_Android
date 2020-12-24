package com.box.app.news.page.mvp.layer.main.inbox.history

import com.box.app.news.R
import com.box.app.news.page.mvp.layer.main.inbox.base.BaseInboxContentFragment
import com.box.app.news.page.mvp.layer.main.inbox.message.MessageContract
import com.box.app.news.page.mvp.layer.main.inbox.message.MessagePresenter
import com.box.common.core.util.ResUtils

class HistoryFragment : BaseInboxContentFragment<
        MessageContract.View,
        MessageContract.Presenter<MessageContract.View>>(),
        MessageContract.View {

    override val mPresenter = HistoryPresenter()

    override fun getPageTitle(): String {
        return ResUtils.getString(R.string.Mine_Notification)
    }

}


