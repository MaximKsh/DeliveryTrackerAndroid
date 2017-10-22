package com.kvteam.deliverytracker.performerapp

import android.os.Bundle
import com.kvteam.deliverytracker.core.DeliveryTrackerActivity
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.models.InvitationModel
import com.kvteam.deliverytracker.core.models.UserModel
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.core.webservice.IWebservice
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : DeliveryTrackerActivity() {
    override val checkHasAccountOnResume
            get() = true

    @Inject
    lateinit var webservice: IWebservice

    @Inject
    lateinit var session: ISession

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        exitButton.setOnClickListener ({ _ ->
            invokeAsync({
                session.logout()
            }, {
                finish()
            })
        })

        loadButton.setOnClickListener { _ ->
            invokeAsync({
                webservice.post<InvitationModel>(
                        "/api/instance/invite_manager",
                        null,
                        InvitationModel::class.java,
                        true)
            }, {
                if(it.statusCode == 200) {
                    val res = it.responseEntity!!
                    textView2.text = res.invitationCode
                } else {
                    textView2.text = ":("
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()

        invokeAsync({
            webservice.get<UserModel>("/api/session/check", UserModel::class.java, true)
        }, {
            if(it.statusCode == 200) {
                textView.text = it.responseEntity?.surname ?: "surname is null"
            } else {
                textView.text = "bad error"
            }
        })
    }
}
