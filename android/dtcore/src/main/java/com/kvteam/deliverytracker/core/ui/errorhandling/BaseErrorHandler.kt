package com.kvteam.deliverytracker.core.ui.errorhandling

import android.app.AlertDialog
import android.view.View
import com.kvteam.deliverytracker.core.models.IError

class BaseErrorHandler: IErrorHandler {
    override fun handleErrors(rootView: View, error: List<IError>) {
        val err = error.first()
        AlertDialog.Builder(rootView.context)
                .setMessage(err.code)
                .show()
    }
}