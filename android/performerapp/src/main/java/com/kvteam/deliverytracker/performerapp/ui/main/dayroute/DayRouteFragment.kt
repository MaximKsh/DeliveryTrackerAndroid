package com.kvteam.deliverytracker.performerapp.ui.main.dayroute

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.MapsAdapter
import com.kvteam.deliverytracker.core.dataprovider.base.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.base.DataProviderGetMode
import com.kvteam.deliverytracker.core.models.Geoposition
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController
import com.kvteam.deliverytracker.performerapp.R
import dagger.android.support.AndroidSupportInjection
import java.util.*
import javax.inject.Inject

class DayRouteFragment : DeliveryTrackerFragment() {

    @Inject
    lateinit var mapsAdapter: MapsAdapter

    @Inject
    lateinit var dp: DataProvider

    private fun setGoogleMap(taskId: UUID) {
        val task = dp.taskInfos.get(taskId, DataProviderGetMode.FORCE_CACHE).entry
        val warehouse = dp.warehouses.get(task.warehouseId as UUID, DataProviderGetMode.FORCE_CACHE).entry
        val clientAddress = dp.clientAddresses.get(
                task.clientAddressId as UUID,
                task.clientId as UUID,
                DataProviderGetMode.FORCE_CACHE
        )

        if (clientAddress.geoposition == null) {
            clientAddress.geoposition = Geoposition(37.625065, 55.759832)
        }

        if (warehouse.geoposition != null && clientAddress.geoposition != null) {
            val routeResults = mapsAdapter.getRoute(
                    warehouse.geoposition!!.toDirectionsLtnLng(),
                    clientAddress.geoposition!!.toDirectionsLtnLng(),
                    task.deliveryFrom)

            val latLngBoundsBuilder = LatLngBounds.builder()
            routeResults.decodedPath.forEach { coordinate -> latLngBoundsBuilder.include(coordinate) }

            mapsAdapter.googleMap!!.setOnMapLoadedCallback {
                mapsAdapter.drawRoute(routeResults.route, routeResults.decodedPath)
            }
        }
    }

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

    override fun onActivityCreated(savedInstanceState: Bundle?) = launchUI {
        super.onActivityCreated(savedInstanceState)
        val viewResult = dp.taskInfoViews.getViewResultAsync(
                "TaskViewGroup",
                "ActualTasksPerformerView",
                mode = DataProviderGetMode.PREFER_CACHE).viewResult

        val tasks = viewResult
                .map { dp.taskInfos.getAsync(it, DataProviderGetMode.PREFER_CACHE).entry }
                .toMutableList()

        tasks.forEach { task ->
            if (task.clientAddressId != null && task.warehouseId != null) {
                setGoogleMap(task.id!!)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_day_route, container, false)
        Handler().postDelayed({
            if (isAdded) {
                val mapFragment = SupportMapFragment()
                childFragmentManager.beginTransaction().add(R.id.fGoogleMap, mapFragment).commit()
                mapFragment.getMapAsync {
                    it.setPadding(0, 0, 0, 0)
                    mapsAdapter.googleMap = it
                    mapsAdapter.moveCameraToPosition(LatLng(55.753774, 37.620998), false)
                }
            }
        }, 250)
        return view
    }

}