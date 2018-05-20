package com.kvteam.deliverytracker.performerapp.ui.main.settings

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.widget.LinearLayout
import com.kvteam.deliverytracker.core.geoposition.GEOPOSITION_PERMISSION_REQUEST_CODE
import com.kvteam.deliverytracker.core.geoposition.isSendingGeoposition
import com.kvteam.deliverytracker.core.geoposition.startSendingGeoposition
import com.kvteam.deliverytracker.core.geoposition.stopSendingGeoposition
import com.kvteam.deliverytracker.core.ui.settings.BaseSettingsFragment
import com.kvteam.deliverytracker.core.webservice.IWebservice
import com.kvteam.deliverytracker.performerapp.R
import com.kvteam.deliverytracker.performerapp.ui.main.NavigationController
import kotlinx.android.synthetic.main.view_to_work.*
import javax.inject.Inject


class SettingsFragment : BaseSettingsFragment() {
    @Inject
    lateinit var navigationController: NavigationController

    @Inject
    lateinit var webservice: IWebservice

    override fun onEditSettingsClicked() {
        navigationController.navigateToEditSettings()
    }

    override fun onChangePasswordClicked() {
        navigationController.navigateToChangePassword()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val llSettingsExtension = view?.findViewById<LinearLayout>(R.id.llSettingsExtension)!!
        val view = layoutInflater.inflate(R.layout.view_to_work, llSettingsExtension, false)
        llSettingsExtension.addView(view)

        view.setOnClickListener {
            sToWork.toggle()
        }

        if (isSendingGeoposition(this.dtActivity) && !sToWork.isChecked) {
            sToWork.toggle()
        }

        sToWork.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && !isSendingGeoposition(this.dtActivity)) {
                if (checkPermissions()) {
                    startSendingGeoposition(this.dtActivity, webservice)
                }
            } else if (!isChecked && isSendingGeoposition(this.dtActivity)) {
                stopSendingGeoposition(this.dtActivity)
            }
        }
    }

    private fun checkPermissions(): Boolean {
        val checkCoarseResult = ContextCompat.checkSelfPermission(dtActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION)
        val checkFineResult = ContextCompat.checkSelfPermission(dtActivity, android.Manifest.permission.ACCESS_FINE_LOCATION)

        if (checkCoarseResult == PackageManager.PERMISSION_GRANTED
            || checkFineResult == PackageManager.PERMISSION_GRANTED) {
            return true
        }

        requestPermissions(
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                GEOPOSITION_PERMISSION_REQUEST_CODE)

        return false
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray) {
        if (requestCode == GEOPOSITION_PERMISSION_REQUEST_CODE) {
            if (grantResults.any { it == PackageManager.PERMISSION_GRANTED }) {
                startSendingGeoposition(this.dtActivity, webservice)
            } else {
                sToWork.toggle()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}