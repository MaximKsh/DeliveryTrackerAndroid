package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.clients

import android.os.Bundle
import android.view.*
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.dataprovider.base.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.base.DataProviderGetMode
import com.kvteam.deliverytracker.core.models.CollectionEntityAction
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.errorhandling.IErrorHandler
import com.kvteam.deliverytracker.core.ui.setPhoneNumber
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController
import com.kvteam.deliverytracker.core.webservice.IReferenceWebservice
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.client_address_item.view.*
import kotlinx.android.synthetic.main.fragment_edit_client.*
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

    private lateinit var validation: AwesomeValidation

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
        super.configureToolbar(toolbar)
        toolbar.setToolbarTitle("Edit client")
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
        etPhoneNumberField.setPhoneNumber(client.phoneNumber)

        val clientAddresses = dp.clientAddresses.getByParent(clientId, DataProviderGetMode.DIRTY)
        clientAddresses.forEach { clientAddress ->
            val view = layoutInflater.inflate(R.layout.client_address_item, llAddressesContainer, false)
            view.tvClientAddress.text = clientAddress.rawAddress
            view.frListContainer.setOnClickListener { _ ->
                navigationController.navigateToEditClientAddress(clientId, clientAddress.id)
            }
            view.tvDeleteItem.setOnClickListener { _ ->
                when (clientAddress.action) {
                    CollectionEntityAction.Create -> {
                        dp.clientAddresses.delete(clientAddress.id!!)
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

        validation = AwesomeValidation(ValidationStyle.UNDERLABEL)
        validation.addValidation(
                etSurnameField, RegexTemplate.NOT_EMPTY, getString(com.kvteam.deliverytracker.core.R.string.Core_SurnameValidationError))
        validation.addValidation(
                etNameField, RegexTemplate.NOT_EMPTY, getString(com.kvteam.deliverytracker.core.R.string.Core_NameValidationError))
        validation.addValidation(
                etPhoneNumberField,
                {
                    it.length == 16
                },
                getString(com.kvteam.deliverytracker.core.R.string.Core_PhoneValidationError))
        validation.setContext(this@EditClientFragment.dtActivity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_client, container, false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = launchUI ({
        when (item.itemId) {
            R.id.action_done -> {
                if(!validation.validate()) {
                    return@launchUI
                }

                val client = dp.clients.getAsync(clientId, DataProviderGetMode.DIRTY).entry
                client.name = etNameField.text.toString()
                client.surname = etSurnameField.text.toString()
                client.patronymic = etPatronymicField.text.toString()
                client.phoneNumber = etPhoneNumberField.text.toString()

                dp.clients.upsertAsync(client)

                navigationController.closeCurrentFragment()
            }
        }

    }, {
        super.onOptionsItemSelected(item)
    })

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.done_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPopFragmentFromBackstack() {
        dp.clients.invalidateDirty(clientId)
        super.onPopFragmentFromBackstack()
    }

}
