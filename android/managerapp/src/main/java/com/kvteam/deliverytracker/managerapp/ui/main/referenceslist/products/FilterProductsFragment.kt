package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.products

import android.os.Bundle
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.dataprovider.DataProviderGetMode
import com.kvteam.deliverytracker.core.models.Product
import com.kvteam.deliverytracker.core.models.TaskProduct
import com.kvteam.deliverytracker.core.ui.BaseFilterFragment
import com.kvteam.deliverytracker.core.ui.IBaseListItemActions
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import eu.davidea.flexibleadapter.FlexibleAdapter
import java.util.*
import javax.inject.Inject

open class FilterProductsFragment : BaseFilterFragment() {
    @Inject
    lateinit var navigationController: NavigationController

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
            val task = dp.taskInfos.get(taskId, DataProviderGetMode.DIRTY).entry
            task.taskProducts.add(TaskProduct(item.product.id, 1))
            navigationController.closeCurrentFragment()
        }
    }

    override fun handleProducts(products: List<Product>, animate: Boolean) {
        val task = dp.taskInfos.get(taskId, DataProviderGetMode.DIRTY).entry
        val productsList = products
                .filter { p -> p.id !in task.taskProducts.map { tp -> tp.productId }  }
                .map { ProductListItem(it, null, lm, activity!!)}.toMutableList()
        (mAdapter as ProductsListFlexibleAdapter).updateDataSet(productsList, animate)
    }

    override fun getViewFilterArguments(viewName: String, type: String?, groupIndex: Int, value: String): Map<String, Any>? {
        return mapOf("search" to value)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) = launchUI {
        mAdapter = ProductsListFlexibleAdapter(mutableListOf(), productActions)
        (mAdapter as ProductsListFlexibleAdapter).hideDeleteButton = true
        super.onActivityCreated(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        toolbarController.disableSearchMode()
    }
}
