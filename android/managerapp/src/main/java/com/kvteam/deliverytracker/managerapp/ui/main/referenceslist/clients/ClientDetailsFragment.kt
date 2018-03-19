package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.clients

import android.os.Bundle
import android.view.*
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.IEventEmitter
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.models.Client
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.errorhandling.IErrorHandler
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController
import com.kvteam.deliverytracker.core.webservice.IReferenceWebservice
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_client_details.*
import java.util.*
import javax.inject.Inject

class ClientDetailsFragment : DeliveryTrackerFragment() {
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

    lateinit var clientId: UUID
    private lateinit var client: Client

    override fun onActivityCreated(savedInstanceState: Bundle?) = launchUI {
        super.onActivityCreated(savedInstanceState)
        val clientResult = referenceWebservice.getAsync("Client", clientId)

        if (eh.handle(clientResult)) {
            return@launchUI
        }

        client = Client()
        client.fromMap(clientResult.entity!!.entity!!)

        tvNameField.text = client.name
        tvSurnameField.text = client.surname
        tvPatronymicField.text = client.patronymic
        tvPhoneNumberField.text = client.phoneNumber
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_client_details, container, false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit_client -> {
                navigationController.navigateToEditClient(client)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_client_details_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}
