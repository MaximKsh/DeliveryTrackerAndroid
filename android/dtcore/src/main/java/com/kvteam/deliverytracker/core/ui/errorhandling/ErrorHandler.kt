package com.kvteam.deliverytracker.core.ui.errorhandling

import android.app.AlertDialog
import android.support.design.widget.Snackbar
import com.kvteam.deliverytracker.core.R
import com.kvteam.deliverytracker.core.common.ErrorListResult
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.dataprovider.base.DataProviderGetOrigin
import com.kvteam.deliverytracker.core.dataprovider.base.DataProviderGetResult
import com.kvteam.deliverytracker.core.dataprovider.base.DataProviderViewResult
import com.kvteam.deliverytracker.core.models.IError
import com.kvteam.deliverytracker.core.models.ModelBase
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

    override fun handleNoInternetWarn(origin: DataProviderGetOrigin): Boolean {
        if(origin != DataProviderGetOrigin.WEB) {
            Snackbar
                    .make(ctx.rootView, lm.getString(R.string.Core_NoInternet), Snackbar.LENGTH_LONG)
                    .show()
            return true
        }
        return false
    }

    override fun <T : ModelBase> handleNoInternetWarn(result: DataProviderGetResult<T>): Boolean {
        return handleNoInternetWarn(result.origin)
    }

    override fun handleNoInternetWarn(result: DataProviderViewResult): Boolean {
        return handleNoInternetWarn(result.origin)
    }

    private fun handleListInternal(errorListResult: ErrorListResult) {
        val text = errorListResult.errors.joinToString(separator = "\n", transform = { errorToString(it)})
        val defaultText = lm.getString(R.string.Core_UnknownError)
        val actualText = if(text.isNotBlank()) text else defaultText
        AlertDialog.Builder(activity)
                .setMessage(actualText)
                .setCancelable(true)
                .show()
    }

    private fun  <T : ResponseBase> handleNetworkInternal(networkResult: NetworkResult<T>) {
        if (!networkResult.fetched) {
            Snackbar
                    .make(ctx.rootView, lm.getString(R.string.Core_NoInternet), Snackbar.LENGTH_LONG)
                    .show()
            return
        }

        if (networkResult.statusCode == 403) {
            AlertDialog.Builder(activity)
                    .setMessage(lm.getString(R.string.Core_InvalidCredentials))
                    .setCancelable(true)
                    .setPositiveButton(lm.getString(R.string.Core_OK), {_, _ ->  })
                    .show()
            return
        }

        if (networkResult.statusCode == 404) {
            AlertDialog.Builder(activity)
                    .setMessage(lm.getString(R.string.Core_PageNotFound))
                    .setCancelable(true)
                    .setPositiveButton(lm.getString(R.string.Core_OK), {_, _ ->  })
                    .show()
            return
        }

        handleListInternal(networkResult)
    }

    private fun handleRawNetworkInternal(networkResult: RawNetworkResult) {
        if (!networkResult.fetched) {
            Snackbar
                    .make(ctx.rootView, lm.getString(R.string.Core_NoInternet), Snackbar.LENGTH_LONG)
                    .show()
            return
        }

        if (networkResult.statusCode == 403) {
            AlertDialog.Builder(activity)
                    .setMessage(lm.getString(R.string.Core_InvalidCredentials))
                    .setCancelable(true)
                    .setPositiveButton(lm.getString(R.string.Core_OK), {_, _ ->  })
                    .show()
            return
        }

        if (networkResult.statusCode == 404) {
            AlertDialog.Builder(activity)
                    .setMessage(lm.getString(R.string.Core_PageNotFound))
                    .setCancelable(true)
                    .setPositiveButton(lm.getString(R.string.Core_OK), {_, _ ->  })
                    .show()
            return
        }

        handleListInternal(networkResult)
    }

    private fun errorToString(error: IError) : String {
        val localizedMessage = lm.getString(error.message)

        val builder = StringBuilder()
        for (param in error.info) {
            builder.append("${param.key} = ${param.value} ")
        }
        return if (error.info.isNotEmpty()) {
            "$localizedMessage\n ($builder)"
        } else {
            localizedMessage
        }
    }
}