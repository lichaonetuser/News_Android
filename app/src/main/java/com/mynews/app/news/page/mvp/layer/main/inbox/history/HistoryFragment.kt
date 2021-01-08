package com.mynews.app.news.page.mvp.layer.main.inbox.history

import com.mynews.app.news.R
import com.mynews.app.news.page.mvp.layer.main.inbox.base.BaseInboxContentFragment
import com.mynews.app.news.page.mvp.layer.main.inbox.message.MessageContract
import com.mynews.common.core.util.ResUtils

class HistoryFragment : BaseInboxContentFragment<
        MessageContract.View,
        MessageContract.Presenter<MessageContract.View>>(),
        MessageContract.View {

    override val mPresenter = HistoryPresenter()

    override fun getPageTitle(): String {
        return ResUtils.getString(R.string.Mine_Notification)
    }

}


