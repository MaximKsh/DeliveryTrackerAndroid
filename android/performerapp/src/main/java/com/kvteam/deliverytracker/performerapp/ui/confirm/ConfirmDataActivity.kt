package com.kvteam.deliverytracker.performerapp.ui.confirm

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.models.UserModel
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
            etConfirmSurname.setText(session.surname ?: "")
            etConfirmName.setText(session.name ?: "")
            etConfirmPhoneNumber.setText(session.phoneNumber ?: "")
        } else {
            etConfirmSurname.setText(savedInstanceState.getString(surnameKey, ""))
            etConfirmName.setText(savedInstanceState.getString(nameKey, ""))
            etConfirmPhoneNumber.setText(savedInstanceState.getString(phoneNumberKey, ""))
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
        val userInfo = UserModel(
                surname = etConfirmSurname.text.toString(),
                name = etConfirmName.text.toString(),
                phoneNumber = etConfirmPhoneNumber.text.toString())

        invokeAsync({
            session.updateUserInfo(userInfo)
        }, {
            if(it) {
                if(settingsContext) {
                    val intent = Intent(
                            this@ConfirmDataActivity,
                            MainActivity::class.java)
                    startActivity(intent)
                }
                finish()
            } else {
                Toast
                        .makeText(
                                this@ConfirmDataActivity,
                                "LOCME ERROR",
                                Toast.LENGTH_LONG)
                        .show()
            }
        })
    }
}
