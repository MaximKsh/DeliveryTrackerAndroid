package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist

import android.os.Bundle
import android.view.*
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.models.Client
import com.kvteam.deliverytracker.core.models.PaymentType
import com.kvteam.deliverytracker.core.models.Product
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
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.paymenttypes.ProductsListFlexibleAdapter
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.paymenttypes.ProductListItem
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

    private val PRODUCTS_MENU_MASK = PRODUCTS_MENU_ITEM
    private val PAYMENT_TYPES__MENU_MASK = PAYMENT_TYPES_MENU_ITEM
    private val CLIENTS_MENU_MASK = CLIENTS_MENU_ITEM

    private val paymentTypesActions = object: IBaseListItemActions<PaymentTypeListItem> {
        override fun onDelete(adapter: FlexibleAdapter<*>, itemList: MutableList<PaymentTypeListItem>, item: PaymentTypeListItem) {
            if (adapter !is PaymentTypesListFlexibleAdapter) {
                return
            }
            invokeAsync({
                referenceWebservice.delete("PaymentType", item.paymentType.id!!)
            }, {
                if(it.success) {
                    itemList.remove(item)
                    adapter.updateDataSet(itemList, true)
                }
            })
        }

        override fun onItemClicked(adapter: FlexibleAdapter<*>, itemList: MutableList<PaymentTypeListItem>, item: PaymentTypeListItem) {}
    }

    private val productsActions = object: IBaseListItemActions<ProductListItem> {
        override fun onDelete(adapter: FlexibleAdapter<*>, itemList: MutableList<ProductListItem>, item: ProductListItem) {
            if(adapter !is ProductsListFlexibleAdapter) {
                return
            }
            invokeAsync({
                referenceWebservice.delete("Product", item.product.id!!)
            }, {
                if(it.success) {
                    itemList.remove(item)
                    adapter.updateDataSet(itemList, true)
                }
            })
        }

        override fun onItemClicked(adapter: FlexibleAdapter<*>, itemList: MutableList<ProductListItem>, item: ProductListItem) {}
    }

    private val clientsActions = object: IBaseListItemActions<ClientListItem> {
        override fun onDelete(adapter: FlexibleAdapter<*>, itemList: MutableList<ClientListItem>, item: ClientListItem) {
            if(adapter !is ClientsListFlexibleAdapter) {
                return
            }
            invokeAsync({
                referenceWebservice.delete("Client", item.client.id!!)
            }, {
                if(it.success) {
                    itemList.remove(item)
                    adapter.updateDataSet(itemList, true)
                }
            })
        }

        override fun onItemClicked(adapter: FlexibleAdapter<*>, itemList: MutableList<ClientListItem>, item: ClientListItem) {}
    }

    private fun formatPaymentTypes(viewResult: List<Map<String, Any?>>): MutableList<PaymentTypeListItem> {
        val header = BaseListHeader("A")

        return viewResult
                .map { referenceMap ->
                    val payment = PaymentType()
                    payment.fromMap(referenceMap)
                    PaymentTypeListItem(payment, header, lm)
                }.toMutableList()
    }

    private fun formatProducts(viewResult: List<Map<String, Any?>>): MutableList<ProductListItem> {
        val header = BaseListHeader("A")

        return viewResult
                .map { referenceMap ->
                    val product = Product()
                    product.fromMap(referenceMap)
                    ProductListItem(product, header, lm)
                }.toMutableList()
    }

    private fun formatClients(viewResult: List<Map<String, Any?>>): MutableList<ClientListItem> {
        val header = BaseListHeader("A")

        return viewResult
                .map { referenceMap ->
                    val client = Client()
                    client.fromMap(referenceMap)
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
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mAdapter = ProductsListFlexibleAdapter(mutableListOf(), productsActions)
        super.onActivityCreated(savedInstanceState)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_client -> {
                navigationController.navigateToAddPaymentReference()
            }
            R.id.action_add_payment_type -> {
                navigationController.navigateToAddPaymentReference()
            }
            R.id.action_add_product -> {
                navigationController.navigateToAddPaymentReference()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_reference_list_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}