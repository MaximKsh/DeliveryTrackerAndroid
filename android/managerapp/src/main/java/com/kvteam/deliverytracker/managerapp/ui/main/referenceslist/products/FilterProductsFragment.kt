package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.products

import android.os.Bundle
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.common.IEventEmitter
import com.kvteam.deliverytracker.core.models.Product
import com.kvteam.deliverytracker.core.ui.BaseListFragment
import com.kvteam.deliverytracker.core.ui.IBaseListItemActions
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import eu.davidea.flexibleadapter.FlexibleAdapter
import javax.inject.Inject

open class FilterProductsFragment : BaseListFragment() {
    @Inject
    lateinit var navigationController: NavigationController

    @Inject
    lateinit var emitter: IEventEmitter

    override val viewGroup: String = "ReferenceViewGroup"

    private val productActions = object : IBaseListItemActions<ProductListItem> {
        override fun onDelete(adapter: FlexibleAdapter<*>, itemList: MutableList<ProductListItem>, item: ProductListItem) {}

        override fun onItemClicked(adapter: FlexibleAdapter<*>, itemList: MutableList<ProductListItem>, item: ProductListItem) {
            emitter.signal("FilterProductSignal", item.product)
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

    override fun handleUpdateList(type: String, viewResult: List<Map<String, Any?>>) {
        val productsList = formatProducts(viewResult)
        (mAdapter as ProductsListFlexibleAdapter).updateDataSet(productsList, true)
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
            invokeAsync({
                viewWebservice.getViewResultAsync(viewGroup, "ProductsView", mapOf(
                        "name" to text
                ))
            }, {
                if (it.success) {
                    val result = it.entity?.viewResult!!
                    val productsList = formatProducts(result)
                    (mAdapter as ProductsListFlexibleAdapter).updateDataSet(productsList, true)
                }
            })
        })
        super.onActivityCreated(savedInstanceState)
    }
}
