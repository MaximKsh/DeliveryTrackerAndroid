package com.kvteam.deliverytracker.core.ui

import android.support.v7.widget.RecyclerView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible

interface IBaseListItemActions <T2 : IFlexible<out RecyclerView.ViewHolder>> {
    suspend fun onDelete(adapter: FlexibleAdapter<*>,
                 itemList: MutableList<T2>,
                 item: T2)
    suspend fun onItemClicked(adapter: FlexibleAdapter<*>,
                      itemList: MutableList<T2>,
                      item: T2)
}