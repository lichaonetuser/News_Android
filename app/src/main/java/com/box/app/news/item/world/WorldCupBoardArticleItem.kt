package com.box.app.news.item.world

import android.annotation.SuppressLint
import android.view.View
import com.box.app.news.R
import com.box.app.news.bean.Comment
import com.box.common.core.image.fresco.ImageManager
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import kotlinx.android.synthetic.main.item_worldcup_board_child_article.view.*

class WorldCupBoardArticleItem(mBean: Comment) : BaseWorldCupBoardItem<WorldCupBoardArticleItem.ViewHolder>(mBean) {

    override fun getLayoutRes(): Int {
        return R.layout.item_worldcup_board_child_article
    }

    @SuppressLint("SetTextI18n")
    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: WorldCupBoardArticleItem.ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        super.bindViewHolder(adapter, holder, position, payloads)
        val article = mBean.article
        if (article == null) {
            holder.content_layout?.visibility = View.GONE
            return
        }

        ImageManager.with(holder.cover_img).load(article.listImageUrls.firstOrNull())
        holder.title_txt.text = article.title
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): WorldCupBoardArticleItem.ViewHolder {
        return ViewHolder(view, adapter)
    }

    class ViewHolder(view: View, adapter: CommonRecyclerAdapter) : BaseWorldCupBoardItem.ViewHolder(view, adapter) {
        val cover_img = itemView.cover_img
        val title_txt = itemView.title_txt
    }

}