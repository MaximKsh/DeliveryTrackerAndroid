package com.kvteam.deliverytracker.core.session

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.google.firebase.iid.FirebaseInstanceId
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.models.Device
import java.util.*


fun getDevice(userId: UUID, deviceType: String, context: Context): Device {
    val refreshedToken = FirebaseInstanceId.getInstance().token ?: EMPTY_STRING
    val device = Device()
    device.userId = userId
    device.type = deviceType
    device.version = Build.VERSION.SDK_INT.toString()
    device.language = Locale.getDefault().language
    device.firebaseId = refreshedToken
    try {
        val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        device.applicationType = pInfo.packageName
        device.applicationVersion = pInfo.versionCode.toString()
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return device
}
