package com.kvteam.deliverytracker.core.ui

import android.view.View
import com.kvteam.deliverytracker.core.R
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractSectionableItem
import eu.davidea.flexibleadapter.items.IFilterable
import eu.davidea.flexibleadapter.items.IFlexible


abstract class BaseListItem <out T, VH : BaseListItem.BaseListViewHolder>(
        val item: T,
        header: BaseListHeader?)
    : AbstractSectionableItem<VH, BaseListHeader>(header) {

    abstract val key: String

    override fun getLayoutRes(): Int {
        return R.layout.base_list_item
    }

    override fun equals(other: Any?): Boolean {
        if (other is BaseListItem<*, *>) {
            return key == other.key
        }
        return false
    }

    override fun hashCode(): Int {
        return key.hashCode()
    }

    abstract class BaseListViewHolder(val view: View, val adapter: FlexibleAdapter<out IFlexible<*>>?)
        : BaseListFlexibleAdapter.BaseListHolder(view, adapter) {}
}