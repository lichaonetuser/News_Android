package com.box.app.news.widget.navigation

import android.content.Context
import androidx.annotation.DrawableRes
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.box.app.news.R
import com.box.common.core.CoreApp
import kotlinx.android.synthetic.main.item_main_bottom_navigation.view.*
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem
import org.jetbrains.anko.dip
import org.jetbrains.anko.imageResource

class MainBottomNavigationItemView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : BaseTabItem(context, attrs, defStyleAttr) {

    var normalTitle = ""
    var checkedTitle = ""

    init {
        LayoutInflater.from(context).inflate(R.layout.item_main_bottom_navigation, this, true)
    }

    fun load(@DrawableRes iconRes: Int,
             normalTitle: String,
             checkedTitle: String = normalTitle,
             indicatorPaddingLeftDp: Int = 0,
             indicatorPaddingTopDp: Int = 0,
             indicatorPaddingRightDp: Int = 0,
             indicatorPaddingBottomDp: Int = 0) {
        this.normalTitle = normalTitle
        this.checkedTitle = checkedTitle
        navigation_icon.imageResource = iconRes
//        navigation_indicator.setPadding(
//                CoreApp.getInstance().dip(indicatorPaddingLeftDp),
//                CoreApp.getInstance().dip(indicatorPaddingTopDp),
//                CoreApp.getInstance().dip(indicatorPaddingRightDp),
//                CoreApp.getInstance().dip(indicatorPaddingBottomDp))
    }

    override fun setChecked(checked: Boolean) {
        navigation_icon.isActivated = checked
        navigation_title.isActivated = checked
        if (checked) {
//            navigation_indicator.visibility = View.VISIBLE
            navigation_title.text = checkedTitle
        } else {
//            navigation_indicator.visibility = View.GONE
            navigation_title.text = normalTitle
        }
    }

    override fun getTitle(): String {
        return navigation_title.text.toString()
    }

    override fun setMessageNumber(number: Int) {
        //Will do
    }

    override fun setHasMessage(hasMessage: Boolean) {
        navigation_remind.visibility = if (hasMessage) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }


}
