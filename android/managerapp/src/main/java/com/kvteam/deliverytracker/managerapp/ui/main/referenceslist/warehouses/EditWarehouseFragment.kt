package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.warehouses

import android.animation.ValueAnimator
import android.app.Service
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.inputmethod.InputMethodManager
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.dataprovider.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.DataProviderGetMode
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.addOnFocusChangeListener
import com.kvteam.deliverytracker.core.ui.errorhandling.IErrorHandler
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController
import com.kvteam.deliverytracker.core.webservice.IReferenceWebservice
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.addresslist.AddressListItem
import com.kvteam.deliverytracker.managerapp.ui.main.MainActivity
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import dagger.android.support.AndroidSupportInjection
import eu.davidea.flexibleadapter.FlexibleAdapter
import kotlinx.android.synthetic.main.fragment_edit_warehouse.*
import java.util.*
import javax.inject.Inject


class EditWarehouseFragment : DeliveryTrackerFragment(), FlexibleAdapter.OnItemClickListener {
    override fun onItemClick(view: View?, position: Int): Boolean {
        hideKeyboard()
        etAddressField.clearFocus()
        etAddressField.setText(mAddressList[position].address.rawAddress)
        toggleWarehouseNameField(true)
        slidingLayout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        return false
    }

    override val useSoftKeyboardFeatures = false

    private var mEtNameHeight = 0

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

    private var mAddressList = listOf<AddressListItem>()

    private val mAddressListAdapter = FlexibleAdapter(mAddressList)

    fun setWarehouse (id: UUID?) {
        this.warehouseId = id ?: UUID.randomUUID()
        this.tryPrefetch = id != null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        AndroidSupportInjection.inject(this)
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onStop() {
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        hideKeyboard()
        super.onStop()
    }

    private fun hideKeyboard () {
        val im = activity!!.getSystemService(Service.INPUT_METHOD_SERVICE) as InputMethodManager
        im.hideSoftInputFromWindow(activity!!.currentFocus.windowToken, 0)
    }

    override fun configureToolbar(toolbar: ToolbarController) {
        super.configureToolbar(toolbar)
        toolbar.setToolbarTitle(lm.getString(R.string.Core_WarehouseHeader))
    }

    fun toggleWarehouseNameField (open: Boolean) {
        val anim = if (open) ValueAnimator.ofInt(llName.height, mEtNameHeight) else ValueAnimator.ofInt(llName.height, 0)
        anim.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            val layoutParams = llName.layoutParams
            layoutParams.height = value
            llName.layoutParams = layoutParams
            slidingLayout.anchorPoint =  1 - rlNameAndAddressContainer.bottom.toFloat() / slidingLayout.height.toFloat()
        }
        anim.duration = 200L
        anim.start()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) = launchUI {
        super.onActivityCreated(savedInstanceState)

        if(tryPrefetch) {
            dp.warehouses.getAsync(warehouseId, DataProviderGetMode.PREFER_CACHE)
            tryPrefetch = false
        }

        val warehouse = dp.warehouses.get(warehouseId, DataProviderGetMode.DIRTY).entry

        val mapFragment = childFragmentManager.findFragmentById(R.id.fGoogleMap) as MapFragment

        mapFragment.getMapAsync { googleMap ->
            val sydney = LatLng(55.751244, 37.618423)
            val cameraPosition = CameraPosition.Builder().target(sydney).zoom(12f).build()
            googleMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }

        etNameField.setText(warehouse.name)
        etAddressField.setText(warehouse.rawAddress)

        etNameField.addOnFocusChangeListener(object: View.OnFocusChangeListener {
            override fun onFocusChange(view: View?, focused: Boolean) {
                slidingLayout.anchorPoint =  1 - rlNameAndAddressContainer.bottom.toFloat() / slidingLayout.height.toFloat()
            }
        })

        val vto = llName.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                llName.viewTreeObserver.removeOnGlobalLayoutListener(this)
                mEtNameHeight = llName.measuredHeight
                slidingLayout.anchorPoint =  1 - rlNameAndAddressContainer.bottom.toFloat() / slidingLayout.height.toFloat()
            }
        })

        etAddressField.addOnFocusChangeListener(object: View.OnFocusChangeListener {
            override fun onFocusChange(view: View?, focused: Boolean) {
                if (focused) {
                    toggleWarehouseNameField(false)
                    if (slidingLayout.panelState != SlidingUpPanelLayout.PanelState.ANCHORED) {
                        slidingLayout.panelState = SlidingUpPanelLayout.PanelState.ANCHORED
                    }
                } else {
                    slidingLayout.anchorPoint =  1 - rlNameAndAddressContainer.bottom.toFloat() / slidingLayout.height.toFloat()
                }
            }
        })

        slidingLayout.addPanelSlideListener(object: SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View?, slideOffset: Float) {}

            override fun onPanelStateChanged(panel: View?, previousState: SlidingUpPanelLayout.PanelState?, newState: SlidingUpPanelLayout.PanelState?) {
                if ((previousState != newState) && (newState == SlidingUpPanelLayout.PanelState.COLLAPSED)) {
                    toggleWarehouseNameField(true)
                }
            }

        })


        mAddressListAdapter.addListener(this@EditWarehouseFragment)
        rvAddressList.layoutManager = LinearLayoutManager(activity)
        rvAddressList.adapter = mAddressListAdapter

        etAddressField.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) = launchUI {
                if (slidingLayout.panelState != SlidingUpPanelLayout.PanelState.ANCHORED) {
                    slidingLayout.panelState = SlidingUpPanelLayout.PanelState.ANCHORED
                }
                val addressList = (activity as MainActivity).mMapsAdapter.getAddressList(text.toString())
                mAddressList = addressList.map { AddressListItem(it) }
                mAddressListAdapter.updateDataSet(mAddressList)
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
        return inflater.inflate(R.layout.fragment_edit_warehouse, container, false)
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
