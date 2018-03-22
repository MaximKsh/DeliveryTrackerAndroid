package com.kvteam.deliverytracker.performerapp.ui.login

import android.content.Intent
import android.os.Bundle
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.core.session.LoginResult
import com.kvteam.deliverytracker.core.session.LoginResultType
import com.kvteam.deliverytracker.core.session.SETTINGS_CONTEXT
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerActivity
import com.kvteam.deliverytracker.core.ui.errorhandling.IErrorHandler
import com.kvteam.deliverytracker.performerapp.R
import com.kvteam.deliverytracker.performerapp.ui.confirm.ConfirmDataActivity
import com.kvteam.deliverytracker.performerapp.ui.main.MainActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

class LoginActivity : DeliveryTrackerActivity() {
    private val usernameKey = "code"
    private val passwordKey = "password"

    @Inject
    lateinit var session: ISession

    @Inject
    lateinit var eh: IErrorHandler

    override val layoutId: Int = R.layout.activity_login

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        savedInstanceState?.apply {
            etLoginUsername.setText(
                    getString(usernameKey, EMPTY_STRING))
            etLoginPassword.setText(
                    getString(passwordKey, EMPTY_STRING))
        }
        bttnSignIn.setOnClickListener { onSignInClicked() }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.apply {
            putString(usernameKey, etLoginUsername.text.toString())
            putString(passwordKey, etLoginPassword.text.toString())
        }
    }

    private fun onSignInClicked() = launchUI {
        val username = etLoginUsername.text.toString()
        val password = etLoginPassword.text.toString()

        val loginResult = session.loginAsync(username, password)
        if(eh.handle(loginResult)) {
            return@launchUI
        }
        navigateToNextActivity(loginResult)
    }

    private fun navigateToNextActivity(result: LoginResult) {
        val fromSettings = intent.getBooleanExtra(SETTINGS_CONTEXT, false)
        when (result.loginResultType) {
            LoginResultType.Registered -> {
                val intent = Intent(this, ConfirmDataActivity::class.java)
                intent.putExtra(SETTINGS_CONTEXT, fromSettings)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
            LoginResultType.Success -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra(SETTINGS_CONTEXT, fromSettings)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
            else -> {
            }
        }
    }
}
