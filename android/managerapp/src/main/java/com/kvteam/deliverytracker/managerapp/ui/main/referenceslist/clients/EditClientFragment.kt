package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.clients

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.dataprovider.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.DataProviderGetMode
import com.kvteam.deliverytracker.core.models.CollectionEntityAction
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.errorhandling.IErrorHandler
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController
import com.kvteam.deliverytracker.core.webservice.IReferenceWebservice
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.client_address_item.view.*
import kotlinx.android.synthetic.main.fragment_add_client.*
import java.util.*
import javax.inject.Inject


class EditClientFragment : DeliveryTrackerFragment() {
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

    private val viewBinderHelper = ViewBinderHelper()

    init {
        viewBinderHelper.setOpenOnlyOne(true)
    }

    private val clientIdKey = "client"
    private var clientId
        get() = arguments?.getSerializable(clientIdKey)!! as UUID
        set(value) = arguments?.putSerializable(clientIdKey, value)!!

    private val tryPrefetchKey = "tryPrefetch"
    private var tryPrefetch : Boolean
        get() = arguments?.getBoolean(tryPrefetchKey) ?: false
        set(value) = arguments?.putBoolean(tryPrefetchKey, value)!!


    fun setClient (id: UUID?) {
        this.clientId = id ?: UUID.randomUUID()
        this.tryPrefetch = id != null
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

    override fun onActivityCreated(savedInstanceState: Bundle?) = launchUI {
        super.onActivityCreated(savedInstanceState)
        if(tryPrefetch) {
            dp.clients.getAsync(clientId, DataProviderGetMode.PREFER_CACHE)
            tryPrefetch = false
        }

        val client = dp.clients.getAsync(clientId, DataProviderGetMode.DIRTY).entry

        tvAddAddress.setOnClickListener { _ -> launchUI {
                val c = dp.clients.getAsync(clientId, DataProviderGetMode.DIRTY).entry
                c.name = etNameField.text.toString()
                c.surname = etSurnameField.text.toString()
                c.patronymic = etPatronymicField.text.toString()
                c.phoneNumber = etPhoneNumberField.text.toString()
                navigationController.navigateToEditClientAddress(clientId)
            }
        }

        etNameField.setText(client.name)
        etSurnameField.setText(client.surname)
        etPatronymicField.setText(client.patronymic)
        etPhoneNumberField.setText(client.phoneNumber)

        client.clientAddresses.forEach { clientAddress ->
            val view = layoutInflater.inflate(R.layout.client_address_item, llAddressesContainer, false)
            view.tvClientAddress.text = clientAddress.rawAddress
            view.frListContainer.setOnClickListener { _ ->
                navigationController.navigateToEditClientAddress(clientId, clientAddress.id)
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
                val client = dp.clients.getAsync(clientId, DataProviderGetMode.DIRTY).entry
                client.name = etNameField.text.toString()
                client.surname = etSurnameField.text.toString()
                client.patronymic = etPatronymicField.text.toString()
                client.phoneNumber = etPhoneNumberField.text.toString()

                dp.clients.upsertAsync(client)

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

    override fun onDestroy() {
        super.onDestroy()
        dp.clients.invalidate(clientId)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_add_client_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}
