package com.kvteam.deliverytracker.core.ui.errorhandling

import android.app.Activity
import android.view.View
import android.view.Window
import com.kvteam.deliverytracker.core.webservice.NetworkResult
import com.kvteam.deliverytracker.core.webservice.viewmodels.ResponseBase

val defaultErrorHandler = BaseErrorHandler()

fun <T : ResponseBase> handleResultErrors(
        activity: Activity?,
        result: NetworkResult<T>,
        handler: IErrorHandler? = null) : Boolean {
    val rootView = activity?.window?.decorView?.rootView
    return rootView != null && handleResultErrors(rootView, result, handler)
}

fun <T : ResponseBase> handleResultErrors(
        window: Window?,
        result: NetworkResult<T>,
        handler: IErrorHandler? = null) : Boolean {
    val rootView = window?.decorView?.rootView
    return rootView != null && handleResultErrors(rootView, result, handler)
}

fun <T : ResponseBase> handleResultErrors(
        rootView: View,
        result: NetworkResult<T>,
        handler: IErrorHandler? = null) : Boolean {
    if(result.success) return false
    val actualHandler = handler ?: defaultErrorHandler
    actualHandler.handleErrors(rootView, result.errors)
    return true
}