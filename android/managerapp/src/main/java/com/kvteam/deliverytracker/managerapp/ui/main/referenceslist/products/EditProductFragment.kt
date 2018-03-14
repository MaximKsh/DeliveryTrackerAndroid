package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.products

import android.os.Bundle
import android.view.*
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.models.Product
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerActivity
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.dropdowntop.ToolbarController
import com.kvteam.deliverytracker.core.webservice.IReferenceWebservice
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_edit_product.*
import java.math.BigDecimal
import javax.inject.Inject

class EditProductFragment : DeliveryTrackerFragment() {
    @Inject
    lateinit var navigationController: NavigationController

    @Inject
    lateinit var referenceWebservice: IReferenceWebservice

    @Inject
    lateinit var lm: ILocalizationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun configureToolbar(toolbar: ToolbarController) {
        toolbar.disableDropDown()
        toolbar.setToolbarTitle("Product")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_product, container, false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_finish -> {
                invokeAsync({
                    val product = Product()
                    product.name = etNameField.text.toString()
                    product.vendorCode = etVendorCodeField.text.toString()
                    product.cost = BigDecimal(etCostField.text.toString())
                    product.description = etDescriptionField.text.toString()

                    referenceWebservice.create("Product", product)
                }, {
                    if (it.success) {
                        navigationController.closeCurrentFragment()
                    }
                })
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_edit_product_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}
