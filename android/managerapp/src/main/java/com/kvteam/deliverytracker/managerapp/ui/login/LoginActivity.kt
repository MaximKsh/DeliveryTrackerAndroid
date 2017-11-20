package com.kvteam.deliverytracker.managerapp.ui.login

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.widget.Toast
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.core.session.LoginResult
import com.kvteam.deliverytracker.core.session.SETTINGS_CONTEXT
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerActivity
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.confirm.ConfirmDataActivity
import com.kvteam.deliverytracker.managerapp.ui.createinstance.CreateInstanceActivity
import com.kvteam.deliverytracker.managerapp.ui.main.MainActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

class LoginActivity : DeliveryTrackerActivity() {
    private val usernameKey = "username"
    private val passwordKey = "password"

    @Inject
    lateinit var session: ISession


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        this.tvForgotPassword.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        if(savedInstanceState != null){
            etLoginField.setText(savedInstanceState.getString(usernameKey, EMPTY_STRING))
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

        outState.putString(usernameKey, this.etLoginField.text.toString())
        outState.putString(passwordKey, this.etPasswordField.text.toString())
    }

    private fun onCreateInstanceClick() {
        val intent = Intent(this, CreateInstanceActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun onLoginClick() {
        val ctx = this
        val fromSettings = this.intent.getBooleanExtra(SETTINGS_CONTEXT, false)
        val username = this.etLoginField.text.toString()
        val password = this.etPasswordField.text.toString()

        setProcessingState()
        invokeAsync({
            session.login(username, password)
        }, {
            when (it) {
                LoginResult.Registered -> {
                    val intent = Intent(ctx, ConfirmDataActivity::class.java)
                    if (fromSettings) {
                        intent.putExtra(SETTINGS_CONTEXT, true)
                    }
                    ctx.startActivity(intent)
                    ctx.finish()
                }
                LoginResult.Success -> {
                    if (!fromSettings) {
                        val intent = Intent(ctx, MainActivity::class.java)
                        ctx.startActivity(intent)
                    }
                    ctx.finish()
                }
                LoginResult.RoleMismatch -> {
                    Toast.makeText(
                            ctx,
                            getString(R.string.ManagerApp_LoginActivity_WrongRole),
                            Toast.LENGTH_LONG).show()
                }
                LoginResult.Error -> {
                    Toast.makeText(
                            ctx,
                            getString(R.string.ManagerApp_LoginActivity_WrongCredentials),
                            Toast.LENGTH_LONG).show()
                }
                else -> {
                    Toast.makeText(
                            ctx,
                            getString(R.string.Core_UnknownError),
                            Toast.LENGTH_LONG).show()
                }
            }
            setProcessingState(false)
        })
    }

    private fun setProcessingState(processing: Boolean = true){
        this.btnLogin.isEnabled = !processing
    }
}

