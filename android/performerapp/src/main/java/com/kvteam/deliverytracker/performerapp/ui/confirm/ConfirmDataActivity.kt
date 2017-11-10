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
            this.etConfirmSurname.setText(this.session.surname ?: "")
            this.etConfirmName.setText(this.session.name ?: "")
            this.etConfirmPhoneNumber.setText(this.session.phoneNumber ?: "")
        }
        this.bttnConfirm.setOnClickListener { onSaveClicked() }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if(outState == null){
            return
        }

        outState.putString(surnameKey, this.etConfirmSurname.text.toString())
        outState.putString(nameKey, this.etConfirmName.text.toString())
        outState.putString(phoneNumberKey, this.etConfirmPhoneNumber.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if(savedInstanceState == null){
            return
        }

        this.etConfirmSurname.setText(savedInstanceState.getString(surnameKey, ""))
        this.etConfirmName.setText(savedInstanceState.getString(nameKey, ""))
        this.etConfirmPhoneNumber.setText(savedInstanceState.getString(phoneNumberKey, ""))
    }

    private fun onSaveClicked() {
        val settingsContext = this.intent.getBooleanExtra(SETTINGS_CONTEXT, false)
        val userInfo = UserModel(
                surname = this.etConfirmSurname.text.toString(),
                name = this.etConfirmName.text.toString(),
                phoneNumber = this.etConfirmPhoneNumber.text.toString())

        invokeAsync({
            session.updateUserInfo(userInfo)
        }, {
            if(it) {
                if(settingsContext) {
                    val intent = Intent(this@ConfirmDataActivity, MainActivity::class.java)
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
