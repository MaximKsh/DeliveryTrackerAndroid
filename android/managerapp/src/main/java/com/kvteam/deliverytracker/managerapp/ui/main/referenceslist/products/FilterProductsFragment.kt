package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.products

import android.os.Bundle
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.dataprovider.CacheException
import com.kvteam.deliverytracker.core.dataprovider.DataProviderGetMode
import com.kvteam.deliverytracker.core.models.Product
import com.kvteam.deliverytracker.core.models.TaskProduct
import com.kvteam.deliverytracker.core.ui.BaseListFragment
import com.kvteam.deliverytracker.core.ui.IBaseListItemActions
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import eu.davidea.flexibleadapter.FlexibleAdapter
import java.util.*
import javax.inject.Inject

open class FilterProductsFragment : BaseListFragment() {
    @Inject
    lateinit var navigationController: NavigationController

    override val tracer
            get() = navigationController.fragmentTracer

    override val viewGroup: String = "ReferenceViewGroup"

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
            val task = dp.taskInfos.getAsync(taskId, DataProviderGetMode.DIRTY).entry
            task.taskProducts.add(TaskProduct(item.product.id, 1))
            navigationController.closeCurrentFragment()
        }
    }

    override fun handleProducts(products: List<Product>, animate: Boolean) {
        val productsList = products.map { ProductListItem(it, null, lm, activity!!)}.toMutableList()
        (mAdapter as ProductsListFlexibleAdapter).updateDataSet(productsList, animate)
    }

    override fun onDestroy() {
        super.onDestroy()
        toolbarController.disableSearchMode()
    }

    private suspend fun updateDataList (argument: Map<String, Any>? = null) : MutableList<Product> {
        val (result, origin) = dp.productsViews.getViewResultAsync(
                viewGroup,
                "ProductsView",
                argument,
                DataProviderGetMode.FORCE_WEB)
        val entities = mutableListOf<Product>()
        for(id in result) {
            try{
                val (e, _) = dp.products.getAsync(id, DataProviderGetMode.FORCE_CACHE)
                entities.add(e)
            } catch (e: CacheException) {}
        }
        return entities
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) = launchUI {
        super.onActivityCreated(savedInstanceState)
        mAdapter = ProductsListFlexibleAdapter(mutableListOf(), productActions)
        (mAdapter as ProductsListFlexibleAdapter).hideDeleteButton = true

        val entities = updateDataList()

        handleProducts(entities, false)

        toolbarController.enableSearchMode({text ->
            val filteredEntities = updateDataList(mapOf(
                    "search" to text
            ))

            handleProducts(filteredEntities, true)
        })
        super.onActivityCreated(savedInstanceState)
    }
}
