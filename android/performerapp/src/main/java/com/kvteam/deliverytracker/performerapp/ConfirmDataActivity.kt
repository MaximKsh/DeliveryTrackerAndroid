package com.kvteam.deliverytracker.performerapp

import android.content.Intent
import android.os.Bundle
import com.kvteam.deliverytracker.core.DeliveryTrackerActivity
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.models.UserModel
import com.kvteam.deliverytracker.core.session.ISession
import kotlinx.android.synthetic.main.activity_confirm_data.*
import javax.inject.Inject

class ConfirmDataActivity : DeliveryTrackerActivity() {
    @Inject
    lateinit var session: ISession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_data)

        surnameEditText.setText(session.surname ?: "")
        nameEditText.setText(session.name ?: "")
        phoneEditText.setText(session.phoneNumber ?: "")

        confirmButton.setOnClickListener{ _ ->
            val userInfo = UserModel(
                    surname = surnameEditText.text.toString(),
                    name = nameEditText.text.toString(),
                    phoneNumber = phoneEditText.text.toString())
            val ctx = this

            invokeAsync({
                session.updateUserInfo(userInfo)
            }, {
                if(it) {
                    val intent = Intent(ctx, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Произоша ошибка
                }
            })
        }
    }
}
