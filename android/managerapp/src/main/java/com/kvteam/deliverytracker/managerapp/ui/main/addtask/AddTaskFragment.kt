package com.kvteam.deliverytracker.managerapp.ui.main.addtask


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.common.IErrorManager
import com.kvteam.deliverytracker.core.models.TaskModel
import com.kvteam.deliverytracker.core.models.UserModel
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.ErrorDialog
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.tasks.ITaskRepository
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_add_task.*
import javax.inject.Inject

class AddTaskFragment : DeliveryTrackerFragment() {
    private val taskNumberKey = "taskNumber"
    private val shippingDescKey = "shippingDesc"
    private val detailsKey = "details"
    private val addressKey = "address"
    private val selectedPerformerUsernameKey = "selectedPerformerUsername"
    private val selectedPerformerKey = "selectedPerformer"

    private var selectedPerformerUsername: String? = null

    @Inject
    lateinit var navigationController: NavigationController

    @Inject
    lateinit var taskRepository: ITaskRepository

    @Inject
    lateinit var errorManager: IErrorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return this.view ?: inflater
                        ?.inflate(R.layout.fragment_add_task, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        savedInstanceState?.apply {
            if(view != null) {
                etTaskNumber.setText(getString(taskNumberKey, EMPTY_STRING))
                etShippingDesc.setText(getString(shippingDescKey, EMPTY_STRING))
                etDetails.setText(getString(detailsKey, EMPTY_STRING))
                etAddress.setText(getString(addressKey, EMPTY_STRING))
                etSelectPerformer.setText(getString(selectedPerformerKey, EMPTY_STRING))
                selectedPerformerUsername = getString(selectedPerformerUsernameKey, null)
            }
        }

        bttnSelectPerformer.setOnClickListener { selectPerformerClicked() }
        bttnAddTask.setOnClickListener { addTaskClicked() }
    }

    override fun onResume() {
        super.onResume()
        if(navigationController.info.containsKey(SELECTED_USERS_KEY)) {
            val user = navigationController.info[SELECTED_USERS_KEY] as UserModel
            navigationController.info.remove(SELECTED_USERS_KEY)
            selectedPerformerUsername = user.username
            etSelectPerformer.setText(user.surname)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.apply {
            if(view != null) {
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

    private fun addTaskClicked() {
        invokeAsync({
            val task = TaskModel()
            task.number = etTaskNumber.text.toString()
            task.shippingDesc = etShippingDesc.text.toString()
            task.address = etAddress.text.toString()
            task.details = etAddress.text.toString()
            if(selectedPerformerUsername != null
                    && selectedPerformerUsername != EMPTY_STRING) {
                task.performer = UserModel(username = selectedPerformerUsername)
            }
            taskRepository.addTask(task)
        }, {
            if(it.success){
                navigationController.closeCurrentFragment()
            } else {
                val dialog = ErrorDialog(this@AddTaskFragment.context)
                if(it.errorChainId != null) {
                    dialog.addChain(errorManager.getAndRemove(it.errorChainId!!)!!)
                }
                dialog.show()
            }
        })
    }
}