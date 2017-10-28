package com.kvteam.deliverytracker.performerapp.ui.login

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerActivity
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerViewModelFactory
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.core.session.LoginResult
import com.kvteam.deliverytracker.core.session.SETTINGS_CONTEXT
import com.kvteam.deliverytracker.core.ui.AutoClearedValue
import dagger.android.AndroidInjection
import com.kvteam.deliverytracker.performerapp.databinding.ActivityLoginBinding
import com.kvteam.deliverytracker.performerapp.ui.confirm.ConfirmDataActivity
import com.kvteam.deliverytracker.performerapp.ui.main.MainActivity
import javax.inject.Inject

class LoginActivity : DeliveryTrackerActivity() {
    @Inject
    lateinit var session: ISession
    @Inject
    lateinit var vmFactory: DeliveryTrackerViewModelFactory

    private lateinit var binding: AutoClearedValue<ActivityLoginBinding>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil
                .setContentView<ActivityLoginBinding>(
                        this,
                        com.kvteam.deliverytracker.performerapp.R.layout.activity_login)
        this.binding = AutoClearedValue(
                this,
                binding,
                {
                    it?.executePendingBindings()
                    it?.unbind()
                    it?.activity = null
                    it?.viewModel = null
                })
        val viewModel = ViewModelProviders
                .of(this, vmFactory)
                .get(LoginViewModel::class.java)
        this.binding.value?.viewModel = viewModel
        this.binding.value?.activity = this

        if(savedInstanceState == null) {
            viewModel.username.value = "Rdpasz26f3"
            viewModel.password.value = "123qQ!"
        }
        if(intent.getBooleanExtra(SETTINGS_CONTEXT, false)) {
            viewModel.openedFromSettings = true
        }
    }

    fun onSignInClicked(v: View) {
        val ctx = this
        val binding = binding.value ?: return
        val fromSettings = binding.viewModel?.openedFromSettings ?: false
        val username = binding.viewModel?.username?.value
        val password = binding.viewModel?.password?.value
        if(username == null
                || password == null) {
            return
        }

        binding.viewModel?.operationPending?.set(true)
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
            binding.viewModel?.operationPending?.set(false)
        })
    }
}
