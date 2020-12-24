package com.box.app.news.item

import android.view.View
import com.box.app.news.R
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.box.common.extension.widget.recycler.holder.BaseViewHolder
import com.box.common.extension.widget.recycler.item.BaseItem
import kotlinx.android.synthetic.main.item_more.view.*
import org.jetbrains.anko.imageResource
import java.io.Serializable

class MoreItem(mBean: More)
    : BaseItem<MoreItem.More, MoreItem.ViewHolder>(mBean) {

    class More(val type: Type, val iconRes: Int, val text: String) : Serializable {

        enum class Type {
            SHARE_FACEBOOK,
            SHARE_TWITTER,
            SHARE_LINE,
            SHARE_COPY,
            SHARE_MESSAGE,
            SHARE_MAIL,
            SHARE_SYSTEM,
            ACTION_DELETE,
            ACTION_REPORT,
            ACTION_COLLECT
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
        return R.layout.item_more
    }

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        holder.more_icon.imageResource = mBean.iconRes
        holder.more_txt.text = mBean.text
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter)
    }

    class ViewHolder(view: View, mCommonAdapter: CommonRecyclerAdapter) : BaseViewHolder(view, mCommonAdapter) {
        val more_txt = itemView.more_txt
        val more_icon = itemView.more_icon

        override fun toggleActivation() {
            super.toggleActivation()
            more_icon.isActivated = mAdapter.isSelected(flexibleAdapterPosition)
        }

    }

}