package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.clients

import android.graphics.Color
import android.view.View
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.models.Client
import com.kvteam.deliverytracker.core.ui.BaseListHeader
import com.kvteam.deliverytracker.core.ui.BaseListItem
import com.kvteam.deliverytracker.managerapp.R
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.utils.DrawableUtils
import kotlinx.android.synthetic.main.client_list_item.view.*

class ClientListItem(
        val client: Client,
        header: BaseListHeader,
        private val lm: ILocalizationManager)
    : BaseListItem<Client, ClientListItem.ClientsListViewHolder>(client, header) {

    override val key: String
        get() = client.id!!.toString()

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>?) : ClientsListViewHolder {
        return ClientsListViewHolder(view, adapter);
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: ClientsListViewHolder, position: Int, payloads: MutableList<Any>?) {
        val context = holder.itemView.context

        val drawable = DrawableUtils.getSelectableBackgroundCompat(
                Color.WHITE, Color.parseColor("#dddddd"),
                DrawableUtils.getColorControlHighlight(context))

        DrawableUtils.setBackgroundCompat(holder.itemView, drawable)

        holder.tvName.text = client.name
        holder.tvSurname.text = client.surname
        holder.tvPhoneNumber.text = client.phoneNumber
    }

    open class ClientsListViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>?) : BaseListItem.BaseListViewHolder(view, adapter) {
        override val layoutID: Int
            get() = R.layout.client_list_item

        val tvName = view.tvName!!
        val tvSurname = view.tvSurname!!
        val tvPhoneNumber = view.tvPhoneNumber!!
    }
}

