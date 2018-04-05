package com.kvteam.deliverytracker.managerapp.ui.createinstance

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.models.CodePassword
import com.kvteam.deliverytracker.core.models.Instance
import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.roles.Role
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.core.session.SETTINGS_CONTEXT
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerActivity
import com.kvteam.deliverytracker.core.ui.errorhandling.IErrorHandler
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarConfiguration
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController
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

    @Inject
    lateinit var errorHandler: IErrorHandler

    override val layoutId = R.layout.activity_create_instance

    private lateinit var validation: AwesomeValidation

    override fun getToolbarConfiguration(): ToolbarConfiguration {
        return ToolbarConfiguration(true, false)
    }

    override fun configureToolbar(toolbar: ToolbarController) {
        toolbar.disableDropDown()
        toolbar.showBackButton()
        toolbar.setToolbarTitle(resources.getString(R.string.ManagerApp_CreateInstanceActivity_CompanyRegistration))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        validation = AwesomeValidation(ValidationStyle.UNDERLABEL)
        validation.addValidation(
                etCompanyName, RegexTemplate.NOT_EMPTY, getString(com.kvteam.deliverytracker.core.R.string.Core_CompanyNameValidationError))
        validation.addValidation(
                etSurname, RegexTemplate.NOT_EMPTY, getString(com.kvteam.deliverytracker.core.R.string.Core_SurnameValidationError))
        validation.addValidation(
                etName, RegexTemplate.NOT_EMPTY, getString(com.kvteam.deliverytracker.core.R.string.Core_NameValidationError))
        validation.addValidation(
                etPhoneNumber,
                {
                    it.length == 16
                },
                getString(com.kvteam.deliverytracker.core.R.string.Core_PhoneValidationError))
        validation.addValidation(
                etPassword,
                { it?.length ?: 0 > 3 },
                getString(com.kvteam.deliverytracker.core.R.string.Core_EnterPassword))
        validation.setContext(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = launchUI ({
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.action_done -> {
                if(!validation.validate()) {
                    return@launchUI
                }

                val result = createInstanceAsync()
                if (errorHandler.handle(result)) {
                    return@launchUI
                }
                val entity = result.entity!!
                val token = entity.token!!
                val user = entity.creator!!
                val code = user.code!!
                val password = etPassword.text.toString()
                session.setAccountExplicitly(code, password, token, user)
                session.updateDeviceAsync()
                navigateToMainActivity()
            }
        }
    }, {
        super.onOptionsItemSelected(item)
    })

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.done_menu, menu)
        return true
    }

    private suspend fun createInstanceAsync() : NetworkResult<InstanceResponse> {
        val instance = Instance()
        instance.name = etCompanyName.text.toString()
        val user = User()
        user.surname = etSurname.text.toString()
        user.name = etName.text.toString()
        user.patronymic = etPatronymic.text.toString()
        user.phoneNumber = etPhoneNumber.text.toString()
        user.role = Role.Creator.id
        return instanceWebservice.createAsync(
                instance,
                user,
                CodePassword(password = etPassword.text.toString()))
    }


    private fun navigateToMainActivity() {
        val settingsContext = intent.getBooleanExtra(SETTINGS_CONTEXT, false)
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(SETTINGS_CONTEXT, settingsContext)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}
