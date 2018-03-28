package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.products

import android.os.Bundle
import com.kvteam.deliverytracker.core.dataprovider.CacheException
import com.kvteam.deliverytracker.core.dataprovider.DataProviderGetMode
import com.kvteam.deliverytracker.core.models.Product
import com.kvteam.deliverytracker.core.ui.BaseListFragment
import com.kvteam.deliverytracker.core.ui.IBaseListItemActions
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import eu.davidea.flexibleadapter.FlexibleAdapter
import javax.inject.Inject

open class FilterProductsFragment : BaseListFragment() {
    @Inject
    lateinit var navigationController: NavigationController

    override val tracer
            get() = navigationController.fragmentTracer

    override val viewGroup: String = "ReferenceViewGroup"

    private val productActions = object : IBaseListItemActions<ProductListItem> {
        override suspend fun onDelete(adapter: FlexibleAdapter<*>, itemList: MutableList<ProductListItem>, item: ProductListItem) {}

        override suspend fun onItemClicked(adapter: FlexibleAdapter<*>, itemList: MutableList<ProductListItem>, item: ProductListItem) {
            navigationController.closeCurrentFragment()
        }
    }

    override fun handleProducts(products: List<Product>, animate: Boolean) {
        val productsList = products.map { ProductListItem(it, null, lm)}.toMutableList()
        (mAdapter as ProductsListFlexibleAdapter).updateDataSet(productsList, animate)
    }

    override fun onDestroy() {
        super.onDestroy()
        toolbarController.disableSearchMode()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mAdapter = ProductsListFlexibleAdapter(mutableListOf(), productActions)
        (mAdapter as ProductsListFlexibleAdapter).hideDeleteButton = true
        toolbarController.enableSearchMode({text ->
            val (result, origin) = dp.productsViews.getViewResultAsync(
                    "ReferenceViewGroup",
                    "ProductsView",
                    mapOf(
                            "name" to text
                    ),
                    DataProviderGetMode.FORCE_WEB)

            val entities = mutableListOf<Product>()
            for(id in result) {
                try{
                    val (e, _) = dp.products.getAsync(id, DataProviderGetMode.FORCE_CACHE)
                    entities.add(e)
                } catch (e: CacheException) {}
            }
            // (mAdapter as ProductsListFlexibleAdapter).updateDataSet(entities, true)
        })
        super.onActivityCreated(savedInstanceState)
    }
}
