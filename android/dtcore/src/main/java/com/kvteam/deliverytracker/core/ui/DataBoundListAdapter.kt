package com.kvteam.deliverytracker.core.ui

import android.databinding.ViewDataBinding
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.kvteam.deliverytracker.core.async.invokeAsync

abstract class DataBoundListAdapter<in T, V : ViewDataBinding>
    : RecyclerView.Adapter<DataBoundViewHolder<V>>() {

    private var items: List<T>? = null

    private var dataVersion = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder<V> {
        val binding = createBinding(parent)
        return DataBoundViewHolder(binding)
    }

    protected abstract fun createBinding(parent: ViewGroup): V

    override fun onBindViewHolder(holder: DataBoundViewHolder<V>, position: Int) {
        bind(holder.binding, items!![position])
        holder.binding.executePendingBindings()
    }

    fun replace(update: List<T>?) {
        dataVersion++
        if (items == null
                && update == null) {
            return
        }

        if (items == null) {
            items = update
            notifyDataSetChanged()
            return
        } else if (update == null) {
            val oldSize = items!!.size
            items = null
            notifyItemRangeRemoved(0, oldSize)
            return
        }

        val startVersion = dataVersion
        val oldItems = items

        invokeAsync({
            DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return oldItems!!.size
                }

                override fun getNewListSize(): Int {
                    return update.size
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val oldItem = oldItems!![oldItemPosition]
                    val newItem = update[newItemPosition]
                    return this@DataBoundListAdapter.areItemsTheSame(oldItem, newItem)
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val oldItem = oldItems!![oldItemPosition]
                    val newItem = update[newItemPosition]
                    return this@DataBoundListAdapter.areContentsTheSame(oldItem, newItem)
                }
            })
        }, {
            if (startVersion == dataVersion) {
                items = update
                it.dispatchUpdatesTo(this@DataBoundListAdapter)
            }
        })
    }


    protected abstract fun bind(binding: V, item: T)

    protected abstract fun areItemsTheSame(oldItem: T, newItem: T): Boolean

    protected abstract fun areContentsTheSame(oldItem: T, newItem: T): Boolean

    override fun getItemCount(): Int {
        return if (items == null) 0 else items!!.size
    }
}
