package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.clients

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.models.ClientAddress
import com.kvteam.deliverytracker.core.models.CollectionEntityAction
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_edit_client_address.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*
import javax.inject.Inject


class EditClientAddressFragment : DeliveryTrackerFragment() {
    private val TYPE_KEY = "TYPE"
    private val ADDRESS_KEY = "ADDRESS"

    @Inject
    lateinit var navigationController: NavigationController

    @Inject
    lateinit var lm: ILocalizationManager

    private var type = CollectionEntityAction.Create

    private var address = EMPTY_STRING

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val args = arguments

        if(args != null) {
            type = args.get(TYPE_KEY) as CollectionEntityAction
            address = args.get(ADDRESS_KEY) as? String ?: EMPTY_STRING
        }

        etAddress.setText(address)
        etAddress.requestFocus()
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_client_address, container, false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_finish -> {
                val address = ClientAddress()
                address.rawAddress = etAddress.text.toString()
                address.action = type
                address.id = UUID.randomUUID()
                navigationController.info["address"] = address
                navigationController.closeCurrentFragment()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_edit_client_address_menu, menu)
        activity!!.toolbar_left_action.setOnClickListener { _ ->
            navigationController.closeCurrentFragment()
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    companion object {
        fun create(type: CollectionEntityAction, address: ClientAddress?): EditClientAddressFragment {
            val fragment = EditClientAddressFragment()
            val args = Bundle()
            args.putSerializable(fragment.TYPE_KEY, type)
            if (address != null) {
                args.putSerializable(fragment.ADDRESS_KEY, address.rawAddress)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
