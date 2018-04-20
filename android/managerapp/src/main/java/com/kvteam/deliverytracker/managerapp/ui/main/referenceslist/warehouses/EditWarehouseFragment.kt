package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.warehouses

import android.os.Bundle
import android.view.*
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.dataprovider.base.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.base.DataProviderGetMode
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.errorhandling.IErrorHandler
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController
import com.kvteam.deliverytracker.core.webservice.IReferenceWebservice
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_edit_warehouse.*
import java.util.*
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

    @Inject
    lateinit var dp: DataProvider

    private val warehouseIdKey = "warehouseId"
    private var warehouseId
        get() = arguments?.getSerializable(warehouseIdKey)!! as UUID
        set(value) = arguments?.putSerializable(warehouseIdKey, value)!!

    private val tryPrefetchKey = "tryPrefetch"
    private var tryPrefetch : Boolean
        get() = arguments?.getBoolean(tryPrefetchKey) ?: false
        set(value) = arguments?.putBoolean(tryPrefetchKey, value)!!

    private lateinit var validation: AwesomeValidation

    fun setWarehouse (id: UUID?) {
        this.warehouseId = id ?: UUID.randomUUID()
        this.tryPrefetch = id != null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun configureToolbar(toolbar: ToolbarController) {
        super.configureToolbar(toolbar)
        toolbar.setToolbarTitle(lm.getString(R.string.Core_WarehouseHeader))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) = launchUI {
        super.onActivityCreated(savedInstanceState)
        if(tryPrefetch) {
            dp.warehouses.getAsync(warehouseId, DataProviderGetMode.PREFER_CACHE)
            tryPrefetch = false
        }
        val warehouse = dp.warehouses.getAsync(warehouseId, DataProviderGetMode.DIRTY).entry

        etNameField.setText(warehouse.name)
        etAddressField.setText(warehouse.rawAddress)

        validation = AwesomeValidation(ValidationStyle.UNDERLABEL)
        validation.addValidation(
                etNameField, RegexTemplate.NOT_EMPTY, getString(com.kvteam.deliverytracker.core.R.string.Core_WarehouseTitleValidationError))
        validation.addValidation(
                etAddressField, RegexTemplate.NOT_EMPTY, getString(com.kvteam.deliverytracker.core.R.string.Core_WarehouseAddressValidationError))
        validation.setContext(this@EditWarehouseFragment.dtActivity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_warehouse, container, false)
    }

    override fun onStop() {
        dp.warehouses.invalidateDirty(warehouseId)
        super.onStop()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = launchUI ({
        when (item.itemId) {
            R.id.action_done -> {
                if(!validation.validate()) {
                    return@launchUI
                }

                val warehouse = dp.warehouses.getAsync(warehouseId, DataProviderGetMode.DIRTY).entry
                warehouse.name = etNameField.text.toString()
                warehouse.rawAddress = etAddressField.text.toString()
                dp.warehouses.upsertAsync(warehouse)

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
}
