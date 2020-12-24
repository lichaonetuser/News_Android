package com.box.app.news.item

import android.view.View
import com.box.app.news.R
import com.box.app.news.bean.Feedback
import com.box.app.news.item.base.BaseFeedbackItem
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter

open class FeedbackServiceItem(mBean: Feedback)
    : BaseFeedbackItem<FeedbackServiceItem.ViewHolder>(mBean) {

    override fun getLayoutRes(): Int {
        return R.layout.item_feedback_service
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter)
    }

    class ViewHolder(view: View, adapter: CommonRecyclerAdapter) : BaseFeedbackItem.ViewHolder(view, adapter)
}