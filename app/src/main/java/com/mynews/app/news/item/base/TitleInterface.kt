package com.mynews.app.news.item.base

import android.view.ViewGroup

/**
 * 标题控件需要实现的接口，用来解耦
 */
interface TitleInterface {

    fun setTitle(text:String)

    fun getTitle():String?

    fun updateTitleTextSize()

    fun setTitleColor(color:Int)

    fun setTitleVisibility(visibility:Int)

    fun setTitleMaxLines(maxLines:Int)

    fun setActivated(activated: Boolean)

    fun getLayoutParams():ViewGroup.LayoutParams

    fun getHeightByExactWidth(width:Int):Int

    fun getHeightByExactLines(lines:Int):Int
}