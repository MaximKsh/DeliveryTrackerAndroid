package com.kvteam.deliverytracker.managerapp.ui.createinstance

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.models.CodePassword
import com.kvteam.deliverytracker.core.models.Instance
import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.roles.Role
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.core.session.LoginResult
import com.kvteam.deliverytracker.core.session.LoginResultType
import com.kvteam.deliverytracker.core.session.SETTINGS_CONTEXT
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerActivity
import com.kvteam.deliverytracker.core.webservice.IInstanceWebservice
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_create_instance.*
import kotlinx.android.synthetic.main.toolbar.*
import javax.inject.Inject


class CreateInstanceActivity : DeliveryTrackerActivity() {
    @Inject
    lateinit var instanceWebservice: IInstanceWebservice

    @Inject
    lateinit var session: ISession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_instance)

        setSupportActionBar(this.toolbar_top)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        this.toolbar_title.text = resources.getString(R.string.ManagerApp_CreateInstanceActivity_CompanyRegistration)
        /*val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.container, LocationFragment())
        transaction.addToBackStack(null)
        transaction.commit()*/
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.action_done -> {
                invokeAsync({
                    val instance = Instance()
                    instance.name = etCompanyNameField.text.toString()
                    val user = User()
                    user.surname = etSurnameField.text.toString()
                    user.name = etNameField.text.toString()
                    user.patronymic = etPatronymicField.text.toString()
                    user.phoneNumber = etPhoneNumberField.text.toString()
                    user.role = Role.Creator.id
                    instanceWebservice.create(
                            instance,
                            user,
                            CodePassword(
                                    password = etPasswordField.text.toString())
                    )
                }, {
                    if (it.success) {
                        invokeAsync({
                            session.login(it.entity?.creator?.code!!, etPasswordField.text.toString())
                        }, {
                            navigateToMainActivity(it)
                        })
                    }
                })
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_confirm_data_menu, menu)
        return true
    }

    private fun navigateToMainActivity(it: LoginResult) {
        val settingsContext = intent.getBooleanExtra(SETTINGS_CONTEXT, false)
        when (it.loginResultType) {
            LoginResultType.Success -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra(SETTINGS_CONTEXT, settingsContext)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }
    }
}
