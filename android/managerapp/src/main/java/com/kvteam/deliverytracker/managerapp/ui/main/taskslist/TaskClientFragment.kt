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
import android.widget.AdapterView
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.dataprovider.CacheException
import com.kvteam.deliverytracker.core.dataprovider.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.DataProviderGetMode
import com.kvteam.deliverytracker.core.dataprovider.NetworkException
import com.kvteam.deliverytracker.core.models.Client
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.autocomplete.ClientsAutoCompleteAdapter
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.client_info.*
import kotlinx.coroutines.experimental.runBlocking
import java.util.*
import javax.inject.Inject

class TaskClientFragment : DeliveryTrackerFragment() {
    @Inject
    lateinit var dp: DataProvider

    @Inject
    lateinit var navigationController: NavigationController

    private val ignoreWatcherKey = "watcher"
    private var ignoreWatcher
        get() = arguments?.getBoolean(ignoreWatcherKey) ?: false
        set(value) = arguments?.putBoolean(ignoreWatcherKey, value)!!

    private val taskIdKey = "task"
    private var taskId
        get() = arguments?.getSerializable(taskIdKey)!! as UUID
        set(value) = arguments?.putSerializable(taskIdKey, value)!!

    fun setTask(id: UUID?) {
        this.taskId = id ?: UUID.randomUUID()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_task_client, container, false) as ViewGroup
        return rootView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onPause() {
        updateClient()
        super.onPause()
    }

    private fun updateClient () {
        val task = dp.taskInfos.get(taskId, DataProviderGetMode.DIRTY).entry
        val client = dp.clients.get(task.clientId as UUID, DataProviderGetMode.DIRTY).entry
        client.name = etName.text.toString()
        client.surname = etSurname.text.toString()
        client.phoneNumber = etPhoneNumberField.text.toString()
    }

    private fun showClientDetails(mode: DataProviderGetMode) = launchUI {
        val task = dp.taskInfos.get(taskId, DataProviderGetMode.DIRTY).entry
        val viewResult = dp.clientsViews.getViewResultAsync(
                "ReferenceViewGroup",
                "ClientsView",
                mapOf("search" to etPhoneNumberField.text.substring(1)),
                DataProviderGetMode.PREFER_CACHE).viewResult

        if (mode != DataProviderGetMode.DIRTY) {
            if (viewResult.isEmpty()) {
                dp.clients.invalidate()
                task.clientId = UUID.randomUUID()
            } else {
                task.clientId = viewResult[0]
            }
        }

        val client = try {
            dp.clients.getAsync(task.clientId as UUID, mode).entry
        } catch (e: CacheException) {
            dp.clients.get(task.clientId as UUID, DataProviderGetMode.DIRTY).entry
        }

        etName.setText(client.name)
        etSurname.setText(client.surname)

        if (client.clientAddresses.size > 0) {
            spinnerAddress.visibility = View.VISIBLE
            val strings = client.clientAddresses.map { it.rawAddress }
            spinnerAddress.attachDataSource(strings)
            spinnerAddress.addOnItemClickListener { adapterView, view, i, l ->
                task.clientAddressId = client.clientAddresses[i].id
            }
            if (task.clientAddressId != null) {
                spinnerAddress.selectedIndex =
                        client.clientAddresses.indexOfFirst { clientAddress -> clientAddress.id == task.clientAddressId }
            } else {
                task.clientAddressId = client.clientAddresses[0].id
            }

        } else {
            spinnerAddress.visibility = View.GONE
        }

        tvAddAddress.setOnClickListener { _ ->
            navigationController.navigateToEditClientAddress(task.clientId!!)
        }

        if (rlClientInfoContainer.height == 0) {
            val anim = ValueAnimator.ofInt(0, 700)
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
        } else {
            task.clientId = UUID.randomUUID()
        }

        val autocomplete = acClient.autoCompleteTextView

        etPhoneNumberField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                hideClientDetails()
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
                } else {
                    ignoreWatcher = false
                    hideClientDetails()
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
