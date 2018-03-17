package com.kvteam.deliverytracker.core.ui.errorhandling

import android.view.View
import com.kvteam.deliverytracker.core.models.IError

interface IErrorHandler {
    fun handleErrors(rootView: View, error: List<IError>)
}