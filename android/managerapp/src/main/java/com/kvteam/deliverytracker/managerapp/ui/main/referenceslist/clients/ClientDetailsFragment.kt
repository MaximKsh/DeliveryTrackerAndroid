package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.clients

import android.os.Bundle
import android.view.*
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.dataprovider.CacheException
import com.kvteam.deliverytracker.core.dataprovider.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.DataProviderGetMode
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.errorhandling.IErrorHandler
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController
import com.kvteam.deliverytracker.core.webservice.IReferenceWebservice
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.client_address_item.view.*
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
    lateinit var eh: IErrorHandler


    @Inject
    lateinit var dp: DataProvider

    private val clientIdKey = "client"
    private var clientId
        get() = arguments?.getSerializable(clientIdKey)!! as UUID
        set(value) = arguments?.putSerializable(clientIdKey, value)!!


    fun setClient (id: UUID) {
        this.clientId = id
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) = launchUI {
        super.onActivityCreated(savedInstanceState)
        val (client, origin) = try {
            dp.clients.getAsync(clientId, DataProviderGetMode.PREFER_WEB)
        } catch (e: CacheException) {
            return@launchUI
        }
        eh.handleNoInternetWarn(origin)

        tvNameField.text = client.name
        tvSurnameField.text = client.surname
        tvPatronymicField.text = client.patronymic
        tvPhoneNumberField.text = client.phoneNumber

        client.clientAddresses.forEach { clientAddress ->
            val view = layoutInflater.inflate(R.layout.client_address_item, llAddressesContainer, false)
            view.tvClientAddress.text = clientAddress.rawAddress
            view.tvDeleteItem.visibility = View.GONE
            llAddressesContainer.addView(view)
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_client_details, container, false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = launchUI ({
        when (item.itemId) {
            R.id.action_edit -> {
                navigationController.navigateToEditClient(clientId)
            }
        }
    }, {
        super.onOptionsItemSelected(item)
    })

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_edit_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}
