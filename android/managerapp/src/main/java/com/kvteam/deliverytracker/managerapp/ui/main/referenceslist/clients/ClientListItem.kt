package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.clients

import android.support.v7.widget.RecyclerView
import android.view.View
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.models.Client
import com.kvteam.deliverytracker.core.models.ClientAddress
import com.kvteam.deliverytracker.core.ui.BaseListHeader
import com.kvteam.deliverytracker.core.ui.BaseListItem
import com.kvteam.deliverytracker.managerapp.R
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import kotlinx.android.synthetic.main.client_list_item.view.*

class ClientListItem(
        val client: Client,
        val displayableAddress: ClientAddress?,
        header: BaseListHeader,
        private val lm: ILocalizationManager)
    : BaseListItem<Client, ClientListItem.ClientsListViewHolder>(client, header) {

    override val key: String
        get() = client.id!!.toString()

    override fun createViewHolder(view: View?, adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?): ClientsListViewHolder {
        return ClientsListViewHolder(view!!, adapter);
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?,
                                holder: ClientsListViewHolder?,
                                position: Int,
                                payloads: MutableList<Any>?) {
        holder!!.tvName.text = client.name
        holder.tvSurname.text = client.surname
        holder.tvPhoneNumber.text = client.phoneNumber
        holder.tvAddress.text = displayableAddress?.rawAddress ?: lm.getString(R.string.ManagerApp_NoGeoposition)
    }


    open class ClientsListViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>?) : BaseListItem.BaseListViewHolder(view, adapter) {
        override val layoutID: Int
            get() = R.layout.client_list_item

        val tvName = view.tvName!!
        val tvSurname = view.tvSurname!!
        val tvPhoneNumber = view.tvPhoneNumber!!
        val tvAddress = view.tvAddress!!
    }
}

