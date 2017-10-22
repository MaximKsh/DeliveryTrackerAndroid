package com.kvteam.deliverytracker.performerapp

import android.os.Bundle
import com.kvteam.deliverytracker.core.DeliveryTrackerActivity
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.instance.IInstanceManager
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
    lateinit var instanceManager: IInstanceManager

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

        loadManagersButton.setOnClickListener { _ ->
            invokeAsync({
                instanceManager.getManagers()
            }, {
                if(it != null) {
                    textView2.text =  if(it.isEmpty()){
                        "empty"
                    } else {
                        it.map { p -> p.username }.reduce{ acc, p -> acc + ", " + p}
                    }
                } else {
                    textView2.text = ":("
                }
            })
        }

        loadPerformersButton.setOnClickListener { _ ->
            invokeAsync({
                instanceManager.getPerformers()
            }, {
                if(it != null) {
                    textView2.text =  if(it.isEmpty()){
                        "empty"
                    } else {
                        it.map { p -> p.username }.reduce{ acc, p -> acc + ", " + p}
                    }
                } else {
                    textView2.text = ":("
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()

        invokeAsync({
            session.surname
        }, {
            textView.text = it ?: "surname is null"
        })
    }
}
