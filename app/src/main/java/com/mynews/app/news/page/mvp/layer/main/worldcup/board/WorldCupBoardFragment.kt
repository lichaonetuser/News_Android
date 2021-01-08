package com.mynews.app.news.page.mvp.layer.main.worldcup.board

import android.os.Bundle
import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.util.IntentUtils
import com.mynews.common.extension.app.mvp.loading.list.MVPListFragment
import com.mynews.common.extension.widget.recycler.decoration.CommonItemDecoration
import com.mynews.common.extension.widget.recycler.item.BaseItem
import com.github.magiepooh.recycleritemdecoration.ItemDecorations
import kotlinx.android.synthetic.main.fragment_world_cup_board.*
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import org.jetbrains.anko.support.v4.alert

class WorldCupBoardFragment : MVPListFragment<WorldCupBoardContract.View,
        WorldCupBoardContract.Presenter<WorldCupBoardContract.View>>(),
        WorldCupBoardContract.View {

    override val mAttachSwipeBack = false
    override var mDispatchBack = false
    override val mPresenter = WorldCupBoardPresenter()
    override val mLayoutRes = R.layout.fragment_world_cup_board
    override var mCommonItemDecorations = CommonItemDecoration()

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        mCommonAdapter.setEnableLoadMore(false)
        mCommonAdapter.setStickyHeaders(true)
        mCommonAdapter.setDisplayHeadersAtStartUp(true)
        common_content_rv.addItemDecoration(ItemDecorations.vertical(_mActivity)
                .first(R.drawable.divider_common)
                .type(R.layout.item_worldcup_board_child_comment, R.drawable.divider_world_cup_board_list)
                .type(R.layout.item_worldcup_board_child_article, R.drawable.divider_world_cup_board_list)
                .type(R.layout.item_worldcup_board_child_video, R.drawable.divider_world_cup_board_list)
                .type(R.layout.item_worldcup_board_child_image, R.drawable.divider_world_cup_board_list)
                .type(R.layout.item_worldcup_board_child_player, R.drawable.divider_world_cup_board_list)
                .type(R.layout.item_worldcup_board_child_team, R.drawable.divider_world_cup_board_list)
                .type(R.layout.item_worldcup_board_child_match, R.drawable.divider_world_cup_board_list)
                .last(R.drawable.divider_common)
                .create())
        comment_btn.setOnClickListener {
            mPresenter.onClickPostComment()
        }

        OverScrollDecoratorHelper.setUpOverScroll(common_content_rv, OverScrollDecoratorHelper.ORIENTATION_VERTICAL)
    }

    override fun onSupportVisible() {
        super.onSupportVisible()
        notifyStatusBarIsLight()
    }

    override fun onLoadMoreComplete(newItems: List<BaseItem<*, *>>) {
        mCommonAdapter.updateDataSet(newItems)
        mCommonAdapter.invalidateLoadMore(newItems)
    }

    override fun scrollToPositionTop(position: Int) {
        common_content_rv.scrollToPositionTop(position)
    }

    override fun showGoLinkDialog(url: String?) {
        if (url == null || url.isBlank()) {
            return
        }

        alert {
            titleResource = R.string.WorldCup_Board_Link_Dialog_Title
            messageResource = R.string.WorldCup_Board_Link_Dialog_Message
            positiveButton(R.string.WorldCup_Board_Link_Dialog_Ok, {
                mPresenter.onClickGoLinkDialog(url, true)
                it.dismiss()
            })
            negativeButton(R.string.WorldCup_Board_Link_Dialog_Cancel, {
                mPresenter.onClickGoLinkDialog(url, false)
                it.dismiss()
            })
        }.show()
    }

    override fun startBrowserIntent(url: String) {
        IntentUtils.startBrowserIntent(_mActivity ?: return, url)
    }

}


