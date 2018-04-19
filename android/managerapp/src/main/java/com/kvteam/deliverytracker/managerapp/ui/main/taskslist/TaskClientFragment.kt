package com.kvteam.deliverytracker.managerapp.ui.main.taskslist

import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.dataprovider.base.CacheException
import com.kvteam.deliverytracker.core.dataprovider.base.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.base.DataProviderGetMode
import com.kvteam.deliverytracker.core.models.Client
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

class TaskClientFragment : PageFragment() {
    @Inject
    lateinit var dp: DataProvider

    @Inject
    lateinit var navigationController: NavigationController

    private val ignoreWatcherKey = "watcher"
    private var ignoreWatcher
        get() = arguments?.getBoolean(ignoreWatcherKey) ?: false
        set(value) = arguments?.putBoolean(ignoreWatcherKey, value)!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_task_client, container, false) as ViewGroup
        val task = dp.taskInfos.get(taskId, DataProviderGetMode.DIRTY).entry
        if (task.clientId != null) {
            rootView.rlClientInfoContainer.layoutParams.height = 700
        }
        rootView.spinnerAddress.setPadding(15, 10, 0, 10)
        return rootView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    private fun showClientDetails(mode: DataProviderGetMode) = launchUI {
        val task = dp.taskInfos.get(taskId, DataProviderGetMode.DIRTY).entry

        if (mode != DataProviderGetMode.DIRTY) {
            val viewResult = dp.clientsViews.getViewResultAsync(
                "ReferenceViewGroup",
                "ClientsView",
                mapOf("search" to etPhoneNumberField.text.substring(1)),
                DataProviderGetMode.PREFER_CACHE).viewResult

            if (viewResult.isEmpty()) {
                dp.clients.invalidate()
                task.clientId = UUID.randomUUID()
            } else {
                task.clientId = viewResult[0]
            }
        }
        val clientId = task.clientId ?: return@launchUI

        val client = try {
            dp.clients.getAsync(clientId, mode).entry
        } catch (e: CacheException) {
            dp.clients.get(clientId, DataProviderGetMode.DIRTY).entry
        }

        if (client.phoneNumber == null) {
            client.phoneNumber = etPhoneNumberField.text.toString()
        }

        if (mode == DataProviderGetMode.DIRTY) {
            etPhoneNumberField.setPhoneNumber(client.phoneNumber!!)
        }

        etName.setText(client.name)
        etSurname.setText(client.surname)

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
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) = launchUI {
        super.onActivityCreated(savedInstanceState)
        val task = dp.taskInfos.getAsync(taskId, DataProviderGetMode.DIRTY).entry

        if (task.clientId != null) {
            showClientDetails(DataProviderGetMode.DIRTY)
        }

        val autocomplete = acClient.autoCompleteTextView

        val etNameWatcher = TaskTextWatcher(
                dp.clients,
                { model, text -> model.name = text },
                taskId,
                dp.taskInfos
        )

        val etSurnameWatcher = TaskTextWatcher(
                dp.clients,
                { model, text -> model.surname = text },
                taskId,
                dp.taskInfos
        )

        val etPhoneNumberWatcher = TaskTextWatcher(
                dp.clients,
                { model, text -> model.phoneNumber = text },
                taskId,
                dp.taskInfos
        )

        etName.addTextChangedListener(etNameWatcher)

        etSurname.addTextChangedListener(etSurnameWatcher)

        etPhoneNumberField.addTextChangedListener(etPhoneNumberWatcher)

        etPhoneNumberField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (text != null && text.length > 7) {
                    autocomplete.setText(text.toString().substring(1))
                    autocomplete.showDropDown()
                }
                if (text != null && etPhoneNumberField.rawText.length == 10) {
                    autocomplete.hideDropdown()
                    if (!ignoreWatcher) {
                        showClientDetails(DataProviderGetMode.PREFER_WEB)
                        val focusedView = activity!!.currentFocus
                        if (focusedView != null) {
                            val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.hideSoftInputFromWindow(focusedView.windowToken, 0)
                        }
                    }
                    ignoreWatcher = true
                } else if (ignoreWatcher) {
                    ignoreWatcher = false
                    hideClientDetails()
                    task.clientId = null
                    task.clientAddressId = null
                }
            }

        })

        autocomplete.setAutoCompleteDelay(200L)
        autocomplete.threshold = 2
        autocomplete.dropDownVerticalOffset = 5
        autocomplete.setAdapter(ClientsAutoCompleteAdapter(
                activity!!,
                {
                    runBlocking {
                        val viewResult = dp.clientsViews.getViewResultAsync(
                                "ReferenceViewGroup",
                                "ClientsView",
                                mapOf("search" to it)).viewResult
                        val result = viewResult
                                .map { dp.clients.getAsync(it, DataProviderGetMode.FORCE_WEB).entry }
                                .toMutableList()
                        result
                    }
                }
        ))
        autocomplete.setOnItemClickListener { adapterView, view, i, l ->
            val client = autocomplete.adapter.getItem(i) as Client
            etPhoneNumberField.setText(client.phoneNumber!!.substring(2))
        }
    }
}
