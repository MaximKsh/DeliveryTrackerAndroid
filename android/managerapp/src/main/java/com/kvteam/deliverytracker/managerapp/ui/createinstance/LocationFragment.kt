package com.kvteam.deliverytracker.managerapp.ui.createinstance

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.managerapp.R

class LocationFragment : DeliveryTrackerFragment() {

    private var googleMap: GoogleMap? = null

    private var locationManager : LocationManager? = null

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location?) {
            if (googleMap != null) {
                if (location != null) {
                    val city = LatLng(location.latitude, location.longitude)
                    val cameraPosition = CameraPosition.Builder().target(city).zoom(12f).build()
                    googleMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                } else {
                    Log.i("LOCATION_INFO", "Unknown location")
                }
            }
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment

        mapFragment.getMapAsync { mMap ->
            googleMap = mMap
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView = inflater.inflate(R.layout.fragment_location, container, false)

        if (ActivityCompat.checkSelfPermission(this.activity!!, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            locationManager = this.activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            val bestProvider = (locationManager as LocationManager).getBestProvider(Criteria(), false)

            (locationManager as LocationManager).requestLocationUpdates(bestProvider, 300, 0f, this.locationListener)
        }

        return rootView
    }
}