package com.kvteam.deliverytracker.managerapp.ui.main.userslist

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.model.LatLng
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.common.MapsAdapter
import com.kvteam.deliverytracker.core.dataprovider.base.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.base.DataProviderGetMode
import com.kvteam.deliverytracker.core.models.Geoposition
import com.kvteam.deliverytracker.core.tasks.TaskState
import com.kvteam.deliverytracker.core.tasks.getTaskState
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.materialDefaultAvatar
import com.kvteam.deliverytracker.managerapp.R
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_user_map.*
import org.joda.time.DateTime
import java.util.*
import javax.inject.Inject

data class TaskStepperInfo(
        val taskId: UUID,
        val name: String,
        val description: String?,
        val predictedTime: DateTime,
        val latLng: com.google.android.gms.maps.model.LatLng,
        val address: String
)


class UserOnMapFragment: DeliveryTrackerFragment() {
    private val userIdKey = "userId"
    private var userId
        get() = arguments?.getSerializable(userIdKey)!! as UUID
        set(value) = arguments?.putSerializable(userIdKey, value)!!

    fun setUser(id: UUID) {
        this.userId = id
    }

    @Inject
    lateinit var mapsAdapter: MapsAdapter

    @Inject
    lateinit var dp: DataProvider

    private val density by lazy { activity?.resources?.displayMetrics?.density!! }

    private fun loadMapAndData() = launchUI {
        val userInfo = dp.users.getAsync(userId, DataProviderGetMode.FORCE_WEB).entry

        val viewResult = dp.taskInfoViews.getViewResultAsync(
                "AuxTaskViewGroup",
                "RouteView",
                mapOf("performer_id" to userId),
                mode = DataProviderGetMode.PREFER_WEB).viewResult

        val tasks = viewResult
                .map { dp.taskInfos.getAsync(it, DataProviderGetMode.PREFER_CACHE).entry }

        // Здесь и далее мы считаем что маршрутная вьюха вернула таски где есть адреса и склады
        if (tasks.isNotEmpty()) {
            val route = ArrayList<LatLng>()
            val taskStepperInfos = ArrayList<TaskStepperInfo>()
            val warehouse = dp.warehouses.get(tasks[0].warehouseId as UUID, DataProviderGetMode.FORCE_CACHE).entry

            val lastCompletedTaskIndex = tasks.indexOfLast {
                it.taskStateCaption == TaskState.Delivered.stateCaption
                        || it.taskStateCaption == TaskState.Complete.stateCaption
            }

            val userGeoposition = userInfo?.geoposition
            if (lastCompletedTaskIndex == -1
                    && userGeoposition != null) {
                route.add(userGeoposition.toDirectionsLtnLng())
            }

            val warehouseGeoposition = warehouse.geoposition
            if (warehouseGeoposition != null) {
                route.add(warehouseGeoposition.toDirectionsLtnLng())
            }

            tasks.forEachIndexed { index, task ->
                val clientAddress = dp.clientAddresses.get(
                        task.clientAddressId as UUID,
                        task.clientId as UUID,
                        DataProviderGetMode.FORCE_CACHE
                )
                val clientGeopos = clientAddress.geoposition
                if (clientGeopos != null) {
                    taskStepperInfos.add(TaskStepperInfo(
                            task.id!!,
                            task.taskNumber ?: EMPTY_STRING,
                            task.comment,
                            task.deliveryEta ?: DateTime.now(),
                            clientGeopos.toLtnLng(),
                            clientAddress.rawAddress ?: EMPTY_STRING
                    ))

                    route.add(clientGeopos.toDirectionsLtnLng())
                }



                // Вставляем курьера после последнего таска "Доставлено" или "Выполнено"
                if (index == lastCompletedTaskIndex
                        && userGeoposition != null) {
                    route.add(userGeoposition.toDirectionsLtnLng())
                }
            }

            // Пока ситаем запрос маршрута со времени первого таска
            val routeResults = mapsAdapter.getRoute(
                    route,
                    tasks[0].deliveryFrom)

            val zoom = mapsAdapter.getBoundsZoomLevel(
                    routeResults.bounds,
                    flMapContainer.width,
                    flMapContainer.height - 40,
                    density
            )

            val cameraPosition = CameraPosition
                    .builder()
                    .target(routeResults.bounds.center)
                    .zoom(zoom)
                    .build()

            val googleMapOptions = GoogleMapOptions()
                    .camera(cameraPosition)

            val mapFragment = SupportMapFragment.newInstance(googleMapOptions)

            childFragmentManager.beginTransaction().add(R.id.flMapContainer, mapFragment).commit()
            mapFragment.getMapAsync {
                it.setPadding(0, 0, 0, 0)
                mapsAdapter.googleMap = it
                mapsAdapter.googleMap!!.setOnMapLoadedCallback {
                    routeResults.decodedPath.forEachIndexed { index, path ->
                        val testColor = when {
                            index <= lastCompletedTaskIndex + 1 -> {
                                ContextCompat.getColor(dtActivity, R.color.colorGreen)
                            }
                            index == lastCompletedTaskIndex + 2 -> {
                                ContextCompat.getColor(dtActivity, R.color.colorBlack)
                            }
                            else -> {
                                ContextCompat.getColor(dtActivity, R.color.colorGray)
                            }
                        }

                        mapsAdapter.addPolyline(path, testColor)
                    }
                    // СКЛАД
                    val warehouseIcon = ContextCompat.getDrawable(dtActivity, R.drawable.warehouse_icon)!!
                    mapsAdapter.addUserMarker(warehouseIcon, (warehouse.geoposition as Geoposition).toLtnLng())

                    // ТАСКИ
                    tasks.forEachIndexed{ index, task ->
                        val markerColor = ContextCompat.getColor(dtActivity, task.getTaskState()!!.color)
                        mapsAdapter.addCustomMarker((index + 1).toString(), taskStepperInfos[index].latLng, markerColor)
                    }

                    // КУРЬЕР
                    if (userInfo!!.geoposition != null) {
                        val backgroundColor = ContextCompat.getColor(dtActivity, R.color.colorPrimary)
                        mapsAdapter.addUserMarker(materialDefaultAvatar(userInfo, backgroundColor), userInfo.geoposition!!.toLtnLng(), true)
                    }
                }
            }
        } else {
            val cameraPosition = CameraPosition
                    .builder()
                    .target(com.google.android.gms.maps.model.LatLng(55.753774, 37.620998))
                    .zoom(12.0f)
                    .build()

            val googleMapOptions = GoogleMapOptions()
                    .camera(cameraPosition)

            val mapFragment = SupportMapFragment.newInstance(googleMapOptions)

            childFragmentManager.beginTransaction().add(R.id.flMapContainer, mapFragment).commit()
            mapFragment.getMapAsync {
                it.setPadding(0, 0, 0, 0)
                mapsAdapter.googleMap = it
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loadMapAndData()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_user_map, container, false) as ViewGroup
        return rootView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }
}