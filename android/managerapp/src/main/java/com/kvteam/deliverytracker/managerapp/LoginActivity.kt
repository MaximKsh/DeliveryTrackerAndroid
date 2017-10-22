package com.kvteam.deliverytracker.managerapp

import android.accounts.AccountManager
import android.os.Bundle
import com.kvteam.deliverytracker.core.DeliveryTrackerActivity
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.models.ErrorListModel
import com.kvteam.deliverytracker.core.models.UserModel
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : DeliveryTrackerActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val button = this.button
        val am = AccountManager.get(this)

        this.button.setOnClickListener({ _ ->
            invokeAsync({
                // val account = am.accounts[0]
                //am.getAuthToken(account, AUTH_TOKEN, null, false, )
                /*instanceManager.create(
                        InstanceModel("Instance1"),
                        UserModel(surname = "1", name = "1", phoneNumber = ""),
                        CredentialsModel(password = "123Bb!"))*/
            }, {
                /*if(it != null) {
                    button.text = it.username
                }*/
            })


         })
/*
        this.button.setOnClickListener({_ ->
            invokeAsync({
                val credentials = CredentialsModel("nRedS2Gd4T", "123qQ!", "CreatorRole")
                val tokenResponse = webservice.post<TokenModel>(
                        "/api/session/login",
                        credentials,
                        TokenModel::class.java)
                val authtoken = tokenResponse.responseEntity?.token
                val res = Intent()
                res.putExtra(AccountManager.KEY_ACCOUNT_NAME, "nRedS2Gd4T")
               // res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, ACCOUNT_TYPE)
                res.putExtra(AccountManager.KEY_AUTHTOKEN, authtoken)
                res.putExtra(USER_PASSWORD, "123qQ!")
                res
            }, {
                val accountName = it.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                val accountPassword = it.getStringExtra(USER_PASSWORD)
                val account = Account(accountName, it.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE))
                try{
                if (it.getBooleanExtra(IS_NEW_ACCOUNT, false)) {
                    val authtoken = it.getStringExtra(AccountManager.KEY_AUTHTOKEN)
                  //  val authtokenType = ACCOUNT_TYPE
                    // Creating the account on the device and setting the auth token we got
                    // (Not setting the auth token will cause another call to the server to authenticate the user)
                    am.addAccountExplicitly(account, accountPassword, null)
                   // am.setAuthToken(account, authtokenType, authtoken)
                } else {
                        am.setPassword(account, accountPassword)

                }
                } catch (e: Exception) {
                  //  val availableAccounts = am.getAccountsByType(ACCOUNT_TYPE)
                }
            })
        })

        val act = this
        this.button2.setOnClickListener({_ ->
            invokeAsync ({
            //    val account = am.getAccountsByType(ACCOUNT_TYPE).first()
          //      am.getAuthToken(account, ACCOUNT_TYPE, null, act, null,null).result

            }, {
                val top = supportFragmentManager.findFragmentById(R.id.container)
                if(top is FirstFragment) {
               //     top.tb.text = it.getString("authtoken")
                }
            })
        })
*/
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.container, FirstFragment())
        transaction.commit()
    }

    private fun onGettingResponse(resp: UserModel) {

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
