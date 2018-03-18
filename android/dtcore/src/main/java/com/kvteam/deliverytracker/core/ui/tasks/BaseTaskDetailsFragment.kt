package com.kvteam.deliverytracker.core.ui.tasks

import android.os.Bundle
import android.view.*
import com.kvteam.deliverytracker.core.R
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.models.TaskPackage
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.errorhandling.IErrorHandler
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController
import com.kvteam.deliverytracker.core.webservice.ITaskWebservice
import com.kvteam.deliverytracker.core.webservice.NetworkResult
import com.kvteam.deliverytracker.core.webservice.viewmodels.TaskResponse
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_base_task_details.*
import java.util.*
import javax.inject.Inject

abstract class BaseTaskDetailsFragment : DeliveryTrackerFragment() {
    private val taskPackageKey = "taskPackage"

    @Inject
    lateinit var taskWebservice: ITaskWebservice

    @Inject
    lateinit var eh: IErrorHandler

    @Inject
    lateinit var lm: ILocalizationManager

    private lateinit var taskId: UUID

    protected abstract fun closeFragment()

    fun setTaskId(id: UUID) {
        taskId = id
        invalidateTaskPackage()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun configureToolbar(toolbar: ToolbarController) {
        toolbar.disableDropDown()
        toolbar.setToolbarTitle("Task")
        toolbar.showBackButton()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) = launchUI {
        super.onActivityCreated(savedInstanceState)

        val taskResult = taskWebservice.getAsync(taskId)
        if(eh.handle(taskResult)) {
            return@launchUI
        }
        val taskPackage = getTaskPackage(taskResult)
        val task = taskPackage.taskInfo.firstOrNull()!!
        val taskNumber = task.taskNumber
        tvTaskNumber.text = taskNumber
        tvTaskState.text = lm.getString(task.taskStateCaption!!)
        if(taskNumber != null) {
            toolbarController.setToolbarTitle(taskNumber)
        }

        llTransitionButtons.removeAllViews()
        for (transition in taskPackage.linkedTaskStateTransitions) {
            val view = SwitchStateView(this@BaseTaskDetailsFragment.context!!)
            view.button.text = transition.buttonCaption
            view.button.setOnClickListener { onChangeStateClick(transition.id!!) }
            llTransitionButtons.addView(view)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_base_task_details, container, false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = launchUI ({
        when (item.itemId) {
            R.id.action_edit_task -> {
            }
        }
    }, {
        super.onOptionsItemSelected(item)
    })

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_base_task_details_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun onChangeStateClick(transitionId: UUID) = launchUI {
        val taskPackage = getTaskPackage()
        val taskId = taskPackage.taskInfo.first().id!!
        val transitResult = taskWebservice.changeStateAsync(taskId, transitionId)
        if(eh.handle(transitResult)) {
            return@launchUI
        }
        closeFragment()
    }

    private fun getTaskPackage(): TaskPackage {
        return arguments?.getSerializable(taskPackageKey)!! as TaskPackage
    }

    private fun invalidateTaskPackage() {
        arguments?.remove(taskPackageKey)
    }

    private fun getTaskPackage(taskResult: NetworkResult<TaskResponse>): TaskPackage {
        val args = arguments
        if(args == null) {
            val task = taskResult.entity?.taskPackage!!
            val bundle = Bundle()
            bundle.putSerializable(taskPackageKey, task)
            arguments = bundle
            return task
        } else if (args.containsKey(taskPackageKey)) {
            return args.getSerializable(taskPackageKey) as TaskPackage
        } else {
            val task = taskResult.entity?.taskPackage!!
            args.putSerializable(taskPackageKey, task)
            return task
        }
    }
}