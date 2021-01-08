package com.mynews.app.news.item

import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.bean.ArticleHeadline
import com.mynews.app.news.item.base.BaseNewsItem
import com.mynews.app.news.item.factory.ItemFactory
import com.mynews.common.core.image.fresco.ImageManager
import com.mynews.common.core.util.ResUtils
import com.mynews.common.core.util.extension.format2DateString
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.mynews.common.extension.widget.recycler.item.BaseItem
import com.mynews.common.extension.widget.recycler.util.convertBeansToItems
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IHeader
import io.reactivex.Observable
import kotlinx.android.synthetic.main.item_article_headline.view.*

class ArticleHeadlineItem(mBean: ArticleHeadline)
    : BaseItem<ArticleHeadline, ArticleHeadlineItem.ViewHolder>(mBean), IHeader<ArticleHeadlineItem.ViewHolder> {

    private var mCurrentAdapter : CommonRecyclerAdapter = CommonRecyclerAdapter()
    private var mCurrentLayoutManager : LinearLayoutManager = LinearLayoutManager(null, LinearLayoutManager.VERTICAL, false)

    override fun getLayoutRes(): Int {
        return R.layout.item_article_headline
    }

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        // 加载失败默认加载本地图片
        ImageManager.with(holder.headlineIconImg).load(mBean.icon)

        if (mBean.displayTime != 0L) {
            holder.headlineTimeTxt.text = mBean.displayTime.format2DateString("yyyy-MM-dd").substring(5)
            holder.headlineTimeTxt.visibility = View.VISIBLE
            holder.headlineTimeIconImg.visibility = View.VISIBLE
        } else {
            holder.headlineTimeTxt.visibility = View.GONE
            holder.headlineTimeIconImg.visibility = View.GONE
        }

        if (mBean.title.isEmpty()) {
            holder.headlineGoTxt.text = ResUtils.getString(R.string.WorldCup2018_Video_SlideForMore)
        } else {
            holder.headlineGoTxt.text = mBean.title
        }

        holder.headlineChildListRv.setHasFixedSize(true)
        holder.headlineChildListRv.layoutManager = mCurrentLayoutManager
        holder.headlineChildListRv.adapter = mCurrentAdapter

        val news = mBean.news
                .distinct()
                .toMutableList()

        val items = Observable.just(news)
                .convertBeansToItems(ItemFactory.NEWS)
                .blockingFirst()
        mCurrentAdapter.updateDataSet(items, false)
        mCurrentAdapter.mItemClickListener = FlexibleAdapter.OnItemClickListener {
            news[it].isRead = true
            mCurrentAdapter.notifyItemChanged(it)
            adapter.mItemChildItemClickListener?.onItemChildItemClick(it, items[it], news[it])
            true
        }
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter)
    }

    @Suppress("HasPlatformType")
    class ViewHolder(view: View, mCommonAdapter: CommonRecyclerAdapter) : BaseNewsItem.ViewHolder(view, mCommonAdapter) {
        val headlineIconImg = itemView.headline_icon_img
        val headlineTimeTxt = itemView.headline_time_txt
        val headlineChildListRv = itemView.headline_child_list_rv
        private val headlineMoreLayout = itemView.headline_more_layout
        val headlineGoTxt = itemView.headline_go_txt
        val headlineTimeIconImg = itemView.headline_time_icon_img

        init {
            bindItemChildViewClick(headlineMoreLayout)
        }
    }

}