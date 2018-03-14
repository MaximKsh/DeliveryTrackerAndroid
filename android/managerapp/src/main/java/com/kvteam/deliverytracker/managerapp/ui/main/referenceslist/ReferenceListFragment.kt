package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.models.Client
import com.kvteam.deliverytracker.core.models.PaymentType
import com.kvteam.deliverytracker.core.models.Product
import com.kvteam.deliverytracker.core.models.Warehouse
import com.kvteam.deliverytracker.core.ui.BaseListFragment
import com.kvteam.deliverytracker.core.ui.BaseListHeader
import com.kvteam.deliverytracker.core.ui.IBaseListItemActions
import com.kvteam.deliverytracker.core.ui.dropdowntop.ToolbarController
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
        override fun onDelete(adapter: FlexibleAdapter<*>, itemList: MutableList<PaymentTypeListItem>, item: PaymentTypeListItem) {
            if (adapter !is PaymentTypesListFlexibleAdapter) {
                return
            }
            invokeAsync({
                referenceWebservice.delete("PaymentType", item.paymentType.id!!)
            }, {
                if (it.success) {
                    itemList.remove(item)
                    adapter.updateDataSet(itemList, true)
                }
            })
        }

        override fun onItemClicked(adapter: FlexibleAdapter<*>, itemList: MutableList<PaymentTypeListItem>, item: PaymentTypeListItem) {}
    }

    private val warehousesActions = object : IBaseListItemActions<WarehouseListItem> {
        override fun onDelete(adapter: FlexibleAdapter<*>, itemList: MutableList<WarehouseListItem>, item: WarehouseListItem) {
            if (adapter !is WarehousesListFlexibleAdapter) {
                return
            }
            invokeAsync({
                referenceWebservice.delete("Warehouse", item.warehouse.id!!)
            }, {
                if (it.success) {
                    itemList.remove(item)
                    adapter.updateDataSet(itemList, true)
                }
            })
        }

        override fun onItemClicked(adapter: FlexibleAdapter<*>, itemList: MutableList<WarehouseListItem>, item: WarehouseListItem) {}
    }

    private val productsActions = object : IBaseListItemActions<ProductListItem> {
        override fun onDelete(adapter: FlexibleAdapter<*>, itemList: MutableList<ProductListItem>, item: ProductListItem) {
            if (adapter !is ProductsListFlexibleAdapter) {
                return
            }
            invokeAsync({
                referenceWebservice.delete("Product", item.product.id!!)
            }, {
                if (it.success) {
                    itemList.remove(item)
                    adapter.updateDataSet(itemList, true)
                }
            })
        }

        override fun onItemClicked(adapter: FlexibleAdapter<*>, itemList: MutableList<ProductListItem>, item: ProductListItem) {}
    }

    private val clientsActions = object : IBaseListItemActions<ClientListItem> {
        override fun onDelete(adapter: FlexibleAdapter<*>, itemList: MutableList<ClientListItem>, item: ClientListItem) {
            if (adapter !is ClientsListFlexibleAdapter) {
                return
            }
            invokeAsync({
                referenceWebservice.delete("Client", item.client.id!!)
            }, {
                if (it.success) {
                    itemList.remove(item)
                    adapter.updateDataSet(itemList, true)
                }
            })
        }

        override fun onItemClicked(adapter: FlexibleAdapter<*>, itemList: MutableList<ClientListItem>, item: ClientListItem) {}
    }

    private fun formatWarehouses(viewResult: List<Map<String, Any?>>): MutableList<WarehouseListItem> {
        return viewResult
                .map { referenceMap ->
                    val warehouse = Warehouse()
                    warehouse.fromMap(referenceMap)
                    WarehouseListItem(warehouse, null, lm)
                }.toMutableList()
    }

    private fun formatPaymentTypes(viewResult: List<Map<String, Any?>>): MutableList<PaymentTypeListItem> {
        return viewResult
                .map { referenceMap ->
                    val payment = PaymentType()
                    payment.fromMap(referenceMap)
                    PaymentTypeListItem(payment, null, lm)
                }.toMutableList()
    }

    private fun formatProducts(viewResult: List<Map<String, Any?>>): MutableList<ProductListItem> {
        return viewResult
                .map { referenceMap ->
                    val product = Product()
                    product.fromMap(referenceMap)
                    ProductListItem(product, null, lm)
                }.toMutableList()
    }

    private fun formatClients(viewResult: List<Map<String, Any?>>): MutableList<ClientListItem> {
        var letter: Char? = null
        var header = BaseListHeader("A")

        return viewResult
                .map { clientMap ->
                    val client = Client()
                    client.fromMap(clientMap)
                    client
                }
                .sortedBy { c -> c.surname }
                .map { client ->
                    if (letter == null || letter != client.surname!![0]) {
                        letter = client.surname!![0]
                        header = BaseListHeader(letter!!.toString())
                    }
                    ClientListItem(client, header, lm)
                }.toMutableList()
    }

    override fun handleUpdateList(type: String, viewResult: List<Map<String, Any?>>) {
        when (type) {
            "PaymentType" -> {
                val referencesList = formatPaymentTypes(viewResult)
                val adapter = mAdapter as? PaymentTypesListFlexibleAdapter
                setMenuMask(PAYMENT_TYPES__MENU_MASK)
                if (adapter != null) {
                    adapter.updateDataSet(referencesList, true)
                } else {
                    mAdapter = PaymentTypesListFlexibleAdapter(referencesList, paymentTypesActions)
                    initAdapter()
                }
            }
            "Product" -> {
                val referencesList = formatProducts(viewResult)
                val adapter = mAdapter as? ProductsListFlexibleAdapter
                setMenuMask(PRODUCTS_MENU_MASK)
                if (adapter != null) {
                    adapter.updateDataSet(referencesList, true)
                } else {
                    mAdapter = ProductsListFlexibleAdapter(referencesList, productsActions)
                    initAdapter()
                }
            }
            "Client" -> {
                val referencesList = formatClients(viewResult)
                val adapter = mAdapter as? ClientsListFlexibleAdapter
                setMenuMask(CLIENTS_MENU_MASK)
                if (adapter != null) {
                    adapter.updateDataSet(referencesList, true)
                } else {
                    mAdapter = ClientsListFlexibleAdapter(referencesList, clientsActions)
                    initAdapter()
                }
            }
            "Warehouse" -> {
                val referencesList = formatWarehouses(viewResult)
                val adapter = mAdapter as? WarehousesListFlexibleAdapter
                setMenuMask(WAREHOUSES_MENU_MASK)
                if (adapter != null) {
                    adapter.updateDataSet(referencesList, true)
                } else {
                    mAdapter = WarehousesListFlexibleAdapter(referencesList, warehousesActions)
                    initAdapter()
                }
            }
        }
    }

    override fun configureToolbar(toolbar: ToolbarController) {
        toolbarController.enableDropdown()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mAdapter = ProductsListFlexibleAdapter(mutableListOf(), productsActions)
        super.onActivityCreated(savedInstanceState)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_client -> {
                navigationController.navigateToAddClient()
            }
            R.id.action_add_payment_type -> {
                navigationController.navigateToAddPaymentType()
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