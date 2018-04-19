package com.kvteam.deliverytracker.managerapp.ui.main.taskslist

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.dataprovider.base.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.base.DataProviderGetMode
import com.kvteam.deliverytracker.core.models.TaskProduct
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_task_products.*
import kotlinx.android.synthetic.main.fragment_task_products.view.*
import kotlinx.android.synthetic.main.selected_product_item.view.*
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

class TaskProductsFragment : PageFragment() {
    @Inject
    lateinit var navigationController: NavigationController

    @Inject
    lateinit var dp: DataProvider

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_task_products, container, false) as ViewGroup
        val taskProducts = dp.taskProducts.getByParent(taskId, DataProviderGetMode.DIRTY)

        taskProducts.forEach { taskProductInfo ->
            val productItemView = inflater.inflate(R.layout.selected_product_item, view.llSelectedProduct, false)

            updateProductView(productItemView, taskProductInfo, view)


            productItemView.ivIconIncreaseQuantity.setOnClickListener { _ ->
                taskProductInfo.quantity = taskProductInfo.quantity!! + 1
                updateProductView(productItemView, taskProductInfo)
            }

            productItemView.ivIconDecreaseQuantity.setOnClickListener { _ ->
                if (taskProductInfo.quantity!! > 1) {
                    taskProductInfo.quantity = taskProductInfo.quantity!! - 1
                    updateProductView(productItemView, taskProductInfo)
                }
            }

            productItemView.ivIconDelete.setOnClickListener { _ ->
                dp.taskProducts.delete(taskProductInfo.id!!)
                updateProductView(productItemView, null)
            }

            productItemView.ivIconInfo.setOnClickListener { _ ->
                navigationController.navigateToProductDetails(taskProductInfo.productId as UUID)
            }

            view.llSelectedProduct.addView(productItemView)
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) = launchUI {
        super.onActivityCreated(savedInstanceState)
        tvSelectProduct.setOnClickListener { _ ->
            navigationController.navigateToFilterProducts(taskId)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    private fun updateProductView (view: View, taskProductInfo: TaskProduct?, container: View? = this.view) {
        if (taskProductInfo == null) {
            val anim = ValueAnimator.ofInt(view.height, 0)
            anim.addUpdateListener { valueAnimator ->
                val value = valueAnimator.animatedValue as Int
                val layoutParams = view.layoutParams
                layoutParams.height = value
                view.layoutParams = layoutParams
            }
            anim.duration = 100L
            anim.start()
        } else {
            val product = dp.products.get(taskProductInfo.productId as UUID, DataProviderGetMode.FORCE_CACHE).entry

            view.tvProductQuantity.text = taskProductInfo.quantity.toString()
            view.tvName.text = product.name
            view.tvCost.text = activity!!.resources.getString(com.kvteam.deliverytracker.core.R.string.Core_Product_Cost_Template, product.cost.toString())
            view.tvVendorCode.text = product.vendorCode.toString()
        }

        val task = dp.taskInfos.get(taskId, DataProviderGetMode.DIRTY).entry
        var newTotalCost = BigDecimal(0)
        val taskProducts = dp.taskProducts.getByParent(taskId, DataProviderGetMode.DIRTY)

        taskProducts.forEach { taskProduct ->
            val product = dp.products.get(taskProduct.productId as UUID, DataProviderGetMode.FORCE_CACHE).entry
            newTotalCost = newTotalCost.add(product.cost!!.multiply(BigDecimal(taskProduct.quantity!!)))
        }
        task.cost = newTotalCost
        container!!.tvTotalProductsPrice.text = activity!!.resources.getString(
                com.kvteam.deliverytracker.core.R.string.Core_Product_Cost_Template,
                newTotalCost.toString()
        )
    }
}

