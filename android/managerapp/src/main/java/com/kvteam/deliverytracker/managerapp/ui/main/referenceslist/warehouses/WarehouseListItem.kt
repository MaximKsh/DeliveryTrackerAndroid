package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.warehouses

import android.graphics.Color
import android.view.View
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.models.Warehouse
import com.kvteam.deliverytracker.core.ui.BaseListHeader
import com.kvteam.deliverytracker.core.ui.BaseListItem
import com.kvteam.deliverytracker.managerapp.R
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.utils.DrawableUtils
import kotlinx.android.synthetic.main.warehouse_list_item.view.*

class WarehouseListItem(
        val warehouse: Warehouse,
        header: BaseListHeader?,
        private val lm: ILocalizationManager)
    : BaseListItem<Warehouse, WarehouseListItem.WarehousesListViewHolder>(warehouse, header) {

    override val key: String
        get() = warehouse.id!!.toString()

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>?) : WarehousesListViewHolder {
        return WarehousesListViewHolder(view, adapter);
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: WarehousesListViewHolder, position: Int, payloads: MutableList<Any>?) {
        holder.tvName.text = warehouse.name
        holder.tvAddress.text = warehouse.rawAddress
    }

    open class WarehousesListViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>?) : BaseListItem.BaseListViewHolder(view, adapter) {
        override val layoutID: Int
            get() = R.layout.warehouse_list_item

        val tvName = view.tvName!!
        val tvAddress = view.tvAddress!!
    }
}
