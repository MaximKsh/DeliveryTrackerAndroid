package com.kvteam.deliverytracker.performerapp.ui.main.taskslist

import android.view.Menu
import android.view.MenuInflater
import com.kvteam.deliverytracker.core.ui.tasks.BaseTaskDetailsFragment
import com.kvteam.deliverytracker.performerapp.ui.main.NavigationController
import javax.inject.Inject

class TaskDetailsFragment : BaseTaskDetailsFragment() {
    @Inject
    lateinit var navigationController: NavigationController

    override fun closeFragment() {
        navigationController.closeCurrentFragment()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

    }

}