package com.mynews.app.news.widget

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.bean.InterestItem
import com.mynews.app.news.page.activity.TagChooseEvent
import com.nex3z.flowlayout.FlowLayout
import mehdi.sakout.fancybuttons.FancyButton

class CustomFlowLayout : FlowLayout {

    private var tagChooseEvent: TagChooseEvent? = null

    private var onTagClickListener: View.OnClickListener? = null

    var icon: String = ""

    var mCategory: String = ""

    var mColor: String = ""

    var mTagList: ArrayList<String>? = null

    var mSelectedTag = arrayListOf<String>()

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    fun setTagChooseEvent(tagChooseEvent: TagChooseEvent) {
        this.tagChooseEvent = tagChooseEvent
    }

    fun setTagListToFlowview(interestItem: InterestItem) {
        mCategory = interestItem.category
        mColor = interestItem.color
        mTagList = interestItem.tags
        icon = interestItem.icon

        for (tag in interestItem.tags.orEmpty()) {
            val button = infateTag()
            button.setBorderColor(Color.parseColor(mColor))
            button.setBorderWidth(1)
            button.setText(tag.trim())
            button.setTextColor(Color.parseColor(mColor))
            val drawable = ContextCompat.getDrawable(this.context, R.drawable.choose_add)
            drawable?.mutate()?.setColorFilter(Color.parseColor(mColor), PorterDuff.Mode.SRC_IN)
            button.setIconResource(drawable)
            if (onTagClickListener != null) {
                button.setOnClickListener(onTagClickListener)
            } else {
                button.tag = false
                button.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {

                        if (v?.tag as Boolean) { // unselect
                            mSelectedTag.remove((v as FancyButton).text.toString())
                            changeBackgroundColor(v, false, mColor)
                            tagChooseEvent?.addOrRemove(-1)
                        } else { // select
                            mSelectedTag.add((v as FancyButton).text.toString())
                            changeBackgroundColor(v, true, mColor)
                            tagChooseEvent?.addOrRemove(1)
                        }
                        v?.tag = !(v?.tag as Boolean)// inverse the selection
                    }
                })
            }
            addView(button)
        }
    }

    private fun changeBackgroundColor(button: FancyButton?, isSelected: Boolean, color: String) {
        if (isSelected) {
            button?.setTextColor(this.context.resources.getColor(R.color.white))
            button?.setBackgroundColor(Color.parseColor(color))
            button?.setIconResource(ContextCompat.getDrawable(this.context, R.drawable.choose_added))
        } else {
            button?.setBackgroundColor(this.context.resources.getColor(R.color.white))
            button?.setTextColor(Color.parseColor(color))
            val drawable = ContextCompat.getDrawable(this.context, R.drawable.choose_add)
            drawable?.mutate()?.setColorFilter(Color.parseColor(mColor), PorterDuff.Mode.SRC_IN)
            button?.setIconResource(drawable)
        }
    }

    private fun infateTag(): FancyButton {
        val button = LayoutInflater.from(this.context).inflate(R.layout.layout_tags, null) as FancyButton
        return button
    }


}