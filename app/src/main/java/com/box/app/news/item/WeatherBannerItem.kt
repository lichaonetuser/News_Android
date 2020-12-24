package com.box.app.news.item

import androidx.core.widget.TextViewCompat
import android.text.Spannable
import android.text.SpannableString
import android.view.View
import com.box.app.news.R
import com.box.app.news.analytics.AnalyticsKey
import com.box.app.news.bean.Weather
import com.box.app.news.data.DataManager
import com.box.app.news.event.EventManager
import com.box.app.news.event.change.MainTabChangeEvent
import com.box.app.news.page.mvp.layer.main.MainTabEnum
import com.box.app.news.page.mvp.layer.main.weather.WeatherFragment
import com.box.app.news.util.WeekUtils
import com.box.common.core.CoreApp
import com.box.common.core.analytics.AnalyticsManager
import com.box.common.core.app.fragment.CoreBaseFragment
import com.box.common.core.image.fresco.ImageManager
import com.box.common.core.util.ResUtils
import com.box.common.extension.spanner.CenteredImageSpan
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.box.common.extension.widget.recycler.holder.BaseViewHolder
import com.box.common.extension.widget.recycler.item.BaseItem
import eu.davidea.flexibleadapter.items.IHeader
import kotlinx.android.synthetic.main.item_weather.view.*
import lt.neworld.spanner.Spanner
import lt.neworld.spanner.Spans.custom
import lt.neworld.spanner.Spans.foreground
import me.yokeyword.fragmentation.SupportFragment
import java.util.*

class WeatherBannerItem(mBean: Weather)
    : BaseItem<Weather, WeatherBannerItem.ViewHolder>(mBean), IHeader<WeatherBannerItem.ViewHolder> {

    private var mCalendar: Calendar = Calendar.getInstance()
    private var mDayOfWeek: Int = 0
    private var mMonth: Int = 0
    private var mDayOfMonth: Int = 0
    private var mDayOfWeekName: String = ""
    private var mDataSpannable: Spannable = SpannableString("")
    private var mTemperatureSpannable: Spannable = SpannableString("")

    init {
        updateWeather(mBean)
    }

    fun updateWeather(weather: Weather) {
        this.mBean = weather
        updateField()
    }

    fun updateField() {
        if (mBean.date > 0) {
            mCalendar.timeInMillis = mBean.date * 1000
        }
        mDayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK) - 1
        mMonth = mCalendar.get(Calendar.MONTH) + 1
        mDayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH)
        mDayOfWeekName = WeekUtils.getDayOfWeekName(mDayOfWeek)

        val dateSlashDrawable = ResUtils.getCompoundDrawable(R.drawable.news_slash_date)
        mDataSpannable = Spanner()
                .append(mMonth.toString())
                .append(" ", custom { CenteredImageSpan(dateSlashDrawable) })
                .append(mDayOfMonth.toString())
        val temperatureSlashDrawable = ResUtils.getCompoundDrawable(R.drawable.news_slash_temperature)
        mTemperatureSpannable = Spanner()
                .append("${mBean.highTemperature}° ", foreground(ResUtils.getColor(R.color.weather_temp_high)))
                .append(" ", custom { CenteredImageSpan(temperatureSlashDrawable) })
                .append(" ${mBean.lowTemperature}°", foreground(ResUtils.getColor(R.color.weather_temp_low)))
    }

    override fun getLayoutRes(): Int {
        return R.layout.item_weather
    }

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        holder.week_txt.text = mDayOfWeekName
        holder.date_txt.text = mDataSpannable
        if (mBean.isEmptyWeather()) {
            holder.set_location_btn.visibility = View.VISIBLE
            holder.weather_layout.visibility = View.INVISIBLE
            holder.location_txt.visibility = View.INVISIBLE
        } else {
            holder.set_location_btn.visibility = View.INVISIBLE
            holder.weather_layout.visibility = View.VISIBLE
            holder.location_txt.visibility = View.VISIBLE
            holder.description_txt.text = mBean.description
            holder.location_txt.text = mBean.cityName
            holder.location_txt.setAutoSizeTextTypeWithDefaults(TextViewCompat.AUTO_SIZE_TEXT_TYPE_NONE)
            holder.location_txt.textSize = 14f
            holder.location_txt.setAutoSizeTextTypeWithDefaults(TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM)
            holder.weather_txt.text = mTemperatureSpannable
            if (mBean.icons.isEmpty()) {
                ImageManager.with(holder.weather_img).load(R.drawable.news_weather_banner_unknown_ic)
            } else {
                ImageManager.with(holder.weather_img).load(mBean.icons[0])
            }
        }
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter)
    }

    class ViewHolder(view: View, mCommonAdapter: CommonRecyclerAdapter) : BaseViewHolder(view, mCommonAdapter) {
        val week_txt = itemView.week_txt
        val date_txt = itemView.date_txt
        val weather_layout = itemView.weather_layout
        val weather_img = itemView.weather_img
        val weather_txt = itemView.weather_txt
        val description_txt = itemView.description_txt
        val location_txt = itemView.location_txt
        val set_location_btn = itemView.set_location_btn

        init {
            itemView.setOnClickListener {
                AnalyticsManager.logEvent(AnalyticsKey.Event.NEWS, AnalyticsKey.Parameter.CLICK_TO_WEATHER)
                val fragment = CoreBaseFragment.instantiate(WeatherFragment::class.java)
                CoreApp.getLastBaseActivityInstance()?.callRootFragmentStart(fragment, SupportFragment.STANDARD, hideSelf = false)
//                EventManager.post(MainTabChangeEvent(MainTabEnum.WEATHER))
            }
        }
    }

}