package com.kvteam.deliverytracker.managerapp.ui.login.addcompany

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerActivity
import com.kvteam.deliverytracker.managerapp.LocationFragment
import com.kvteam.deliverytracker.managerapp.MainActivity
import com.kvteam.deliverytracker.managerapp.R
import kotlinx.android.synthetic.main.toolbar.*


class AddCompanyActivity : DeliveryTrackerActivity() {

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
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }
}
