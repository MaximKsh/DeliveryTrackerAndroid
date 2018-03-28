package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.warehouses

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
import kotlinx.android.synthetic.main.fragment_warehouse_details.*
import java.util.*
import javax.inject.Inject


class WarehouseDetailsFragment : DeliveryTrackerFragment() {
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

    private val warehouseIdKey = "warehouse_id"
    private var warehouseId
        get() = arguments?.getSerializable(warehouseIdKey)!! as UUID
        set(value) = arguments?.putSerializable(warehouseIdKey, value)!!


    fun setWarehouse (id: UUID) {
        this.warehouseId = id
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun configureToolbar(toolbar: ToolbarController) {
        toolbar.disableDropDown()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) = launchUI {
        super.onActivityCreated(savedInstanceState)

        val (warehouse, origin) = try {
            dp.warehouses.getAsync(warehouseId, DataProviderGetMode.PREFER_WEB)
        } catch (e: CacheException) {
            return@launchUI
        }
        eh.handleNoInternetWarn(origin)

        val name = warehouse.name
        tvNameField.text = name
        tvAddressField.text = warehouse.rawAddress

        if(name?.isNotBlank() == true) {
            toolbarController.setToolbarTitle(name)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_warehouse_details, container, false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit -> {
                navigationController.navigateToEditWarehouse(warehouseId)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_edit_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}
