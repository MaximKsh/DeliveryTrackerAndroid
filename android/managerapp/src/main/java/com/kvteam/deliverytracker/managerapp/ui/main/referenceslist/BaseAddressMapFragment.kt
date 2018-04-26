package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.warehouses

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Service
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.common.MapsAdapter
import com.kvteam.deliverytracker.core.models.Geoposition
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.addOnFocusChangeListener
import com.kvteam.deliverytracker.core.ui.errorhandling.IErrorHandler
import com.kvteam.deliverytracker.core.ui.removeOnFocusChangeListener
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController
import com.kvteam.deliverytracker.core.webservice.IReferenceWebservice
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.addresslist.AddressListItem
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import dagger.android.support.AndroidSupportInjection
import eu.davidea.flexibleadapter.FlexibleAdapter
import kotlinx.android.synthetic.main.fragment_base_edit_address.*
import kotlinx.android.synthetic.main.fragment_base_edit_address.view.*
import javax.inject.Inject


open class BaseAddressMapFragment : DeliveryTrackerFragment(), FlexibleAdapter.OnItemClickListener {

    override val useSoftKeyboardFeatures = false

    @Inject
    lateinit var navigationController: NavigationController

    @Inject
    lateinit var mapsAdapter: MapsAdapter

    @Inject
    lateinit var referenceWebservice: IReferenceWebservice

    protected lateinit var validation: AwesomeValidation

    @Inject
    lateinit var lm: ILocalizationManager

    @Inject
    lateinit var eh: IErrorHandler

    private val addressFieldFocusListener = object : View.OnFocusChangeListener {
        override fun onFocusChange(view: View?, focused: Boolean) {
            if (focused) {
                if (slidingLayout.panelState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    etAddressField.clearFocus()
                    toggleAnimations(false)
                }
                ivDeleteTextIcon.visibility = View.VISIBLE
                if (slidingLayout.panelState != SlidingUpPanelLayout.PanelState.ANCHORED) {
                    slidingLayout.panelState = SlidingUpPanelLayout.PanelState.ANCHORED
                }
            } else {
                ivDeleteTextIcon.visibility = View.GONE
//                updateAddressesPanelHeight()
            }
        }
    }

    private val addressFieldTextChangedListener = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {}

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) = launchUI {
            if (slidingLayout.panelState != SlidingUpPanelLayout.PanelState.ANCHORED) {
                slidingLayout.panelState = SlidingUpPanelLayout.PanelState.ANCHORED
            }
            val addressList = mapsAdapter.getAddressList(text.toString())
            mAddressList = addressList.map { AddressListItem(it) }
            mAddressListAdapter.updateDataSet(mAddressList)
        }
    }

    protected var mAddressList = listOf<AddressListItem>()

    private val mAddressListAdapter = FlexibleAdapter(mAddressList)

    override fun onCreate(savedInstanceState: Bundle?) {
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        AndroidSupportInjection.inject(this)
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onItemClick(view: View?, position: Int): Boolean {
        val googleMapAddress = mAddressList[position].googleMapAddress
        etAddressField.setText(googleMapAddress.primaryText)
        etAddressField.clearFocus()
        hideKeyboard()
        toggleAnimations(true)
        slidingLayout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        mapsAdapter.setMarker(
                googleMapAddress.geoposition!!.toLtnLng(),
                googleMapAddress.viewPort,
                true)
        return false
    }

    private fun hideKeyboard() {
        val im = activity!!.getSystemService(Service.INPUT_METHOD_SERVICE) as InputMethodManager
        im.hideSoftInputFromWindow(activity!!.currentFocus.windowToken, 0)
    }

    private fun openKeyboard() {
        val im = activity!!.getSystemService(Service.INPUT_METHOD_SERVICE) as InputMethodManager
        im.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun configureToolbar(toolbar: ToolbarController) {
        super.configureToolbar(toolbar)
        toolbar.setToolbarTitle("")
    }

    protected lateinit var anchorElement: View

    fun toggleAnimations(open: Boolean) {
        if (!open) {
            val endPercent = 1 - (anchorElement.bottom.toFloat() + originalTopMarginOfRl - originalTopPaddingOfRl) / slidingLayout.height
            slidingLayout.anchorPoint = 0.01f
            autoAnimateInterpolation(200, slidingLayout.anchorPoint, endPercent)
        } else {
            autoAnimateInterpolation(200, slidingLayout.anchorPoint, 0.01f,true)
        }
    }

    private fun autoAnimateInterpolation(duration: Long, startPercent: Float, endPercent: Float, reverse: Boolean = false) {
        val anim = ValueAnimator.ofFloat(startPercent, endPercent)
        val anchor = if (reverse) startPercent else endPercent

        anim.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Float
            slidingLayout.anchorPoint = value
            interpolatedAnimations(value, anchor)
        }

        anim.addListener(object: AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                if (!reverse) {
                    etAddressField.requestFocus()
                    openKeyboard()
                }
                slidingLayout.anchorPoint = endPercent
                interpolatedAnimations(endPercent, anchor)
            }
        })

        anim.duration = duration
        anim.start()
    }

    private var originalTopMarginOfRl = 0

    private var originalTopPaddingOfRl = 0

    private var originalElevationOfRl = 0f

    private var originalSideMarginOfRl = 0

    protected fun normalizePercent(percent: Float, endPercent: Float): Float {
        return if (percent >= endPercent) {
            (percent - endPercent) / (1 - endPercent)
        } else {
            (endPercent - percent) / endPercent
        }
    }

    open fun interpolatedAnimations(percent: Float, endPercent: Float) {
        // Нормализуем значения
        val normalizedPercent = normalizePercent(percent, endPercent)

        // Движемся вверх
        if (percent >= endPercent) {
            val newElevation = normalizedPercent * originalElevationOfRl
            rlSlidingAddressList.elevation = newElevation
            rlSlidingAddressList.setPadding(0, (normalizedPercent * dtActivity.statusBarHeight).toInt(), 0,0)
        }

        // Движемся вниз
        if (percent <= endPercent) {
            val layoutParams = flContainer.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.marginStart = (normalizedPercent * originalSideMarginOfRl).toInt()
            layoutParams.marginEnd = (normalizedPercent * originalSideMarginOfRl).toInt()
            layoutParams.topMargin = (normalizedPercent * originalTopMarginOfRl).toInt()
            flContainer.layoutParams = layoutParams
            flContainer.setPadding(0, ((1 - normalizedPercent) * originalTopMarginOfRl + normalizedPercent * originalTopPaddingOfRl).toInt(), 0, 0)

            flContainer.elevation = normalizedPercent * originalElevationOfRl
        }
    }

    protected fun updateAddressesPanelHeight() {
        val topMargin = (flContainer.layoutParams as ViewGroup.MarginLayoutParams).topMargin
        slidingLayout.anchorPoint = 1 - (llWarehouseAddress.bottom.toFloat() + topMargin) / slidingLayout.height
    }

    protected fun setAddressFieldText(text: String?) {
        etAddressField.removeTextChangedListener(addressFieldTextChangedListener)
        etAddressField.setText(text)
        etAddressField.addTextChangedListener(addressFieldTextChangedListener)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) = launchUI {
        super.onActivityCreated(savedInstanceState)

        val vto = flContainer.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                flContainer.viewTreeObserver.removeOnGlobalLayoutListener(this)
                originalElevationOfRl = flContainer.elevation
                originalTopPaddingOfRl = flContainer.paddingTop
                originalTopMarginOfRl = (flContainer.layoutParams as ViewGroup.MarginLayoutParams).topMargin
                originalSideMarginOfRl = (flContainer.layoutParams as ViewGroup.MarginLayoutParams).marginStart
            }
        })

        anchorElement = llWarehouseAddress

        etAddressField.addOnFocusChangeListener(addressFieldFocusListener)

        ivDeleteTextIcon.setOnClickListener {
            etAddressField.text = null
        }

        slidingLayout.addPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelStateChanged(panel: View?, previousState: SlidingUpPanelLayout.PanelState?, newState: SlidingUpPanelLayout.PanelState?) {
                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    etAddressField.clearFocus()
                    hideKeyboard()
                }
            }

            override fun onPanelSlide(panel: View?, slideOffset: Float) {
                interpolatedAnimations(slideOffset, slidingLayout.anchorPoint)
            }
        })


        mAddressListAdapter.addListener(this@BaseAddressMapFragment)
        rvAddressList.layoutManager = LinearLayoutManager(activity)
        rvAddressList.adapter = mAddressListAdapter

        etAddressField.addTextChangedListener(addressFieldTextChangedListener)

        validation = AwesomeValidation(ValidationStyle.UNDERLABEL)
        validation.addValidation(
                etAddressField, RegexTemplate.NOT_EMPTY, getString(com.kvteam.deliverytracker.core.R.string.Core_WarehouseAddressValidationError))
        validation.setContext(this@BaseAddressMapFragment.dtActivity)
    }

    protected var startGeoposition: Geoposition? = null

    open val editContainerLayoutID: Int? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_base_edit_address, container, false)

        if (editContainerLayoutID != null) {
            inflater.inflate(editContainerLayoutID as Int, rootView.rlInflateContainer)
        }

        (rootView.flContainer.layoutParams as ViewGroup.MarginLayoutParams).topMargin =
                (rootView.flContainer.layoutParams as ViewGroup.MarginLayoutParams).topMargin + dtActivity.statusBarHeight

        Handler().postDelayed({
            if (isAdded) {
                val mapFragment = SupportMapFragment()
                childFragmentManager.beginTransaction().add(R.id.fGoogleMap, mapFragment).commit()
                mapFragment.getMapAsync {
                    it.setPadding(0, flContainer.height, 0, 0)
                    mapsAdapter.googleMap = it
                    if (startGeoposition != null) {
                        mapsAdapter.setMarker(
                                (startGeoposition as Geoposition).toLtnLng(),
                                null,
                                false
                        )
                    } else {
                        mapsAdapter.moveCameraToPosition(LatLng(55.753774, 37.620998), false)
                    }
                }
            }
        }, 250)
        return rootView
    }

    override fun onResume() {
        super.onResume()
        dtActivity.toolbarController.setTransparent(true)
    }

    override fun onStop() {
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        hideKeyboard()
        dtActivity.toolbarController.setTransparent(false)
        super.onStop()
    }
}
