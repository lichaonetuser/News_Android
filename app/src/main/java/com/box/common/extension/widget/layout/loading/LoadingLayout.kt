package com.box.common.extension.widget.layout.loading


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment;
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.RelativeLayout
import com.box.app.news.R
import com.box.common.core.widget.CoreRelativeLayout
import java.util.*

class LoadingLayout
@JvmOverloads
constructor(context: Context,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = R.attr.LoadingLayout)
    : CoreRelativeLayout(context, attrs, defStyleAttr) {

    private var mContentId = View.NO_ID
    private var mLoadingId = View.NO_ID
    private var mEmptyId = View.NO_ID
    private var mFailIdId = View.NO_ID
    private var mDefaultVisibleId = mLoadingId
    private var mCurrentId = mDefaultVisibleId
    @SuppressLint("UseSparseArrays")
    private var mLayouts = HashMap<Int, View>()
    private var onRetryClickListener: ((id: Int) -> Unit)? = null

    private val mInflater by lazy {
        LayoutInflater.from(context)
    }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.LoadingLayout, defStyleAttr, R.style.Core_LoadingLayout)
        mContentId = a.getResourceId(R.styleable.LoadingLayout_contentId, View.NO_ID)
        mLoadingId = a.getResourceId(R.styleable.LoadingLayout_loadingId, R.layout.core_widget_loadinglayout_loading)
        mEmptyId = a.getResourceId(R.styleable.LoadingLayout_emptyId, R.layout.core_widget_loadinglayout_empty)
        mFailIdId = a.getResourceId(R.styleable.LoadingLayout_failId, R.layout.core_widget_loadinglayout_fail)
        mDefaultVisibleId = a.getInt(R.styleable.LoadingLayout_defaultVisibleId, mLoadingId)
        a.recycle()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (id == View.NO_ID) {
            id = R.id.loading_layout
        }

        val contentView: View? = {
            if (mContentId != View.NO_ID) {
                findViewById(mContentId)
            } else {
                getChildAt(0)
            }
        }.invoke()

        if (contentView != null) {
            mContentId = contentView.id
            mLayouts.put(mContentId, contentView)
        }

        if (isInEditMode) {
            showContent()
        } else {
            show(mDefaultVisibleId)
        }
    }

    fun showLoading() {
        if (mCurrentId == mLoadingId){
            return
        }
        show(mLoadingId)
    }

    fun showEmpty() {
        show(mEmptyId)
    }

    fun showFail() {
        show(mFailIdId)
    }

    fun showContent() {
        show(mContentId)
    }

    private fun show(layoutId: Int) { for (view in mLayouts.values) {
            view.visibility = View.GONE
        }
        layout(layoutId)?.visibility = View.VISIBLE
    }

    private fun layout(layoutId: Int): View? {
        if (mLayouts.containsKey(layoutId)) {
            return mLayouts[layoutId]
        }

        if (layoutId == View.NO_ID) {
            return null
        }

        var layout: View? = findViewById(layoutId)
        if (layout == null) {
            layout = mInflater.inflate(layoutId, this, false)
        }

        if (layout != null) {
            if (layout.isClickable && (layoutId == mEmptyId || layoutId == mFailIdId)) {
                layout.setOnClickListener { onRetryClickListener?.invoke(layoutId) }
            }
            mLayouts.put(layoutId, layout)
            addView(layout, MATCH_PARENT, MATCH_PARENT)
        }

        mCurrentId = layoutId

        return layout
    }


    fun wrap(activity: Activity): LoadingLayout {
        return wrap((activity.findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0))
    }

    fun wrap(fragment: Fragment): LoadingLayout {
        return wrap(fragment.view)
    }

    fun wrap(view: View?): LoadingLayout {
        if (view == null) {
            throw RuntimeException("content view can not be null")
        }
        val parent = view.parent as ViewGroup
        val lp = view.layoutParams
        val index = parent.indexOfChild(view)
        parent.removeView(view)

        val layout = LoadingLayout(view.context)
        parent.addView(layout, index, lp)
        layout.addView(view)
        layout.mContentId = view.id
        layout.mLayouts.put(mContentId, view)
        return layout
    }

    fun onRetryClick(listener: ((id: Int) -> Unit)?) {
        onRetryClickListener = listener
    }

}
