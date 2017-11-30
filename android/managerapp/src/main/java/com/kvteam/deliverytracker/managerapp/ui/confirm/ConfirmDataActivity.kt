package com.kvteam.deliverytracker.managerapp.ui.confirm

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.common.IErrorManager
import com.kvteam.deliverytracker.core.common.SimpleResult
import com.kvteam.deliverytracker.core.models.UserModel
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.core.session.SETTINGS_CONTEXT
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerActivity
import com.kvteam.deliverytracker.core.ui.ErrorDialog
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.R.id.action_done
import com.kvteam.deliverytracker.managerapp.ui.main.MainActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_confirm_data.*
import kotlinx.android.synthetic.main.toolbar.*
import javax.inject.Inject

class ConfirmDataActivity : DeliveryTrackerActivity() {
    private val surnameKey = "surname"
    private val nameKey = "name"
    private val phoneNumberKey = "phoneNumber"

    @Inject
    lateinit var session: ISession

    @Inject
    lateinit var errorManager: IErrorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_data)

        setSupportActionBar(this.toolbar_top)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        this.toolbar_title.text = resources.getString(R.string.ManagerApp_ConfirmDataActivity_Confirm)

        if(savedInstanceState == null) {
            etSurnameField.setText(session.surname ?: EMPTY_STRING)
            etNameField.setText(session.name ?: EMPTY_STRING)
            etPhoneNumberField.setText(session.phoneNumber ?: EMPTY_STRING)
        } else {
            etSurnameField.setText(savedInstanceState.getString(surnameKey, EMPTY_STRING))
            etNameField.setText(savedInstanceState.getString(nameKey, EMPTY_STRING))
            etPhoneNumberField.setText(savedInstanceState.getString(phoneNumberKey, EMPTY_STRING))
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.apply {
            putString(surnameKey, etSurnameField.text.toString())
            putString(nameKey, etNameField.text.toString())
            putString(phoneNumberKey, etPhoneNumberField.text.toString())
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
            action_done -> {
                val userInfo = UserModel(
                        surname = etSurnameField.text.toString(),
                        name = etNameField.text.toString(),
                        phoneNumber = etPhoneNumberField.text.toString())

                invokeAsync({
                    session.editUserInfo(userInfo)
                }, {
                    navigateToMainAcitity(it)
                })
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    private fun navigateToMainAcitity(result: SimpleResult) {
        val settingsContext = intent.getBooleanExtra(SETTINGS_CONTEXT, false)

        if (result.success) {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(SETTINGS_CONTEXT, settingsContext)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        } else {
            val dialog = ErrorDialog(this@ConfirmDataActivity)
            if(result.errorChainId != null) {
                dialog.addChain(errorManager.getAndRemove(result.errorChainId!!)!!)
            }
            dialog.show()
        }
    }
}
