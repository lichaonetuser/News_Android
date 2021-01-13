package com.mynews.app.news.page.mvp.layer.main.channel

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import com.kongzue.dialog.v2.TipDialog
import com.kongzue.dialog.v2.WaitDialog
import com.mynews.app.news.R
import com.mynews.app.news.analytics.AnalyticsKey
import com.mynews.app.news.bean.Channel
import com.mynews.app.news.bean.ChannelInput
import com.mynews.app.news.data.DataDictionary
import com.mynews.app.news.data.DataManager
import com.mynews.app.news.event.EventManager
import com.mynews.app.news.event.change.HideOrShowNewChannelTipEvent
import com.mynews.app.news.event.refresh.ChannelListChangeEvent
import com.mynews.app.news.page.Adapter.MyGridAdapter
import com.mynews.app.news.util.ReddotUtils
import com.mynews.common.core.CoreApp
import com.mynews.common.core.analytics.AnalyticsManager
import com.mynews.common.core.rx.schedulers.io
import com.mynews.common.core.rx.schedulers.ioToMain
import com.mynews.common.core.util.ResUtils
import com.mynews.common.core.widget.CoreImageView
import com.mynews.common.extension.app.mvp.loading.MVPLoadingFragment
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_select_channel.*
import org.jetbrains.anko.dip
import org.jetbrains.anko.find
import org.jetbrains.anko.image
import java.util.*

/**
 *
 */

class ChannelEditFragment : MVPLoadingFragment<ChannelEditContract.View,
        ChannelEditContract.Presenter<ChannelEditContract.View>>(), ChannelEditContract.View , MyGridAdapter.OnItemClick {

    override val mPresenter = ChannelEditPresenter()
    override val mLayoutRes: Int = R.layout.activity_select_channel

    companion object {
        val dp44 = CoreApp.getInstance().dip(44) //单个item的高度
        val dp150 = CoreApp.getInstance().dip(150)
    }

    private var originChannels = arrayListOf<Channel>()//现有频道
    private var currentChannels = arrayListOf<Channel>()//现有频道，可以修改
    private var allChannels = arrayListOf<Channel>()//未添加频道
    private var temporaryChannels = arrayListOf<Channel>()//临时频道

    private var isUploading = false

    private var lastNewChannelIndex = -1
    private lateinit var adapter1: MyGridAdapter
    private lateinit var adapter2: MyGridAdapter

    override fun initView(view: View?, savedInstanceState: Bundle?) {

        AnalyticsManager.logEvent(AnalyticsKey.Event.ARTICLE_CHANNEL_EDIT, AnalyticsKey.Parameter.ENTER)

        title_bar.setTitle(ResUtils.getString(R.string.edit_channel))

        title_bar.findViewById<CoreImageView>(R.id.back_btn).setOnClickListener {
            onBackPressedSupport()
        }

        if (DataManager.Memory.getChannelList().articleChannels.isNotEmpty()
                && DataManager.Memory.getRecommendChannelList().articleChannels.isNotEmpty()) {
            val selectedChannelInCache = arrayListOf<Channel>()
            selectedChannelInCache.addAll(DataManager.Memory.getChannelList().articleChannels)
            selectedChannelInCache.removeAt(0)//去掉推荐频道
            val recommendChannelInCache = arrayListOf<Channel>()
            recommendChannelInCache.addAll(DataManager.Memory.getRecommendChannelList().articleChannels)
            originChannels.addAll(selectedChannelInCache)
            currentChannels.addAll(selectedChannelInCache)
            allChannels.addAll(recommendChannelInCache)
            addSelectedChannels(selectedChannelInCache)
            addAllChannels(recommendChannelInCache)
        }
        initChannelFromNet()

        EventManager.post(HideOrShowNewChannelTipEvent(false))
    }

    private fun initChannelFromNet() {
        showLoading()
        DataManager.Remote.getChannelList()
                .ioToMain()
                .subscribeBy(onNext = {
                    Log.d("channel", it.recommendChannels.toString())
                    Log.d("channel", it.selectedChannels.toString())
                    originChannels.clear()
                    currentChannels.clear()
                    allChannels.clear()

                    originChannels.addAll(it.selectedChannels.articleChannels)
                    currentChannels.addAll(it.selectedChannels.articleChannels)
                    allChannels.addAll(it.recommendChannels.articleChannels)
                    //推荐频道由客户端自行添加
                    val articleRecommendChannel = Channel(DataDictionary.CHANNEL_ID_RECOMMEND_ARTICLE, ResUtils.getString(R.string.Channel_ForYou), index = 0)
                    currentChannels.add(0,articleRecommendChannel)
                    originChannels.add(0,articleRecommendChannel)
                    addSelectedChannels(originChannels)
                    addAllChannels(allChannels)
                    hideLoading()

                    scrollToLastNewChannel()
                }, onError = {
                    hideLoading()
                    //请求失败后使用
                    TipDialog.show(CoreApp.getLastBaseActivityInstance()
                            ?: return@subscribeBy,
                            ResUtils.getString(R.string.Tip_ServerError),
                            TipDialog.SHOW_TIME_SHORT, TipDialog.TYPE_ERROR)
                    Log.d("channel_error", it.localizedMessage)

                    scrollToLastNewChannel()
                })
    }

    private fun hasChannelChanged(): Boolean {
        if (originChannels.size != currentChannels.size) {
            return true
        }
        repeat(originChannels.size) {
            if (originChannels[it].chid != currentChannels[it].chid) {
                return true
            }
        }
        return false
    }

    private fun saveChannel() {
        showLoading()

        val input = ChannelInput()

        for (item in currentChannels) {
            if (!item.chid.equals("__for_you__")){
                input.clientChannels.channels.add(item.chid)
            }
        }
        input.clientChannels.mtime = System.currentTimeMillis()

        DataManager.Remote.saveClientChannelList(input)
                .ioToMain()
                .subscribeBy(onNext = {
                    hideLoading()

                    val articleRecommendChannel = Channel(DataDictionary.CHANNEL_ID_RECOMMEND_ARTICLE, ResUtils.getString(R.string.Channel_ForYou), index = 0)
                    currentChannels.add(0, articleRecommendChannel)
                    val origin = DataManager.Memory.getChannelList()
                    origin.articleChannels = currentChannels

                    DataManager.Memory.putChannelList(origin)

                    Observable.just(currentChannels).io().subscribe {
                        DataManager.Local.saveChannelList(currentChannels)
                        DataManager.Local.saveFileChannelList(DataManager.Memory.getChannelList())
                    }

                    EventManager.post(ChannelListChangeEvent(currentChannels))

                    isUploading = false

                    super.back()
                }, onError = {
                    hideLoading()
                    Log.d("save_channel_error", it.localizedMessage)
                    TipDialog.show(CoreApp.getLastBaseActivityInstance()
                            ?: return@subscribeBy,
                            ResUtils.getString(R.string.Tip_ServerError),
                            TipDialog.SHOW_TIME_SHORT, TipDialog.TYPE_ERROR)
                    isUploading = false

                    super.back()
                })
    }

    @SuppressLint("InflateParams")
    private fun getChannelView(): View {
        val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return inflater.inflate(R.layout.item_selected_channel, null)
    }

//    private fun addSelectedChannel(channel: Channel) {
//        val view = getChannelView()
//        val iconView = view.find<ImageView>(R.id.select_channel_delete_icon)
//        iconView.image = SELECT_CHANNEL_ICON_DELETE
//        view.find<TextView>(R.id.selected_channel_tv).text = channel.name
//        selected_channels.addView(view)
//        selected_channels.setViewDraggable(view, view.find<ImageView>(R.id.drag_enabler))
//
//        view.tag = channel.chid
//        iconView.setOnClickListener {
//            updateAllChannel(channel.chid)
//        }
//    }

//    private fun deleteSelectedChannel(chid: String) {
//        val channel = currentChannels.filter { it.chid == chid }[0]
//        currentChannels.remove(channel)
//
//        repeat(selected_channels.childCount) {
//            val v = selected_channels.getChildAt(it)
//            if (chid == v.tag as String) {
//                selected_channels.removeDragView(v)
//                return
//            }
//        }
//    }

    private fun addSelectedChannels(selectedChannels: ArrayList<Channel>) {
//        selected_channels.removeAllViews()
        adapter1 = MyGridAdapter(_mActivity, 0)
        drag_grid_view.adapter = adapter1
        adapter1.listBean = selectedChannels
        adapter1.setOnItemClick(this)
//        selectedChannels.forEach {
//            addSelectedChannel(it)
//        }
    }

    private fun addAllChannels(allChannels: ArrayList<Channel>) {
//        selected_channels.removeAllViews()
        adapter2 = MyGridAdapter(_mActivity, 1)
        gv_channels_container.adapter = adapter2
        adapter2.listBean = allChannels
        adapter2.setOnItemClick(this)
    }

//    private fun addAllChannels(allChannels: ArrayList<Channel>, fromNet: Boolean = false) {
//        all_channels_container.removeAllViews()
//        var i = 0
//        allChannels.forEach { it ->
//            val view = getChannelView()
//            val chid = it.chid
//
//            val drawable = if (hasSelected(chid)) SELECT_CHANNEL_ICON_ADDED else SELECT_CHANNEL_ICON_ADD
//            view.find<ImageView>(R.id.select_channel_delete_icon).image = drawable
//            view.find<TextView>(R.id.selected_channel_tv).text = it.name
//            view.find<ImageView>(R.id.drag_enabler).visibility = INVISIBLE
//
//            view.tag = chid
//            view.setOnClickListener { updateAllChannel(chid) }
//            all_channels_container.addView(view)
//
//            if (!ReddotUtils.containRedDot(it.redDot.toString()) && fromNet) {
//                ReddotUtils.addRedDot(it.redDot.toString())
//                lastNewChannelIndex = i
//                view.find<ImageView>(R.id.new_channel_tip_icon).visibility = VISIBLE
//            }
//            i++
//        }
//    }

//    private fun updateAllChannel(chid: String) {
//        //全部频道的列表里是否含有当前的频道
//        var allChannelContains = false
//
//        //如果目标频道在全部频道里
//        repeat(all_channels_container.childCount) {
//            val v = all_channels_container.getChildAt(it)
//            val iv = v.find<ImageView>(R.id.select_channel_delete_icon)
//            if (chid == v.tag as String) {
//                allChannelContains = true
//                if (iv.image == SELECT_CHANNEL_ICON_ADDED) {
//                    iv.image = SELECT_CHANNEL_ICON_ADD
//                    deleteSelectedChannel(chid)
//
//                    AnalyticsManager.logEvent(AnalyticsKey.Event.ARTICLE_CHANNEL_EDIT, AnalyticsKey.Parameter.DELETE_MY_CHANNEL)
//
//                } else {
//                    iv.image = SELECT_CHANNEL_ICON_ADDED
//                    val channel = allChannels.filter { it.chid == chid }[0]
//                    currentChannels.add(channel)
//                    addSelectedChannel(channel)
//
//                    AnalyticsManager.logEvent(AnalyticsKey.Event.ARTICLE_CHANNEL_EDIT, AnalyticsKey.Parameter.ADD_CANDIDATE_CHANNEL)
//                }
//                return
//            }
//        }
//        //如果全部频道里面不含目前频道，则说明该频道已经下架或者因为其他原因已经不在全部频道里了，就应该从已经选择的频道里删除
//        if (!allChannelContains) {
//            deleteSelectedChannel(chid)
//
//            AnalyticsManager.logEvent(AnalyticsKey.Event.ARTICLE_CHANNEL_EDIT, AnalyticsKey.Parameter.DELETE_MY_CHANNEL)
//        }
//    }

//    private fun hasSelected(chid: String): Boolean {
//        originChannels.forEach {
//            if (it.chid == chid) {
//                return true
//            }
//        }
//        return false
//    }

    private fun showLoading() {
        WaitDialog.show(CoreApp.getLastBaseActivityInstance(), ResUtils.getString(R.string.Tip_Loading))
    }

    private fun hideLoading() {
        WaitDialog.dismiss()
    }

    override fun onBackPressedSupport(): Boolean {
        if (isUploading) {
            return true
        }
        isUploading = true
        return if (hasChannelChanged()) {
            saveChannel()
            true
        } else {
            super.onBackPressedSupport()
        }
    }

    //滑动到所有频道里最后一个新频道处
    private fun scrollToLastNewChannel(){
        channel_scroll_view?.scrollTo(0,  0)
//        if (lastNewChannelIndex == -1) {
//            return
//        }
//        val parentTop = all_channels_description.height + has_added_channels_description.height + dp150
//        val lastChannelTop = parentTop + (currentChannels.size + lastNewChannelIndex) * dp44
//        val wholeHeight = parentTop + (currentChannels.size + allChannels.size) * dp44 + dp44
//        val scrollViewHeight = channel_root_view.height - title_bar.bottom
//        val scrollEnd = if (wholeHeight - lastChannelTop > scrollViewHeight) {
//            lastChannelTop
//        } else {
//            wholeHeight - scrollViewHeight
//        }
//
//        //滑动到最后一个新频道处
//        val animator = ValueAnimator.ofInt(0, scrollEnd)
//        animator.duration = 300
//        animator.addUpdateListener {
//            try {
//                channel_scroll_view?.scrollTo(0, it.animatedValue as? Int ?: 0)
//                if (it.animatedValue as? Int ?: 0 == scrollEnd) {
//                    channel_scroll_view?.isEnabled = true
//                }
//            } catch (e: Exception) {
//                channel_scroll_view?.isEnabled = true
//            }
//        }
//        animator.startDelay = 200//等待布局结束动画才开始
//        animator.start()
    }

    override fun onItemClick(type: Int, position: Int) {
//            updateAllChannel(channel.chid)
        if (type == 0){
            currentChannels.removeAll(currentChannels)
            temporaryChannels.addAll(adapter1.listBean)
            var mChannel : Channel = temporaryChannels.get(position)
            allChannels.add(mChannel)
            temporaryChannels.removeAt(position)
            currentChannels.addAll(temporaryChannels)
            adapter1.listBean = currentChannels
            adapter2.listBean = allChannels
            temporaryChannels.removeAll(temporaryChannels)
//            temporaryChannels.addAll(adapter1.listBean)
//            var mChannel : Channel = temporaryChannels.get(position)
//            originChannels.removeAt(position)
//            allChannels.add(mChannel)
//            temporaryChannels.removeAll(temporaryChannels)
//            adapter1.listBean = originChannels
//            adapter2.listBean = allChannels
//            currentChannels.addAll(originChannels)
        }else{
            temporaryChannels.addAll(adapter2.listBean)
            var mChannel : Channel = temporaryChannels.get(position)
            allChannels.removeAt(position)
            currentChannels.add(mChannel)
            temporaryChannels.removeAt(position)
            temporaryChannels.removeAll(temporaryChannels)
            adapter1.listBean = currentChannels
            adapter2.listBean = allChannels
        }
    }
}
