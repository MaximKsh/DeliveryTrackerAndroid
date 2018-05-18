package com.kvteam.deliverytracker.performerapp.ui.main.dayroute

import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.MapsAdapter
import com.kvteam.deliverytracker.core.common.toGeoposition
import com.kvteam.deliverytracker.core.dataprovider.base.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.base.DataProviderGetMode
import com.kvteam.deliverytracker.core.models.Geoposition
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController
import com.kvteam.deliverytracker.performerapp.R
import com.kvteam.deliverytracker.performerapp.ui.main.MainActivity
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_day_route.*
import kotlinx.android.synthetic.main.fragment_day_route.view.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class DayRouteFragment : DeliveryTrackerFragment() {

    @Inject
    lateinit var mapsAdapter: MapsAdapter

    @Inject
    lateinit var dp: DataProvider

    private val density by lazy { activity?.resources?.displayMetrics?.density!! }

    override fun configureToolbar(toolbar: ToolbarController) {
        super.configureToolbar(toolbar)
        toolbar.setToolbarTitle("")
    }

    override fun onResume() {
        super.onResume()
        dtActivity.toolbarController.setTransparent(true)
    }

    override fun onStop() {
        dtActivity.toolbarController.setTransparent(false)
        super.onStop()
    }

    private fun toggleBottomNavigation () {
        val navigation = (activity as MainActivity).navigation
        val width = navigation.width
        val anim = ValueAnimator.ofInt(0, width)
        anim.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            val layoutParams = (navigation.layoutParams as ViewGroup.MarginLayoutParams)
            layoutParams.leftMargin = value
            navigation.layoutParams = layoutParams

            val layoutParams2 = (rlSlidingRouteTasksList.layoutParams as ViewGroup.MarginLayoutParams)
            layoutParams2.leftMargin = value - width
            rlSlidingRouteTasksList.layoutParams = layoutParams2
        }
        anim.duration = 200L
        anim.start()

        ivBackToNavigation.setOnClickListener { anim.reverse()  }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) = launchUI {
        super.onActivityCreated(savedInstanceState)

        toggleBottomNavigation()

        val viewResult = dp.taskInfoViews.getViewResultAsync(
                "TaskViewGroup",
                "ActualTasksPerformerView",
                mode = DataProviderGetMode.PREFER_CACHE).viewResult

        val tasks = viewResult
                .map { dp.taskInfos.getAsync(it, DataProviderGetMode.PREFER_CACHE).entry }
                .toMutableList()

        // Здесь и далее мы считаем что маршрутная вьэха вернула таски где есть адреса и склады
        if (tasks.size != 0) {
            val route = ArrayList<com.google.maps.model.LatLng>()
            val warehouse = dp.warehouses.get(tasks[0].warehouseId as UUID, DataProviderGetMode.FORCE_CACHE).entry
            route.add(warehouse.geoposition!!.toDirectionsLtnLng())
            tasks.forEach { task ->
                val clientAddress = dp.clientAddresses.get(
                        task.clientAddressId as UUID,
                        task.clientId as UUID,
                        DataProviderGetMode.FORCE_CACHE
                )
                route.add(clientAddress.geoposition!!.toDirectionsLtnLng())
            }
            // Пока ситаем запрос маршрута со времени первого таска
            val routeResults = mapsAdapter.getRoute(
                    route,
                    tasks[0].deliveryFrom)

            val latLngBoundsBuilder = LatLngBounds.builder()
            routeResults.decodedPath.forEach { coordinate -> latLngBoundsBuilder.include(coordinate) }

            Handler().postDelayed({
                if (isAdded) {
                    val zoom = mapsAdapter.getBoundsZoomLevel(
                            latLngBoundsBuilder.build(),
                            fGoogleMap.width,
                            fGoogleMap.height,
                            density
                    )

                    val cameraPosition = CameraPosition
                            .builder()
                            .target(latLngBoundsBuilder.build().center)
                            .zoom(zoom)
                            .build()

                    val googleMapOptions = GoogleMapOptions()
                            .camera(cameraPosition)

                    val mapFragment = SupportMapFragment.newInstance(googleMapOptions)

                    childFragmentManager.beginTransaction().add(R.id.fGoogleMap, mapFragment).commit()
                    mapFragment.getMapAsync {
                        it.setPadding(0, 0, 0, 0)
                        mapsAdapter.googleMap = it
                        mapsAdapter.googleMap!!.setOnMapLoadedCallback {
                            mapsAdapter.addPolyline(routeResults.decodedPath)
                            route.forEachIndexed { index, latLng ->
                                var text = index.toString()
                                if (index == 0) {
                                    text = "С"
                                }
                                mapsAdapter.addCustomMarker(text, latLng.toGeoposition().toLtnLng())
                            }
                        }
                    }
                }
            }, 250)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_day_route, container, false)
        val lp = view.rlSlidingRouteTasksList.layoutParams as ViewGroup.MarginLayoutParams
        lp.marginStart = -view.width
        view.rlSlidingRouteTasksList.layoutParams = lp
        return view
    }

}