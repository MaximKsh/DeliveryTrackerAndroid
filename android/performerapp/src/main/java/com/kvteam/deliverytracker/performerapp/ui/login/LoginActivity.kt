package com.kvteam.deliverytracker.performerapp.ui.login

import android.content.Intent
import android.os.Bundle
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerActivity
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.core.session.LoginResult
import com.kvteam.deliverytracker.core.session.SETTINGS_CONTEXT
import com.kvteam.deliverytracker.performerapp.R
import dagger.android.AndroidInjection
import com.kvteam.deliverytracker.performerapp.ui.confirm.ConfirmDataActivity
import com.kvteam.deliverytracker.performerapp.ui.main.MainActivity
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

        if(savedInstanceState != null) {
            savedInstanceState.apply {
                etLoginUsername.setText(
                        getString(usernameKey, ""))
                etLoginPassword.setText(
                        getString(passwordKey, ""))
            }
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

    private fun onSignInClicked() {
        val fromSettings = intent.getBooleanExtra(SETTINGS_CONTEXT, false)
        val username = etLoginUsername.text.toString()
        val password = etLoginPassword.text.toString()

        setProcessingState()
        invokeAsync({
            session.login(username, password)
        }, {
            when (it) {
                LoginResult.Registered -> {
                    val intent = Intent(
                            this@LoginActivity,
                            ConfirmDataActivity::class.java)
                    if (fromSettings) {
                        intent.putExtra(SETTINGS_CONTEXT, true)
                    }
                    startActivity(intent)
                    finish()
                }
                LoginResult.Success -> {
                    if (!fromSettings) {
                        val intent = Intent(
                                this@LoginActivity,
                                MainActivity::class.java)
                        startActivity(intent)
                    }
                    finish()
                }
                LoginResult.RoleMismatch -> {
                    showError(getString(R.string.PerformerApp_LoginActivity_IncorrectRole))

                }
                LoginResult.Error -> {
                    showError(getString(R.string.PerformerApp_LoginActivity_WrongCredentials))
                }
                else -> {}
            }
            setProcessingState(false)
        })
    }

    private fun setProcessingState(processing: Boolean = true){
        bttnSignIn.isEnabled = !processing
    }

    private fun showError(text: String) {
        tvLoginError.text = text
    }
}
