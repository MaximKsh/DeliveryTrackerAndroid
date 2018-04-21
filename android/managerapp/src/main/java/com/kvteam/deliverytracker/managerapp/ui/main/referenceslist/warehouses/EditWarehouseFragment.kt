package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.warehouses

import android.animation.ValueAnimator
import android.app.Service
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.inputmethod.InputMethodManager
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.SupportMapFragment
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
        val googleMapAddress = mAddressList[position].googleMapAddress
        etAddressField.setText(googleMapAddress.primaryText)
        etAddressField.clearFocus()
        hideKeyboard()
        toggleWarehouseNameField(true)
        slidingLayout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        mainActivity.mMapsAdapter.setMarker(
                googleMapAddress.address!!.geoposition!!.toLtnLng(),
                googleMapAddress.viewPort,
                true)
        return false
    }

    override val useSoftKeyboardFeatures = false

    private var mEtNameHeight = 0

    private val mainActivity
        get() = activity as MainActivity

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

    private var density = 1f

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
            updateAddressesPanelHeight()
        }
        anim.duration = 200L
        anim.start()
        if (!open) {
            openPanelAnimations()
        } else {
            closePanelAnimations()
        }
    }

    private lateinit var mGoogleMap: GoogleMap

    private lateinit var animRlNameAndAddressContainer: ValueAnimator

    private lateinit var animElevation: ValueAnimator

    private fun openPanelAnimations() {
        animRlNameAndAddressContainer.start()
        animElevation.start()
    }

    private fun closePanelAnimations() {
        animRlNameAndAddressContainer.reverse()
        animElevation.reverse()
    }

    private fun interpolatedAnimations(percent: Float) {
        // Движемся вверх
        if (percent >= slidingLayout.anchorPoint) {
            // Нормализуем значения
            val newElevation = (percent - slidingLayout.anchorPoint) / (1 - slidingLayout.anchorPoint) * 16 * density
            rlSlidingAddressList.elevation = newElevation
        }

        // Движемся вниз
        if (percent <= slidingLayout.anchorPoint) {
            val layoutParams = rlNameAndAddressContainer.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.marginStart = ((1 - percent) * 20 * density).toInt()
            layoutParams.marginEnd = ((1 - percent) * 20 * density).toInt()
            rlNameAndAddressContainer.layoutParams = layoutParams
            rlNameAndAddressContainer.elevation = (1 - percent) * 16 * density

            val llNameLayoutParams = llName.layoutParams

            // Нормализуем значения
            llNameLayoutParams.height = ((slidingLayout.anchorPoint - percent) / (slidingLayout.anchorPoint) * mEtNameHeight).toInt()
            llName.layoutParams = llNameLayoutParams
        }
    }

    private fun updateAddressesPanelHeight() {
        slidingLayout.anchorPoint =  1 - llWarehouseAddress.bottom.toFloat() / slidingLayout.height.toFloat()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) = launchUI {
        super.onActivityCreated(savedInstanceState)

        if(tryPrefetch) {
            dp.warehouses.getAsync(warehouseId, DataProviderGetMode.PREFER_CACHE)
            tryPrefetch = false
        }

        val warehouse = dp.warehouses.get(warehouseId, DataProviderGetMode.DIRTY).entry

        density = activity!!.resources.displayMetrics.density

        etNameField.setText(warehouse.name)
        etAddressField.setText(warehouse.rawAddress)

        etNameField.addOnFocusChangeListener(object: View.OnFocusChangeListener {
            override fun onFocusChange(view: View?, focused: Boolean) {
                updateAddressesPanelHeight()
            }
        })

        val vto = llName.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                llName.viewTreeObserver.removeOnGlobalLayoutListener(this)
                mEtNameHeight = llName.measuredHeight
                animRlNameAndAddressContainer = ValueAnimator.ofInt((rlNameAndAddressContainer.layoutParams as ViewGroup.MarginLayoutParams).marginStart, 0)
                animRlNameAndAddressContainer.addUpdateListener { valueAnimator ->
                    val value = valueAnimator.animatedValue as Int
                    val layoutParams = rlNameAndAddressContainer.layoutParams as ViewGroup.MarginLayoutParams
                    layoutParams.marginStart = value
                    layoutParams.marginEnd = value
                    rlNameAndAddressContainer.layoutParams = layoutParams
                }
                animRlNameAndAddressContainer.duration = 200L
                animElevation = ValueAnimator.ofFloat(rlNameAndAddressContainer.elevation, 0f)
                animElevation.addUpdateListener { valueAnimator ->
                    val value = valueAnimator.animatedValue as Float
                    rlNameAndAddressContainer.elevation = value
                }
                animElevation.duration = 200L
                updateAddressesPanelHeight()
            }
        })

        etAddressField.addOnFocusChangeListener(object: View.OnFocusChangeListener {
            override fun onFocusChange(view: View?, focused: Boolean) {
                if (focused) {
                    ivDeleteTextIcon.visibility = View.VISIBLE
                    if (slidingLayout.panelState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                        toggleWarehouseNameField(false)
                    }
                    if (slidingLayout.panelState != SlidingUpPanelLayout.PanelState.ANCHORED) {
                        slidingLayout.panelState = SlidingUpPanelLayout.PanelState.ANCHORED
                    }
                } else {
                    ivDeleteTextIcon.visibility = View.GONE
                    updateAddressesPanelHeight()
                }
            }
        })

        ivDeleteTextIcon.setOnClickListener {
            etAddressField.text = null
        }

        slidingLayout.addPanelSlideListener(object: SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelStateChanged(panel: View?, previousState: SlidingUpPanelLayout.PanelState?, newState: SlidingUpPanelLayout.PanelState?) {
                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    etAddressField.clearFocus()
                    hideKeyboard()
                }
            }

            override fun onPanelSlide(panel: View?, slideOffset: Float) {
                interpolatedAnimations(slideOffset)
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
                val addressList = mainActivity.mMapsAdapter.getAddressList(text.toString())
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
        val rootView = inflater.inflate(R.layout.fragment_edit_warehouse, container, false)
        val mapFragment = SupportMapFragment()
        childFragmentManager.beginTransaction().add(R.id.fGoogleMap, mapFragment).commit()
        mapFragment.getMapAsync {
            it.setPadding(0, rlNameAndAddressContainer.height, 0, 0)
            mainActivity.mMapsAdapter.googleMap = it
            mainActivity.mMapsAdapter.moveCameraToPosition(LatLng(55.753774, 37.620998), false)
        }
        return rootView
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
