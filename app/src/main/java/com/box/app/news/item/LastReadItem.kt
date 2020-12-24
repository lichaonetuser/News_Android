package com.box.app.news.item

import android.view.View
import com.box.app.news.AppHelper
import com.box.app.news.R
import com.box.app.news.analytics.AnalyticsHelper
import com.box.app.news.analytics.AnalyticsKey
import com.box.app.news.bean.Channel
import com.box.app.news.event.EventManager
import com.box.app.news.event.refresh.NewsListRefreshEvent
import com.box.common.core.analytics.AnalyticsManager
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.box.common.extension.widget.recycler.holder.BaseViewHolder
import com.box.common.extension.widget.recycler.item.BaseItem
import eu.davidea.flexibleadapter.items.IHeader

class LastReadItem(mBean: Channel)
    : BaseItem<Channel, LastReadItem.ViewHolder>(mBean), IHeader<LastReadItem.ViewHolder> {

    override fun getLayoutRes(): Int {
        return R.layout.item_last_read
    }

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {

    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter, mBean)
    }

    class ViewHolder(view: View, mCommonAdapter: CommonRecyclerAdapter, private val mChannel: Channel) : BaseViewHolder(view, mCommonAdapter) {
        init {
            itemView.setOnClickListener {
                AnalyticsManager.logEvent(AppHelper.currentMainTab.getAnalyticsEventKey(), AnalyticsKey.Parameter.CLICK_LATELY_READ_TO_REFRESH)
                EventManager.post(NewsListRefreshEvent(mChannel))
            }
        }
    }

}