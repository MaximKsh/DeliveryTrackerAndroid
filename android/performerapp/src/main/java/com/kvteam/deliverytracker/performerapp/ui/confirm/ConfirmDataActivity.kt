package com.kvteam.deliverytracker.performerapp.ui.confirm

import android.content.Intent
import android.os.Bundle
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.core.session.SETTINGS_CONTEXT
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerActivity
import com.kvteam.deliverytracker.performerapp.R
import com.kvteam.deliverytracker.performerapp.ui.main.MainActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_confirm_data.*
import javax.inject.Inject

class ConfirmDataActivity : DeliveryTrackerActivity() {
    private val surnameKey = "surname"
    private val nameKey = "name"
    private val phoneNumberKey = "phoneNumber"

    @Inject
    lateinit var session: ISession

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_data)

        if(savedInstanceState == null) {
            etConfirmSurname.setText(session.surname ?: EMPTY_STRING)
            etConfirmName.setText(session.name ?: EMPTY_STRING)
            etConfirmPhoneNumber.setText(session.phoneNumber ?: EMPTY_STRING)
        } else {
            etConfirmSurname.setText(savedInstanceState.getString(surnameKey, EMPTY_STRING))
            etConfirmName.setText(savedInstanceState.getString(nameKey, EMPTY_STRING))
            etConfirmPhoneNumber.setText(savedInstanceState.getString(phoneNumberKey, EMPTY_STRING))
        }
        bttnConfirm.setOnClickListener { onSaveClicked() }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.apply {
            putString(surnameKey, etConfirmSurname.text.toString())
            putString(nameKey, etConfirmName.text.toString())
            putString(phoneNumberKey, etConfirmPhoneNumber.text.toString())
        }
    }

    private fun onSaveClicked() {
        val settingsContext = intent.getBooleanExtra(SETTINGS_CONTEXT, false)

        invokeAsync({
            val userInfo = User()
            userInfo.surname = etConfirmSurname.text.toString()
            userInfo.name = etConfirmName.text.toString()
            userInfo.phoneNumber = etConfirmPhoneNumber.text.toString()
            session.editUserInfo(userInfo)
        }, {
            if(it.success) {
                val intent = Intent(this@ConfirmDataActivity, MainActivity::class.java)
                intent.putExtra(SETTINGS_CONTEXT, settingsContext)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            } else {
            }
        })
    }
}
