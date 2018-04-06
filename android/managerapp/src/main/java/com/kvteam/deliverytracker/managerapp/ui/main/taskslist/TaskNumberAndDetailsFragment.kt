package com.kvteam.deliverytracker.managerapp.ui.main.taskslist

import android.view.ViewGroup
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.dataprovider.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.DataProviderGetMode
import com.kvteam.deliverytracker.core.models.TaskInfo
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.ModelTextWatcher
import com.kvteam.deliverytracker.managerapp.R
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_task_number_and_details.*
import kotlinx.android.synthetic.main.fragment_task_number_and_details.view.*
import java.util.*
import javax.inject.Inject


class TaskNumberAndDetailsFragment : PageFragment() {
    @Inject
    lateinit var dp: DataProvider

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_task_number_and_details, container, false) as ViewGroup
        val etTaskNumberWatcher = ModelTextWatcher<TaskInfo>(
                dp.taskInfos,
                taskId,
                { model, text -> model.taskNumber = text }
        )

        val etDescriptionWatcher = ModelTextWatcher<TaskInfo>(
                dp.taskInfos,
                taskId,
                { model, text -> model.comment = text }
        )

        rootView.etTaskNumber.addTextChangedListener(etTaskNumberWatcher)

        rootView.etDescriptionField.addTextChangedListener(etDescriptionWatcher)

        return rootView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }
}
