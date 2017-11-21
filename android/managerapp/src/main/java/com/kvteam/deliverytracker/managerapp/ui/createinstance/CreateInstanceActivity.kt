package com.kvteam.deliverytracker.managerapp.ui.createinstance

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.instance.IInstanceManager
import com.kvteam.deliverytracker.core.models.CredentialsModel
import com.kvteam.deliverytracker.core.models.InstanceModel
import com.kvteam.deliverytracker.core.models.UserModel
import com.kvteam.deliverytracker.core.roles.Role
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.core.session.LoginResult
import com.kvteam.deliverytracker.core.session.SETTINGS_CONTEXT
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerActivity
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_create_instance.*
import kotlinx.android.synthetic.main.toolbar.*
import javax.inject.Inject


class CreateInstanceActivity : DeliveryTrackerActivity() {
    @Inject
    lateinit var instanceManager: IInstanceManager

    @Inject
    lateinit var session: ISession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_instance)

        setSupportActionBar(this.toolbar_top)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        this.toolbar_title.text = resources.getString(R.string.ManagerApp_CreateInstanceActivity_CompanyRegistration)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.container, LocationFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.action_done -> {
                invokeAsync({
                    instanceManager.create(
                            InstanceModel(
                                    instanceName = etCompanyNameField.text.toString()),
                            UserModel(
                                    surname = etSurnameField.text.toString(),
                                    name = etNameField.text.toString(),
                                    phoneNumber = etPhoneNumberField.text.toString(),
                                    role = Role.Creator.simpleName),
                            CredentialsModel(
                                    password = etPasswordField.text.toString())
                    )
                }, {
                    if (it != null) {
                        invokeAsync({
                            session.login(it.username!!, etPasswordField.text.toString())
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
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    private fun navigateToMainActivity(it: LoginResult) {
        val settingsContext = intent.getBooleanExtra(SETTINGS_CONTEXT, false)
        when (it) {
            LoginResult.Success -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra(SETTINGS_CONTEXT, settingsContext)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
            else -> {
                // Сразу после создания если не получилось залогинится, значит какой-то косяк
                // надо разбираться
                Toast.makeText(this, getString(R.string.Core_UnknownError), Toast.LENGTH_LONG).show()
            }
        }
    }
}
