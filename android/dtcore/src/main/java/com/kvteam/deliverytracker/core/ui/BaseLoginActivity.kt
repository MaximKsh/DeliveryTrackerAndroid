package com.kvteam.deliverytracker.core.ui

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate
import com.kvteam.deliverytracker.core.R
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.session.*
import com.kvteam.deliverytracker.core.storage.IStorage
import com.kvteam.deliverytracker.core.ui.errorhandling.IErrorHandler
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

abstract class BaseLoginActivity : DeliveryTrackerActivity() {
    private val usernameKey = "code"
    private val passwordKey = "password"

    @Inject
    lateinit var session: ISession

    @Inject
    lateinit var errorHandler: IErrorHandler

    @Inject
    lateinit var storage: IStorage

    protected lateinit var validation: AwesomeValidation

    override val layoutId = R.layout.activity_login

    protected abstract fun createInstanceIntent(): Intent?
    protected abstract fun mainActivityIntent(): Intent
    protected abstract fun confirmDataIntent(): Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        this.tvForgotPassword.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        if(savedInstanceState != null){
            etPasswordField.setText(savedInstanceState.getString(passwordKey, EMPTY_STRING))
        } else {
            val lastCode = storage.getString(LAST_CODE_KEY)
            if (lastCode.length == 6) {
                tvLastCode.text = applicationContext.getString(R.string.Core_LastCode, lastCode)
                tvLastCode.visibility = View.VISIBLE
            } else {
                tvLastCode.visibility = View.GONE
            }
        }

        validation = AwesomeValidation(ValidationStyle.UNDERLABEL)
        validation.addValidation(
                etPasswordField,
                RegexTemplate.NOT_EMPTY,
                getString(R.string.Core_EnterPasswordValidationError))
        validation.setContext(this)


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
        val intent = createInstanceIntent()
        if (intent != null) {
            if (fromSettings) {
                intent.putExtra(SETTINGS_CONTEXT, true)
            }
            startActivity(intent)
        }
    }

    private fun onLoginClick() = launchUI {
        if(!validation.validate()) {
            return@launchUI
        }

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
                val intent = confirmDataIntent()
                intent.putExtra(SETTINGS_CONTEXT, fromSettings)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                this.startActivity(intent)
            }
            LoginResultType.Success -> {
                val intent = mainActivityIntent()
                intent.putExtra(SETTINGS_CONTEXT, fromSettings)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                this.startActivity(intent)
            }

            else -> {
            }
        }
    }
}

