package com.box.app.news.page.mvp.layer.main.list.comment.my

import com.box.app.news.bean.base.BaseNewsBean
import com.box.common.extension.app.mvp.loading.list.MVPListContract
import com.box.common.extension.widget.recycler.item.BaseItem

interface MyCommentContract {

    interface View : MVPListContract.View

    interface Presenter<in V : View> : MVPListContract.Presenter<V>

}