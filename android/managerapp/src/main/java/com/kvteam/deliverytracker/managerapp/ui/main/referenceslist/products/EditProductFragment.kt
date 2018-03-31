package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.products

import android.os.Bundle
import android.view.*
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
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
import kotlinx.android.synthetic.main.fragment_edit_product.*
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

class EditProductFragment : DeliveryTrackerFragment() {
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

    private val productIdKey = "product_id"
    private var productId
        get() = arguments?.getSerializable(productIdKey)!! as UUID
        set(value) = arguments?.putSerializable(productIdKey, value)!!

    private val tryPrefetchKey = "tryPrefetch"
    private var tryPrefetch : Boolean
        get() = arguments?.getBoolean(tryPrefetchKey) ?: false
        set(value) = arguments?.putBoolean(tryPrefetchKey, value)!!


    fun setProduct (id: UUID?) {
        this.productId = id ?: UUID.randomUUID()
        this.tryPrefetch = id != null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun configureToolbar(toolbar: ToolbarController) {
        toolbar.disableDropDown()
        toolbar.setToolbarTitle("Product")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) = launchUI{
        super.onActivityCreated(savedInstanceState)
        if(tryPrefetch) {
            dp.products.getAsync(productId, DataProviderGetMode.PREFER_CACHE)
            tryPrefetch = false
        }
        val product = dp.products.getAsync(productId, DataProviderGetMode.DIRTY).entry
        etNameField.setText(product.name)
        etVendorCodeField.setText(product.vendorCode)
        etDescriptionField.setText(product.description)
        etCostField.setText(product.cost?.toString() ?: EMPTY_STRING)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_product, container, false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = launchUI ({
        when (item.itemId) {
            R.id.action_done -> {
                val product = dp.products.getAsync(productId, DataProviderGetMode.DIRTY).entry
                product.name = etNameField.text.toString()
                product.vendorCode = etVendorCodeField.text.toString()
                product.cost = try {
                    BigDecimal(etCostField.text?.toString())
                } catch (e : Exception) {
                    BigDecimal.ZERO
                }
                product.description = etDescriptionField.text.toString()

                dp.products.upsertAsync(product)

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
