package com.kvteam.deliverytracker.core.ui

import android.view.View
import com.kvteam.deliverytracker.core.R
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractHeaderItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import kotlinx.android.synthetic.main.base_list_header.view.*


class BaseListHeader(
        private val letter: String
) : AbstractHeaderItem<BaseListHeader.BaseListHeaderViewHolder>() {

    override fun equals(other: Any?): Boolean {
        if (other is BaseListHeader) {
            return letter == other.letter
        }
        return false
    }

    override fun getLayoutRes(): Int {
        return R.layout.base_list_header
    }

    override fun hashCode(): Int {
        return letter.hashCode()
    }

    override fun createViewHolder(view: View?, adapter: FlexibleAdapter<out IFlexible<*>>?): BaseListHeaderViewHolder {
        return BaseListHeaderViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: BaseListHeaderViewHolder?, position: Int, payloads: MutableList<Any>?) {
        holder?.tvLetter?.text = letter
    }

    class BaseListHeaderViewHolder(val view: View?, val adapter: FlexibleAdapter<out IFlexible<*>>?) : FlexibleViewHolder(view, adapter, true) {
        val tvLetter = view?.tvLetter
    }
}