package com.kvteam.deliverytracker.managerapp.ui.main.taskslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.dataprovider.base.CacheException
import com.kvteam.deliverytracker.core.dataprovider.base.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.base.DataProviderGetMode
import com.kvteam.deliverytracker.core.models.PaymentType
import com.kvteam.deliverytracker.core.models.TaskInfo
import com.kvteam.deliverytracker.core.ui.ModelTextWatcher
import com.kvteam.deliverytracker.managerapp.R
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_task_number_and_details.*
import kotlinx.android.synthetic.main.fragment_task_number_and_details.view.*
import javax.inject.Inject


class TaskNumberAndDetailsFragment : BaseTaskPageFragment() {
    @Inject
    lateinit var dp: DataProvider

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

        val paymentTypesIds = dp.paymentTypesViews
                .getViewResultAsync("ReferenceViewGroup", "PaymentTypesView").viewResult

        val strings = arrayListOf<String>()
        val paymentTypes = arrayListOf<PaymentType>()
        if (paymentTypesIds.isNotEmpty()) {
            for (id in paymentTypesIds) {
                try {
                    val (e, _) = dp.paymentTypes.get(id, DataProviderGetMode.FORCE_CACHE)
                    paymentTypes.add(e)
                    strings.add(e.name!!)
                } catch (e: CacheException) {
                }
            }
            spinnerPaymentType.attachDataSource(strings)
            spinnerPaymentType.addOnItemClickListener { adapterView, view, i, l ->
                setTaskPaymentType(paymentTypes[i])
            }

            if (task.paymentTypeId == null) {
                setTaskPaymentType(paymentTypes[0])
            }
            val selected = paymentTypes.indexOfFirst { it.id == task.paymentTypeId }
            spinnerPaymentType.selectedIndex = selected
        }
    }

    private fun setTaskPaymentType(paymentType: PaymentType) {
        val task = dp.taskInfos.get(taskId, DataProviderGetMode.DIRTY).entry
        task.paymentTypeId = paymentType.id
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }
}
