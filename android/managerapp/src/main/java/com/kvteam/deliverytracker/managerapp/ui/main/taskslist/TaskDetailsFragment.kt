package com.kvteam.deliverytracker.managerapp.ui.main.taskslist

import com.kvteam.deliverytracker.core.ui.tasks.BaseTaskDetailsFragment
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import javax.inject.Inject

class TaskDetailsFragment : BaseTaskDetailsFragment() {
    @Inject
    lateinit var navigationController: NavigationController

    override fun closeFragment() {
        navigationController.closeCurrentFragment()
    }

}