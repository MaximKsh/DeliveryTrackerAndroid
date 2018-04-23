package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.clients

import android.os.Bundle
import android.view.*
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.dataprovider.base.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.base.DataProviderGetMode
import com.kvteam.deliverytracker.core.models.CollectionEntityAction
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_edit_client_address.*
import java.util.*
import javax.inject.Inject


class EditClientAddressFragment : DeliveryTrackerFragment() {
    @Inject
    lateinit var session: ISession

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

    private val newKey = "new"
    private var newFlag
        get() = arguments?.getSerializable(newKey)!! as? Boolean ?: false
        set(value) = arguments?.putSerializable(newKey, value)!!

    private lateinit var validation: AwesomeValidation

    fun setAddress (clientId: UUID, addressId: UUID?) {
        this.clientId = clientId
        this.addressId = addressId ?: UUID.randomUUID()
        this.newFlag = addressId == null
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
      
        val address = dp.clientAddresses.get(addressId, clientId, DataProviderGetMode.DIRTY)
        etAddress.setText(address.rawAddress)
        etAddress.requestFocus()

        validation = AwesomeValidation(ValidationStyle.UNDERLABEL)
        validation.addValidation(
                etAddress, RegexTemplate.NOT_EMPTY, getString(com.kvteam.deliverytracker.core.R.string.Core_ClientAddressValidationError))
        validation.setContext(this@EditClientAddressFragment.dtActivity)
    }

    override fun onStop() {
        val address = dp.clientAddresses.get(addressId, clientId, DataProviderGetMode.DIRTY)
        if (address.action == CollectionEntityAction.None
            && newFlag) {
            dp.clientAddresses.forgetChanges(addressId)
        }

        super.onStop()
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

                val address = dp.clientAddresses.get(addressId, clientId, DataProviderGetMode.DIRTY)
                address.rawAddress = etAddress.text.toString()
                address.instanceId = session.user!!.instanceId
                dp.clientAddresses.saveChanges(address)

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
