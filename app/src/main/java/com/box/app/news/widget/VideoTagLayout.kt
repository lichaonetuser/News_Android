package com.box.app.news.widget

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.box.app.news.R
import com.box.common.core.CoreApp
import com.box.common.core.util.ResUtils
import com.box.common.core.widget.CoreTextView
import kotlinx.android.synthetic.main.video_tag.view.*
import org.jetbrains.anko.dip
import org.jetbrains.anko.singleLine
import org.jetbrains.anko.textColor
import java.util.*

class VideoTagLayout @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val mLayout: View = LayoutInflater.from(context).inflate(R.layout.video_tag, this)

    fun setContent(list: ArrayList<String>, background: Int, textSize : Float, textColor: String, wholeWidth : Int, minWidth : Int, maxleftWidth : Int, paddingLeft : Int, paddingRight: Int, marginLeft :Int) {
        mLayout.tag_container.removeAllViews()
        val textViewList = arrayListOf<CoreTextView>()
        for (i in list.indices) {
            val textView = CoreTextView(context)
            textView.text = list[i]
            textView.textSize = textSize
            textView.textColor = Color.parseColor(textColor)
            textView.background = ResUtils.getDrawable(background)
            val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            if (i == 0) {
                lp.leftMargin = 0
            } else {
                lp.leftMargin = CoreApp.getInstance().dip(marginLeft)
            }
            lp.rightMargin = 0

            textView.layoutParams = lp
            textView.setPadding(CoreApp.getInstance().dip(paddingLeft), 0, CoreApp.getInstance().dip(paddingRight), 0)
            textView.singleLine = true
            textView.setGravity(Gravity.CENTER_VERTICAL)
            textView.ellipsize = TextUtils.TruncateAt.END

            textView.minWidth = Math.min(CoreApp.getInstance().dip(minWidth), textView.width)
            textView.maxWidth = wholeWidth - CoreApp.getInstance().dip(maxleftWidth)
            textView.measure(0, 0)
            textViewList.add(textView)
        }
        var length: Int = 0
        for (j in textViewList.indices) {
            length = textViewList[j].measuredWidth + length
        }

        var wholeLength = wholeWidth - CoreApp.getInstance().dip(maxleftWidth)
        if (length > wholeLength) {
            var lengthLast: Int = 0
            var index: Int = 0
            for (i in textViewList.indices) {
                for (j in 0..i) {
                    lengthLast = lengthLast + textViewList[j].measuredWidth + CoreApp.getInstance().dip(minWidth) * (textViewList.size - 1 - j)
                    index = j
                }
                if (lengthLast > wholeLength
                        || ((lengthLast > wholeLength - CoreApp.getInstance().dip(minWidth)) && lengthLast < wholeLength)) {
                    for (m in index + 1..textViewList.size - 1) {
                        textViewList[m].width = Math.min(CoreApp.getInstance().dip(minWidth), textViewList[m].measuredWidth)
                    }

                    var temp: Int = 0
                    if (index == 0) {
                        temp = 0
                    } else if (index == 1) {
                        temp = textViewList[0].measuredWidth
                    } else {
                        for (m in 0..index - 1) {
                            temp = temp + textViewList[m].measuredWidth
                        }
                    }
//                    textViewList[index].width = wholeLength - (textViewList.size - 1 - index) * CoreApp.getInstance().dip(minWidth) - temp
//                    - CoreApp.getInstance().dip(20)

                    textViewList[index].width = Math.min(textViewList[index].measuredWidth, wholeLength - (textViewList.size - 1 - index) * CoreApp.getInstance().dip(minWidth) - temp
                            - CoreApp.getInstance().dip(20))
                    break
                }
            }
        }
        for (i in textViewList.indices) {
            mLayout.tag_container.addView(textViewList[i])
        }
    }
}