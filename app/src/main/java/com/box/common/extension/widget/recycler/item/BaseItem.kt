package com.box.common.extension.widget.recycler.item

import android.view.View
import com.box.common.core.log.Logger
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.box.common.extension.widget.recycler.holder.BaseViewHolder
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible

/**
 * This class will benefit of the already implemented methods (getter and setters) in
 * [AbstractFlexibleItem].
 *
 *
 * It is used as Base item for all example models.
 */
abstract class BaseItem<Bean, VH : BaseViewHolder>(protected var mBean: Bean)
    : AbstractFlexibleItem<VH>() {

    @Suppress("DeprecatedCallableAddReplaceWith")
    fun <T> getModel(clazz: Class<T>): T? {
        return if (clazz.isInstance(mBean)) {
            @Suppress("UNCHECKED_CAST")
            mBean as T?
        } else null
    }

    fun getModel(): Bean {
        return mBean
    }

    fun setModel(bean: Bean) {
        this.mBean = bean
    }

    fun updateByModel(bean: Bean, adapter: CommonRecyclerAdapter) {
        this.mBean = bean
        adapter.updateItem(this)
    }

    override final fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>?): VH {
        return createViewHolder(view, adapter as CommonRecyclerAdapter)
    }

    abstract fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): VH

    override final fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>, holder: VH, position: Int, payloads: MutableList<Any?>?) {
        bindViewHolder(adapter as CommonRecyclerAdapter, holder, position, payloads)
    }

    abstract fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: VH, position: Int, payloads: MutableList<Any?>?)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return mBean == (other as BaseItem<*, *>).mBean
    }

    override fun hashCode(): Int {
        return mBean?.hashCode() ?: 0
    }

}