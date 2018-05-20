package com.kvteam.deliverytracker.core.ui.tasks

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.DirectionsApi
import com.google.maps.model.TravelMode
import com.kvteam.deliverytracker.core.R
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.common.MapsAdapter
import com.kvteam.deliverytracker.core.dataprovider.base.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.base.DataProviderGetMode
import com.kvteam.deliverytracker.core.dataprovider.base.NetworkException
import com.kvteam.deliverytracker.core.models.Geoposition
import com.kvteam.deliverytracker.core.models.TaskInfo
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.errorhandling.IErrorHandler
import com.kvteam.deliverytracker.core.ui.materialDefaultAvatar
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController
import com.kvteam.deliverytracker.core.webservice.ITaskWebservice
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_base_task_details.*
import kotlinx.android.synthetic.main.task_product_item.view.*
import org.joda.time.DateTime
import java.util.*
import javax.inject.Inject
import com.google.maps.GeoApiContext
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.common.toGeoposition
import com.kvteam.deliverytracker.core.tasks.getTaskState
import org.joda.time.DateTimeZone
import java.io.InputStream
import java.net.URL


abstract class BaseTaskDetailsFragment : DeliveryTrackerFragment() {
    @Inject
    lateinit var taskWebservice: ITaskWebservice

    @Inject
    lateinit var eh: IErrorHandler

    @Inject
    lateinit var lm: ILocalizationManager

    @Inject
    lateinit var mapsAdapter: MapsAdapter

    @Inject
    lateinit var dp: DataProvider

    private val density by lazy { activity?.resources?.displayMetrics?.density!! }
    private val toggleIcon by lazy { ContextCompat.getDrawable(activity!!, R.drawable.ic_expand_more_black_24dp) }
    private val toggleIconResized by lazy {
        BitmapDrawable(
                activity!!.resources,
                Bitmap.createScaledBitmap(
                        (toggleIcon as BitmapDrawable).bitmap,
                        Math.round(30 * density),
                        Math.round(30 * density),
                        true)
        )
    }
    private val rotatedToggleIcon by lazy {
        BitmapDrawable(activity!!.resources, rotateBitmap(toggleIconResized.bitmap, 180f))
    }

    private val taskIdKey = "taskIdKey"
    protected var taskId
        get() = arguments?.getSerializable(taskIdKey)!! as UUID
        set(value) = arguments?.putSerializable(taskIdKey, value)!!


    private val transitionsCountKey = "transitionsCountKey"
    private var transitionsCount
        get() = arguments?.getInt(transitionsCountKey)!!
        set(value) = arguments?.putInt(transitionsCountKey, value)!!

    fun setTask(id: UUID) {
        this.taskId = id
    }

    protected abstract fun closeFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun configureToolbar(toolbar: ToolbarController) {
        super.configureToolbar(toolbar)
        toolbar.setToolbarTitle(lm.getString(R.string.Core_TaskDetails_Header))
    }

    private fun setGoogleMap() = launchUI {
        val skeletonMap = Skeleton.bind(vSkeletonMapLoader)
                .load(R.layout.layout_img_sketelon)
                .angle(0)
                .color(R.color.colorLightGray)
                .show()

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

            val route = arrayListOf<com.google.maps.model.LatLng>(
                    warehouse.geoposition!!.toDirectionsLtnLng(),
                    clientAddress.geoposition!!.toDirectionsLtnLng()
            )
            val routeResults = mapsAdapter.getRoute(
                    route,
                    task.deliveryFrom)

            val zoom = mapsAdapter.getBoundsZoomLevel(
                    routeResults.bounds,
                    flTaskLiteMap.width,
                    flTaskLiteMap.height,
                    density
            )

            val cameraPosition = CameraPosition
                    .builder()
                    .target(routeResults.bounds.center)
                    .zoom(zoom)
                    .build()

            val googleMapOptions = GoogleMapOptions()
                    .camera(cameraPosition)
                    .liteMode(true)

            val mapFragment = SupportMapFragment.newInstance(googleMapOptions)

            childFragmentManager.beginTransaction().add(R.id.flTaskLiteMap, mapFragment).commit()

            mapFragment.getMapAsync {
                mapsAdapter.googleMap = it
                mapsAdapter.googleMap!!.setOnMapClickListener {
                    val intent = Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?" +
                                    "saddr=${warehouse.geoposition!!.latitude},${warehouse.geoposition!!.longitude}" +
                                    "&daddr=${clientAddress.geoposition!!.latitude},${clientAddress.geoposition!!.longitude}"))
                    startActivity(intent)
                }
                mapsAdapter.googleMap!!.setOnMapLoadedCallback {
                    mapsAdapter.addPolyline(routeResults.decodedPath)
                    val warehouseIcon = ContextCompat.getDrawable(dtActivity, R.drawable.warehouse_icon)!!
                    mapsAdapter.addUserMarker(warehouseIcon, routeResults.route.startLocation.toGeoposition().toLtnLng())
                    val markerColor = ContextCompat.getColor(dtActivity, task.getTaskState()!!.color)
                    mapsAdapter.addCustomMarker("З", routeResults.route.endLocation.toGeoposition().toLtnLng(), markerColor)
                    skeletonMap.hide()
                }
            }
        }
    }

    private fun setStreetViewPicture() {
        val skeletonStreetView = Skeleton.bind(flStreetView)
                .load(R.layout.layout_img_sketelon)
                .angle(0)
                .color(R.color.colorLightGray)
                .show()

        val task = dp.taskInfos.get(taskId, DataProviderGetMode.FORCE_CACHE).entry
        val clientAddress = dp.clientAddresses.get(
                task.clientAddressId as UUID,
                task.clientId as UUID,
                DataProviderGetMode.FORCE_CACHE
        )

        if (clientAddress.geoposition == null) {
            clientAddress.geoposition = Geoposition(37.625065, 55.759832)
        }

        imageURL = "$imageURL${(clientAddress.geoposition as Geoposition).latitude},${(clientAddress.geoposition as Geoposition).longitude}"
        invokeAsync({
            BitmapFactory.decodeStream(URL(imageURL).content as InputStream)
        }, {
            flStreetView.setImageBitmap(it)
            skeletonStreetView.hide()
        })
    }

    private lateinit var imageURL: String

    override fun onActivityCreated(savedInstanceState: Bundle?) = launchUI {
        super.onActivityCreated(savedInstanceState)

        imageURL = "https://maps.googleapis.com/maps/api/streetview?size=${(300 * density).toInt()}x${(220 * density).toInt()}&fov=90&heading=235&pitch=10&location="

        val taskResult = try {
            dp.taskInfos.getAsync(taskId, DataProviderGetMode.FORCE_WEB)
        } catch (e: NetworkException) {
            eh.handle(e.result)
            return@launchUI
        }
        val task = taskResult.entry

        if (task.clientAddressId != null && task.warehouseId != null) {
            setGoogleMap()
            setStreetViewPicture()
        }

        tvTaskStatus.text = lm.getString(task.taskStateCaption!!)
        tvTaskNumber.text = task.taskNumber

        if (task.authorId != null) {
            val author = dp.users.get(task.authorId as UUID, DataProviderGetMode.FORCE_CACHE).entry
            tvAuthorName.text = author.name
            tvAuthorSurname.text = author.surname
        }

        if (task.performerId != null) {
            val user = dp.users.get(task.performerId!!, DataProviderGetMode.FORCE_CACHE).entry
            val materialAvatarDefault = materialDefaultAvatar(user)
            ivPerformerImage.setImageDrawable(materialAvatarDefault)
            tvPerformerName.text = user.name
            tvPerformerSurname.text = user.surname
        } else {
            val materialAvatarDefault = materialDefaultAvatar(null)
            ivPerformerImage.setImageDrawable(materialAvatarDefault)
        }

        if (task.receipt != null) {
            tvTaskReceiptAt.text = (task.receipt as DateTime).toString("dd/MM HH:mm")
        }

        if (task.warehouseId != null) {
            val warehouse = dp.warehouses.get(task.warehouseId as UUID, DataProviderGetMode.FORCE_CACHE).entry
            tvWarehouseName.text = warehouse.name
        }

        if (task.comment != null) {
            tvTaskDescription.text = task.comment
        }

        if (task.deliveryFrom != null) {
            tvDeliveryDate.text = "${(task.deliveryFrom as DateTime).toString("dd.MM")}, " +
                    "${(task.deliveryFrom as DateTime).toString("HH:mm")}-" +
                    "${(task.deliveryTo as DateTime).toString("HH:mm")}"
        }

        if (task.clientId != null) {
            val client = dp.clients.get(task.clientId as UUID, DataProviderGetMode.FORCE_CACHE).entry
            if (task.clientAddressId != null) {
                val ca = dp.clientAddresses.get(task.clientAddressId!!, task.clientId!!, DataProviderGetMode.FORCE_CACHE)
                tvClientAddress.text = ca.rawAddress
            }
            if (client.name != null) {
                tvClientName.text = client.name
            }
            if (client.surname != null) {
                tvClientSurname.text = client.surname
            }
            if (client.phoneNumber != null) {
                tvClientPhoneNumber.text = client.phoneNumber
            }
        }

        val taskProducts = dp.taskProducts.getByParent(task.id!!, DataProviderGetMode.FORCE_CACHE)
        if (taskProducts.isNotEmpty()) {
            tvNoProducts.visibility = View.GONE
            taskProducts.forEach { productInfo ->
                val product = dp.products.get(productInfo.productId!!, DataProviderGetMode.FORCE_CACHE).entry
                val inflatedProductView = layoutInflater.inflate(R.layout.task_product_item, llProductsContainer, false)
                inflatedProductView.tvProductQuantity.text = productInfo.quantity.toString()
                inflatedProductView.tvName.text = product.name
                inflatedProductView.tvCost.text = activity!!.resources.getString(com.kvteam.deliverytracker.core.R.string.Core_Product_Cost_Template, product.cost.toString())
                inflatedProductView.tvVendorCode.text = product.vendorCode.toString()
                llProductsContainer.addView(inflatedProductView)
            }

            rlTotalCost.visibility = View.VISIBLE
            tvTotalCost.text = activity!!.resources.getString(
                    com.kvteam.deliverytracker.core.R.string.Core_Product_Cost_Template,
                    task.cost.toString())
        }

        val created = task.created?.withZoneRetainFields(DateTimeZone.UTC)?.withZone(DateTime.now().zone)
        if (created != null) {
            tvCreateDate.text = "${created.toString("dd.MM")}, ${created.toString("HH:mm")}"

        }

        llTransitionButtons.removeAllViews()
        transitionsCount = task.taskStateTransitions.size
        for (transition in task.taskStateTransitions) {
            val view = SwitchStateView(this@BaseTaskDetailsFragment.context!!)
            view.button.text = lm.getString(transition.buttonCaption ?: EMPTY_STRING)
            view.button.setOnClickListener { onChangeStateClick(transition.id!!) }
            llTransitionButtons.addView(view)
        }

        ivTaskStateExpandIcon.setImageDrawable(toggleIconResized)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_base_task_details, container, false)
        return rootView
    }

    private fun onChangeStateClick(transitionId: UUID) = launchUI {
        val transitResult = taskWebservice.changeStateAsync(taskId, transitionId)
        dp.taskInfoViews.invalidate()
        if (eh.handle(transitResult)) {
            return@launchUI
        }
        closeFragment()
    }

    private fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }
}