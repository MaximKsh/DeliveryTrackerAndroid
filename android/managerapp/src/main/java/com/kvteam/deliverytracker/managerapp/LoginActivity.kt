package com.kvteam.deliverytracker.managerapp

import android.os.Bundle
import com.kvteam.deliverytracker.core.DeliveryTrackerActivity
import com.kvteam.deliverytracker.core.webservice.*
import com.kvteam.deliverytracker.managerapp.dagger.ISimpleActivity
import com.kvteam.deliverytracker.managerapp.dagger.ISimpleSingleton
import com.kvteam.deliverytracker.managerapp.dagger.ISimpleType
import com.kvteam.deliverytracker.managerapp.dagger.SimpleSingleton
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

class LoginActivity : DeliveryTrackerActivity() {

    @Inject
    lateinit var instanceManager: IInstanceManager
    @Inject
    lateinit var webservice: IWebservice

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


       /* this.button.setOnClickListener({ _ ->
            webservice.post<UserInfoModel>(
                    "/api/group/create",
                    CreateGroupModel("1","2", "123Bb!"),
                    UserInfoModel::class.java,
                    {r -> onGettingResponse(r)},
                    {r -> onError(r)})
        })
*/
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
