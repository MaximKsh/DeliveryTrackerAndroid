package com.kvteam.deliverytracker.performerapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import com.kvteam.deliverytracker.core.R
import com.kvteam.deliverytracker.core.ui.BaseLoginActivity
import com.kvteam.deliverytracker.performerapp.ui.confirm.ConfirmDataActivity
import com.kvteam.deliverytracker.performerapp.ui.main.MainActivity

class LoginActivity : BaseLoginActivity() {
    override fun createInstanceIntent(): Intent? {
        return null
    }

    override fun mainActivityIntent(): Intent {
        return Intent(this, MainActivity::class.java)
    }

    override fun confirmDataIntent(): Intent {
        return Intent(this, ConfirmDataActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        findViewById<LinearLayout>(R.id.llRegister).visibility = View.GONE
    }
}