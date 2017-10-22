package com.kvteam.deliverytracker.performerapp

import android.accounts.AccountAuthenticatorActivity
import android.accounts.AccountManager
import android.content.Intent
import android.os.Bundle
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.core.session.LoginResult
import com.kvteam.deliverytracker.core.session.SETTINGS_CONTEXT
import com.kvteam.deliverytracker.core.webservice.IWebservice
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

class LoginActivity : AccountAuthenticatorActivity() {
    @Inject
    lateinit var session: ISession

    @Inject
    lateinit var webservice: IWebservice

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        usernameEditText.setText("nRedS2Gd4T")
        passwordEditText.setText("123qQ!")
        val settingsContext = intent.getBooleanExtra(SETTINGS_CONTEXT, false)

        signInButton.setOnClickListener { _ ->
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            val ctx = this

            invokeAsync({
                session.login(username, password)
            }, {
                when (it) {
                    LoginResult.Registered -> {
                        val intent = Intent(ctx, ConfirmDataActivity::class.java)
                        if(settingsContext){
                            intent.putExtra(SETTINGS_CONTEXT, true)
                        }
                        startActivity(intent)
                        finish()
                    }
                    LoginResult.Success -> {
                        if(!settingsContext) {
                            val intent = Intent(ctx, MainActivity::class.java)
                            startActivity(intent)
                        }
                        finish()
                    }
                    else -> {
                        // показать ошибку
                    }
                }
            })
        }
    }
}
