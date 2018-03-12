package com.kvteam.deliverytracker.managerapp.ui.main.taskslist

import android.os.Bundle
import android.view.*
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.models.TaskInfo
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerActivity
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.webservice.ITaskWebservice
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.toolbar.*
import javax.inject.Inject

class EditTaskFragment : DeliveryTrackerFragment() {
    @Inject
    lateinit var navigationController: NavigationController

    @Inject
    lateinit var taskWebservice: ITaskWebservice

    @Inject
    lateinit var lm: ILocalizationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as DeliveryTrackerActivity).dropDownTop.disableDropDown()
        (activity as DeliveryTrackerActivity).dropDownTop.setToolbarTitle("Task")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_task, container, false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_finish -> {
                invokeAsync({
                    val task = TaskInfo()
                    taskWebservice.create(task)
                }, {
                    if (it.success) {
                        navigationController.closeCurrentFragment()
                    }
                })
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_edit_task_menu, menu)
        activity!!.toolbar_left_action.setOnClickListener { _ ->
            navigationController.closeCurrentFragment()
        }
        super.onCreateOptionsMenu(menu, inflater)
    }
}

