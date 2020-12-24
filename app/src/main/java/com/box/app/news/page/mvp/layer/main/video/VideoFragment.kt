package com.box.app.news.page.mvp.layer.main.video

import android.os.Bundle
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import android.view.View
import com.box.app.news.analytics.AnalyticsKey
import com.box.app.news.bean.Channel
import com.box.app.news.data.DataDictionary
import com.box.app.news.data.DataDictionary.CHANNEL_ID_RECOMMEND_VIDEO
import com.box.app.news.page.mvp.layer.main.article.ArticleFragment
import com.box.app.news.page.mvp.layer.main.list.news.NewsListFragment
import com.box.app.news.page.mvp.layer.main.list.news.NewsListPresenterAutoBundle
import com.box.common.core.app.fragment.CoreBaseFragment
import kotlinx.android.synthetic.main.fragment_article.*

class VideoFragment : ArticleFragment<VideoContract.View,
        VideoContract.Presenter<VideoContract.View>>(),
        VideoContract.View {

    override val mPresenter = VideoPresenter()
    override val mArticleTab = false

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        super.initView(view, savedInstanceState)
        hideSearchLayout()
    }


    override fun getListPagerAdapter(channels: List<Channel>): ListPagerAdapter {
        return ListPagerAdapter(channels, childFragmentManager)
    }


    class ListPagerAdapter(private val channels: List<Channel>, fm: FragmentManager)
        : FragmentStatePagerAdapter(fm), ArticleFragment.IChannelPagerAdapter {

        override fun getItem(position: Int): Fragment {
            val channel = channels[position]
            if (channel.chid == CHANNEL_ID_RECOMMEND_VIDEO) {
                channel.channelType = DataDictionary.ChannelType.VIDEO.value
            }
            val bundle = NewsListPresenterAutoBundle
                    .builder(channel, "feed", AnalyticsKey.Event.VIDEO)
                    .bundle()
            return CoreBaseFragment.instantiate(NewsListFragment::class.java, bundle)
        }

        override fun getCount(): Int {
            return channels.size
        }

        override fun getPageTitle(position: Int): CharSequence {
            return channels[position].name
        }

        override fun getPageChannels(): List<Channel> {
            return channels
        }

    }

    //注意：子类父类该方法完全不一致
    //更新换过标题位置的导航头,文章跟视频应该独立
    override fun updateChannels(channels: List<Channel>, position: Int, forceUpdatePostion: Boolean) {
        if (container_vp.adapter is ListPagerAdapter) {
            container_vp.adapter = getListPagerAdapter(channels)
            container_vp.currentItem = position
            mIndicatorNavigator.adapter = NewsListNavigatorAdapter(channels)
            if (forceUpdatePostion) {
                mIndicatorNavigator.onPageSelected(position)
            }
        }
    }
}


