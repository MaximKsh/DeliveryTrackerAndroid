package com.kvteam.deliverytracker.managerapp.ui.main.taskdetails


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.common.EntityResult
import com.kvteam.deliverytracker.core.models.TaskInfo
import com.kvteam.deliverytracker.core.models.TaskProduct
import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.webservice.ITaskWebservice
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_task_details.*
import java.util.*
import javax.inject.Inject

class TaskDetailsFragment : DeliveryTrackerFragment() {
    private val taskIdKey = "taskId"
    private val modeKey = "mode"
    private val taskNumberKey = "taskNumber"
    private val shippingDescKey = "shippingDesc"
    private val detailsKey = "details"
    private val addressKey = "address"
    private val selectedPerformerUsernameKey = "selectedPerformerUsername"
    private val selectedPerformerKey = "selectedPerformer"

    private var selectedPerformerUsername: String? = null

    @Inject
    lateinit var navigationController: NavigationController

    //@Inject
    //lateinit var taskRepository: ITaskRepository

    @Inject
    lateinit var taskWebservice: ITaskWebservice

    var taskId: UUID? = null
        private set

    lateinit var mode: TaskDetailsFragmentMode
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return this.view ?: inflater
                        .inflate(R.layout.fragment_task_details, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val taskInfo = TaskInfo()
        taskInfo.taskNumber = "001"
        taskInfo.taskProducts = mutableListOf(TaskProduct())
        taskInfo.taskProducts[0].productId = UUID.fromString("2bff4e04-e6f2-4240-aebc-4c7568938f63")
        taskInfo.taskProducts[0].quantity = 1

        invokeAsync({
            taskWebservice.create(taskInfo)
        }, {
            val result = it
            if(result.success) {

            }
        })


        if(savedInstanceState != null
                && view != null) {
            savedInstanceState.apply {
                taskId = getSerializable(taskIdKey) as? UUID
                mode = getSerializable(modeKey) as TaskDetailsFragmentMode
                etTaskNumber.setText(getString(taskNumberKey, EMPTY_STRING))
                etShippingDesc.setText(getString(shippingDescKey, EMPTY_STRING))
                etDetails.setText(getString(detailsKey, EMPTY_STRING))
                etAddress.setText(getString(addressKey, EMPTY_STRING))
                etSelectPerformer.setText(getString(selectedPerformerKey, EMPTY_STRING))
                selectedPerformerUsername = getString(selectedPerformerUsernameKey, null)
            }

        } else if(mode in arrayOf(TaskDetailsFragmentMode.EDIT, TaskDetailsFragmentMode.READONLY)) {
            /*invokeAsync({
                taskRepository.getTask(taskId!!)
            }, {
                val task = it.entity
                if(it.success
                        && task != null) {
                    val performer = task.performer
                    etTaskNumber.setText(task.number ?: EMPTY_STRING)
                    etShippingDesc.setText(task.shippingDesc ?: EMPTY_STRING)
                    etDetails.setText(task.details ?: EMPTY_STRING)
                    etAddress.setText(task.address ?: EMPTY_STRING)
                    etSelectPerformer.setText(
                            if(performer != null) formatPerformerName(performer)
                            else EMPTY_STRING)
                    selectedPerformerUsername = task.performer?.code
                }
            })*/
        }

        if (mode == TaskDetailsFragmentMode.ADD) {
            bttnAddTask.visibility = View.VISIBLE
           /* bttnAddTask.setOnClickListener {
                performTaskAction {
                    val task = TaskModel()
                    task.number = etTaskNumber.text.toString()
                    task.shippingDesc = etShippingDesc.text.toString()
                    task.address = etAddress.text.toString()
                    task.details = etAddress.text.toString()
                    if(selectedPerformerUsername != null
                            && selectedPerformerUsername != EMPTY_STRING) {
                        task.performer = User(code = selectedPerformerUsername)
                    }
                    taskRepository.addTask(task)
                }
            }*/
            bttnSelectPerformer.setOnClickListener { selectPerformerClicked() }
        } else if (mode == TaskDetailsFragmentMode.EDIT) {
            bttnCancelTask.visibility = View.VISIBLE
            bttnCancelTask.setOnClickListener {
                performTaskAction {
                    /*if(it != null) {
                        taskRepository.cancelTask(it)
                    } else {
                        EntityResult(null, false, false)
                    }*/
                    EntityResult(null, false, false)
                }
            }
            bttnSelectPerformer.setOnClickListener { selectPerformerClicked() }
        } else if (mode == TaskDetailsFragmentMode.READONLY) {
            etTaskNumber.isEnabled = false
            etShippingDesc.isEnabled = false
            etDetails.isEnabled = false
            etAddress.isEnabled = false
            etSelectPerformer.isEnabled = false

            bttnCancelTask.visibility = View.VISIBLE
            bttnCancelTask.setOnClickListener {
                performTaskAction {
                    /*if(it != null) {
                        taskRepository.cancelTask(it)
                    } else {
                        EntityResult(null, false, false)
                    }*/
                    EntityResult(null, false, false)
                }
            }
            bttnSelectPerformer.isEnabled = false
        }
    }

    override fun onResume() {
        super.onResume()
        if(navigationController.info.containsKey(SELECTED_USERS_KEY)) {
            val user = navigationController.info[SELECTED_USERS_KEY] as User
            navigationController.info.remove(SELECTED_USERS_KEY)
            selectedPerformerUsername = user.code
            etSelectPerformer.setText(formatPerformerName(user))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.apply {
            if(view != null) {
                putSerializable(taskIdKey, taskId)
                putSerializable(modeKey, mode)
                putString(taskNumberKey, etTaskNumber.text.toString())
                putString(shippingDescKey, etShippingDesc.text.toString())
                putString(detailsKey, etDetails.text.toString())
                putString(addressKey, etAddress.text.toString())
                putString(selectedPerformerUsernameKey, selectedPerformerUsername)
                putString(selectedPerformerKey, etSelectPerformer.text.toString())
            }
        }
    }

    private fun selectPerformerClicked() {
        navigationController.navigateToSelectPerformer()
    }

    private fun formatPerformerName(performer: User) =
        "${performer.surname} ${performer.name}"

    private fun performTaskAction(action: ((taskId: UUID?)-> EntityResult<Any>)) {
        setProcessingState()
        invokeAsync({
            action(taskId)
        }, {
            if(it.success) {
                navigationController.closeCurrentFragment()
            }
            setProcessingState(false)
        })
    }

    private fun setProcessingState(processing: Boolean = true){
        bttnCancelTask.isEnabled = !processing
        bttnAddTask.isEnabled = !processing
    }

    companion object {
        fun create(
                mode: TaskDetailsFragmentMode,
                taskId: UUID? = null): TaskDetailsFragment {
            val fragment = TaskDetailsFragment()
            fragment.mode = mode
            fragment.taskId = taskId
            return fragment
        }
    }
}