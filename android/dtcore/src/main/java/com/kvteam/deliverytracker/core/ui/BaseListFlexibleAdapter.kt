package com.kvteam.deliverytracker.core.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.kvteam.deliverytracker.core.async.launchUI
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import kotlinx.android.synthetic.main.base_list_item.view.*


abstract class BaseListFlexibleAdapter <out T1, T2 : BaseListItem<T1, VH>, VH : BaseListItem.BaseListViewHolder >(
        private var noHeaderItems: MutableList<T2>,
        private val itemActions: IBaseListItemActions<T2>) : FlexibleAdapter<T2>(noHeaderItems) {
    private val viewBinderHelper = ViewBinderHelper()

    var hideDeleteButton = false

    init {
        viewBinderHelper.setOpenOnlyOne(true)
    }

    abstract class BaseListHolder(itemView: View, adapter: FlexibleAdapter<out IFlexible<*>>?) : FlexibleViewHolder(itemView, adapter) {
        abstract val layoutID: Int

        var container : ViewGroup
            private set
        var swipeRevealLayout: SwipeRevealLayout
            private set
        var tvDeleteItem : TextView
            private set

        init {
            container = itemView.frListContainer!!
            swipeRevealLayout = itemView.swipeRevealLayout!!
            tvDeleteItem = itemView.tvDeleteItem!!

            container.removeAllViews()
            val inflater = LayoutInflater.from(container.context)
            val view = inflater.inflate(layoutID, container, false)
            container.addView(view)
        }
    }

    override fun onPostUpdate() {
        super.onPostUpdate()
        noHeaderItems = currentItems.filter { item: IFlexible<*> -> item is BaseListItem<*, *> }.toMutableList()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any?>) {
        if (holder is BaseListHolder) {

            val item = getItem(position)!!

            holder.swipeRevealLayout.setSwipeListener(object: SwipeRevealLayout.SimpleSwipeListener() {
                override fun onClosed(view: SwipeRevealLayout) {
                    holder.container.setOnClickListener {
                        launchUI {
                            itemActions.onItemClicked(this@BaseListFlexibleAdapter, noHeaderItems, item)
                        }
                    }
                }

                override fun onOpened(view: SwipeRevealLayout) {
                    holder.container.setOnClickListener {
                        holder.swipeRevealLayout.close(true)
                    }
                }

                override fun onSlide(view: SwipeRevealLayout, slideOffset: Float) {}
            })

            holder.tvDeleteItem.setOnClickListener {
                launchUI {
                    itemActions.onDelete(this@BaseListFlexibleAdapter, noHeaderItems, item)
                }
            }

            holder.container.setOnClickListener {
                launchUI {
                    itemActions.onItemClicked(this@BaseListFlexibleAdapter, noHeaderItems, item)
                }
            }

            viewBinderHelper.bind(holder.swipeRevealLayout, item.key)

            holder.swipeRevealLayout.setLockDrag(hideDeleteButton)
        }

        super.onBindViewHolder(holder, position, payloads)
    }
}

