package com.kvteam.deliverytracker.core.common

import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.kvteam.deliverytracker.core.models.ClientAddress
import com.kvteam.deliverytracker.core.models.Geoposition
import kotlinx.coroutines.experimental.async
import android.R.attr.y
import android.R.attr.x

data class GoogleMapAddress(
        val placeId: String?,
        val primaryText: CharSequence,
        val secondaryText: CharSequence,
        var address: ClientAddress?,
        var viewPort: LatLngBounds?
)

fun LatLng.toGeoposition(): Geoposition {
    return Geoposition(this.longitude, this.latitude)
}

class MapsAdapter (private val googleApiClient: GoogleApiClient) {
    private val topLeftBound = LatLng(55.013424, 39.549786)
    private val bottomRightBound = LatLng(56.253291, 36.355584)
    private val typeFilter = AutocompleteFilter.Builder()
            .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
            .build()
    lateinit var googleMap: GoogleMap

    fun moveCameraToPosition(position: LatLng, animated: Boolean) {
        if (animated) {
            val cameraPosition = CameraPosition.Builder().target(position).zoom(18f).build()
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        } else {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 12.0f))
        }
    }

    private fun moveCameraToViewport(viewPort: LatLngBounds, animated: Boolean) {
        if (animated) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(viewPort, 0))
        } else {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(viewPort, 0))
        }
    }

    fun setMarker(position: LatLng, viewPort: LatLngBounds?, animated: Boolean) {
        if (viewPort != null) {
            moveCameraToViewport(viewPort, animated)
        } else {
            moveCameraToPosition(position, animated)
        }
        googleMap.addMarker(MarkerOptions()
                .position(position))
    }

    private suspend fun getAddressesForPlaces(googleMapAddresses: List<GoogleMapAddress>): List<GoogleMapAddress> = async {
        val addresses = Places.GeoDataApi.getPlaceById(
                googleApiClient,
                *googleMapAddresses.map{ it.placeId }.toTypedArray()
        ).await()

        addresses.forEachIndexed { index, address ->
            googleMapAddresses[index].viewPort = address.viewport
            googleMapAddresses[index].address = ClientAddress(address.address.toString(), address.latLng.toGeoposition())
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