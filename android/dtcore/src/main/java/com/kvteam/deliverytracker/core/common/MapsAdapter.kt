package com.kvteam.deliverytracker.core.common

import android.content.pm.PackageManager
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

    private fun getEndLocationTitle(results: DirectionsResult): String {
        return "Time :" + results.routes[0].legs[0].duration.humanReadable + " Distance :" + results.routes[0].legs[0].distance.humanReadable
    }

    private fun addPolyline(results: DirectionsResult) {
        val decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.encodedPath)
        googleMap!!.addPolyline(PolylineOptions().addAll(decodedPath))
        val latLngBoundsBuilder = LatLngBounds.builder()
        decodedPath.forEach { coordinate -> latLngBoundsBuilder.include(coordinate) }
        (googleMap as GoogleMap).moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBoundsBuilder.build(), 0));
    }

    private fun addMarkersToMap(results: DirectionsResult) {
        googleMap!!.addMarker(MarkerOptions()
                .position(LatLng(results.routes[0].legs[0].startLocation.lat, results.routes[0].legs[0].startLocation.lng))
                .title(results.routes[0].legs[0].startAddress))

        googleMap!!.addMarker(MarkerOptions()
                                .position(LatLng(results.routes[0].legs[0].endLocation.lat, results.routes[0].legs[0].endLocation.lng))
                                .title(results.routes[0].legs[0].startAddress)
                                .snippet(getEndLocationTitle(results)))
    }

    fun buildRoute(origin: com.google.maps.model.LatLng,
                   destination: com.google.maps.model.LatLng,
                   departureTime: DateTime?) {

        val result = DirectionsApi.newRequest(getGeoContext)
                .mode(TravelMode.DRIVING)
                .origin(origin)
                .destination(destination)
                .departureTime(departureTime?: DateTime.now())
                .await()

        addMarkersToMap(result)
        addPolyline(result)
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