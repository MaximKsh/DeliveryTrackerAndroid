package com.kvteam.deliverytracker.managerapp

import android.accounts.AccountManager
import android.app.Activity
import android.os.Bundle
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val am = AccountManager.get(this)


    }
}
