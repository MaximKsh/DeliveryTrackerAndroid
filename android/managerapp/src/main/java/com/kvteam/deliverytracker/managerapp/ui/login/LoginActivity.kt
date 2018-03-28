package com.kvteam.deliverytracker.managerapp.ui.login

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.core.session.LoginResult
import com.kvteam.deliverytracker.core.session.LoginResultType
import com.kvteam.deliverytracker.core.session.SETTINGS_CONTEXT
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerActivity
import com.kvteam.deliverytracker.core.ui.errorhandling.IErrorHandler
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.confirm.ConfirmDataActivity
import com.kvteam.deliverytracker.managerapp.ui.createinstance.CreateInstanceActivity
import com.kvteam.deliverytracker.managerapp.ui.main.MainActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

class LoginActivity : DeliveryTrackerActivity() {
    private val usernameKey = "code"
    private val passwordKey = "password"

    @Inject
    lateinit var session: ISession

    @Inject
    lateinit var errorHandler: IErrorHandler

    override val layoutId = R.layout.activity_login

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        this.tvForgotPassword.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        if(savedInstanceState != null){
//            etLoginField.setText(savedInstanceState.getString(usernameKey, EMPTY_STRING))
            etPasswordField.setText(savedInstanceState.getString(passwordKey, EMPTY_STRING))
        }

        btnLogin.setOnClickListener { onLoginClick() }
        btnAddCompany.setOnClickListener { onCreateInstanceClick() }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if(outState == null){
            return
        }

        outState.putString(usernameKey, this.etLoginField.code.toString())
        outState.putString(passwordKey, this.etPasswordField.text.toString())
    }

    private fun onCreateInstanceClick() {
        val fromSettings = this.intent.getBooleanExtra(SETTINGS_CONTEXT, false)
        val intent = Intent(this, CreateInstanceActivity::class.java)
        if (fromSettings) {
            intent.putExtra(SETTINGS_CONTEXT, true)
        }
        startActivity(intent)
    }

    private fun onLoginClick() = launchUI {
        val username = etLoginField.code.joinToString(EMPTY_STRING)
        val password = etPasswordField.text.toString()

        val result = session.loginAsync(username, password)
        if(errorHandler.handle(result))
            return@launchUI

        afterLogin(result)
    }

    private fun afterLogin(result: LoginResult) {
        val fromSettings = intent.getBooleanExtra(SETTINGS_CONTEXT, false)
        when (result.loginResultType) {
            LoginResultType.Registered -> {
                val intent = Intent(this, ConfirmDataActivity::class.java)
                intent.putExtra(SETTINGS_CONTEXT, fromSettings)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                this.startActivity(intent)
            }
            LoginResultType.Success -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra(SETTINGS_CONTEXT, fromSettings)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                this.startActivity(intent)
            }

            else -> {
            }
        }
    }
}

