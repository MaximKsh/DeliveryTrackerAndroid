package com.kvteam.deliverytracker.managerapp.ui.createinstance

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.models.CodePassword
import com.kvteam.deliverytracker.core.models.Instance
import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.roles.Role
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.core.session.SETTINGS_CONTEXT
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerActivity
import com.kvteam.deliverytracker.core.ui.dropdowntop.ToolbarConfiguration
import com.kvteam.deliverytracker.core.ui.dropdowntop.ToolbarController
import com.kvteam.deliverytracker.core.ui.errorhandling.handleResultErrors
import com.kvteam.deliverytracker.core.webservice.IInstanceWebservice
import com.kvteam.deliverytracker.core.webservice.NetworkResult
import com.kvteam.deliverytracker.core.webservice.viewmodels.InstanceResponse
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_create_instance.*
import javax.inject.Inject


class CreateInstanceActivity : DeliveryTrackerActivity() {
    @Inject
    lateinit var instanceWebservice: IInstanceWebservice

    @Inject
    lateinit var session: ISession

    override val layoutId = R.layout.activity_create_instance

    override fun getToolbarConfiguration(): ToolbarConfiguration {
        return ToolbarConfiguration(true, false)
    }

    override fun configureToolbar(toolbar: ToolbarController) {
        toolbar.disableDropDown()
        toolbar.showBackButton()
        toolbar.setToolbarTitle(resources.getString(R.string.ManagerApp_CreateInstanceActivity_CompanyRegistration))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = launchUI ({
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.action_done -> {
                val result = createInstanceAsync()
                if(handleResultErrors(this@CreateInstanceActivity, result))
                    return@launchUI
                val entity = result.entity!!
                val token = entity.token!!
                val user = entity.creator!!
                val code = user.code!!
                val password = etPasswordField.text.toString()
                session.setAccountExplicitly(code, password, token, user)
                navigateToMainActivity()
            }
        }
    }, {
        super.onOptionsItemSelected(item)
    })

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_confirm_data_menu, menu)
        return true
    }

    private suspend fun createInstanceAsync() : NetworkResult<InstanceResponse> {
        val instance = Instance()
        instance.name = etCompanyNameField.text.toString()
        val user = User()
        user.surname = etSurnameField.text.toString()
        user.name = etNameField.text.toString()
        user.patronymic = etPatronymicField.text.toString()
        user.phoneNumber = etPhoneNumberField.text.toString()
        user.role = Role.Creator.id
        return instanceWebservice.createAsync(
                instance,
                user,
                CodePassword(password = etPasswordField.text.toString()))
    }


    private fun navigateToMainActivity() {
        val settingsContext = intent.getBooleanExtra(SETTINGS_CONTEXT, false)
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(SETTINGS_CONTEXT, settingsContext)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}
