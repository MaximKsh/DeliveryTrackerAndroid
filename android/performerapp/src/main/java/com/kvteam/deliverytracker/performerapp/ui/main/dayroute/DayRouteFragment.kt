package com.kvteam.deliverytracker.performerapp.ui.main.dayroute

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.common.MapsAdapter
import com.kvteam.deliverytracker.core.dataprovider.base.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.base.DataProviderGetMode
import com.kvteam.deliverytracker.core.models.Geoposition
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.core.tasks.TaskState
import com.kvteam.deliverytracker.core.tasks.getTaskState
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.materialDefaultAvatar
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController
import com.kvteam.deliverytracker.performerapp.R
import com.kvteam.deliverytracker.performerapp.ui.main.MainActivity
import com.kvteam.deliverytracker.performerapp.ui.main.NavigationController
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_day_route.*
import kotlinx.android.synthetic.main.fragment_day_route.view.*
import org.joda.time.DateTime
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


data class TaskStepperInfo(
        val taskId: UUID,
        val name: String,
        val description: String?,
        val predictedTime: DateTime,
        val latLng: LatLng,
        val address: String
)

class DayRouteFragment : DeliveryTrackerFragment() {
    @Inject
    lateinit var session: ISession

    @Inject
    lateinit var mapsAdapter: MapsAdapter

    @Inject
    lateinit var navigationController: NavigationController

    @Inject
    lateinit var dp: DataProvider

    private val density by lazy { activity?.resources?.displayMetrics?.density!! }

    private var originalPaddingTop: Int = 0

    override fun configureToolbar(toolbar: ToolbarController) {
        super.configureToolbar(toolbar)
        toolbar.setToolbarTitle(EMPTY_STRING)
    }

    override fun onResume() {
        super.onResume()

        dtActivity.toolbarController.setTransparent(true)
        val x = (activity!!.mainContainer.layoutParams as ViewGroup.MarginLayoutParams)
        x.bottomMargin = 0
        activity!!.mainContainer.layoutParams = x

        val navigation = (activity as MainActivity).navigation
        val layoutParams = (navigation.layoutParams as ViewGroup.MarginLayoutParams)
        layoutParams.leftMargin = navigation.width
        navigation.layoutParams = layoutParams
        try {

        } catch (e: Exception) {
            Log.e("DayRouteFragment", e.toString())
        }
    }

    override fun onStop() {
        dtActivity.toolbarController.setTransparent(false)
        val x = (activity!!.mainContainer.layoutParams as ViewGroup.MarginLayoutParams)
        x.bottomMargin = (58 * density).toInt()
        activity!!.mainContainer.layoutParams = x

        val navigation = (activity as MainActivity).navigation
        val layoutParams = (navigation.layoutParams as ViewGroup.MarginLayoutParams)
        layoutParams.leftMargin = 0
        navigation.layoutParams = layoutParams
        super.onStop()
    }

    private fun toggleBottomNavigation(): ValueAnimator? {
        val navigation = (activity as? MainActivity)?.navigation ?: return null
        val width = navigation.width
        val anim = ValueAnimator.ofInt(0, width)
        anim.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            val slidingList = rlSlidingRouteTasksList
            val layoutParams = (navigation.layoutParams as? ViewGroup.MarginLayoutParams)
            val layoutParams2 = (slidingList?.layoutParams as? ViewGroup.MarginLayoutParams)
            // Проверяем, что во время анимации не был уничтожен view
            if (layoutParams == null
                || layoutParams2 == null) {
                return@addUpdateListener
            }

            layoutParams.leftMargin = value
            navigation.layoutParams = layoutParams

            layoutParams2.leftMargin = value - width
            slidingList.layoutParams = layoutParams2
        }
        anim.duration = 200L
        return anim
    }

    private fun interpolatePanelPadding(offset: Float) {
        val normalizedOffset = ((offset - 0.1) / (1 - 0.1)).toFloat()
        rlSlidingRouteTasksList.setPadding(
                rlSlidingRouteTasksList.paddingLeft,
                (originalPaddingTop + (normalizedOffset * dtActivity.statusBarHeight)).toInt(),
                rlSlidingRouteTasksList.paddingRight,
                rlSlidingRouteTasksList.paddingBottom
        )
        ivBackToNavigation.pivotX = ivBackToNavigation.width / 2f
        ivBackToNavigation.pivotY = ivBackToNavigation.height / 2f
        ivBackToNavigation.rotation = normalizedOffset * 90
    }

    private fun goToMarker(latLng: LatLng) {
        slidingLayout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        mapsAdapter.moveCameraToPosition(latLng, true)
    }

    private fun goToTasksDetails(taskId: UUID) {
        navigationController.navigateToTaskDetails(taskId)
    }

    private fun loadMapAndData() = launchUI {
        val userInfo = session.refreshUserInfoAsync().entity!!.user
        val viewResult = dp.taskInfoViews.getViewResultAsync(
                "AuxTaskViewGroup",
                "RouteView",
                mode = DataProviderGetMode.PREFER_WEB).viewResult

        val tasks = viewResult
                .map { dp.taskInfos.getAsync(it, DataProviderGetMode.PREFER_CACHE).entry }

        // Здесь и далее мы считаем что маршрутная вьюха вернула таски где есть адреса и склады
        if (tasks.isNotEmpty()) {
            val route = ArrayList<com.google.maps.model.LatLng>()
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

            stepperTaskList.setStepperAdapter(TasksStepperContainer(
                    ::goToTasksDetails,
                    ::goToMarker,
                    taskStepperInfos,
                    activity as FragmentActivity
            ).getAdapter())

            // Пока ситаем запрос маршрута со времени первого таска
            val routeResults = mapsAdapter.getRoute(
                    route,
                    tasks[0].deliveryFrom)

            val zoom = mapsAdapter.getBoundsZoomLevel(
                    routeResults.bounds,
                    fGoogleMap.width,
                    fGoogleMap.height,
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

            childFragmentManager.beginTransaction().add(R.id.fGoogleMap, mapFragment).commit()
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
                                ContextCompat.getColor(dtActivity, R.color.colorYellow)
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

                    aviRoutes.hide()
                    if (slidingLayout.panelState != SlidingUpPanelLayout.PanelState.EXPANDED) {
                        slidingLayout.panelState = SlidingUpPanelLayout.PanelState.ANCHORED
                    }
                    slidingLayout.isTouchEnabled = true
                }
            }
        } else {
            val cameraPosition = CameraPosition
                    .builder()
                    .target(LatLng(55.753774, 37.620998))
                    .zoom(12.0f)
                    .build()

            val googleMapOptions = GoogleMapOptions()
                    .camera(cameraPosition)

            val mapFragment = SupportMapFragment.newInstance(googleMapOptions)

            childFragmentManager.beginTransaction().add(R.id.fGoogleMap, mapFragment).commit()
            mapFragment.getMapAsync {
                it.setPadding(0, 0, 0, 0)
                mapsAdapter.googleMap = it
                mapsAdapter.googleMap!!.setOnMapLoadedCallback {
                    aviRoutes.hide()
                    if (slidingLayout.panelState != SlidingUpPanelLayout.PanelState.EXPANDED) {
                        slidingLayout.panelState = SlidingUpPanelLayout.PanelState.ANCHORED
                    }
                    slidingLayout.isTouchEnabled = true
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) = launchUI {
        super.onActivityCreated(savedInstanceState)

        slidingLayout.anchorPoint = 0.1f

        stepperTaskList.setStepperAdapter(TasksStepperContainer(
                ::goToTasksDetails,
                ::goToMarker,
                listOf(),
                activity as FragmentActivity
        ).getAdapter())

        val anim = toggleBottomNavigation()
        anim?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                animation.removeAllListeners()
                val sl = slidingLayout ?: return
                val ar = aviRoutes ?: return
                val routeTask = rlSlidingRouteTasksList ?: return

                if (sl.panelState != SlidingUpPanelLayout.PanelState.EXPANDED) {
                    sl.isTouchEnabled = false
                    ar.smoothToShow()
                } else {
                    ar.visibility = View.GONE
                    routeTask.setPadding(
                            routeTask.paddingLeft,
                            originalPaddingTop + dtActivity.statusBarHeight,
                            routeTask.paddingRight,
                            routeTask.paddingBottom)
                }
                loadMapAndData()
            }
        })

        anim?.start()

        ivBackToNavigation.setOnClickListener {
            canBeCollapsed = true
            slidingLayout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
            anim?.reverse()
        }
    }

    private var canBeCollapsed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_day_route, container, false)
        val lp = view.rlSlidingRouteTasksList.layoutParams as ViewGroup.MarginLayoutParams
        lp.marginStart = -view.width
        view.rlSlidingRouteTasksList.layoutParams = lp
        originalPaddingTop = view.rlSlidingRouteTasksList.paddingTop
        view.slidingLayout.addPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View?, slideOffset: Float) {
                interpolatePanelPadding(slideOffset)
            }

            override fun onPanelStateChanged(panel: View?, previousState: SlidingUpPanelLayout.PanelState?, newState: SlidingUpPanelLayout.PanelState?) {
                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED && !canBeCollapsed) {
                    view.slidingLayout.panelState = SlidingUpPanelLayout.PanelState.ANCHORED
                }
            }

        })
        return view
    }

}