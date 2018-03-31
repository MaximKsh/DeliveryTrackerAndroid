package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.products

import android.os.Bundle
import android.view.*
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.dataprovider.CacheException
import com.kvteam.deliverytracker.core.dataprovider.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.DataProviderGetMode
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.errorhandling.IErrorHandler
import com.kvteam.deliverytracker.core.webservice.IReferenceWebservice
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_product_details.*
import java.util.*
import javax.inject.Inject

class ProductDetailsFragment : DeliveryTrackerFragment() {
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


    fun setProduct (id: UUID) {
        this.productId = id
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) = launchUI {
        super.onActivityCreated(savedInstanceState)

        val (product, origin) = try {
            dp.products.getAsync(productId, DataProviderGetMode.PREFER_WEB)
        } catch (e: CacheException) {
            return@launchUI
        }
        eh.handleNoInternetWarn(origin)

        val name = product.name
        tvNameField.text = name
        tvVendorCodeField.text = product.vendorCode
        tvDescriptionField.text = product.description
        tvCostField.text = product.cost.toString()

        if(name?.isNotBlank() == true) {
            toolbarController.setToolbarTitle(name)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_product_details, container, false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit -> {
                navigationController.navigateToEditProduct(productId)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}
