package com.kvteam.deliverytracker.core.ui.errorhandling

import android.app.AlertDialog
import android.support.design.widget.Snackbar
import com.kvteam.deliverytracker.core.common.ErrorListResult
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerActivity
import com.kvteam.deliverytracker.core.webservice.NetworkResult
import com.kvteam.deliverytracker.core.webservice.RawNetworkResult
import com.kvteam.deliverytracker.core.webservice.viewmodels.ResponseBase

class ErrorHandler(
        private val activity: DeliveryTrackerActivity,
        private val rootLayoutId: Int,
        private val lm: ILocalizationManager
) : IErrorHandler {

    private val ctx: ErrorHandlerContext by lazy {
        ErrorHandlerContext(
                activity,
                activity.findViewById(rootLayoutId),
                lm)
    }

    override fun handle(errorListResult: ErrorListResult,
                        customAction: ((ctx:ErrorHandlerContext) -> Boolean)?): Boolean {
        if(errorListResult.success) return false
        if (customAction != null) {
            return customAction(ctx)
        }
        handleListInternal(errorListResult)
        return true
    }

    override fun <T : ResponseBase> handle(networkResult: NetworkResult<T>,
                                           customAction: ((ctx:ErrorHandlerContext) -> Boolean)? ): Boolean {
        if(networkResult.success) return false
        if (customAction != null) {
            return customAction(ctx)
        }

        handleNetworkInternal(networkResult)
        return true
    }

    override fun handle(networkResult: RawNetworkResult,
                        customAction: ((ctx:ErrorHandlerContext) -> Boolean)?): Boolean {
        if(networkResult.success) return false
        if (customAction != null) {
            return customAction(ctx)
        }
        handleRawNetworkInternal(networkResult)
        return true
    }

    private fun handleListInternal(errorListResult: ErrorListResult) {

    }

    private fun  <T : ResponseBase> handleNetworkInternal(networkResult: NetworkResult<T>) {
        if (!networkResult.fetched) {
            Snackbar
                    .make(ctx.rootView, "No Internet", Snackbar.LENGTH_LONG)
                    .show()
            return
        }

        if (networkResult.statusCode == 403) {
            AlertDialog.Builder(activity)
                    .setMessage("Invalid credentials")
                    .setCancelable(true)
                    .setPositiveButton("OK", {_, _ ->  })
                    .show()
            return
        }

        val text = networkResult.errors.joinToString(separator = "\n", transform = { it.message })
        AlertDialog.Builder(activity)
                .setMessage(text)
                .setCancelable(true)
                .show()
    }

    private fun handleRawNetworkInternal(networkResult: RawNetworkResult) {

    }
}