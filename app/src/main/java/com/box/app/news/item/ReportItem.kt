package com.box.app.news.item

import android.view.View
import com.box.app.news.R
import com.box.app.news.data.DataDictionary
import com.box.common.core.util.ResUtils
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.box.common.extension.widget.recycler.holder.BaseViewHolder
import com.box.common.extension.widget.recycler.item.BaseItem
import kotlinx.android.synthetic.main.item_more.view.*
import kotlinx.android.synthetic.main.item_report.view.*
import org.jetbrains.anko.imageResource
import java.io.Serializable

class ReportItem(mBean: ReportReason)
    : BaseItem<ReportItem.ReportReason, ReportItem.ViewHolder>(mBean) {

    class ReportReason(val key: Int, val text: String) : Serializable {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ReportReason

            if (key != other.key) return false
            if (text != other.text) return false

            return true
        }

        override fun hashCode(): Int {
            var result = key
            result = 31 * result + text.hashCode()
            return result
        }

    }

    override fun getLayoutRes(): Int {
        return R.layout.item_report
    }

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        holder.reportReasonTxt.text = mBean.text
        holder.reportReasonTxt.setCompoundDrawables(null, null,
                if (mBean.key == DataDictionary.ReportReasonKey.OTHER ||
                        mBean.key == DataDictionary.ReportReasonKey.COPYRIGHT) {
                    ResUtils.getCompoundDrawable(R.drawable.report_go_ic)
                } else {
                    null
                },
                null)
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter)
    }

    class ViewHolder(view: View, mCommonAdapter: CommonRecyclerAdapter) : BaseViewHolder(view, mCommonAdapter) {
        val reportReasonTxt = itemView.report_reason_txt
    }

}