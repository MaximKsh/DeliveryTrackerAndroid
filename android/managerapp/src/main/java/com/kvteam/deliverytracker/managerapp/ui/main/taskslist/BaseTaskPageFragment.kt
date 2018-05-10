package com.kvteam.deliverytracker.managerapp.ui.main.taskslist

import android.os.Bundle
import com.kvteam.deliverytracker.core.common.*
import com.kvteam.deliverytracker.core.dataprovider.base.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.base.DataProviderGetMode
import com.kvteam.deliverytracker.core.dataprovider.base.NetworkException
import com.kvteam.deliverytracker.core.models.PaymentType
import com.kvteam.deliverytracker.core.models.Warehouse
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import java.util.*
import javax.inject.Inject

abstract class BaseTaskPageFragment : DeliveryTrackerFragment() {
    @Inject
    lateinit var dp: DataProvider

    @Inject
    lateinit var navigationController: NavigationController

    @Inject
    lateinit var lm: ILocalizationManager

    private val taskIdKey = "task"
    protected var taskId
        get() = arguments?.getSerializable(taskIdKey)!! as UUID
        set(value) = arguments?.putSerializable(taskIdKey, value)!!

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    fun setTask(id: UUID?) {
        this.taskId = id ?: UUID.randomUUID()
    }
}