package com.kvteam.deliverytracker.managerapp.ui.main.taskslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.dataprovider.CacheException
import com.kvteam.deliverytracker.core.dataprovider.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.DataProviderGetMode
import com.kvteam.deliverytracker.core.models.PaymentType
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.dropdownselect.DropdownSelect
import com.kvteam.deliverytracker.core.ui.dropdownselect.DropdownSelectItem
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_task_payment_type.*
import java.util.*
import javax.inject.Inject

data class PaymentTypeItem(
        var name: String,
        var paymentType: PaymentType? = null
)

class TaskPaymentTypeFragment : PageFragment() {
    @Inject
    lateinit var navigationController: NavigationController

    @Inject
    lateinit var dp: DataProvider

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_task_payment_type, container, false) as ViewGroup
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) = launchUI {
        super.onActivityCreated(savedInstanceState)
        val paymentTypesData = dp.paymentTypesViews
                .getViewResultAsync("ReferenceViewGroup", "PaymentTypesView").viewResult

        if (paymentTypesData.isNotEmpty()) {
            paymentTypes.removeAt(0)
            for (id in paymentTypesData) {
                try {
                    val (e, _) = dp.paymentTypes.getAsync(id, DataProviderGetMode.FORCE_CACHE)
                    val item = PaymentTypeItem(e.name!!, e)
                    paymentTypes.add(DropdownSelectItem(item, false, false))
                } catch (e: CacheException) {
                }
            }
        }

        DropdownSelect<PaymentTypeItem>(
                "Payment type",
                paymentTypes,
                selectedPaymentTypeIndex,
                ::onPaymentTypeSelect,
                ::paymentTypeTextSelector,
                llPaymentTypesContainer,
                activity!!
        )
    }

    // SECTION: PaymentTypes --START--

    private val selectedPaymentTypeKey = "delivery_payment_type"
    private var selectedPaymentTypeIndex: Int?
        get() = arguments?.get(selectedPaymentTypeKey) as Int?
        set(value) = arguments?.putInt(selectedPaymentTypeKey, value!!)!!

    private val paymentTypes = mutableListOf<DropdownSelectItem<PaymentTypeItem>>(
            DropdownSelectItem(PaymentTypeItem("Add", null), true, false)
    )

    private fun setTaskPaymentType(paymentType: PaymentType) = launchUI {
        val task = dp.taskInfos.getAsync(taskId, DataProviderGetMode.DIRTY).entry
        task.paymentTypeId = paymentType.id
    }

    private fun onPaymentTypeSelect(index: Int, oldIndex: Int): Unit = launchUI {
        selectedPaymentTypeIndex = index
        if (paymentTypes[index].isLink) {
            navigationController.navigateToEditPaymentType()
        } else {
            setTaskPaymentType(paymentTypes[index].data.paymentType!!)
        }
    }

    private fun paymentTypeTextSelector(paymentTypeItem: PaymentTypeItem): String {
        return paymentTypeItem.name
    }

    // SECTION: PaymentTypes --END--


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }
}
