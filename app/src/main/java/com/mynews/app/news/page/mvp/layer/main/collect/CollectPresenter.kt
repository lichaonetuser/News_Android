package com.mynews.app.news.page.mvp.layer.main.collect

import android.os.Bundle
import com.mynews.app.news.R
import com.mynews.app.news.analytics.AnalyticsKey
import com.mynews.app.news.applog.AppLogKey
import com.mynews.app.news.bean.*
import com.mynews.app.news.bean.base.BaseNewsBean
import com.mynews.app.news.bean.list.ListFavorite
import com.mynews.app.news.data.DataAction
import com.mynews.app.news.data.DataDictionary
import com.mynews.app.news.data.DataManager
import com.mynews.app.news.event.EventManager
import com.mynews.app.news.event.change.CollectionListChangeEvent
import com.mynews.app.news.event.change.FontSizeChangeEvent
import com.mynews.app.news.event.change.NewsListChangeEvent
import com.mynews.app.news.item.base.*
import com.mynews.app.news.item.factory.ItemFactory
import com.mynews.app.news.item.payload.NewsPayload
import com.mynews.app.news.page.mvp.layer.main.article.detail.ArticleDetailFragment
import com.mynews.app.news.page.mvp.layer.main.article.detail.ArticleDetailPresenterAutoBundle
import com.mynews.app.news.page.mvp.layer.main.essay.EssayDetailFragment
import com.mynews.app.news.page.mvp.layer.main.essay.EssayDetailPresenterAutoBundle
import com.mynews.app.news.page.mvp.layer.main.gif.GifDetailFragment
import com.mynews.app.news.page.mvp.layer.main.gif.GifDetailPresenterAutoBundle
import com.mynews.app.news.page.mvp.layer.main.image.browser.ImageBrowserFragment
import com.mynews.app.news.page.mvp.layer.main.image.browser.ImageBrowserPresenterAutoBundle
import com.mynews.app.news.page.mvp.layer.main.image.detail.ImageDetailFragment
import com.mynews.app.news.page.mvp.layer.main.image.detail.ImageDetailPresenterAutoBundle
import com.mynews.app.news.page.mvp.layer.main.video.detail.VideoDetailFragment
import com.mynews.app.news.page.mvp.layer.main.video.detail.VideoDetailPresenterAutoBundle
import com.mynews.app.news.proto.AppLog
import com.mynews.app.news.util.GSYVideoTransferUtils
import com.mynews.app.news.util.checkHasMore
import com.mynews.common.core.analytics.AnalyticsManager
import com.mynews.common.core.rx.schedulers.ioToMain
import com.mynews.common.core.util.ResUtils
import com.mynews.common.core.util.extension.distinctWith
import com.mynews.common.extension.app.mvp.bindToLifecycle
import com.mynews.common.extension.app.mvp.loading.list.MVPListPresent
import com.mynews.common.extension.widget.recycler.decoration.CommonItemDecoration
import com.mynews.common.extension.widget.recycler.item.BaseItem
import com.mynews.common.extension.widget.recycler.item.DefaultNoMoreItem
import com.mynews.common.extension.widget.recycler.util.convertBeansToItems
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.yatatsu.autobundle.AutoBundleField
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import me.yokeyword.fragmentation.anim.FragmentAnimator
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class CollectPresenter : MVPListPresent<CollectContract.View>(),
        CollectContract.Presenter<CollectContract.View> {

    @AutoBundleField(required = false)
    var mListFavorites = ListFavorite()

    val decoration = CommonItemDecoration().withDivider(R.drawable.divider_common)

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        AnalyticsManager.logEvent(AnalyticsKey.Event.FAVORITE_PAGE, AnalyticsKey.Parameter.ENTER)
        EventManager.register(this)
        val decoration = CommonItemDecoration().withDivider(R.drawable.divider_common).withDrawDividerOnLastItem(true).setPadding(10)
        mView?.setCommonItemDecoration(decoration)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventManager.unregister(this)
    }

    override fun onEnterEnd() {
        super.onEnterEnd()
        when {
            isNotRestore() -> {
                loadData(getAdapter()?.loadMoreCurrentPage?.plus(1) ?: 1)
            }
            isRestore() && mListFavorites.favoriteNews.isNotEmpty() -> {
                mView?.setLoadMoreEnable(mListFavorites.hasMore)
                loadDataComplete(Observable.just(mListFavorites.favoriteNews)
                        .convertBeansToItems(ItemFactory.NEWS)
                        .blockingSingle())
            }
        }
        mView?.refreshDeleteNum()
    }

    override fun loadData(pageNum: Int, isRefresh: Boolean) {
        DataManager.Remote.getFavorite(pageNo = pageNum)
                .checkHasMore(getAdapter())
                .map { it: ListFavorite ->
                    val currentFavorites = mListFavorites.favoriteNews.toMutableList()
                    val loadFavorites = it.favoriteNews
                            .distinctWith(currentFavorites)
                            .filter { checkDataTypeValid(it.type) }
                    mListFavorites.favoriteNews.addAll(loadFavorites)
                    mListFavorites.hasMore = it.hasMore
                    loadFavorites
                }
                .convertBeansToItems(ItemFactory.COLLECT)
                .ioToMain()
                .bindToLifecycle(this)
                .subscribeBy(
                        onNext = { items ->
                            if (items.isEmpty() && mListFavorites.hasMore) {
                                val adapter = getAdapter() ?: return@subscribeBy
                                adapter.loadMoreCurrentPage++
                                loadData(adapter.loadMoreCurrentPage + 1, false)
                                return@subscribeBy
                            }

                            items.forEach { (it as? BaseNewsItem)?.isFavoriteStyle = true }

                            mView?.onLoadMoreComplete(items)
                            if (!mListFavorites.hasMore && items.isNotEmpty()) {
                                getAdapter()?.addItem(DefaultNoMoreItem())
                            }
                            mView?.setCommonItemDecoration(decoration)
                        },
                        onError = { _ ->
                            loadDataFail()
                        })
    }

    private fun checkDataTypeValid(type: Int): Boolean {
        return type == DataDictionary.NewsType.ARTICLE.value
                || type == DataDictionary.NewsType.VIDEO.value
                || type == DataDictionary.NewsType.IMAGE.value
                || type == DataDictionary.NewsType.MULTIPLEIMAGE.value
                || type == DataDictionary.NewsType.GIF.value
                || type == DataDictionary.NewsType.ESSAY.value
    }

    override fun onClickEditButton() {
        getAdapter()?.toggleEditing(false)
        val isEditing: Boolean = getAdapter()?.isEditing() ?: false
        getAdapter()?.notifyItemRangeChanged(0, getAdapter()?.itemCount
                ?: 0, NewsPayload.UPDATE_EDIT)
        mView?.setDeleteButtonShow(isEditing)
        mView?.refreshDeleteNum()
        getAdapter()?.clearSelection()
        if (isEditing) {
            AnalyticsManager.logEvent(AnalyticsKey.Event.FAVORITE_PAGE, AnalyticsKey.Parameter.CLICK_EDIT)
            mView?.setEditBtnText(ResUtils.getString(R.string.Common_Cancel))
        } else {
            mView?.setEditBtnText(ResUtils.getString(R.string.Common_Edit))
        }
    }

    override fun onClickDeleteButton() {
        AnalyticsManager.logEvent(AnalyticsKey.Event.FAVORITE_PAGE, AnalyticsKey.Parameter.EDIT_DELETE)
        val pos = getAdapter()?.selectedPositions ?: return
        val list = pos.map {
            val item = getAdapter()?.getItem(it)
            item?.getModel(BaseNewsBean::class.java) ?: return
        }.toList()

        DataManager.Remote.uncollectNewsList(list, checkChannel = false)
        getAdapter()?.removeItems(pos)
        if (getAdapter()?.itemCount == 1 && getAdapter()?.getItem(0) is DefaultNoMoreItem) {
            getAdapter()?.removeItem(0)
            mView?.setDeleteButtonShow(false)
        }
        mView?.refreshDeleteNum()
    }

    override fun onItemClick(position: Int, item: BaseItem<*, *>): Boolean {
        val isEditing: Boolean = mView?.getCommonAdapter()?.isEditing() ?: false
        if (isEditing) {
            getAdapter()?.toggleSelection(position)
            mView?.refreshDeleteNum()
        } else {
            AnalyticsManager.logEvent(AnalyticsKey.Event.FAVORITE_PAGE, AnalyticsKey.Parameter.CLICK_NEWS)
            when (item) {
                is BaseArticleItem -> {
                    GSYVideoManager.releaseAllVideos()
                    val article = item.getModel()
                    mView?.go(ArticleDetailFragment::class.java, ArticleDetailPresenterAutoBundle
                            .builder(article, false, AppLog.Refer.newBuilder()
                                    .setName(AppLogKey.Refer.FAVORITE)
                                    .build())
                            .bundle())
                }
                is BaseVideoItem -> {
                    GSYVideoTransferUtils.prepareTransfer()
                    GSYVideoManager.releaseAllVideos()
                    val video = item.getModel()
                    mView?.goFromRoot(VideoDetailFragment::class.java, VideoDetailPresenterAutoBundle
                            .builder(video, false, AppLog.Refer.newBuilder()
                                    .setName(AppLogKey.Refer.FAVORITE)
                                    .build())
                            .bundle())
                }
                is BasePictureItem -> {
                    GSYVideoManager.releaseAllVideos()
                    val image = item.getModel()
                    mView?.goFromRoot(ImageDetailFragment::class.java, ImageDetailPresenterAutoBundle
                            .builder(image, false, AppLog.Refer.newBuilder()
                                    .setName(AppLogKey.Refer.FAVORITE)
                                    .build())
                            .bundle())
                }
                is BaseGifItem -> {
                    GSYVideoManager.releaseAllVideos()
                    val gif = item.getModel()
                    mView?.goFromRoot(GifDetailFragment::class.java, GifDetailPresenterAutoBundle
                            .builder(gif, false, AppLog.Refer.newBuilder()
                                    .setName(AppLogKey.Refer.FAVORITE)
                                    .build())
                            .bundle())
                }
                is BaseEssayItem -> {
                    GSYVideoManager.releaseAllVideos()
                    val essay = item.getModel()
                    mView?.goFromRoot(EssayDetailFragment::class.java, EssayDetailPresenterAutoBundle
                            .builder(essay, false, AppLog.Refer.newBuilder()
                                    .setName(AppLogKey.Refer.FAVORITE)
                                    .build())
                            .bundle())
                }
            }
        }
        return true
    }

    override fun onItemChildClick(position: Int, item: BaseItem<*, *>, id: Int): Boolean {
        val isEditing: Boolean = mView?.getCommonAdapter()?.isEditing() ?: false
        if (isEditing) {
            getAdapter()?.toggleSelection(position)
            getAdapter()?.notifyItemChanged(position, -1)
            mView?.refreshDeleteNum()
            return true
        }
        val bean = item.getModel() as? BaseNewsBean ?: return true
        when (item) {
            is BasePictureItem -> {
                when (id) {
                    R.id.news_img -> {
                        val image = bean as? Image ?: return true
                        AnalyticsManager.logEvent("", AnalyticsKey.Parameter.CLICK_IMAGE_TO_VIEWER)
                        mView?.goFromRoot(
                                clazz = ImageBrowserFragment::class.java,
                                bundle = ImageBrowserPresenterAutoBundle
                                        .builder(ArrayList(image.info.urls), 0)
                                        .mNews(image)
                                        .mAnalyticsEventKey("")
                                        .mRefer(AppLog.Refer.newBuilder()
                                                .setName(AppLogKey.Refer.ARTICLE_LIST)
                                                .build())
                                        .bundle(),
                                fragmentAnimator = FragmentAnimator(
                                        R.anim.core_fade_in,
                                        R.anim.core_fade_out,
                                        R.anim.no_anim,
                                        R.anim.core_fade_out))
                    }
                    R.id.mutiple_news_img0,
                    R.id.mutiple_news_img1,
                    R.id.mutiple_news_img2,
                    R.id.mutiple_news_img3 -> {
                        val index = when (id) {
                            R.id.mutiple_news_img0 -> 0
                            R.id.mutiple_news_img1 -> 1
                            R.id.mutiple_news_img2 -> 2
                            else -> 3
                        }
                        val image = bean as? Image ?: return true
                        val images = if (image.images.isEmpty()) image.info.urls else {
                            image.images.map { it.urls.firstOrNull()!! }
                        }
                        AnalyticsManager.logEvent("", AnalyticsKey.Parameter.CLICK_IMAGE_TO_VIEWER)
                        mView?.goFromRoot(
                                clazz = ImageBrowserFragment::class.java,
                                bundle = ImageBrowserPresenterAutoBundle
                                        .builder(ArrayList(images), index)
                                        .mNews(image)
                                        .mAnalyticsEventKey("")
                                        .mRefer(AppLog.Refer.newBuilder()
                                                .setName(AppLogKey.Refer.ARTICLE_LIST)
                                                .build())
                                        .bundle(),
                                fragmentAnimator = FragmentAnimator(
                                        R.anim.core_fade_in,
                                        R.anim.core_fade_out,
                                        R.anim.no_anim,
                                        R.anim.core_fade_out))
                    }
                }
            }
        }
        return super.onItemChildClick(position, item, id)
    }

    override fun onUpdateEmptyView(size: Int) {
        super.onUpdateEmptyView(size)
        if (size == 0) {
            mView?.setEditBtnText(ResUtils.getString(R.string.Common_Edit))
            mView?.setEditBtnEnabled(false)
        } else {
            mView?.setEditBtnEnabled(true)
        }
    }

    override fun onLoadingLayoutRetryClicked(id: Int) {
        mView?.showLoading()
        loadData(1)
    }

    //收藏列表发生变化的event，修复之前列表没有及时添加/删除新item的bug
    @Subscribe(threadMode = ThreadMode.MAIN)
    @Suppress("unused")
    fun onCollectionListChangeEvent(event: CollectionListChangeEvent) {
        val news = event.news
        news.favoriteTime = System.currentTimeMillis()
        val position = getAdapter()?.currentItems?.indexOfFirst {
            it.getModel(BaseNewsBean::class.java)?.aid == news.aid
        } ?: return
        val index = mListFavorites.favoriteNews.indexOf(news)
        when (event.action) {
            DataAction.INSERT -> {
                if (index < 0) {
                    mListFavorites.favoriteNews.add(0, news)
                }
                if (position < 0) {
                    getAdapter()?.addItem(0, ItemFactory.COLLECT(news))
                }
                getAdapter()?.notifyDataSetChanged()
            }
            DataAction.DELETE -> {
                if (index >= 0 && index < mListFavorites.favoriteNews.size) {
                    mListFavorites.favoriteNews.removeAt(index)
                }
                if (position >= 0 && position < getAdapter()?.itemCount?: 0) {
                    getAdapter()?.removeItem(position)
                }
                getAdapter()?.notifyDataSetChanged()
                if (getAdapter()?.itemCount == 1 && getAdapter()?.getItem(0) is DefaultNoMoreItem) {
                    getAdapter()?.removeItem(0)
                    mView?.setDeleteButtonShow(false)
                }
                mView?.refreshDeleteNum()
            }
            else -> {

            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Suppress("unused")
    fun onNewsListChangeEvent(event: NewsListChangeEvent) {
        val news = event.news
        val position = getAdapter()?.currentItems?.indexOfFirst {
            it.getModel(BaseNewsBean::class.java)?.aid == news.aid
        } ?: return
        val item = getAdapter()?.getItem(position)
        val index = mListFavorites.favoriteNews.indexOf(news)
        if (index < 0 || index >= mListFavorites.favoriteNews.size) {
            return
        }
        when (event.action) {
            DataAction.DELETE -> {
//                mListFavorites.favoriteNews.removeAt(index)
//                getAdapter()?.removeItem(position)
            }
            DataAction.UPDATE -> {
                when (event.extra) {
                    NewsListChangeEvent.EXTRA.NORMAL -> {
                        mListFavorites.favoriteNews[index] = news
                        if (item is BaseArticleItem && news is Article) {
                            item.setModel(news)
                        }
                        if (item is BaseVideoItem && news is Video) {
                            item.setModel(news)
                        }
                        if (item is BasePictureItem && news is Image) {
                            item.setModel(news)
                        }
                        if (item is BaseGifItem && news is GIF) {
                            item.setModel(news)
                        }
                        if (item is BaseEssayItem && news is Essay) {
                            item.setModel(news)
                        }
                        getAdapter()?.notifyItemChanged(position)
                    }
                    NewsListChangeEvent.EXTRA.UPDATE_INFORMATION -> {
                        if (news.isFavorite) {
                            mListFavorites.favoriteNews[index].updateInformation(news)
                            getAdapter()?.notifyItemChanged(position, NewsPayload.UPDATE_INFORMATION)
                        } else {
                            mListFavorites.favoriteNews.removeAt(index)
                            getAdapter()?.removeItem(position)
                            if (getAdapter()?.itemCount == 1 && getAdapter()?.getItem(0) is DefaultNoMoreItem) {
                                getAdapter()?.removeItem(0)
                                mView?.setDeleteButtonShow(false)
                            }
                            mView?.refreshDeleteNum()
                        }
                    }
                }
            }
            else -> {

            }
        }
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onFontSizeChangeEvent(@Suppress("UNUSED_PARAMETER") event: FontSizeChangeEvent) {
        getAdapter()?.notifyDataSetChanged()
    }
}