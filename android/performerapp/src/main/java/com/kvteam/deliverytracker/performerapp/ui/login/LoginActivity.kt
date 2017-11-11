package com.kvteam.deliverytracker.performerapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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

        if(savedInstanceState == null) {
            this.usernameEditText.setText("Rdpasz26f3")
            this.passwordEditText.setText("123qQ!")
        }
        this.bttnSignIn.setOnClickListener { onSignInClicked() }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if(outState == null){
            return
        }

        outState.putString(usernameKey, this.usernameEditText.text.toString())
        outState.putString(passwordKey, this.passwordEditText.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if(savedInstanceState == null){
            return
        }

        this.usernameEditText.setText(savedInstanceState.getString(usernameKey, ""))
        this.passwordEditText.setText(savedInstanceState.getString(passwordKey, ""))
    }

    private fun onSignInClicked() {
        val ctx = this
        val fromSettings = this.intent.getBooleanExtra(SETTINGS_CONTEXT, false)
        val username = this.usernameEditText.text.toString()
        val password = this.passwordEditText.text.toString()

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
                    Toast.makeText(ctx, "Не твоя роль", Toast.LENGTH_LONG).show()
                }
                LoginResult.Error -> {
                    Toast.makeText(ctx, "Неверные данные", Toast.LENGTH_LONG).show()
                }
                else -> {
                    Toast.makeText(ctx, "Неизвестная ошибка", Toast.LENGTH_LONG).show()
                }
            }
            setProcessingState(false)
        })
    }

    private fun setProcessingState(processing: Boolean = true){
        this.bttnSignIn.isEnabled = !processing
    }
}
