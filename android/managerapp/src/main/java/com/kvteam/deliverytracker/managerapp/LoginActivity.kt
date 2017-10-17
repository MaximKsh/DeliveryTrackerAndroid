package com.kvteam.deliverytracker.managerapp

import android.os.Bundle
import com.kvteam.deliverytracker.core.DeliveryTrackerActivity
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.instance.IInstanceManager
import com.kvteam.deliverytracker.core.webservice.*
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

import kotlinx.coroutines.experimental.*

class LoginActivity : DeliveryTrackerActivity() {

    @Inject
    lateinit var instanceManager: IInstanceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val button = this.button

        this.button.setOnClickListener({ _ ->
            invokeAsync({
                instanceManager.create("1", "2", "123Bb!")
            }, {
                if(it != null) {
                    button.text = it.userName
                }
            })


         })

        this.button2.setOnClickListener({_ ->
            toggleState()
        })

    }

    private fun onGettingResponse(resp: UserInfoModel) {

    }

    private fun onError(resp: ErrorListModel) {

    }

    private fun toggleState() {
        val transaction = supportFragmentManager.beginTransaction()
        val top = supportFragmentManager.findFragmentById(R.id.container)
        val bottom = supportFragmentManager.findFragmentById(R.id.container2)

        if (top != null && top.isAdded) {
            transaction.remove(top)
            transaction.add(R.id.container2, SecondFragment())
        } else if (bottom != null && bottom.isAdded) {
            transaction.remove(bottom)
            transaction.add(R.id.container, FirstFragment())
        } else {
            transaction.add(R.id.container, FirstFragment())
        }

        transaction.addToBackStack(null)
        transaction.commit()
    }
}
