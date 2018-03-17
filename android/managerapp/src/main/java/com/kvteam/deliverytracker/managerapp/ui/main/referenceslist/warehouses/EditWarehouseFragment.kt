package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.warehouses

import android.os.Bundle
import android.view.*
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.models.Warehouse
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.dropdowntop.ToolbarController
import com.kvteam.deliverytracker.core.ui.errorhandling.IErrorHandler
import com.kvteam.deliverytracker.core.webservice.IReferenceWebservice
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_edit_warehouse.*
import javax.inject.Inject

class EditWarehouseFragment : DeliveryTrackerFragment() {
    @Inject
    lateinit var navigationController: NavigationController

    @Inject
    lateinit var referenceWebservice: IReferenceWebservice

    @Inject
    lateinit var lm: ILocalizationManager

    @Inject
    lateinit var eh: IErrorHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun configureToolbar(toolbar: ToolbarController) {
        toolbar.disableDropDown()
        toolbar.setToolbarTitle("Warehouse")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_warehouse, container, false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = launchUI ({
        when (item.itemId) {
            R.id.action_finish -> {
                val warehouse = Warehouse()
                warehouse.name = etNameField.text.toString()
                warehouse.rawAddress = etAddressField.text.toString()

                val result = referenceWebservice.createAsync("Warehouse", warehouse)
                if(eh.handle(result)) {
                    return@launchUI
                }
                navigationController.closeCurrentFragment()
            }
        }
    }, {
        super.onOptionsItemSelected(item)
    })

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_edit_warehouse_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}
