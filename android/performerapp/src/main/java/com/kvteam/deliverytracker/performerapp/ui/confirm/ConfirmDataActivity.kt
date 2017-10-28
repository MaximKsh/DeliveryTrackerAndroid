package com.kvteam.deliverytracker.performerapp.ui.confirm

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.View
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerActivity
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerViewModelFactory
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.models.UserModel
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.core.session.SETTINGS_CONTEXT
import com.kvteam.deliverytracker.core.ui.AutoClearedValue
import com.kvteam.deliverytracker.performerapp.databinding.ActivityConfirmDataBinding
import com.kvteam.deliverytracker.performerapp.ui.main.MainActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_confirm_data.*
import javax.inject.Inject

class ConfirmDataActivity : DeliveryTrackerActivity() {
    @Inject
    lateinit var session: ISession
    @Inject
    lateinit var vmFactory: DeliveryTrackerViewModelFactory

    private lateinit var binding: AutoClearedValue<ActivityConfirmDataBinding>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil
                .setContentView<ActivityConfirmDataBinding>(
                        this,
                        com.kvteam.deliverytracker.performerapp.R.layout.activity_confirm_data)
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
                .get(ConfirmDataViewModel::class.java)
        this.binding.value?.viewModel = viewModel
        this.binding.value?.activity = this

        if(intent.getBooleanExtra(SETTINGS_CONTEXT, false)) {
            viewModel.openedFromSettings = true
        }
    }

    fun onSaveClicked(v: View) {
        val binding = this.binding.value ?: return
        val ctx = this
        val settingsContext = binding.viewModel?.openedFromSettings ?: false
        val userInfo = UserModel(
                surname = binding.viewModel?.surname?.value,
                name = binding.viewModel?.name?.value,
                phoneNumber =binding.viewModel?.phoneNumber?.value)

        invokeAsync({
            session.updateUserInfo(userInfo)
        }, {
            if(it) {
                if(settingsContext) {
                    val intent = Intent(ctx, MainActivity::class.java)
                    startActivity(intent)
                }
                finish()
            } else {
                // Произоша ошибка
            }
        })
    }
}
