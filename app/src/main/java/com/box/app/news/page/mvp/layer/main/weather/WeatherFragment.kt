package com.box.app.news.page.mvp.layer.main.weather

import android.os.Bundle
import android.view.View
import com.box.app.news.R
import com.box.common.core.app.fragment.CoreBaseFragment
import com.box.common.extension.app.mvp.base.MVPBaseFragment
import kotlinx.android.synthetic.main.fragment_weather.*

class WeatherFragment : MVPBaseFragment<WeatherContract.View,
        WeatherContract.Presenter<WeatherContract.View>>(),
        WeatherContract.View {

    override val mPresenter = WeatherPresenter()
    override val mLayoutRes = R.layout.fragment_weather
    override var mDispatchBack = true
    override val mAttachSwipeBack = true

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        back_btn.setOnClickListener {
            onBackPressedSupport()
        }
    }

    override fun <F : CoreBaseFragment> loadContainer(clazz: Class<F>) {
        loadRootFragment(R.id.container_layout,
                CoreBaseFragment.instantiate(clazz),
                false, false)
    }


    override fun onSupportVisible() {
        super.onSupportVisible()
        notifyStatusBarIsLight()
    }

}


