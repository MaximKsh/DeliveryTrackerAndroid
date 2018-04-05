package com.kvteam.deliverytracker.managerapp.ui.main.taskslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.dataprovider.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.DataProviderGetMode
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_task_products.*
import kotlinx.android.synthetic.main.selected_product_item.*
import kotlinx.android.synthetic.main.selected_product_item.view.*
import java.util.*
import javax.inject.Inject

class TaskProductsFragment : DeliveryTrackerFragment() {
    @Inject
    lateinit var navigationController: NavigationController

    @Inject
    lateinit var dp: DataProvider

    private val taskIdKey = "task"
    private var taskId
        get() = arguments?.getSerializable(taskIdKey)!! as UUID
        set(value) = arguments?.putSerializable(taskIdKey, value)!!

    fun setTask(id: UUID?) {
        this.taskId = id ?: UUID.randomUUID()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_task_products, container, false) as ViewGroup
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) = launchUI {
        super.onActivityCreated(savedInstanceState)
        val task = dp.taskInfos.getAsync(taskId, DataProviderGetMode.DIRTY).entry

        tvSelectProduct.setOnClickListener { _ ->
            navigationController.navigateToFilterProducts(taskId)
        }

        if (task.taskProducts.size != 0) {
            val firstProduct = dp.products.getAsync(task.taskProducts[0].productId as UUID, DataProviderGetMode.FORCE_CACHE).entry

            rlSelectedProduct.tvName.text = firstProduct.name
            rlSelectedProduct.tvCost.text = firstProduct.cost.toString()
            rlSelectedProduct.tvVendorCode.text = firstProduct.vendorCode.toString()
            rlSelectedProduct.visibility = View.VISIBLE
        } else {
            rlSelectedProduct.visibility = View.GONE
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }
}

