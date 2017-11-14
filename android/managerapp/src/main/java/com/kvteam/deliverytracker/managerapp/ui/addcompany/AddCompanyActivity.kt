package com.kvteam.deliverytracker.managerapp.ui.addcompany

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
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerActivity
import com.kvteam.deliverytracker.managerapp.ui.main.MainActivity
import com.kvteam.deliverytracker.managerapp.R
import kotlinx.android.synthetic.main.activity_add_company.*
import kotlinx.android.synthetic.main.toolbar.*
import javax.inject.Inject


class AddCompanyActivity : DeliveryTrackerActivity() {
    @Inject
    lateinit var instanceManager: IInstanceManager

    @Inject
    lateinit var session: ISession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_company)

        setSupportActionBar(this.toolbar_top)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        this.toolbar_title.text = resources.getString(R.string.company_addition)
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
                            when (it) {
                                LoginResult.Registered -> {
                                    val intent = Intent(this@AddCompanyActivity, MainActivity::class.java)
                                    startActivity(intent)
                                    Toast.makeText(this@AddCompanyActivity, "Ваша компания успешно добавлена", Toast.LENGTH_LONG).show()
                                }
                                LoginResult.Success -> {
                                    val intent = Intent(this@AddCompanyActivity, MainActivity::class.java)
                                    startActivity(intent)
                                    Toast.makeText(this@AddCompanyActivity, "Ваша компания успешно добавлена", Toast.LENGTH_LONG).show()
                                }
                                LoginResult.RoleMismatch -> {
                                    Toast.makeText(this@AddCompanyActivity, "Не твоя роль", Toast.LENGTH_LONG).show()
                                }
                                LoginResult.Error -> {
                                    Toast.makeText(this@AddCompanyActivity, "Неверные данные", Toast.LENGTH_LONG).show()
                                }
                                else -> {
                                    Toast.makeText(this@AddCompanyActivity, "Неизвестная ошибка", Toast.LENGTH_LONG).show()
                                }
                            }
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
}
