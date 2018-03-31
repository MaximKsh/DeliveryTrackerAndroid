package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.warehouses

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.dataprovider.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.DataProviderGetMode
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
        toolbar.setToolbarTitle("Warehouse")
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
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_warehouse, container, false)
    }

    override fun onPause() {
        super.onPause()
        val view =  activity!!.currentFocus
        if (view != null) {
            val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = launchUI ({
        when (item.itemId) {
            R.id.action_done -> {
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
