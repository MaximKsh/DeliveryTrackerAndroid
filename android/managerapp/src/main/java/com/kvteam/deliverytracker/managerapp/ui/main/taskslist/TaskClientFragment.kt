package com.kvteam.deliverytracker.managerapp.ui.main.taskslist

import android.animation.ValueAnimator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.ClientsView
import com.kvteam.deliverytracker.core.common.ReferenceViewGroup
import com.kvteam.deliverytracker.core.dataprovider.base.CacheException
import com.kvteam.deliverytracker.core.dataprovider.base.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.base.DataProviderGetMode
import com.kvteam.deliverytracker.core.models.Client
import com.kvteam.deliverytracker.core.models.TaskInfo
import com.kvteam.deliverytracker.core.ui.autocomplete.ClientsAutoCompleteAdapter
import com.kvteam.deliverytracker.core.ui.setPhoneNumber
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.client_info.*
import kotlinx.android.synthetic.main.client_info.view.*
import kotlinx.coroutines.experimental.runBlocking
import java.util.*
import javax.inject.Inject

class TaskClientFragment : BaseTaskPageFragment() {
    inner class ClientTextWatcher (fragment: TaskClientFragment) : TextWatcher {
        private val autocomplete = fragment.etPhoneNumberField

        private var ignoreWatcher = true

        override fun afterTextChanged(p0: Editable?) {}

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
            val task = dp.taskInfos.get(taskId, DataProviderGetMode.DIRTY).entry

            val phoneNumber = text?.toString()
            if (phoneNumber?.length == 16) {
                autocomplete.hideDropdown()
                if (!ignoreWatcher) {
                    showClientDetails(phoneNumber)
                    dtActivity.softKeyboard.closeSoftKeyboard()
                }
                ignoreWatcher = true
            } else if (ignoreWatcher) {
                ignoreWatcher = false
                hideClientDetails()
                task.clientId = null
                task.clientAddressId = null
            }
        }

    }

    @Inject
    lateinit var dp: DataProvider

    @Inject
    lateinit var navigationController: NavigationController

    private var etNameWatcher: TaskTextWatcher<Client>? = null
    private var etSurnameWatcher: TaskTextWatcher<Client>? = null
    private var clientPhoneTextWatcher: ClientTextWatcher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_task_client, container, false) as ViewGroup
        rootView.spinnerAddress.setPadding(15, 10, 0, 10)
        return rootView
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) = launchUI {
        super.onActivityCreated(savedInstanceState)
        //val autocomplete = acClient.autoCompleteTextView
        val task = dp.taskInfos.get(taskId, DataProviderGetMode.DIRTY).entry
        if (task.clientId != null) {
            showClientDetails()
        }

        etPhoneNumberField.setAutoCompleteDelay(200L)
        etPhoneNumberField.threshold = 8
        etPhoneNumberField.dropDownVerticalOffset = 5
        etPhoneNumberField.setAdapter(ClientsAutoCompleteAdapter(
                activity!!,
                {
                    runBlocking {
                        val phone = it

                        val viewResult = dp.clientsViews.getViewResultAsync(
                                ReferenceViewGroup,
                                ClientsView,
                                mapOf("phone_number" to phone)).viewResult
                        val result = viewResult
                                .map { dp.clients.getAsync(it, DataProviderGetMode.PREFER_CACHE).entry }
                                .toMutableList()
                        result
                    }
                }
        ))
        etPhoneNumberField.setOnItemClickListener { _, _, i, _ ->
            val client = etPhoneNumberField.adapter.getItem(i) as Client
            etPhoneNumberField.setPhoneNumber(client.phoneNumber)
        }
    }

    override fun onStart() {
        super.onStart()
        clientPhoneTextWatcher = ClientTextWatcher(this@TaskClientFragment)
        etPhoneNumberField.addTextChangedListener(clientPhoneTextWatcher)
    }

    override fun onStop() {
        unsubscribeSurnameName()
        etPhoneNumberField.removeTextChangedListener(clientPhoneTextWatcher)
        clientPhoneTextWatcher = null
        super.onStop()
    }

    private fun showClientDetails(phoneNumber: String? = null) = launchUI {
        val task = dp.taskInfos.get(taskId, DataProviderGetMode.DIRTY).entry

        var mode = DataProviderGetMode.DIRTY
        var isNew = true
        if (phoneNumber != null) {
            mode = DataProviderGetMode.PREFER_CACHE

            val viewResult = dp.clientsViews.getViewResultAsync(
                    ReferenceViewGroup,
                    ClientsView,
                    mapOf("phone_number" to phoneNumber),
                    DataProviderGetMode.PREFER_CACHE).viewResult

            if (viewResult.isEmpty()) {
                dp.clients.invalidate()
                task.clientId = UUID.randomUUID()
            } else {
                isNew = false
                task.clientId = viewResult[0]
            }
        } else if (task.clientId == null) {
            task.clientId = UUID.randomUUID()
        }
        val clientId = task.clientId!!

        val client = if (!isNew) {
            try {
                dp.clients.getAsync(clientId, mode).entry
            } catch (e: CacheException) {
                dp.clients.get(clientId, DataProviderGetMode.DIRTY).entry
            }
        } else {
            dp.clients.get(clientId, DataProviderGetMode.DIRTY).entry
        }

        // Создаем нового клиента по введенному юзером номеру
        if (isNew && phoneNumber != null) {
            client.phoneNumber = phoneNumber
        }

        // Открываем клиента при входе на фрагмент с заполненным номером
        if (phoneNumber == null && client.phoneNumber != null) {
            etPhoneNumberField.setPhoneNumber(client.phoneNumber)
        }

        etName.setText(client.name)
        etSurname.setText(client.surname)
        subscribeSurnameName()
        showAddresses(clientId, task)

        animateShowClient()
    }

    private fun hideClientDetails() {
        if (rlClientInfoContainer.height != 0) {
            val anim = ValueAnimator.ofInt(rlClientInfoContainer.height, 0)
            anim.addUpdateListener { valueAnimator ->
                val value = valueAnimator.animatedValue as Int
                val layoutParams = rlClientInfoContainer.layoutParams
                layoutParams.height = value
                rlClientInfoContainer.layoutParams = layoutParams
            }
            anim.duration = 75L
            anim.start()
            tvAddAddress.setOnClickListener(null)
            unsubscribeSurnameName()
        }
    }

    private fun subscribeSurnameName() {
        etNameWatcher = TaskTextWatcher(
                dp.clients,
                { model, text -> model.name = text },
                taskId,
                dp.taskInfos)
        etSurnameWatcher = TaskTextWatcher(
                dp.clients,
                { model, text -> model.surname = text },
                taskId,
                dp.taskInfos)
        etName.addTextChangedListener(etNameWatcher)
        etSurname.addTextChangedListener(etSurnameWatcher)
    }

    private fun unsubscribeSurnameName() {
        if (etNameWatcher != null) {
            etName.removeTextChangedListener(etNameWatcher)
            etNameWatcher = null
        }

        if (etSurnameWatcher != null) {
            etSurname.removeTextChangedListener(etSurnameWatcher)
            etSurnameWatcher = null
        }
    }

    private fun showAddresses(clientId: UUID, task: TaskInfo) {
        val clientAddresses = dp.clientAddresses.getByParent(clientId, DataProviderGetMode.DIRTY)
        if (clientAddresses.isNotEmpty()) {
            spinnerAddress.visibility = View.VISIBLE
            val strings = clientAddresses.map { it.rawAddress }
            spinnerAddress.attachDataSource(strings)
            spinnerAddress.addOnItemClickListener { _, _, i, _ ->
                task.clientAddressId = clientAddresses[i].id
            }
            if (task.clientAddressId != null) {
                spinnerAddress.selectedIndex =
                        clientAddresses.indexOfFirst { clientAddress -> clientAddress.id == task.clientAddressId }
            } else {
                task.clientAddressId = clientAddresses[0].id
            }
        } else {
            spinnerAddress.visibility = View.GONE
        }


        tvAddAddress.setOnClickListener { _ ->
            navigationController.navigateToEditClientAddress(task.clientId!!)
        }
    }

    private fun animateShowClient() {
        if (rlClientInfoContainer.height == 0) {
            rlClientInfoContainer.measure(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            val height = rlClientInfoContainer.measuredHeight

            val anim = ValueAnimator.ofInt(0, height)
            anim.addUpdateListener { valueAnimator ->
                val value = valueAnimator.animatedValue as Int
                val layoutParams = rlClientInfoContainer.layoutParams
                layoutParams.height = value
                rlClientInfoContainer.layoutParams = layoutParams

            }
            anim.duration = 75L
            anim.start()
        } else {
            rlClientInfoContainer.measure(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            val height = rlClientInfoContainer.measuredHeight
            val layoutParams = rlClientInfoContainer.layoutParams
            layoutParams.height = height
            rlClientInfoContainer.layoutParams = layoutParams
        }
    }
}
