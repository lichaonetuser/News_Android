package com.box.app.news.page.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.widget.ImageView
import android.widget.TextView
import com.box.app.news.R
import com.box.app.news.bean.Channel
import com.box.app.news.bean.ChannelInput
import com.box.app.news.data.DataDictionary
import com.box.app.news.data.DataManager
import com.box.app.news.event.EventManager
import com.box.app.news.event.refresh.ChannelListChangeEvent
import com.box.common.core.app.activity.CoreBaseActivity
import com.box.common.core.rx.schedulers.io
import com.box.common.core.rx.schedulers.ioToMain
import com.box.common.core.util.ResUtils
import com.box.common.core.widget.CoreImageView
import com.kongzue.dialog.v2.WaitDialog
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_select_channel.*
import org.jetbrains.anko.find
import org.jetbrains.anko.image
import java.util.*

/**
 *
 */

class SelectChannelActivity : CoreBaseActivity() {

    companion object {
        val SELECT_CHANEL_ICON_DELETE = ResUtils.getDrawable(R.drawable.edit_delete)
        val SELECT_CHANEL_ICON_ADDED = ResUtils.getDrawable(R.drawable.edit_added)
        val SELECT_CHANEL_ICON_ADD = ResUtils.getDrawable(R.drawable.edit_add)
    }

    private var originChannels = arrayListOf<Channel>()
    private var currentChannels = arrayListOf<Channel>()
    private var allChannels = arrayListOf<Channel>()

    private var isUploading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_channel)
        initView()
    }

    private fun initView() {
        title_bar.setTitle(ResUtils.getString(R.string.edit_channel))

        title_bar.findViewById<CoreImageView>(R.id.back_btn).setOnClickListener {
            onBackPressedSupport()
        }

        selected_channels.setOnViewSwapListener { _, firstPosition, _, secondPosition ->
            Collections.swap(currentChannels, firstPosition, secondPosition)
        }

        initChannelFromNet()
    }

    private fun initChannelFromNet() {
        showLoading()
        DataManager.Remote.getChannelList()
                .ioToMain()
                .subscribeBy(onNext = {
                    Log.d("channel", it.recommendChannels.toString())
                    Log.d("channel", it.selectedChannels.toString())
                    originChannels.addAll(it.selectedChannels.articleChannels)
                    currentChannels.addAll(it.selectedChannels.articleChannels)
                    allChannels.addAll(it.recommendChannels.articleChannels)
                    addSelectedChannels(it.selectedChannels.articleChannels)
                    addAllChannels(it.recommendChannels.articleChannels)
                    hideLoading()
                }, onError = {
                    hideLoading()
                    Log.d("channel_error", it.localizedMessage)
                })
    }

    private fun hasChannelChanged(): Boolean {
        if (originChannels.size != currentChannels.size) {
            return true
        }
        repeat(originChannels.size, {
            if (originChannels[it].chid != currentChannels[it].chid) {
                return true
            }
        })
        return false
    }

    private fun saveChannel() {
        showLoading()

        val input = ChannelInput()

        for (item in currentChannels) {
            input.clientChannels.channels.add(item.chid)
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

                    Observable.just(currentChannels).io().subscribe({
                        DataManager.Local.saveChannelList(currentChannels)
                        DataManager.Local.saveFileChannelList(DataManager.Memory.getChannelList())
                    })

                    EventManager.postSticky(ChannelListChangeEvent(currentChannels))

                    isUploading = false

                    super.finish()
                }, onError = {
                    hideLoading()
                    Log.d("save_channel_error", it.localizedMessage)

                    isUploading = false
                })
    }

    @SuppressLint("InflateParams")
    private fun getChannelView(): View {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return inflater.inflate(R.layout.item_selected_channel, null)
    }

    private fun addSelectedChannel(channel: Channel) {
        val view = getChannelView()
        val iconView = view.find<ImageView>(R.id.select_channel_delete_icon)
        iconView.image = SELECT_CHANEL_ICON_DELETE
        view.find<TextView>(R.id.selected_channel_tv).text = channel.name
        selected_channels.addView(view)
        selected_channels.setViewDraggable(view, view.find<ImageView>(R.id.drag_enabler))

        view.tag = channel.chid
        iconView.setOnClickListener {
            updateAllChannel(channel.chid)
        }
    }

    private fun deleteSelectedChannel(chid: String) {
        val channel = currentChannels.filter { it.chid == chid }[0]
        currentChannels.remove(channel)

        repeat(selected_channels.childCount, {
            val v = selected_channels.getChildAt(it)
            if (chid == v.tag as String) {
                selected_channels.removeDragView(v)
                return
            }
        })
    }

    private fun addSelectedChannels(selectedChannels: ArrayList<Channel>) {
        selected_channels.removeAllViews()
        selectedChannels.forEach {
            addSelectedChannel(it)
        }
    }

    private fun addAllChannels(allChannels: ArrayList<Channel>) {
        all_channels_container.removeAllViews()
        allChannels.forEach {
            val view = getChannelView()
            val chid = it.chid

            val drawable = if (hasSelected(chid)) SELECT_CHANEL_ICON_ADDED else SELECT_CHANEL_ICON_ADD
            view.find<ImageView>(R.id.select_channel_delete_icon).image = drawable
            view.find<TextView>(R.id.selected_channel_tv).text = it.name
            view.find<ImageView>(R.id.drag_enabler).visibility = INVISIBLE

            view.tag = chid
            view.setOnClickListener { updateAllChannel(chid) }
            all_channels_container.addView(view)
        }
    }

    private fun updateAllChannel(chid: String) {
        repeat(all_channels_container.childCount, {
            val v = all_channels_container.getChildAt(it)
            val iv = v.find<ImageView>(R.id.select_channel_delete_icon)
            if (chid == v.tag as String) {
                if (iv.image == SELECT_CHANEL_ICON_ADDED) {
                    iv.image = SELECT_CHANEL_ICON_ADD
                    deleteSelectedChannel(chid)
                } else {
                    iv.image = SELECT_CHANEL_ICON_ADDED
                    val channel = allChannels.filter { it.chid == chid }[0]
                    currentChannels.add(channel)
                    addSelectedChannel(channel)
                }
                return
            }
        })
    }

    private fun hasSelected(chid: String): Boolean {
        originChannels.forEach {
            if (it.chid == chid) {
                return true
            }
        }
        return false
    }

    private fun showLoading() {
        WaitDialog.show(this, ResUtils.getString(R.string.Tip_Loading))
    }

    private fun hideLoading() {
        WaitDialog.dismiss()
    }

    override fun onBackPressedSupport() {
        if(isUploading) {
            return
        }
        isUploading = true
        if (hasChannelChanged()) {
            saveChannel()
        } else {
            super.finish()
        }
    }
}
