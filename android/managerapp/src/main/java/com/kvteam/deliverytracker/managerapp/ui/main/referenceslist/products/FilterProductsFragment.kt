package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.products

import android.os.Bundle
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


    private fun formatProducts(viewResult: List<Map<String, Any?>>): MutableList<ProductListItem> {
        return viewResult
                .map { referenceMap ->
                    val product = Product()
                    product.fromMap(referenceMap)
                    ProductListItem(product, null, lm)
                }.toMutableList()
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
            mAdapter.searchText = text
            val result = viewWebservice.getViewResultAsync(viewGroup, "ProductsView", mapOf(
                    "name" to text
            ))
            if(eh.handle(result)) {
                return@enableSearchMode
            }
            val productsList = formatProducts(result.entity?.viewResult!!)
            (mAdapter as ProductsListFlexibleAdapter).updateDataSet(productsList, true)

        })
        super.onActivityCreated(savedInstanceState)
    }
}
