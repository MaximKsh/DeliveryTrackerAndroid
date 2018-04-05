package com.kvteam.deliverytracker.core.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate
import com.kvteam.deliverytracker.core.R
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.core.session.SETTINGS_CONTEXT
import com.kvteam.deliverytracker.core.ui.errorhandling.IErrorHandler
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarConfiguration
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_confirm_data.*
import javax.inject.Inject

abstract class BaseConfirmDataActivity : DeliveryTrackerActivity() {
    private val surnameKey = "surname"
    private val nameKey = "name"
    private val patronymicKey = "patronymic"
    private val phoneNumberKey = "phoneNumber"

    @Inject
    lateinit var session: ISession

    @Inject
    lateinit var eh: IErrorHandler

    private lateinit var validation: AwesomeValidation

    protected abstract fun mainActivityIntent() : Intent

    override val layoutId = R.layout.activity_confirm_data

    override fun getToolbarConfiguration(): ToolbarConfiguration {
        return ToolbarConfiguration(true, false)
    }

    override fun configureToolbar(toolbar: ToolbarController) {
        super.configureToolbar(toolbar)
        toolbar.setToolbarTitle(getString(R.string.Core_ConfirmUserData))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        if(savedInstanceState == null) {
            val user = session.user!!
            etSurname.setText(user.surname ?: EMPTY_STRING)
            etName.setText(user.name ?: EMPTY_STRING)
            etPatronymic.setText(user.patronymic ?: EMPTY_STRING)
            etPhoneNumber.setPhoneNumber(user.phoneNumber ?: EMPTY_STRING)
        } else {
            etSurname.setText(savedInstanceState.getString(surnameKey, EMPTY_STRING))
            etName.setText(savedInstanceState.getString(nameKey, EMPTY_STRING))
            etPatronymic.setText(savedInstanceState.getString(patronymicKey, EMPTY_STRING))
            etPhoneNumber.setPhoneNumber(savedInstanceState.getString(phoneNumberKey, EMPTY_STRING))
        }

        validation = AwesomeValidation(ValidationStyle.UNDERLABEL)
        validation.addValidation(
                etSurname, RegexTemplate.NOT_EMPTY, getString(R.string.Core_SurnameValidationError))
        validation.addValidation(
                etName, RegexTemplate.NOT_EMPTY, getString(R.string.Core_NameValidationError))
        validation.addValidation(
                etPhoneNumber,
                {
                    it.length == 16
                },
                getString(R.string.Core_PhoneValidationError))
        validation.setContext(this)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.apply {
            putString(surnameKey, etSurname.text.toString())
            putString(nameKey, etName.text.toString())
            putString(patronymicKey, etPatronymic.text.toString())
            putString(phoneNumberKey, etPhoneNumber.text.toString())
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = launchUI ({
        when (item.itemId) {
            R.id.home -> {
                finish()
            }
            R.id.action_done -> {
                if(!validation.validate()) {
                    return@launchUI
                }
                editSettings()
            }
        }
    }, {
        super.onOptionsItemSelected(item)
    })

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.done_menu, menu)
        return true
    }

    private suspend fun editSettings() {
        if(!validation.validate()) {
            return
        }

        val modifiedUser = User()
        val originalUser = session.user!!

        modifiedUser.id = originalUser.id
        modifiedUser.instanceId = originalUser.instanceId

        var cnt = 0
        cnt += appendIfChanged(etSurname, originalUser.name, { modifiedUser.name = it })
        cnt += appendIfChanged(etName, originalUser.surname, { modifiedUser.surname = it })
        cnt += appendIfChanged(etPatronymic, originalUser.patronymic, { modifiedUser.patronymic = it })
        cnt += appendIfChanged(etPhoneNumber, originalUser.phoneNumber, { modifiedUser.phoneNumber = it })

        if(cnt != 0){
            val editResult = session.editUserInfoAsync(modifiedUser)
            if(eh.handle(editResult)) {
                return
            }
        }

        navigateToMainActivity()
    }


    private fun appendIfChanged(et: EditText, originalText: String?, appendFunc: (String) -> Unit) : Int {
        val text = et.text.toString()
        if(originalText == null) {
            if(text.isNotBlank()) {
                appendFunc(text)
                return 1
            }
        }
        else if(text != originalText) {
            appendFunc(text)
            return 1
        }
        return 0
    }

    private fun navigateToMainActivity() {
        val settingsContext = intent.getBooleanExtra(SETTINGS_CONTEXT, false)
        val intent = mainActivityIntent()
        intent.putExtra(SETTINGS_CONTEXT, settingsContext)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}
