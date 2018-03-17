package com.kvteam.deliverytracker.core.ui.errorhandling

import android.app.Activity
import android.view.View
import com.kvteam.deliverytracker.core.common.ILocalizationManager

data class ErrorHandlerContext(
        val activity: Activity,
        val rootView: View,
        val lm: ILocalizationManager)