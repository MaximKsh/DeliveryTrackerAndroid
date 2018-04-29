package com.kvteam.deliverytracker.core.common

import android.content.pm.PackageManager
import android.util.Log
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.DirectionsApi
import com.google.maps.model.TravelMode
import com.kvteam.deliverytracker.core.models.Geoposition
import kotlinx.coroutines.experimental.async
import com.google.maps.GeoApiContext
import org.joda.time.DateTime
import com.google.maps.model.DirectionsResult
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import com.google.maps.model.DirectionsLeg
import java.util.*
import kotlin.concurrent.schedule


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

fun com.google.maps.model.LatLng.toGeoposition() : Geoposition {
    return Geoposition(this.lng, this.lat)
}

data class GoogleMapRouteResults(
        val decodedPath: Iterable<LatLng>,
        val route: DirectionsLeg
)

class MapsAdapter (private val googleApiClient: GoogleApiClient) {
    private val topLeftBound = LatLng(55.013424, 39.549786)
    private val bottomRightBound = LatLng(56.253291, 36.355584)
    private val typeFilter = AutocompleteFilter.Builder()
            .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
            .build()
    var googleMap: GoogleMap? = null

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

    fun getRoute(origin: com.google.maps.model.LatLng,
                                    destination: com.google.maps.model.LatLng,
                                    departureTime: DateTime?) : GoogleMapRouteResults {
        val results = DirectionsApi.newRequest(getGeoContext)
                .mode(TravelMode.DRIVING)
                .origin(origin)
                .destination(destination)
                .departureTime(departureTime?: DateTime.now())
                .await()

        val decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.encodedPath)
        return GoogleMapRouteResults(decodedPath, results.routes[0].legs[0])
    }

    private fun getEndLocationTitle(results: DirectionsResult): String {
        return "Time :" + results.routes[0].legs[0].duration.humanReadable + " Distance :" + results.routes[0].legs[0].distance.humanReadable
    }

    private fun addPolyline(decodedPath: Iterable<LatLng>) {
        googleMap!!.addPolyline(PolylineOptions().addAll(decodedPath))
    }

    private fun addMarkersToMap(route: DirectionsLeg) {
        googleMap!!.addMarker(MarkerOptions()
                .position(LatLng(route.startLocation.lat, route.startLocation.lng))
                .title(route.startAddress))

        googleMap!!.addMarker(MarkerOptions()
                                .position(LatLng(route.endLocation.lat, route.endLocation.lng))
                                .title(route.startAddress))
    }

    fun drawRoute(route: DirectionsLeg, decodedPath: Iterable<LatLng>) {
        addMarkersToMap(route)
        addPolyline(decodedPath)
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
                *googleMapAddresses.map{ it.placeId }.toTypedArray()
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
                .map { GoogleMapAddress(
                        it.placeId,
                        it.getPrimaryText(null),
                        it.getSecondaryText(null),
                        null,
                        null)
                }

        return@async getAddressesForPlaces(list)
    }.await()
}