package com.kvteam.deliverytracker.managerapp.ui.main.taskslist

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.ui.tasks.BaseTaskDetailsFragment
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import javax.inject.Inject

class TaskDetailsFragment : BaseTaskDetailsFragment() {
    @Inject
    lateinit var navigationController: NavigationController

    override fun closeFragment() {
        navigationController.closeCurrentFragment()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = launchUI({
        when (item.itemId) {
            R.id.action_edit -> {
                navigationController.navigateToEditTask(taskId)
            }
        }
    }, {
        super.onOptionsItemSelected(item)
    })

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}