package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.warehouses

import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.dataprovider.base.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.base.DataProviderGetMode
import com.kvteam.deliverytracker.core.ui.addOnFocusChangeListener
import com.kvteam.deliverytracker.managerapp.R
import kotlinx.android.synthetic.main.fragment_edit_warehouse.*
import java.util.*
import javax.inject.Inject


class EditWarehouseFragment : BaseAddressMapFragment() {

    private var mEtNameHeight = 0

    @Inject
    lateinit var dp: DataProvider

    override val editContainerLayoutID: Int
        get() = R.layout.fragment_edit_warehouse

    private val warehouseIdKey = "warehouseId"
    private var warehouseId
        get() = arguments?.getSerializable(warehouseIdKey)!! as UUID
        set(value) = arguments?.putSerializable(warehouseIdKey, value)!!

    private val tryPrefetchKey = "tryPrefetch"
    private var tryPrefetch: Boolean
        get() = arguments?.getBoolean(tryPrefetchKey) ?: false
        set(value) = arguments?.putBoolean(tryPrefetchKey, value)!!

    private lateinit var validation: AwesomeValidation

    fun setWarehouse(id: UUID?) {
        this.warehouseId = id ?: UUID.randomUUID()
        this.tryPrefetch = id != null
    }

    override fun onItemClick(view: View?, position: Int): Boolean {
        val googleMapAddress = mAddressList[position].googleMapAddress
        val geoposition = googleMapAddress.geoposition
        val warehouse = dp.warehouses.get(warehouseId, DataProviderGetMode.DIRTY).entry
        warehouse.geoposition = geoposition
        return super.onItemClick(view, position)
    }

    override fun interpolatedAnimations(percent: Float, endPercent: Float) {
        if (percent <= endPercent) {
            val normalizedPercent = normalizePercent(percent, endPercent)
            val llNameLayoutParams = llName.layoutParams
            llNameLayoutParams.height = (normalizedPercent * mEtNameHeight).toInt()
            llName.layoutParams = llNameLayoutParams
        }
        super.interpolatedAnimations(percent, endPercent)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) = launchUI {
        super.onActivityCreated(savedInstanceState)

        if (tryPrefetch) {
            dp.warehouses.getAsync(warehouseId, DataProviderGetMode.PREFER_CACHE)
            tryPrefetch = false
        }

        val warehouse = dp.warehouses.get(warehouseId, DataProviderGetMode.DIRTY).entry

        val vto = llName.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                llName.viewTreeObserver.removeOnGlobalLayoutListener(this)
                mEtNameHeight = llName.measuredHeight
            }
        })

        etNameField.setText(warehouse.name)

        setAddressFieldText(warehouse.rawAddress)

        etNameField.addOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(view: View?, focused: Boolean) {
                updateAddressesPanelHeight()
            }
        })

        validation = AwesomeValidation(ValidationStyle.UNDERLABEL)
        validation.addValidation(
                etNameField, RegexTemplate.NOT_EMPTY, getString(com.kvteam.deliverytracker.core.R.string.Core_WarehouseTitleValidationError))
        validation.addValidation(
                etAddressField, RegexTemplate.NOT_EMPTY, getString(com.kvteam.deliverytracker.core.R.string.Core_WarehouseAddressValidationError))
        validation.setContext(this@EditWarehouseFragment.dtActivity)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val warehouse = dp.warehouses.get(warehouseId, DataProviderGetMode.DIRTY).entry
        startGeoposition = warehouse.geoposition
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onStop() {
        dp.warehouses.invalidateDirty(warehouseId)
        super.onStop()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = launchUI({
        when (item.itemId) {
            R.id.action_done -> {
                if (!validation.validate()) {
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
