package com.box.app.news.page.mvp.layer.main.list.news

import com.box.app.news.bean.Tip
import com.box.app.news.bean.base.BaseNewsBean
import com.box.common.extension.app.mvp.loading.list.MVPListContract
import com.box.common.extension.widget.recycler.item.BaseItem

interface NewsListContract {

    interface View : MVPListContract.View {

        fun showTip(tip: Tip)
        fun showRemoveDialog(news: BaseNewsBean)
        fun scrollCommonRVBy(y: Int)
        fun scrollCommonRVTo(position: Int)
        fun smoothScrollCommonRVTo(position: Int)
        fun setShowRefer(text: String)

    }

    interface Presenter<in V : View> : MVPListContract.Presenter<V> {

        fun onClickRemoveNews(news: BaseNewsBean, isYes: Boolean)
        fun onItemVisibility(position: Int, item: BaseItem<*, *>, visible: Boolean)
        fun onBindItem(position: Int, item: BaseItem<*, *>, payloads: MutableList<Any?>?)
        fun onRefreshScrollFinish(success: Boolean)
        fun onPause()

    }

}