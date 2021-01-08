package com.mynews.app.news.widget

import android.content.Context
import androidx.annotation.ColorInt;
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.mynews.app.news.R
import com.mynews.common.core.anim.FrameAnim
import com.mynews.common.core.util.ResUtils
import com.scwang.smartrefresh.layout.api.RefreshHeader
import com.scwang.smartrefresh.layout.api.RefreshKernel
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.constant.RefreshState
import com.scwang.smartrefresh.layout.constant.SpinnerStyle
import org.jetbrains.anko.dip

class NewsRefreshHeader @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), RefreshHeader {

    companion object {
        const val FRAME_ANIM_DURATION = 1300
    }

    private val mHeaderImg: ImageView
    private val mHeaderText: TextView
    private val mFrameAnim: FrameAnim

    init {
        orientation = LinearLayout.VERTICAL
        gravity = Gravity.CENTER

        mHeaderImg = ImageView(context)
        val imageLP = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        imageLP.topMargin = dip(5)
        addView(mHeaderImg, imageLP)

        mHeaderText = TextView(context)
        mHeaderText.setText(R.string.Common_PullDownToRefresh)
        mHeaderText.textSize = 9f
        mHeaderText.setTextColor(ResUtils.getColor(R.color.color_4))
        val textLP = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        textLP.topMargin = dip(1)
        textLP.bottomMargin = dip(6)
        addView(mHeaderText, textLP)

        val headerAnimRes = getHeaderAnimRes()
        mFrameAnim = FrameAnim(mHeaderImg, headerAnimRes, FRAME_ANIM_DURATION / headerAnimRes.size, true)
        mFrameAnim.cancelTo(0)
    }

    private fun getHeaderAnimRes(): IntArray {
        val typedArray = resources.obtainTypedArray(R.array.frame_anim_news_refresh_header)
        val len = typedArray.length()
        val resId = IntArray(len)
        for (i in 0 until len) {
            resId[i] = typedArray.getResourceId(i, -1)
        }
        typedArray.recycle()
        return resId
    }

    override fun getView(): View {
        return this//真实的视图就是自己，不能返回null
    }

    override fun getSpinnerStyle(): SpinnerStyle {
        return SpinnerStyle.Translate//指定为平移，不能null
    }

    override fun onStartAnimator(layout: RefreshLayout, headHeight: Int, extendHeight: Int) {
        //开始动画
        mFrameAnim.play(0)
    }

    override fun onFinish(layout: RefreshLayout, success: Boolean): Int {
        //停止动画
        mFrameAnim.cancelTo(0)
        return 0//立刻弹回
    }

    override fun onStateChanged(refreshLayout: RefreshLayout, oldState: RefreshState, newState: RefreshState) {
        when (newState) {
            RefreshState.None, RefreshState.PullDownToRefresh -> mHeaderText.setText(R.string.Common_PullDownToRefresh) //下拉开始刷新
            RefreshState.Refreshing -> mHeaderText.setText(R.string.Common_Loading) //正在刷新
            RefreshState.ReleaseToRefresh -> mHeaderText.setText(R.string.Common_ReleaseToRefresh)  //释放立即刷新
            else -> {
                //什么也不错
            }
        }
    }

    override fun isSupportHorizontalDrag(): Boolean {
        return false//支持横向拖拽
    }

    override fun onInitialized(kernel: RefreshKernel, height: Int, extendHeight: Int) {}

    override fun onHorizontalDrag(percentX: Float, offsetX: Int, offsetMax: Int) {}

    override fun onPullingDown(percent: Float, offset: Int, headHeight: Int, extendHeight: Int) {}

    override fun onReleasing(percent: Float, offset: Int, headHeight: Int, extendHeight: Int) {}

    override fun setPrimaryColors(@ColorInt vararg colors: Int) {}
}