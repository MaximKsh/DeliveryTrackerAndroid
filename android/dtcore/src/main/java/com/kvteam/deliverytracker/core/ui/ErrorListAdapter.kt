package com.kvteam.deliverytracker.core.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kvteam.deliverytracker.core.R
import com.kvteam.deliverytracker.core.common.ErrorChain
import com.kvteam.deliverytracker.core.common.ErrorItem
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder
import kotlinx.android.synthetic.main.dialog_error_item.view.*
import kotlinx.android.synthetic.main.dialog_error_title.view.*

class ErrorListAdapter
    constructor(items: List<ErrorChain>)
    : ExpandableRecyclerViewAdapter<
        ErrorListAdapter.ErrorChainViewHolder,
        ErrorListAdapter.ErrorViewHolder>(items.map { ExpandableGroup(it.alias, it.items) }) {

    init {
        for (i in 0..(expandableList.expandedGroupIndexes.size - 1)) {
            expandableList.expandedGroupIndexes[i] = true
        }
    }

    override fun onCreateChildViewHolder(parent: ViewGroup, viewType: Int): ErrorViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.dialog_error_item, parent, false)
        return ErrorViewHolder(view)
    }

    override fun onCreateGroupViewHolder(parent: ViewGroup, viewType: Int): ErrorChainViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.dialog_error_title, parent, false)
        return ErrorChainViewHolder(view)
    }

    override fun onBindChildViewHolder(
            holder: ErrorViewHolder,
            flatPosition: Int,
            group: ExpandableGroup<*>,
            childIndex: Int) {
        holder.tvErrorItem.text = (group.items[childIndex] as ErrorItem).message
    }

    override fun onBindGroupViewHolder(
            holder: ErrorChainViewHolder,
            flatPosition: Int,
            group: ExpandableGroup<*>) {
        holder.tvErrorTitle.text = group.title
    }

    class ErrorChainViewHolder constructor(view: View)
        : GroupViewHolder(view) {
        val tvErrorTitle = view.tvErrorTitle!!
    }
    class ErrorViewHolder constructor(view: View)
        : ChildViewHolder(view) {
        val tvErrorItem = view.tvErrorItem!!
    }

}