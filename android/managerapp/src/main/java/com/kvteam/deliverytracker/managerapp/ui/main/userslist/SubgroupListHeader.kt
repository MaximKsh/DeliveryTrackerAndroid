package com.kvteam.deliverytracker.managerapp.ui.main.userslist

import android.view.View
import com.kvteam.deliverytracker.managerapp.R
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractHeaderItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import kotlinx.android.synthetic.main.subgroup_list_header.view.*

class SubgroupListHeader(
        private val letter: String
) : AbstractHeaderItem<SubgroupListHeader.SubgroupListHeaderViewHolder>() {
    override fun getLayoutRes(): Int {
        return R.layout.subgroup_list_header
    }

    override fun hashCode(): Int {
        return letter.hashCode()
    }

    override fun createViewHolder(view: View?, adapter: FlexibleAdapter<out IFlexible<*>>?): SubgroupListHeaderViewHolder {
        return SubgroupListHeaderViewHolder(view, adapter)
    }

    override fun equals(other: Any?): Boolean {
        if (other is SubgroupListHeader) {
            return letter.equals(other.letter)
        }
        return false
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: SubgroupListHeaderViewHolder?, position: Int, payloads: MutableList<Any>?) {
        holder?.tvLetter?.text = letter
    }

    class SubgroupListHeaderViewHolder(val view: View?, val adapter: FlexibleAdapter<out IFlexible<*>>?) : FlexibleViewHolder(view, adapter, true) {
        val tvLetter = view?.tvLetter
    }
}
