package com.kvteam.deliverytracker.performerapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.core.session.LoginResult
import com.kvteam.deliverytracker.core.session.LoginResultType
import com.kvteam.deliverytracker.core.session.SETTINGS_CONTEXT
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerActivity
import com.kvteam.deliverytracker.core.ui.errorhandling.IErrorHandler
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarConfiguration
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

    override fun getToolbarConfiguration(): ToolbarConfiguration {
        return ToolbarConfiguration(false, false)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        btnLogin.setOnClickListener { onSignInClicked() }
    }


    private fun onSignInClicked() = launchUI {
        val username = etLoginField.code.joinToString(EMPTY_STRING)
        val password = etPasswordField.text.toString()

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
                Toast.makeText(this, "Wrong role", Toast.LENGTH_LONG).show()
            }
        }
    }
}
