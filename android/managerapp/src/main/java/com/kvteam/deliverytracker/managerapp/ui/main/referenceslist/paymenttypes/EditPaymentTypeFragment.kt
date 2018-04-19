package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.paymenttypes

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
import kotlinx.android.synthetic.main.fragment_edit_payment_type.*
import java.util.*
import javax.inject.Inject


class EditPaymentTypeFragment : DeliveryTrackerFragment() {
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

    private val paymentTypeIdKey = "payment_type"
    private var paymentTypeId
        get() = arguments?.getSerializable(paymentTypeIdKey)!! as UUID
        set(value) = arguments?.putSerializable(paymentTypeIdKey, value)!!

    private val tryPrefetchKey = "tryPrefetch"
    private var tryPrefetch : Boolean
        get() = arguments?.getBoolean(tryPrefetchKey) ?: false
        set(value) = arguments?.putBoolean(tryPrefetchKey, value)!!

    private lateinit var validation: AwesomeValidation

    fun setPaymentType (id: UUID?) {
        this.paymentTypeId = id ?: UUID.randomUUID()
        this.tryPrefetch = id != null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }
    override fun configureToolbar(toolbar: ToolbarController) {
        super.configureToolbar(toolbar)
        toolbar.setToolbarTitle(lm.getString(R.string.Core_PaymentTypeHeader))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) = launchUI {
        super.onActivityCreated(savedInstanceState)
        if(tryPrefetch) {
            dp.paymentTypes.getAsync(paymentTypeId, DataProviderGetMode.PREFER_CACHE)
            tryPrefetch = false
        }
        val paymentType = dp.paymentTypes.getAsync(paymentTypeId, DataProviderGetMode.DIRTY).entry
        dtActivity.softKeyboard.openSoftKeyboard()

        etNameField.setText(paymentType.name)
        etNameField.requestFocus()

        validation = AwesomeValidation(ValidationStyle.UNDERLABEL)
        validation.addValidation(
                etNameField, RegexTemplate.NOT_EMPTY, getString(com.kvteam.deliverytracker.core.R.string.Core_PaymentTypeValidationError))
        validation.setContext(this@EditPaymentTypeFragment.dtActivity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_payment_type, container, false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =  launchUI ({
        when (item.itemId) {
            R.id.action_done -> {
                if(!validation.validate()) {
                    return@launchUI
                }

                val paymentType = dp.paymentTypes.getAsync(paymentTypeId, DataProviderGetMode.DIRTY).entry
                paymentType.name = etNameField.text.toString()
                dp.paymentTypes.upsertAsync(paymentType)
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
