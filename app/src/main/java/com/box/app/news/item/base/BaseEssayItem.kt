package com.box.app.news.item.base

import com.box.app.news.bean.Essay

/**
 * 段子cell的父类
 * 7.1新需求
 */
abstract class BaseEssayItem<VH : BaseNewsItem.ViewHolder>(mBean: Essay)
    : BaseNewsItem<Essay, VH>(mBean){

    companion object {
        const val ItemViewTypeExtra = 3000
    }

    override fun getItemViewType(): Int {
        return super.getItemViewType() - ItemViewTypeExtra
    }
}