package com.kvteam.deliverytracker.core.common

import android.content.pm.PackageManager
import android.graphics.*
import android.util.Log
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.DirectionsApi
import com.google.maps.model.TravelMode
import com.kvteam.deliverytracker.core.models.Geoposition
import kotlinx.coroutines.experimental.async
import com.google.maps.GeoApiContext
import org.joda.time.DateTime
import com.google.maps.model.DirectionsResult
import com.google.maps.android.PolyUtil
import com.google.maps.model.DirectionsLeg
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule
import android.graphics.Paint.Align
import com.basgeekball.awesomevalidation.helper.SpanHelper.setColor
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.drawable.Drawable
import android.support.v4.graphics.drawable.RoundedBitmapDrawable
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import java.lang.Float.max
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import android.view.animation.CycleInterpolator
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Handler


data class GoogleMapAddress(
        val placeId: String?,
        val primaryText: CharSequence,
        val secondaryText: CharSequence,
        var geoposition: Geoposition?,
        var viewPort: LatLngBounds?
)

fun LatLng.toGeoposition(): Geoposition {
    return Geoposition(this.longitude, this.latitude)
}

fun com.google.maps.model.LatLng.toGeoposition(): Geoposition {
    return Geoposition(this.lng, this.lat)
}

data class GoogleMapRouteResults(
        val decodedPath: Array<ArrayList<LatLng>>,
        val route: Array<DirectionsLeg>,
        val bounds: LatLngBounds
)

class MapsAdapter(private val googleApiClient: GoogleApiClient) {
    private val topLeftBound = LatLng(55.013424, 39.549786)
    private val bottomRightBound = LatLng(56.253291, 36.355584)
    private val typeFilter = AutocompleteFilter.Builder()
            .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
            .build()
    var googleMap: GoogleMap? = null

    val density = googleApiClient.context?.resources?.displayMetrics?.density!!

    private val mapsApiKey =
            googleApiClient.context.packageManager.getApplicationInfo(
                    googleApiClient.context.packageName,
                    PackageManager.GET_META_DATA).metaData.getString("com.google.android.geo.API_KEY")

    private val getGeoContext = GeoApiContext.Builder()
            .apiKey(mapsApiKey)
            .build()

    private val LN2 = 0.6931471805599453
    private val WORLD_PX_HEIGHT = 256
    private val WORLD_PX_WIDTH = 256
    private val ZOOM_MAX = 21

    fun getBoundsZoomLevel(bounds: LatLngBounds, mapWidthPx: Int, mapHeightPx: Int, density: Float): Float {
        val ne = bounds.northeast
        val sw = bounds.southwest

        val latFraction = (latRad(ne.latitude) - latRad(sw.latitude)) / Math.PI

        val lngDiff = ne.longitude - sw.longitude
        val lngFraction = (if (lngDiff < 0) lngDiff + 360 else lngDiff) / 360

        val latZoom = zoom(mapHeightPx, WORLD_PX_HEIGHT * density, latFraction)
        val lngZoom = zoom(mapWidthPx, WORLD_PX_WIDTH * density, lngFraction)

        val result = Math.min(latZoom.toInt(), lngZoom.toInt())
        return Math.min(result, ZOOM_MAX).toFloat()
    }

    private fun latRad(lat: Double): Double {
        val sin = Math.sin(lat * Math.PI / 180)
        val radX2 = Math.log((1 + sin) / (1 - sin)) / 2
        return Math.max(Math.min(radX2, Math.PI), -Math.PI) / 2
    }

    private fun zoom(mapPx: Int, worldPx: Float, fraction: Double): Double {
        return Math.floor(Math.log(mapPx.toDouble() / worldPx.toDouble() / fraction) / LN2)
    }

    private fun drawUserMarker(picture: Drawable): Bitmap {
        val circle = Paint(ANTI_ALIAS_FLAG)
        circle.color = Color.BLACK

        val image = Bitmap.createBitmap((40 * density).toInt(), (60 * density).toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(image)
        canvas.drawCircle((20 * density), (20 * density), (20 * density), circle)
        val paint = Paint(ANTI_ALIAS_FLAG)
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL

        val a = Point(0, (20 * density).toInt())
        val b = Point((20 * density).toInt(), (60 * density).toInt())
        val c = Point((40 * density).toInt(), (20 * density).toInt())

        val path = Path()
        path.moveTo(a.x.toFloat(), a.y.toFloat())
        path.lineTo(b.x.toFloat(), b.y.toFloat())
        path.lineTo(c.x.toFloat(), c.y.toFloat())
        path.close()

        canvas.drawPath(path, paint)

        picture.setBounds((4 * density).toInt(), (4 * density).toInt(), (36 * density).toInt(), (36 * density).toInt())
        picture.draw(canvas)
        return image
    }

    private fun textAsBitmap(text: String, textSize: Float, textColor: Int, backgroundColor: Int): Bitmap {
        val paint = Paint(ANTI_ALIAS_FLAG)
        paint.textSize = textSize
        paint.color = textColor
        paint.textAlign = Paint.Align.CENTER
        val baseline = -paint.ascent()
        val width = (paint.measureText(text) + 0.5f)
        val height = (baseline + paint.descent() + 0.5f)
        val size = Math.max(width, height)
        val image = Bitmap.createBitmap(size.toInt(), size.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(image)
        val circle = Paint(ANTI_ALIAS_FLAG)
        circle.color = backgroundColor
        canvas.drawCircle(size / 2, size / 2, size / 2, circle)
        canvas.drawText(text, size / 2, baseline, paint)
        return image
    }

    suspend fun getRoute(route: ArrayList<com.google.maps.model.LatLng>,
                         departureTime: DateTime?): GoogleMapRouteResults = async {

        val time = if (departureTime?.isAfterNow == true) {
            departureTime
        } else {
            DateTime.now()
        }

        val waypointsArray = route.slice(1 until (route.size - 1)).toTypedArray()

        val results = DirectionsApi.newRequest(getGeoContext)
                .mode(TravelMode.DRIVING)
                .origin(route[0])
                .waypoints(*waypointsArray)
                .destination(route.last())
                .departureTime(time)
                .await()

        val resultRoute = results.routes[0]
        val decodedPathPerStep = Array(resultRoute.legs.size) { i ->
            val t = ArrayList<LatLng>()
            resultRoute.legs[i].steps.forEach {
                t.addAll(PolyUtil.decode(it.polyline.encodedPath))
            }
            t
        }
        val latLngBounds = LatLngBounds(
                resultRoute.bounds.southwest.toGeoposition().toLtnLng(),
                resultRoute.bounds.northeast.toGeoposition().toLtnLng())
        return@async GoogleMapRouteResults(decodedPathPerStep, resultRoute.legs, latLngBounds)
    }.await()

    private fun getEndLocationTitle(results: DirectionsResult): String {
        return "Time :" + results.routes[0].legs[0].duration.humanReadable + " Distance :" + results.routes[0].legs[0].distance.humanReadable
    }

    fun addPolyline(decodedPath: Iterable<LatLng>, color: Int? = Color.BLACK) {
        googleMap!!.addPolyline(PolylineOptions()
                .addAll(decodedPath)
                .color(color!!)
        )
    }

    fun moveCameraToPosition(position: LatLng, animated: Boolean) {
        if (animated) {
            val cameraPosition = CameraPosition.Builder().target(position).zoom(18f).build()
            googleMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        } else {
            googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 12.0f))
        }
    }

    private fun moveCameraToViewport(viewPort: LatLngBounds, animated: Boolean) {
        if (animated) {
            googleMap!!.animateCamera(CameraUpdateFactory.newLatLngBounds(viewPort, 0))
        } else {
            googleMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(viewPort, 0))
        }
    }

    fun moveToLatLngBounds(bounds: LatLngBounds) {
        googleMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50))
    }

    fun addCustomMarker(text: String, position: LatLng, backgroundColor: Int? = Color.WHITE) {
        val taskDrawable = BitmapDrawable(googleApiClient.context.resources, textAsBitmap(text, 70f, Color.BLACK, backgroundColor!!))
        addUserMarker(taskDrawable, position)
    }

    private fun scaleBitmap(bitmap: Bitmap, scaleFactor: Float): Bitmap {
        val sizeX = Math.round(bitmap.width * scaleFactor)
        val sizeY = Math.round(bitmap.height * scaleFactor)
        return Bitmap.createScaledBitmap(bitmap, sizeX, sizeY, false)
    }

    private fun pulseMarker(markerIcon: Bitmap, marker: Marker, onePulseDuration: Long) {
        val handler = Handler()
        val startTime = System.currentTimeMillis()

        val interpolator = CycleInterpolator(1f)
        handler.post(object : Runnable {
            override fun run() {
                val elapsed = System.currentTimeMillis() - startTime
                val t = interpolator.getInterpolation(elapsed.toFloat() / onePulseDuration)
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(scaleBitmap(markerIcon, 1f + 0.05f * t)))
                handler.postDelayed(this, 16)
            }
        })
    }

    fun addUserMarker(image: Drawable, position: LatLng, animated: Boolean? = false) {
        val iconBitmap = drawUserMarker(image)
        val marker = googleMap!!.addMarker(MarkerOptions()
                .position(position)
                .icon(BitmapDescriptorFactory.fromBitmap(iconBitmap))
                .zIndex(1f)
        )
        if (animated!!) {
            pulseMarker(iconBitmap, marker, 1000)
        }
    }

    fun setMarker(position: LatLng, viewPort: LatLngBounds?, animated: Boolean) {
        if (viewPort != null) {
            moveCameraToViewport(viewPort, animated)
        } else {
            moveCameraToPosition(position, animated)
        }
        googleMap!!.addMarker(MarkerOptions()
                .position(position))
    }

    private suspend fun getAddressesForPlaces(googleMapAddresses: List<GoogleMapAddress>): List<GoogleMapAddress> = async {
        val addresses = Places.GeoDataApi.getPlaceById(
                googleApiClient,
                *googleMapAddresses.map { it.placeId }.toTypedArray()
        ).await()

        addresses.forEachIndexed { index, address ->
            googleMapAddresses[index].viewPort = address.viewport
            googleMapAddresses[index].geoposition = address.latLng.toGeoposition()
        }

        return@async googleMapAddresses
    }.await()

    suspend fun getAddressList(search: String): List<GoogleMapAddress> = async {
        val list = Places.GeoDataApi.getAutocompletePredictions(
                googleApiClient,
                search,
                LatLngBounds(topLeftBound, bottomRightBound),
                null
        )
                .await()
                .map {
                    GoogleMapAddress(
                            it.placeId,
                            it.getPrimaryText(null),
                            it.getSecondaryText(null),
                            null,
                            null)
                }

        return@async getAddressesForPlaces(list)
    }.await()
}