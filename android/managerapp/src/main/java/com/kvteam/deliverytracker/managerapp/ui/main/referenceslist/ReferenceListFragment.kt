package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.dataprovider.base.DataProviderGetMode
import com.kvteam.deliverytracker.core.dataprovider.base.NetworkException
import com.kvteam.deliverytracker.core.models.Client
import com.kvteam.deliverytracker.core.models.PaymentType
import com.kvteam.deliverytracker.core.models.Product
import com.kvteam.deliverytracker.core.models.Warehouse
import com.kvteam.deliverytracker.core.ui.BaseListFragment
import com.kvteam.deliverytracker.core.ui.BaseListHeader
import com.kvteam.deliverytracker.core.ui.IBaseListItemActions
import com.kvteam.deliverytracker.core.webservice.IReferenceWebservice
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.clients.ClientListItem
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.clients.ClientsListFlexibleAdapter
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.paymenttypes.PaymentTypeListItem
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.paymenttypes.PaymentTypesListFlexibleAdapter
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.products.ProductListItem
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.products.ProductsListFlexibleAdapter
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.warehouses.WarehouseListItem
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.warehouses.WarehousesListFlexibleAdapter
import eu.davidea.flexibleadapter.FlexibleAdapter
import javax.inject.Inject

open class ReferenceListFragment : BaseListFragment() {
    @Inject
    lateinit var navigationController: NavigationController

    @Inject
    lateinit var referenceWebservice: IReferenceWebservice

    override val tracer
            get() = navigationController.fragmentTracer

    override val defaultHeader by lazy { lm.getString(R.string.ServerMessage_Views_ProductsView) }

    override val viewGroup: String = "ReferenceViewGroup"

    private val paymentTypesActions = object : IBaseListItemActions<PaymentTypeListItem> {
        override suspend fun onDelete(adapter: FlexibleAdapter<*>, itemList: MutableList<PaymentTypeListItem>, item: PaymentTypeListItem) {
            if (adapter !is PaymentTypesListFlexibleAdapter) {
                return
            }
            try {
                dp.paymentTypes.deleteAsync(item.paymentType.id!!)
            } catch (e: NetworkException) {
                eh.handle(e.result)
                return
            }
            itemList.remove(item)
            adapter.updateDataSet(itemList, true)
        }

        override suspend fun onItemClicked(adapter: FlexibleAdapter<*>, itemList: MutableList<PaymentTypeListItem>, item: PaymentTypeListItem) {
            navigationController.navigateToPaymentTypeDetails(item.paymentType.id!!)
        }
    }

    private val warehousesActions = object : IBaseListItemActions<WarehouseListItem> {
        override suspend fun onDelete(adapter: FlexibleAdapter<*>, itemList: MutableList<WarehouseListItem>, item: WarehouseListItem) {
            if (adapter !is WarehousesListFlexibleAdapter) {
                return
            }
            try {
                dp.warehouses.deleteAsync(item.warehouse.id!!)
            } catch (e: NetworkException) {
                eh.handle(e.result)
                return
            }
            itemList.remove(item)
            adapter.updateDataSet(itemList, true)
        }

        override suspend fun onItemClicked(adapter: FlexibleAdapter<*>, itemList: MutableList<WarehouseListItem>, item: WarehouseListItem) {
            navigationController.navigateToWarehouseDetails(item.warehouse.id!!)
        }
    }

    private val productsActions = object : IBaseListItemActions<ProductListItem> {
        override suspend fun onDelete(adapter: FlexibleAdapter<*>, itemList: MutableList<ProductListItem>, item: ProductListItem) {
            if (adapter !is ProductsListFlexibleAdapter) {
                return
            }
            try {
                dp.products.deleteAsync(item.product.id!!)
            } catch (e: NetworkException) {
                eh.handle(e.result)
                return
            }
            itemList.remove(item)
            adapter.updateDataSet(itemList, true)
        }

        override suspend fun onItemClicked(adapter: FlexibleAdapter<*>, itemList: MutableList<ProductListItem>, item: ProductListItem) {
            navigationController.navigateToProductDetails(item.product.id!!)
        }
    }

    private val clientsActions = object : IBaseListItemActions<ClientListItem> {
        override suspend fun onDelete(adapter: FlexibleAdapter<*>, itemList: MutableList<ClientListItem>, item: ClientListItem) {
            if (adapter !is ClientsListFlexibleAdapter) {
                return
            }

            try {
                dp.clients.deleteAsync(item.client.id!!)
            } catch (e: NetworkException) {
                eh.handle(e.result)
                return
            }
            itemList.remove(item)
            adapter.updateDataSet(itemList, true)
        }

        override suspend fun onItemClicked(adapter: FlexibleAdapter<*>, itemList: MutableList<ClientListItem>, item: ClientListItem) {
            navigationController.navigateToClientDetails(item.client.id!!)
        }
    }


    override fun handlePaymentTypes(paymentTypes: List<PaymentType>, animate: Boolean) {
        val referencesList = paymentTypes
                .map { PaymentTypeListItem(it, null, lm) }
                .toMutableList()
        updateDataSet(referencesList, { PaymentTypesListFlexibleAdapter(paymentTypesActions) }, animate)
    }

    override fun handleProducts(products: List<Product>, animate: Boolean) {
        val referencesList = products
                .map { ProductListItem(it, null, lm, activity!!) }
                .toMutableList()
        updateDataSet(referencesList, { ProductsListFlexibleAdapter(productsActions) }, animate)
    }

    override fun handleClients(clients: List<Client>, animate: Boolean) {
        var letter: Char? = null
        var header = BaseListHeader("A")

        val referencesList = clients
                .sortedBy { c -> c.surname }
                .map { client ->
                    val firstLetter = client.surname?.firstOrNull()
                    if (letter == null || letter != firstLetter) {
                        letter = firstLetter
                        header = BaseListHeader(letter?.toString() ?: EMPTY_STRING)
                    }
                    val addresses = dp.clientAddresses.getByParent(client.id!!, DataProviderGetMode.FORCE_CACHE)

                    ClientListItem(client, addresses.firstOrNull(), header, lm)
                }.toMutableList()
        updateDataSet(referencesList, { ClientsListFlexibleAdapter(clientsActions) }, animate)
    }

    override fun handleWarehouses(warehouses: List<Warehouse>, animate: Boolean) {
        val referencesList = warehouses
                .map { WarehouseListItem(it, null, lm) }
                .toMutableList()
        updateDataSet(referencesList, { WarehousesListFlexibleAdapter(warehousesActions) }, animate)
    }

    override fun getViewFilterArguments(viewName: String, type: String?, groupIndex: Int, value: String): Map<String, Any>? {
        return mapOf("search" to value)
    }

    override fun configureFloatingActionButton(button: FloatingActionButton) {
        button.visibility = View.VISIBLE
        button.setImageResource(R.drawable.ic_add_black_24dp)
        button.setOnClickListener {
            val selected = dtActivity.toolbarController.dropDownTop.lastSelectedIndex.get()
            when (selected) {
                0 -> navigationController.navigateToEditProduct()
                1 -> navigationController.navigateToEditPaymentType()
                2 -> navigationController.navigateToEditClient()
                3 -> navigationController.navigateToEditWarehouse()
            }
        }
    }

    override fun refreshMenuItems() {
        if(toolbarController.isSearchEnabled) {
            setMenuMask(0)
        } else {
            setMenuMask(1)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mAdapter = ProductsListFlexibleAdapter(productsActions)
        super.onActivityCreated(savedInstanceState)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_search) {
            search()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}