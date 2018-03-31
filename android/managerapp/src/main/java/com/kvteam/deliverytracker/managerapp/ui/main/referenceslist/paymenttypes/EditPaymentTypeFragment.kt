package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.paymenttypes

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
        toolbar.setToolbarTitle("Payment Type")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) = launchUI {
        super.onActivityCreated(savedInstanceState)
        if(tryPrefetch) {
            dp.paymentTypes.getAsync(paymentTypeId, DataProviderGetMode.PREFER_CACHE)
            tryPrefetch = false
        }
        val paymentType = dp.paymentTypes.getAsync(paymentTypeId, DataProviderGetMode.DIRTY).entry

        etNameField.setText(paymentType.name)
        etNameField.requestFocus()
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_payment_type, container, false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =  launchUI ({
        when (item.itemId) {
            R.id.action_done -> {
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
