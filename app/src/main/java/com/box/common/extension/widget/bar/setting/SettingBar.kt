package com.box.common.extension.widget.bar.setting

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.RelativeLayout
import com.box.app.news.R
import kotlinx.android.synthetic.main.core_widget_setting_bar.view.*
import org.jetbrains.anko.dip

class SettingBar @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0)
    : RelativeLayout(context, attrs, defStyleAttr) {

    init {
        View.inflate(context, R.layout.core_widget_setting_bar, this)
        initViews(attrs)
    }

    private fun initViews(attrs: AttributeSet?) {
        val context = context
        val a = context.obtainStyledAttributes(attrs, R.styleable.SettingBar, 0, 0)

        val imgRes = a.getResourceId(R.styleable.SettingBar_icon, -1)
        if (imgRes != -1) {
            setting_icon.visibility = View.VISIBLE
            setting_icon.setImageResource(imgRes)
        } else {
            if (isInEditMode) {
                return
            }
            setting_title.setPadding(dip(12), 0, 0, 0)
            setting_title.setPaddingRelative(dip(12), 0, 0, 0)
        }

        val title = a.getString(R.styleable.SettingBar_title)
        if (!TextUtils.isEmpty(title)) {
            setting_title.text = title
        }

        val titleSize = a.getDimension(R.styleable.SettingBar_titleSize, -1f)
        if (titleSize != -1f) {
            setting_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize)
        }

        val showArrow = a.getBoolean(R.styleable.SettingBar_showArrow, true)
        if (showArrow) {
            setting_arrow.visibility = View.VISIBLE
            if (!isInEditMode) {
                val params = setting_value.layoutParams as RelativeLayout.LayoutParams
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0)
                params.addRule(RelativeLayout.LEFT_OF, R.id.setting_arrow)
                params.setMargins(0, 0, dip(10), 0)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    params.addRule(RelativeLayout.ALIGN_PARENT_END, 0)
                    params.addRule(RelativeLayout.START_OF, R.id.setting_arrow)
                    params.marginEnd = dip(10)
                }
                setting_value.layoutParams = params
            }
        } else {
            setting_arrow.visibility = View.GONE
            if (!isInEditMode) {
                val params = setting_value.layoutParams as RelativeLayout.LayoutParams
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)
                params.addRule(RelativeLayout.LEFT_OF, 0)
                params.setMargins(0, 0, dip(18), 0)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    params.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE)
                    params.addRule(RelativeLayout.START_OF, 0)
                    params.marginEnd = dip(18)
                }
                setting_value.layoutParams = params
            }
        }

        val showValue = a.getBoolean(R.styleable.SettingBar_showValue, false)
        setting_value.visibility = if (showValue) View.VISIBLE else View.INVISIBLE

        val titleBold = a.getBoolean(R.styleable.SettingBar_titleBold, false)
        setting_value.paint.isFakeBoldText = titleBold

        val valueColor = a.getColor(R.styleable.SettingBar_valueColor, -1)
        if (valueColor != -1) {
            setting_value.setTextColor(valueColor)
        }

        val titleColor = a.getColor(R.styleable.SettingBar_titleColor, -1)
        if (titleColor != -1) {
            setting_title.setTextColor(titleColor)
        }

        a.recycle()
    }

    fun setRemindVisibility(visibility: Int) {
        setting_remind.visibility = visibility
    }

    fun setTitle(title: CharSequence) {
        setting_title.text = title
    }

    fun setValue(value: CharSequence) {
        setting_value.text = value
    }

    fun setValueBold(boldText: Boolean) {
        setting_value.paint.isFakeBoldText = boldText
    }

    fun setValueDrawables(left: Drawable?, top: Drawable?, right: Drawable?, bottom: Drawable?) {
        setting_title.setCompoundDrawables(left, top, right, bottom)
    }

}
