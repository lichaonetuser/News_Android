package com.mynews.app.news.page.mvp.layer.main.inbox.message

import com.mynews.app.news.bean.list.ListInbox
import com.mynews.app.news.data.DataDictionary
import com.mynews.app.news.page.mvp.layer.main.inbox.base.BaseInboxContentPresenter
import com.yatatsu.autobundle.AutoBundleField

class MessagePresenter : BaseInboxContentPresenter<MessageContract.View>(),
        MessageContract.Presenter<MessageContract.View> {

    override val mInboxType: Int = DataDictionary.InboxType.MESSAGE.intValue

    @AutoBundleField(required = false)
    override var mListInbox: ListInbox = ListInbox()

}

