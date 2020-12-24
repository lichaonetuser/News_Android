package com.box.app.news.page.mvp.layer.main.weather.guide

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ScrollView
import com.box.app.news.R
import com.box.common.extension.app.mvp.base.MVPBaseFragment
import com.box.common.extension.location.RxLocation
import kotlinx.android.synthetic.main.fragment_weather_guide.*
import me.yokeyword.fragmentation.anim.FragmentAnimator

class WeatherGuideFragment : MVPBaseFragment<WeatherGuideContract.View,
        WeatherGuideContract.Presenter<WeatherGuideContract.View>>(),
        WeatherGuideContract.View {

    override val mPresenter = WeatherGuidePresenter()
    override val mLayoutRes = R.layout.fragment_weather_guide
    override var mDispatchBack = false

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        choose_city_btn.setOnClickListener {
            mPresenter.onClickChooseCity()
        }
        current_location_btn.setOnClickListener {
            mPresenter.onClickCurrentLocation()
        }
        scroll_view.overScrollMode = ScrollView.OVER_SCROLL_NEVER
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == RxLocation.REQUEST_CODE_SETTINGDIALOG && RxLocation.haveLocationPermission().blockingGet()) {
//            mPresenter.onClickCurrentLocation()
//        }
    }

    override fun showSettingDialogIfAlwaysDenied() {
        RxLocation.showSettingDialogIfAlwaysDenied(this)
    }

    override fun showLocationProgress() {
        location_pb.visibility = View.VISIBLE
        current_location_btn.text = ""
        current_location_btn.isClickable = false
    }

    override fun hideLocationProgress() {
        location_pb.visibility = View.GONE
        current_location_btn.isClickable = true
        current_location_btn.setText(R.string.Weather_UseCurrentLocation)
    }

    override fun onCreateFragmentAnimator(): FragmentAnimator {
        return FragmentAnimator(
                R.anim.no_anim,
                R.anim.no_anim,
                R.anim.no_anim,
                R.anim.no_anim)
    }

}


