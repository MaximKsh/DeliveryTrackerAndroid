package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.dataprovider.NetworkException
import com.kvteam.deliverytracker.core.models.Client
import com.kvteam.deliverytracker.core.models.PaymentType
import com.kvteam.deliverytracker.core.models.Product
import com.kvteam.deliverytracker.core.models.Warehouse
import com.kvteam.deliverytracker.core.ui.BaseListFragment
import com.kvteam.deliverytracker.core.ui.BaseListHeader
import com.kvteam.deliverytracker.core.ui.IBaseListItemActions
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController
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

    override val viewGroup: String = "ReferenceViewGroup"

    private val PRODUCTS_MENU_ITEM = 1
    private val PAYMENT_TYPES_MENU_ITEM = PRODUCTS_MENU_ITEM shl 1
    private val CLIENTS_MENU_ITEM = PAYMENT_TYPES_MENU_ITEM shl 1
    private val WAREHOUSES_MENU_ITEM = CLIENTS_MENU_ITEM shl 1

    private val PRODUCTS_MENU_MASK = PRODUCTS_MENU_ITEM
    private val PAYMENT_TYPES__MENU_MASK = PAYMENT_TYPES_MENU_ITEM
    private val CLIENTS_MENU_MASK = CLIENTS_MENU_ITEM
    private val WAREHOUSES_MENU_MASK = WAREHOUSES_MENU_ITEM

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
        val adapter = mAdapter as? PaymentTypesListFlexibleAdapter
        setMenuMask(PAYMENT_TYPES__MENU_MASK)
        if (adapter != null) {
            adapter.updateDataSet(referencesList, animate)
        } else {
            mAdapter = PaymentTypesListFlexibleAdapter(referencesList, paymentTypesActions)
        }
    }

    override fun handleProducts(products: List<Product>, animate: Boolean) {
        val referencesList = products
                .map { ProductListItem(it, null, lm) }
                .toMutableList()
        val adapter = mAdapter as? ProductsListFlexibleAdapter
        setMenuMask(PRODUCTS_MENU_MASK)
        if (adapter != null) {
            adapter.updateDataSet(referencesList, animate)
        } else {
            mAdapter = ProductsListFlexibleAdapter(referencesList, productsActions)
        }
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
                    ClientListItem(client, header, lm)
                }.toMutableList()
        val adapter = mAdapter as? ClientsListFlexibleAdapter
        setMenuMask(CLIENTS_MENU_MASK)
        if (adapter != null) {
            adapter.updateDataSet(referencesList, animate)
        } else {
            mAdapter = ClientsListFlexibleAdapter(referencesList, clientsActions)
        }
    }

    override fun handleWarehouses(warehouses: List<Warehouse>, animate: Boolean) {
        val referencesList = warehouses
                .map { WarehouseListItem(it, null, lm) }
                .toMutableList()
        val adapter = mAdapter as? WarehousesListFlexibleAdapter
        setMenuMask(WAREHOUSES_MENU_MASK)
        if (adapter != null) {
            adapter.updateDataSet(referencesList, animate)
        } else {
            mAdapter = WarehousesListFlexibleAdapter(referencesList, warehousesActions)
        }
    }

    override fun getViewFilterArguments(viewName: String, type: String?, groupIndex: Int, value: String): Map<String, Any>? {
        return mapOf("search" to value)
    }

    override fun configureToolbar(toolbar: ToolbarController) {
        toolbarController.enableDropdown()
        useSearchInToolbar(toolbar)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mAdapter = ProductsListFlexibleAdapter(mutableListOf(), productsActions)
        super.onActivityCreated(savedInstanceState)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_client -> {
                navigationController.navigateToEditClient()
            }
            R.id.action_add_payment_type -> {
                navigationController.navigateToEditPaymentType()
            }
            R.id.action_add_product -> {
                navigationController.navigateToEditProduct()
            }
            R.id.action_add_warehouse -> {
                navigationController.navigateToEditWarehouse()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_reference_list_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}