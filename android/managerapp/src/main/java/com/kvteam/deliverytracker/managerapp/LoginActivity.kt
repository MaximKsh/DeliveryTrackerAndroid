package com.kvteam.deliverytracker.managerapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.kvteam.deliverytracker.core.TestType

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val a = TestType()
    }
}
