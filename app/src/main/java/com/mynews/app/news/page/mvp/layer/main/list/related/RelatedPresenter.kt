package com.mynews.app.news.page.mvp.layer.main.list.related

import com.mynews.app.news.analytics.AnalyticsKey
import com.mynews.app.news.applog.AppLogKey
import com.mynews.app.news.bean.*
import com.mynews.app.news.bean.base.BaseNewsBean
import com.mynews.app.news.data.adapter.bundle.AppLogReferConverter
import com.mynews.app.news.item.factory.ItemFactory
import com.mynews.app.news.page.mvp.layer.main.article.detail.ArticleDetailFragment
import com.mynews.app.news.page.mvp.layer.main.article.detail.ArticleDetailPresenterAutoBundle
import com.mynews.app.news.page.mvp.layer.main.essay.EssayDetailFragment
import com.mynews.app.news.page.mvp.layer.main.essay.EssayDetailPresenterAutoBundle
import com.mynews.app.news.page.mvp.layer.main.gif.GifDetailFragment
import com.mynews.app.news.page.mvp.layer.main.gif.GifDetailPresenterAutoBundle
import com.mynews.app.news.page.mvp.layer.main.image.detail.ImageDetailFragment
import com.mynews.app.news.page.mvp.layer.main.image.detail.ImageDetailPresenterAutoBundle
import com.mynews.app.news.page.mvp.layer.main.video.detail.VideoDetailFragment
import com.mynews.app.news.page.mvp.layer.main.video.detail.VideoDetailPresenterAutoBundle
import com.mynews.app.news.proto.AppLog
import com.mynews.common.core.analytics.AnalyticsManager
import com.mynews.common.extension.app.mvp.loading.list.MVPListPresent
import com.mynews.common.extension.widget.recycler.item.BaseItem
import com.mynews.common.extension.widget.recycler.util.convertBeansToItems
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.yatatsu.autobundle.AutoBundleField
import io.reactivex.Observable
import me.yokeyword.fragmentation.SupportFragment

class RelatedPresenter : MVPListPresent<RelatedContract.View>(),
        RelatedContract.Presenter<RelatedContract.View> {

    @AutoBundleField
    @JvmField
    var mRelatedNews: ArrayList<BaseNewsBean> = arrayListOf()
    @AutoBundleField
    lateinit var mAnalyticsEventKey: String
    @AutoBundleField(converter = AppLogReferConverter::class)
    var mRefer: AppLog.Refer = AppLog.Refer.getDefaultInstance()
    @AutoBundleField(required = false)
    var mParentChannel: Channel = Channel()

    override fun onEnterEnd() {
        super.onEnterEnd()
        loadData(0)
    }

    override fun loadData(pageNum: Int, isRefresh: Boolean) {
        val relatedNews = mRelatedNews
        loadDataComplete(Observable.just(relatedNews)
                .onErrorResumeNext(Observable.empty())
                .convertBeansToItems(ItemFactory.RELATED)
                .blockingSingle())
    }

    override fun onItemClick(position: Int, item: BaseItem<*, *>): Boolean {
        GSYVideoManager.releaseAllVideos()
        val bean = item.getModel(BaseNewsBean::class.java) ?: return false
        when (bean) {
            is Video -> {
                AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_RELATED_VIDEO)
                GSYVideoManager.releaseAllVideos()
                mView?.goFromRoot(VideoDetailFragment::class.java, VideoDetailPresenterAutoBundle
                        .builder(bean, false, AppLog.Refer.newBuilder(mRefer)
                                .setName(AppLogKey.Refer.RELATE).build())
                        .mChannel(mParentChannel)
                        .bundle(), SupportFragment.STANDARD)
            }
            is Article -> {
                AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_RELATED_NEWS)
                mView?.goFromRoot(ArticleDetailFragment::class.java, ArticleDetailPresenterAutoBundle
                        .builder(bean, false, AppLog.Refer.newBuilder(mRefer)
                                .setName(AppLogKey.Refer.RELATE).build())
                        .mChannel(mParentChannel)
                        .bundle(), SupportFragment.STANDARD)
            }
            is Image -> {
                mView?.goFromRoot(ImageDetailFragment::class.java, ImageDetailPresenterAutoBundle
                        .builder(bean, false, AppLog.Refer.newBuilder(mRefer)
                                .setName(AppLogKey.Refer.RELATE).build())
                        .mChannel(mParentChannel)
                        .bundle(), SupportFragment.STANDARD)
            }
            is GIF -> {
                mView?.goFromRoot(GifDetailFragment::class.java, GifDetailPresenterAutoBundle
                        .builder(bean, false, AppLog.Refer.newBuilder(mRefer)
                                .setName(AppLogKey.Refer.RELATE).build())
                        .mChannel(mParentChannel)
                        .bundle(), SupportFragment.STANDARD)
            }
            is Essay -> {
                mView?.goFromRoot(EssayDetailFragment::class.java, EssayDetailPresenterAutoBundle
                        .builder(bean, false, AppLog.Refer.newBuilder(mRefer)
                                .setName(AppLogKey.Refer.RELATE).build())
                        .mChannel(mParentChannel)
                        .bundle(), SupportFragment.STANDARD)
            }
        }
        return true
    }

}

