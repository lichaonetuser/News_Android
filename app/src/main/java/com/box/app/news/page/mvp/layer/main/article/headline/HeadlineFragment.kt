package com.box.app.news.page.mvp.layer.main.article.headline

import android.os.Build
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.box.app.news.R
import com.box.app.news.page.mvp.layer.main.list.news.NewsListFragment
import com.box.common.core.util.ResUtils
import com.box.common.extension.widget.recycler.decoration.CommonItemDecoration
import kotlinx.android.synthetic.main.fragment_headline.*

open class HeadlineFragment : NewsListFragment<HeadlineContract.View,
        HeadlineContract.Presenter<HeadlineContract.View>>(),
        HeadlineContract.View {

    @Suppress("UNCHECKED_CAST")
    override val mPresenter = HeadlinePresenter()
    override val mLayoutRes = R.layout.fragment_headline
    override var mDispatchBack = true
    override val mAttachSwipeBack = true
    override var mCommonItemDecorations: CommonItemDecoration = CommonItemDecoration().hide()

    lateinit var mScrollListener : RecyclerView.OnScrollListener


    override fun initView(view: View?, savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            status_bar.setBackgroundColor(ResUtils.getColor(R.color.color_1))
        }

        mScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (mCommonLayoutManage is LinearLayoutManager) {
                    val layoutManager = mCommonLayoutManage as LinearLayoutManager
                    val pos = layoutManager.findFirstVisibleItemPosition()
                    title_time_txt.text = mPresenter.getTitleTime(pos)
                    title_time_txt.visibility = View.VISIBLE
                    title_logo_img.visibility = View.VISIBLE
                }
            }
        }
        common_content_rv.addOnScrollListener(mScrollListener)

        back_img.setOnClickListener {
            back()
        }
    }

    override fun onDestroyView() {
        common_content_rv.removeOnScrollListener(mScrollListener)
        super.onDestroyView()
    }
}