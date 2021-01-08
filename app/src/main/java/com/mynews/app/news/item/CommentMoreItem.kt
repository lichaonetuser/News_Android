package com.mynews.app.news.item

import android.view.View
import com.mynews.app.news.R
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.mynews.common.extension.widget.recycler.holder.BaseViewHolder
import com.mynews.common.extension.widget.recycler.item.BaseItem
import kotlinx.android.synthetic.main.item_more.view.*
import java.io.Serializable

class CommentMoreItem(mBean: More)
    : BaseItem<CommentMoreItem.More, CommentMoreItem.ViewHolder>(mBean) {

    class More(val type: Type, val text: String) : Serializable {
        var openUrl: String? = null

        enum class Type {
            NETWORK_COMMENT,
            REPLY,
            DELETE,
            REPORT
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as More

            if (type != other.type) return false

            return true
        }

        override fun hashCode(): Int {
            return type.hashCode()
        }


    }

    override fun getLayoutRes(): Int {
        return R.layout.item_more_comment
    }

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        holder.more_txt.text = mBean.text
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter)
    }

    class ViewHolder(view: View, mCommonAdapter: CommonRecyclerAdapter) : BaseViewHolder(view, mCommonAdapter) {
        val more_txt = itemView.more_txt
    }

}