package com.mynews.app.news.widget.nested

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.view.*
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.mynews.common.core.log.Logger
import com.mynews.common.extension.widget.layout.loading.LoadingLayout

class NestedParentScrollView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : NestedScrollView(context, attrs, defStyleAttr), NestedScrollingParent2 {

    companion object {
        const val TAG = "NestedParentScrollView"
    }

    private val mParentHelper = NestedScrollingParentHelper(this)
    private val mNestedChildren = arrayListOf<View>()
    private var mIsNestNonTouching = false
    private var mIsNestTouching = false

    init {
        overScrollMode = View.OVER_SCROLL_NEVER
        isVerticalFadingEdgeEnabled = false
    }

    /*-----------------------*/
    /* NestedScrollingParent */
    /*-----------------------*/

    /**
     * 子视图通知要开始进行嵌套滑动了
     * @param child target的直接父类
     * @param target 通知要开始进行嵌套滑动的子视图
     * @param axes 方向
     * @param type 类型，是否是Touch触发
     * @return 是否配合进行嵌套滑动，返回false的话将收不到后面的其它回调
     */
    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        Logger.tag(TAG).d("onStartNestedScroll axes :$axes | type : $type")
        when (type) {
            ViewCompat.TYPE_TOUCH -> mIsNestTouching = true
            ViewCompat.TYPE_NON_TOUCH -> mIsNestNonTouching = true
        }
        stopNestedScroll(ViewCompat.TYPE_TOUCH)
        stopNestedScroll(ViewCompat.TYPE_NON_TOUCH)
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL // 只在纵向滑动时与targetView配合进行嵌套滑动
    }

    /**
     * 当onStartNestedScroll返回true时，首先回调此方法表示嵌套滑动正式被应用
     */
    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        Logger.tag(TAG).d("onNestedScrollAccepted axes :$axes | type : $type")
        mParentHelper.onNestedScrollAccepted(child, target, axes, type)
    }

    /**
     * 当子视图调用dispatchNestedPreScroll时会收到该回调,约束为在子视图滑动开始前回调
     * @param dx 表示target本次滚动产生的x方向的滚动总距离
     * @param dy:表示target本次滚动产生的y方向的滚动总距离
     * @param consumed 表示父布局要消费的滚动距离,consumed[0]和consumed[1]分别表示父布局在x和y方向上消费的距离.
     */
    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        Logger.tag(TAG).d("onNestedPreScroll dx :$dx | dy : $dy | consumed[${consumed.firstOrNull()},${consumed.lastOrNull()}] | type : $type | target : $target")
        if (target !is INestedChild || dy == 0) {
            //不是我定义的，或者没有滑动，不处理
            return
        }

        /**
         * 因为有可能封装，获取嵌套视图的top变复杂了
         */
        val targetParent = findTargetParent(target)
        val targetParentTop = targetParent.top
        val targetTop = if (target.parent is LoadingLayout) {
            (target.parent as LoadingLayout).top
        } else {
            target.top
        }
        val top = targetParentTop + targetTop
        /**
         * 这里说一下scrollY > target.top这个判断
         * 如果父视图当前滑动的位置大于目标的Top
         * 说明这是一个滑动到底部向上触发父视图滑动后
         * 又向下滑动，或者更多的改变方向的结果
         * 这个时候父视图需要配合滑动掉所有距离
         */
        if (target.isScrolledToBottom() && scrollY > top) {
            Logger.d("onNestedPreScroll  over bottom")
            consumed.set(1, dy)
            if (scrollY + dy >= top) {
                scrollBy(0, dy)
            } else {
                scrollTo(0, top)
            }
            //父视图滑动到底部就不管了
            if (!isScrolledToBottom()) {
                return
            }
        }

        /**
         * 同上，滑动到顶部的场合
         */
        if (target.isScrolledToTop() && scrollY < top) {
            Logger.d("onNestedPreScroll  over top")
            consumed.set(1, dy)
            if (scrollY + dy <= top) {
                scrollBy(0, dy)
            } else {
                scrollTo(0, top)
            }
            //父视图滑动到顶部就不管了
            if (!isScrolledToTop()) {
                return
            }
        }


        when (type) {
        //用户触摸引起嵌套滑动的场合
            ViewCompat.TYPE_TOUCH -> {
                /**
                 * 核心问题在于我们该不该让父视图配合进行滑动
                 * 即当前视图自己划不动了，条件暂定为当前视图已经到达了头部或者尾部
                 * 这里为了方便搞清楚各个场景，就没有用逻辑嵌套来绕
                 * 直接有一写一了
                 * 有时间整理下逻辑
                 */
                when {
                //状况一，子视图滑动到了底部并且（手指）还在向上移动，即上拉
                    target.isScrolledToBottom() && dy > 0 -> {
                        Logger.tag(TAG).d("onNestedPreScroll TYPE_TOUCH Status 1")
                        /**
                         * 实际上就是造成一种子视图进行了滑动，但滑动距离是0的结果
                         * 将这个本该进行的滑动距离转移给父视图
                         */
                        consumed.set(1, dy)//父视图消耗所有嵌套滑动距离，等价于子视图不滚动
                        scrollBy(0, dy)//滚动父视图
                    }
                //状况二，子视图滑动到了底部但是（手指）是在向下移动，即下拉
                    target.isScrolledToBottom() && dy < 0 -> {
                        Logger.tag(TAG).d("onNestedPreScroll TYPE_TOUCH Status 2")
                        consumed.set(1, 0)//父视图不消耗任何滑动距离，让子视图自己处理即可
                    }
                //状况三，子视图滑动到了顶部并且还在向上滑动，即手指向下移动
                    target.isScrolledToTop() && dy < 0 -> {
                        Logger.tag(TAG).d("onNestedPreScroll TYPE_TOUCH Status 3")
                        /**
                         * 同一，让父视图来接管
                         */
                        consumed.set(1, dy)
                        scrollBy(0, dy)
                    }
                //状况四，子视图滑动到了顶部但是是在向下滑动，即手指向上移动
                    target.isScrolledToTop() && dy > 0 -> {
                        Logger.tag(TAG).d("onNestedPreScroll TYPE_TOUCH Status 4")
                        /**
                         * 同二，让子视图自己处理
                         */
                        consumed.set(1, 0)//父视图不消耗任何滑动距离，让子视图自己处理即可
                    }
                }
            }

        // 非用户触摸引起嵌套滑动的场合,如Fling
            ViewCompat.TYPE_NON_TOUCH -> {
                /**
                 * Fling的特点：多次调用，依次衰减
                 * 也是分情况的有一写一来写
                 * 这里有个非常头疼的问题
                 * 就是惯性滑动的距离在子视图滑动到底部或顶部之前就产生了
                 * WebView还好，RecyclerView涉及溢出后加载更多时清除之前的惯性事件
                 * 外部在得知加载更多完成后调一下scrollToPosition(lastPosition)就可以解决
                 * 内部要怎么才能处理这个事儿呢？findLastVisibleItemPosition?
                 * 暂时先支持部分可以支持的布局(LinearLayoutManager,GridLayoutManager)
                 */
                when {
                //当子视图滑动到底部，产生向下的惯性衰减
                    dy > 0 && target.isScrolledToBottom() -> {
                        consumed.set(1, dy)
                        scrollBy(0, dy)
                        if (target is RecyclerView) {
                            val layoutManager = target.layoutManager
                            if (layoutManager is LinearLayoutManager) {
                                target.scrollToPosition(layoutManager.findLastVisibleItemPosition())
                            }
                            if (layoutManager is GridLayoutManager) {
                                target.scrollToPosition(layoutManager.findLastVisibleItemPosition())
                            }
                        }
                    }
                //当子视图滑动到顶部，产生向上的惯性衰减
                    dy < 0 && target.isScrolledToTop() -> {
                        consumed.set(1, dy)
                        scrollBy(0, dy)
                        if (scrollY == 0) {
                            val firstChild = mNestedChildren.first()
                            firstChild.scrollBy(0, dy)
                        }
                    }
                }
            }
        }
    }

    fun findTargetParent(target: View): View {
        return if (target.parent == this.getChildAt(0)) {
            target
        } else {
            findTargetParent(target.parent as View)
        }
    }

    /**
     * 当子视图调用dispatchNestedScroll时会收到该回调,约束为在子视图滑动结束后回调
     * @param dxConsumed 表示target已经消费的x方向的距离
     * @param dyConsumed 表示target已经消费的y方向的距离
     * @param dxUnconsumed 表示x方向剩下的滑动距离
     * @param dyUnconsumed 表示y方向剩下的滑动距离
     */
    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {
        Logger.tag(TAG).d("onNestedScroll dxConsumed : $dxConsumed | dyConsumed : $dyConsumed | dxUnconsumed : $dxUnconsumed | dyUnconsumed : $dyUnconsumed | type : $type")
        if (target !is INestedChild) {
            return
        }
    }

    /**
     * 子视图通知要结束嵌套滑动了
     */
    override fun onStopNestedScroll(target: View, type: Int) {
        Logger.tag(TAG).d("onStopNestedScroll type : $type")
        when (type) {
            ViewCompat.TYPE_TOUCH -> mIsNestTouching = false
            ViewCompat.TYPE_NON_TOUCH -> mIsNestNonTouching = false
        }
        mParentHelper.onStopNestedScroll(target, type) // 停止嵌套滑动
    }

    /*-----------*/
    /* onMeasure */
    /*-----------*/

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val layout = getChildAt(0) // 唯一的子布局
        if (layout is ViewGroup) {
            findNested(layout)
        }
    }

    fun findNested(viewGroup: ViewGroup) {
        // 如果被嵌套的子视图比当前视图高，设置为和当前视图一样高，并启用子视图的嵌套滑动
        // 否则有多高用多高，并关闭子视图的嵌套滑动让父视图来处理滑动行为
        (0 until viewGroup.childCount).map { viewGroup.getChildAt(it) }
                .filter { it is ViewGroup || it is INestedChild }
                .forEach {
                    if (it is ViewGroup && it !is INestedChild) {
                        findNested(it)
                        return@forEach
                    }
                    if (!mNestedChildren.contains(it)) {
                        mNestedChildren.add(it)
                    }

                    val nestChild = it as NestedScrollingChild2
                    val params = it.layoutParams

                    /**
                     * RecyclerView设为匹配父类时不知为何measuredHeight的高度和父视图并不一致
                     * 临时强制处理了
                     */
                    if (it is NestedChildRecyclerView) {
                        it.maxHeight = this.measuredHeight
                        nestChild.isNestedScrollingEnabled = true
                        return
                    }

                    if (it.measuredHeight >= this.measuredHeight) {
                        params.height = this.measuredHeight
                        it.layoutParams = params
                        /**
                         * 不能重复付true，特定系统版本上会Crash(已知4.4)
                         * 你问我为啥？看起来像是直接调用了View的相关方法（而不是兼容包），但明显低版本是不存在的
                         * 所以这是Google的bug？还是刻意为之？
                         */
                        if (!nestChild.isNestedScrollingEnabled) {
                            nestChild.isNestedScrollingEnabled = true
                        }
                    } else {
                        nestChild.isNestedScrollingEnabled = false
                    }
                }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        val view = mNestedChildren.lastOrNull()?:return super.onInterceptTouchEvent(ev)
        if (view is RecyclerView && view.adapter?.itemCount == 0) {
            return super.onInterceptTouchEvent(ev)
        }
        if (isScrolledToBottom() && view.height >= this.height) {
            return false
        }
        return super.onInterceptTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.action //触摸动作
        when (action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                val child = mNestedChildren.lastOrNull()?:return super.onTouchEvent(event)
                if (child is RecyclerView) {
                    child.stopScroll()
                }
            }
        }
        return super.onTouchEvent(event)
    }

    /*---------------------------*/
    /* 由父布局向子布局方向发起的消息 */
    /*---------------------------*/

    override fun hasNestedScrollingParent(type: Int): Boolean {
        return true
    }

    override fun dispatchNestedPreScroll(dx: Int, dy: Int, consumed: IntArray?, offsetInWindow: IntArray?, type: Int): Boolean {
        val lastNestedChildren = mNestedChildren.lastOrNull()
        if (lastNestedChildren is INestedChild) {
            if (isScrolledToBottom()) {
                if (type == ViewCompat.TYPE_TOUCH && dy < 0 && !lastNestedChildren.isScrolledToTop()) {
                    lastNestedChildren.scrollBy(0, dy)
                    consumed?.set(1, dy)
                    return true
                }
                lastNestedChildren.scrollBy(0, dy)
            }
        }
        return super.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type)
    }

    fun isScrolledToTop(): Boolean {
        return !canScrollVertically(-1)
    }

    fun isScrolledToBottom(): Boolean {
        return !canScrollVertically(1)
    }

}