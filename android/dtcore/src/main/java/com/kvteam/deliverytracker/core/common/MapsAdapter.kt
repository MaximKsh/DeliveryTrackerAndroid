package com.kvteam.deliverytracker.core.common

import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.kvteam.deliverytracker.core.models.ClientAddress
import com.kvteam.deliverytracker.core.models.Geoposition
import kotlinx.coroutines.experimental.async

fun LatLng.toGeoposition(): Geoposition {
    return Geoposition(this.longitude, this.latitude)
}

class MapsAdapter (private val googleApiClient: GoogleApiClient) {
    private val topLeftBound = LatLng(55.013424, 39.549786)
    private val bottomRightBound = LatLng(56.253291, 36.355584)
    private val typeFilter = AutocompleteFilter.Builder()
            .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
            .build()

    private suspend fun getAddressesForPlaces(vararg placeIds: String): List<ClientAddress> = async {
        val addresses = Places.GeoDataApi.getPlaceById(googleApiClient, *placeIds).await()
        return@async addresses.map{
            ClientAddress(it.address.toString(), it.latLng.toGeoposition())
        }
    }.await()

    suspend fun getAddressList(search: String): List<ClientAddress> = async {
        val list = Places.GeoDataApi.getAutocompletePredictions(
                googleApiClient,
                search,
                LatLngBounds(topLeftBound, bottomRightBound),
                null
        )
                .await()
                .map { it.placeId!! }
                .toTypedArray()

        val resp = getAddressesForPlaces(*list)

        return@async resp
    }.await()
}