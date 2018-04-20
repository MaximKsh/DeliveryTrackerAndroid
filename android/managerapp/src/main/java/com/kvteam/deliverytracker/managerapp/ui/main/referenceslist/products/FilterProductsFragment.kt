package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.products

import android.os.Bundle
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.dataprovider.base.DataProviderGetMode
import com.kvteam.deliverytracker.core.models.Product
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.core.ui.BaseFilterFragment
import com.kvteam.deliverytracker.core.ui.IBaseListItemActions
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import eu.davidea.flexibleadapter.FlexibleAdapter
import java.util.*
import javax.inject.Inject

open class FilterProductsFragment : BaseFilterFragment() {
    @Inject
    lateinit var navigationController: NavigationController

    @Inject
    lateinit var session: ISession

    override val tracer
        get() = navigationController.fragmentTracer

    override val viewGroup: String = "ReferenceViewGroup"

    override val viewName = "ProductsView"

    override val type = "Product"

    private val taskIdKey = "task_id_key"
    private var taskId
        get() = arguments?.getSerializable(taskIdKey)!! as UUID
        set(value) = arguments?.putSerializable(taskIdKey, value)!!

    fun setEditTaskId (taskId: UUID) {
        this.taskId = taskId
    }

    private val productActions = object : IBaseListItemActions<ProductListItem> {
        override suspend fun onDelete(adapter: FlexibleAdapter<*>, itemList: MutableList<ProductListItem>, item: ProductListItem) {}

        override suspend fun onItemClicked(adapter: FlexibleAdapter<*>, itemList: MutableList<ProductListItem>, item: ProductListItem) {
            val taskProduct = dp.taskProducts.get(UUID.randomUUID(), taskId, DataProviderGetMode.DIRTY)
            taskProduct.instanceId = session.instance?.id!!
            taskProduct.productId = item.product.id
            taskProduct.quantity = 1
            dp.taskProducts.saveChanges(taskProduct)
            navigationController.closeCurrentFragment()
        }
    }

    override fun handleProducts(products: List<Product>, animate: Boolean) {
        val taskProducts = dp.taskProducts.getByParent(taskId, DataProviderGetMode.DIRTY)
        val productsList = products
                .filter { p -> p.id !in taskProducts.map { tp -> tp.productId }  }
                .map { ProductListItem(it, null, lm, activity!!)}.toMutableList()
        (mAdapter as ProductsListFlexibleAdapter).updateDataSet(productsList, animate)
    }

    override fun getViewFilterArguments(viewName: String, type: String?, groupIndex: Int, value: String): Map<String, Any>? {
        return mapOf("search" to value)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) = launchUI {
        mAdapter = ProductsListFlexibleAdapter(productActions)
        (mAdapter as ProductsListFlexibleAdapter).hideDeleteButton = true
        super.onActivityCreated(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        toolbarController.disableSearchMode()
    }
}
