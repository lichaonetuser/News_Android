package com.mynews.app.news.page.mvp.layer.main.digbury

import android.os.Bundle
import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.bean.base.BaseNewsBean
import com.mynews.common.extension.app.mvp.base.MVPBaseFragment
import kotlinx.android.synthetic.main.fragment_dig_bury.*
import me.yokeyword.fragmentation.anim.FragmentAnimator
import org.jetbrains.anko.noButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.yesButton

class DigBuryragment : MVPBaseFragment<DigBuryContract.View,
        DigBuryContract.Presenter<DigBuryContract.View>>(),
        DigBuryContract.View {

    override val mAttachSwipeBack = false
    override var mDispatchBack = false
    override val mPresenter = DigBuryPresenter()
    override val mLayoutRes = R.layout.fragment_dig_bury

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        dig_btn.setOnClickListener {
            mPresenter.onClickDig()
        }
        bury_btn.setOnClickListener {
            mPresenter.onClickBury()
        }
        delete_btn.setOnClickListener {
            mPresenter.onClickDelete()
        }
    }

    override fun setNews(news: BaseNewsBean) {
        dig_btn.isActivated = news.isDigged
        bury_btn.isActivated = news.isBuried
        dig_btn.text = news.digCount.toString()
        bury_btn.text = news.buryCount.toString()
    }

    override fun showDeleteDialog(news: BaseNewsBean) {
        alert(R.string.Tip_ContentTrashDescription) {
            yesButton { mPresenter.onClickDeleteConfirm(true) }
            noButton { mPresenter.onClickDeleteConfirm(false) }
            onCancelled { mPresenter.onClickDeleteConfirm(false) }
        }.show()
    }

    override fun onCreateFragmentAnimator(): FragmentAnimator {
        return FragmentAnimator(
                R.anim.no_anim,
                R.anim.no_anim,
                R.anim.no_anim,
                R.anim.no_anim)
    }

    override fun back() {}
}


