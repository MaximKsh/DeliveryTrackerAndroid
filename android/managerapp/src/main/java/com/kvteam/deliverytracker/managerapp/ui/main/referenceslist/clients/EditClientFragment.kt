package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.clients

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.common.IEventEmitter
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.models.Client
import com.kvteam.deliverytracker.core.models.ClientAddress
import com.kvteam.deliverytracker.core.models.CollectionEntityAction
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.errorhandling.IErrorHandler
import com.kvteam.deliverytracker.core.ui.setNullableText
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController
import com.kvteam.deliverytracker.core.webservice.IReferenceWebservice
import com.kvteam.deliverytracker.core.webservice.NetworkResult
import com.kvteam.deliverytracker.core.webservice.ReferenceWebservice
import com.kvteam.deliverytracker.core.webservice.viewmodels.ReferenceResponse
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.client_address_item.view.*
import kotlinx.android.synthetic.main.fragment_add_client.*
import javax.inject.Inject


class EditClientFragment : DeliveryTrackerFragment() {
    @Inject
    lateinit var navigationController: NavigationController

    @Inject
    lateinit var referenceWebservice: IReferenceWebservice

    @Inject
    lateinit var lm: ILocalizationManager

    @Inject
    lateinit var ee: IEventEmitter

    @Inject
    lateinit var eh: IErrorHandler

    private val viewBinderHelper = ViewBinderHelper()

    init {
        viewBinderHelper.setOpenOnlyOne(true);
    }

    private val clientKey = "client"
    private var client
        get() = arguments?.getSerializable(clientKey)!! as Client
        set(value) = arguments?.putSerializable(clientKey, value)!!

    private val modeKey = "mode"
    private var mode
        get() = arguments?.getString(modeKey)!!
        set(value) = arguments?.putString(modeKey, value)!!

    fun setClientInfo (client: Client?) {
        if (client != null) {
            mode = "EDIT"
            this.client = client.copy()
        } else {
            mode = "CREATE"
            this.client = Client()
        }
    }

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
            ee.subscribe("EditClientFragment", "address")
            navigationController.navigateToEditClientAddress(CollectionEntityAction.Create)
        }

        etNameField.setText(client.name)
        etSurnameField.setText(client.surname)
        etPatronymicField.setText(client.patronymic)
        etPhoneNumberField.setText(client.phoneNumber)

        val sig = ee.get("EditClientFragment", "address")
        if (sig != null){
            val address = sig as ClientAddress
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean = launchUI ({
        when (item.itemId) {
            R.id.action_finish -> {
                client.name = etNameField.text.toString()
                client.surname = etSurnameField.text.toString()
                client.patronymic = etPatronymicField.text.toString()
                client.phoneNumber = etPhoneNumberField.text.toString()
                val result: NetworkResult<ReferenceResponse>
                if (mode == "EDIT") {
                    val currentClientId = this@EditClientFragment.client.id!!
                    result = referenceWebservice.editAsync("Client", currentClientId, client)
                } else {
                    result = referenceWebservice.createAsync("Client", client)
                }
                if(eh.handle(result)) {
                    return@launchUI
                }
                val view =  activity!!.currentFocus
                if (view != null) {
                    val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
                navigationController.closeCurrentFragment()
            }
        }

    }, {
        super.onOptionsItemSelected(item)
    })

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_add_client_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}
