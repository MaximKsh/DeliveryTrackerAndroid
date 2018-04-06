package com.kvteam.deliverytracker.managerapp.ui.main.taskslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.dataprovider.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.DataProviderGetMode
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.dropdownselect.DropdownSelect
import com.kvteam.deliverytracker.core.ui.dropdownselect.DropdownSelectItem
import com.kvteam.deliverytracker.managerapp.R
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_task_receipt_at.view.*
import org.joda.time.DateTime
import java.util.*
import javax.inject.Inject
import java.util.Arrays.asList



data class DeliveryReceiptAtItem(
        var name: String,
        var selectedDateTime: DateTime?
)

class TaskReceiptAtFragment : PageFragment() {
    @Inject
    lateinit var dp: DataProvider

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_task_receipt_at, container, false) as ViewGroup
        val strings = receiptTypes.map { it.name }
        view.spinnerReceiptAt.attachDataSource(strings)
        view.spinnerReceiptAt.addOnItemClickListener { adapterView, view, i, l ->
            selectedReceiptDateIndex = i
            setTaskReceiptAtDate(receiptTypes[i].selectedDateTime!!)
        }
        return view
    }

    // SECTION: ReceiptAt --START--

    private val RECEIPT_ASAP = "As soon as possible"
    private val RECEIPT_AUTO = "Auto"
    private val RECEIPT_CUSTOM = "Exact time"

    private val selectedReceiptDateKey = "delivery_receipt_at"
    private var selectedReceiptDateIndex: Int?
        get() = arguments?.get(selectedReceiptDateKey) as Int?
        set(value) = arguments?.putInt(selectedReceiptDateKey, value!!)!!

    private val receiptTypes = arrayListOf<DeliveryReceiptAtItem>(
            DeliveryReceiptAtItem(RECEIPT_AUTO, DateTime.now()),
            DeliveryReceiptAtItem(RECEIPT_ASAP, DateTime.now()),
            DeliveryReceiptAtItem(RECEIPT_CUSTOM, DateTime.now())
    )

    private fun setTaskReceiptAtDate(date: DateTime) = launchUI {
        val task = dp.taskInfos.getAsync(taskId, DataProviderGetMode.DIRTY).entry
        val dateReal = task.receipt ?: DateTime.now()
        task.receipt = dateReal.withDate(date.toLocalDate())
    }

    // SECTION: ReceiptAt --END--

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }
}

