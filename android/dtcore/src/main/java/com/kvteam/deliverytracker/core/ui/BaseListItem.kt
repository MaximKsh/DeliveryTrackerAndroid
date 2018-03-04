package com.kvteam.deliverytracker.core.ui

import android.graphics.Color
import android.view.View
import com.amulyakhare.textdrawable.TextDrawable
import com.kvteam.deliverytracker.core.R
import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.roles.Role
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractSectionableItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.utils.DrawableUtils
import eu.davidea.viewholders.FlexibleViewHolder


abstract class BaseListItem <out T, VH : BaseListItem.BaseListViewHolder>(
        val item: T,
        header: BaseListHeader)
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