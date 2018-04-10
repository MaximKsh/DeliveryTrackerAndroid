package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.clients

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.EMPTY_UUID
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.dataprovider.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.DataProviderGetMode
import com.kvteam.deliverytracker.core.models.ClientAddress
import com.kvteam.deliverytracker.core.models.CollectionEntityAction
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_edit_client_address.*
import java.util.*
import javax.inject.Inject


class EditClientAddressFragment : DeliveryTrackerFragment() {
    private val TYPE_KEY = "TYPE"
    private val ADDRESS_KEY = "ADDRESS"

    @Inject
    lateinit var navigationController: NavigationController

    @Inject
    lateinit var lm: ILocalizationManager

    @Inject
    lateinit var dp: DataProvider

    private val clientIdKey = "client"
    private var clientId
        get() = arguments?.getSerializable(clientIdKey)!! as UUID
        set(value) = arguments?.putSerializable(clientIdKey, value)!!

    private val addressIdKey = "address"
    private var addressId
        get() = arguments?.getSerializable(addressIdKey)!! as UUID
        set(value) = arguments?.putSerializable(addressIdKey, value)!!

    private lateinit var validation: AwesomeValidation

    fun setAddress (clientId: UUID, addressId: UUID?) {
        this.clientId = clientId
        this.addressId = addressId ?: EMPTY_UUID
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun configureToolbar(toolbar: ToolbarController) {
        super.configureToolbar(toolbar)
        toolbar.setToolbarTitle(lm.getString(R.string.Core_ClientAddressHeader))
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) = launchUI {
        super.onActivityCreated(savedInstanceState)
        dtActivity.softKeyboard.openSoftKeyboard()

        val client = dp.clients.getAsync(clientId, DataProviderGetMode.DIRTY).entry
        val address = client.clientAddresses.firstOrNull { it.id == addressId }
        etAddress.setText(address?.rawAddress)

        etAddress.requestFocus()
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)

        validation = AwesomeValidation(ValidationStyle.UNDERLABEL)
        validation.addValidation(
                etAddress, RegexTemplate.NOT_EMPTY, getString(com.kvteam.deliverytracker.core.R.string.Core_ClientAddressValidationError))
        validation.setContext(this@EditClientAddressFragment.dtActivity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_client_address, container, false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = launchUI ({
        when (item.itemId) {
            R.id.action_done -> {
                if(!validation.validate()) {
                    return@launchUI
                }

                val client = dp.clients.getAsync(clientId, DataProviderGetMode.DIRTY).entry
                var address = client.clientAddresses.firstOrNull { it.id == addressId }
                if(address == null) {
                    address = ClientAddress()
                    address.id = UUID.randomUUID()
                    address.action = CollectionEntityAction.Create
                    client.clientAddresses.add(address)
                } else {
                    address.action = CollectionEntityAction.Edit
                }
                address.rawAddress = etAddress.text.toString()
                navigationController.closeCurrentFragment()
            }
        }
        return@launchUI
    }, {
        super.onOptionsItemSelected(item)
    })

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.done_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}
