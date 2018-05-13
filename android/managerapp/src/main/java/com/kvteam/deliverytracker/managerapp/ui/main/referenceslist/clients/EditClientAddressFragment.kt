package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.clients

import android.os.Bundle
import android.util.Log
import android.view.*
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.dataprovider.base.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.base.DataProviderGetMode
import com.kvteam.deliverytracker.core.models.CollectionEntityAction
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.warehouses.BaseAddressMapFragment
import java.util.*
import javax.inject.Inject


class EditClientAddressFragment : BaseAddressMapFragment() {
    @Inject
    lateinit var session: ISession

    @Inject
    lateinit var dp: DataProvider

    private val clientIdKey = "client"
    private var clientId
        get() = arguments?.getSerializable(clientIdKey)!! as UUID
        set(value) = arguments?.putSerializable(clientIdKey, value)!!

    private val addressIdKey = "geoposition"
    private var addressId
        get() = arguments?.getSerializable(addressIdKey)!! as UUID
        set(value) = arguments?.putSerializable(addressIdKey, value)!!

    private val newKey = "new"
    private var newFlag
        get() = arguments?.getSerializable(newKey)!! as? Boolean ?: false
        set(value) = arguments?.putSerializable(newKey, value)!!

    fun setAddress (clientId: UUID, addressId: UUID?) {
        this.clientId = clientId
        this.addressId = addressId ?: UUID.randomUUID()
        this.newFlag = addressId == null
    }

    override fun onItemClick(view: View?, position: Int): Boolean {
        val googleMapAddress = mAddressList[position].googleMapAddress
        val geoposition = googleMapAddress.geoposition
        val address = dp.clientAddresses.get(addressId, clientId, DataProviderGetMode.DIRTY)
        address.geoposition = geoposition
        address.rawAddress = googleMapAddress.primaryText.toString()
        return super.onItemClick(view, position)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) = launchUI {
        super.onActivityCreated(savedInstanceState)

        val address = dp.clientAddresses.get(addressId, clientId, DataProviderGetMode.DIRTY)
        setAddressFieldText(address.rawAddress)
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
        val clientAddress = dp.clientAddresses.get(addressId, clientId, DataProviderGetMode.DIRTY)
        startGeoposition = clientAddress.geoposition
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = launchUI ({
        when (item.itemId) {
            R.id.action_done -> {
                if(!validation.validate()) {
                    return@launchUI
                }

                val address = dp.clientAddresses.get(addressId, clientId, DataProviderGetMode.DIRTY)
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
