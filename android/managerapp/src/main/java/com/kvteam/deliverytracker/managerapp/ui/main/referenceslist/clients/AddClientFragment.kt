package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.clients

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.models.Client
import com.kvteam.deliverytracker.core.models.ClientAddress
import com.kvteam.deliverytracker.core.models.CollectionEntityAction
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.dropdowntop.ToolbarController
import com.kvteam.deliverytracker.core.webservice.IReferenceWebservice
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.client_address_item.view.*
import kotlinx.android.synthetic.main.fragment_add_client.*
import javax.inject.Inject


class AddClientFragment : DeliveryTrackerFragment() {
    @Inject
    lateinit var navigationController: NavigationController

    @Inject
    lateinit var referenceWebservice: IReferenceWebservice

    @Inject
    lateinit var lm: ILocalizationManager

    private val viewBinderHelper = ViewBinderHelper()

    init {
        viewBinderHelper.setOpenOnlyOne(true);
    }

    private val client = Client()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun configureToolbar(toolbar: ToolbarController) {
        toolbar.disableDropDown()
        toolbar.setToolbarTitle("Client")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        tvAddAddress.setOnClickListener { _ ->
            navigationController.navigateToEditClientAddress(CollectionEntityAction.Create)
        }
        if (navigationController.info.containsKey("address")) {
            val address = navigationController.info["address"] as ClientAddress
            navigationController.info.remove("address")

            when (address.action) {
                CollectionEntityAction.Edit -> {}
                CollectionEntityAction.Create -> {
                    client.clientAddresses.add(address)
                }
            }
        }
        client.clientAddresses.forEach { clientAddress ->
            val view = layoutInflater.inflate(R.layout.client_address_item, llAddressesContainer, false)
            view.tvClientAddress.text = clientAddress.rawAddress
            view.setOnClickListener { _ ->
                navigationController.navigateToEditClientAddress(CollectionEntityAction.Edit, clientAddress)
            }
            view.tvDeleteItem.setOnClickListener { _ ->
                when (clientAddress.action) {
                    CollectionEntityAction.Create -> {
                        client.clientAddresses.remove(clientAddress)
                        view.visibility = View.GONE
                    }
                    else -> {
                        clientAddress.action = CollectionEntityAction.Delete
                        view.visibility = View.GONE
                    }
                }
            }
            viewBinderHelper.bind(view.swipeRevealLayout, clientAddress.id.toString())
            llAddressesContainer.addView(view)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_client, container, false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_finish -> {
                invokeAsync({
                    client.name = etNameField.text.toString()
                    client.surname = etSurnameField.text.toString()
                    client.patronymic = etPatronymicField.text.toString()
                    client.phoneNumber = etPhoneNumberField.text.toString()
                    referenceWebservice.createAsync("Client", client)
                }, {
                    if (it.success) {
                        val view =  activity!!.currentFocus
                        if (view != null) {
                            val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.hideSoftInputFromWindow(view.windowToken, 0)
                        }
                        navigationController.closeCurrentFragment()
                    }
                })
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_add_client_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}
