package com.kvteam.deliverytracker.managerapp.ui.main.taskslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.common.PaymentTypesView
import com.kvteam.deliverytracker.core.common.ReferenceViewGroup
import com.kvteam.deliverytracker.core.dataprovider.base.CacheException
import com.kvteam.deliverytracker.core.dataprovider.base.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.base.DataProviderGetMode
import com.kvteam.deliverytracker.core.dataprovider.base.NetworkException
import com.kvteam.deliverytracker.core.models.PaymentType
import com.kvteam.deliverytracker.core.models.TaskInfo
import com.kvteam.deliverytracker.core.ui.ModelTextWatcher
import com.kvteam.deliverytracker.managerapp.R
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_task_number_and_details.*
import kotlinx.android.synthetic.main.fragment_task_number_and_details.view.*
import javax.inject.Inject


class TaskNumberAndDetailsFragment : BaseTaskPageFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_task_number_and_details, container, false) as ViewGroup

        val etTaskNumberWatcher = ModelTextWatcher(
                dp.taskInfos,
                taskId,
                { model, text -> model.taskNumber = text }
        )

        val etDescriptionWatcher = ModelTextWatcher(
                dp.taskInfos,
                taskId,
                { model, text -> model.comment = text }
        )

        val task = dp.taskInfos.get(taskId, DataProviderGetMode.DIRTY).entry

        rootView.etTaskNumber.setText(task.taskNumber)

        rootView.etDescriptionField.setText(task.comment)

        rootView.etTaskNumber.addTextChangedListener(etTaskNumberWatcher)

        rootView.etDescriptionField.addTextChangedListener(etDescriptionWatcher)

        rootView.spinnerPaymentType.setPadding(15, 10, 0, 10)

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) = launchUI {
        super.onActivityCreated(savedInstanceState)
        val task = dp.taskInfos.get(taskId, DataProviderGetMode.DIRTY).entry

        val paymentTypes = loadPaymentTypes(dp)
        val strings = getPaymentTypeSpinnerList(paymentTypes)
        spinnerPaymentType.attachDataSource(strings)

        if (paymentTypes.isNotEmpty()) {
            spinnerPaymentType.addOnItemClickListener { _, _, i, _ ->
                setTaskPaymentType(paymentTypes[i])
            }

            val selected = paymentTypes.indexOfFirst { it.id == task.paymentTypeId }
            spinnerPaymentType.selectedIndex = selected
        }
    }

    private fun setTaskPaymentType(paymentType: PaymentType) {
        val task = dp.taskInfos.get(taskId, DataProviderGetMode.DIRTY).entry
        task.paymentTypeId = paymentType.id
    }

    private fun getPaymentTypeSpinnerList(pt: List<PaymentType>) : List<String> {
        return if (pt.isNotEmpty()) {
            pt.map { it.name ?: EMPTY_STRING }
        } else {
            return listOf(lm.getString(R.string.ManagerApp_EditTaskFragment_NoAvailablePaymentType))
        }
    }
}
