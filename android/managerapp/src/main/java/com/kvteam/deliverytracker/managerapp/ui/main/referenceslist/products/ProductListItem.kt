package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.products

import android.graphics.Color
import android.view.View
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.models.Product
import com.kvteam.deliverytracker.core.ui.BaseListHeader
import com.kvteam.deliverytracker.core.ui.BaseListItem
import com.kvteam.deliverytracker.managerapp.R
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.utils.DrawableUtils
import eu.davidea.flexibleadapter.utils.FlexibleUtils
import kotlinx.android.synthetic.main.product_list_item.view.*

class ProductListItem(
        val product: Product,
        header: BaseListHeader?,
        private val lm: ILocalizationManager)
    : BaseListItem<Product, ProductListItem.ProductsListViewHolder>(product, header) {

    override val key: String
        get() = product.id!!.toString()

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>?) : ProductsListViewHolder {
        return ProductsListViewHolder(view, adapter);
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: ProductsListViewHolder, position: Int, payloads: MutableList<Any>?) {
        if (adapter!!.hasSearchText()) {
            FlexibleUtils.highlightText(
                    holder.tvName, product.name, adapter.searchText, Color.MAGENTA)
        } else {
            holder.tvName.text = product.name
        }
        holder.tvCost.text = product.cost.toString()
        holder.tvVendorCode.text = product.vendorCode
    }

    open class ProductsListViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>?) : BaseListItem.BaseListViewHolder(view, adapter) {
        override val layoutID: Int
            get() = R.layout.product_list_item

        val tvName = view.tvName!!
        val tvCost = view.tvCost!!
        val tvVendorCode = view.tvVendorCode!!
    }
 }
