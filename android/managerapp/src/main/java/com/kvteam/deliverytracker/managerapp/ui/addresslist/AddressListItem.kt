package com.kvteam.deliverytracker.managerapp.ui.addresslist

import android.support.v7.widget.RecyclerView
import android.view.View
import com.kvteam.deliverytracker.core.models.ClientAddress
import com.kvteam.deliverytracker.managerapp.R
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import kotlinx.android.synthetic.main.address_list_item.view.*

class AddressListItem(val address: ClientAddress): AbstractFlexibleItem<AddressListItem.AddressViewHolder>() {

    override fun equals(other: Any?): Boolean {
        if (other is AddressListItem) {
            return address.equals(other.address)
        }
        return false
    }

    override fun hashCode(): Int {
        return address.hashCode()
    }

    override fun getLayoutRes(): Int {
        return R.layout.address_list_item
    }

    override fun createViewHolder(view: View?, adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?): AddressViewHolder {
        return AddressViewHolder(view!!, adapter)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?, holder: AddressViewHolder, position: Int, payloads: MutableList<Any>?) {
        holder.tvRawAddress.text = address.rawAddress
    }

    class AddressViewHolder(
            val view: View,
            val adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?)
        : FlexibleViewHolder(view, adapter) {
        var tvRawAddress = view.tvRawAddress!!
    }
}