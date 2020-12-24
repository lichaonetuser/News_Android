package com.box.app.news.page.mvp.layer.main.weather.detail

import android.os.Bundle
import android.view.View
import com.box.app.news.R
import com.box.app.news.widget.WebViewOverScrollDecorAdapter
import com.box.common.extension.app.mvp.loading.browser.MVPBrowserFragment
import kotlinx.android.synthetic.main.fragment_weather_detail.*
import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator
import me.yokeyword.fragmentation.anim.FragmentAnimator

class WeatherDetailFragment : MVPBrowserFragment<WeatherDetailContract.View,
        WeatherDetailContract.Presenter<WeatherDetailContract.View>>(),
        WeatherDetailContract.View {

    override var mDispatchBack = false
    override val mLayoutRes = R.layout.fragment_weather_detail
    override val mPresenter = WeatherDetailPresenter()

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        VerticalOverScrollBounceEffectDecorator(WebViewOverScrollDecorAdapter(mAgentWeb.webCreator.get()))

        region_name_txt.setOnClickListener({
            mPresenter.onClickChooseCity()
        })
    }

    override fun onWebViewDispatch(): Boolean {
        return false
    }

    override fun setRegionName(name: String) {
        region_name_txt.text = name
    }

    override fun onCreateFragmentAnimator(): FragmentAnimator {
        return FragmentAnimator(
                R.anim.no_anim,
                R.anim.no_anim,
                R.anim.no_anim,
                R.anim.no_anim)
    }
}


