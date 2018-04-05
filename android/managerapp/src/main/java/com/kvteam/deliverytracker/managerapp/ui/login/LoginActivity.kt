package com.kvteam.deliverytracker.managerapp.ui.login

import android.content.Intent
import com.kvteam.deliverytracker.core.ui.BaseLoginActivity
import com.kvteam.deliverytracker.managerapp.ui.confirm.ConfirmDataActivity
import com.kvteam.deliverytracker.managerapp.ui.createinstance.CreateInstanceActivity
import com.kvteam.deliverytracker.managerapp.ui.main.MainActivity

class LoginActivity : BaseLoginActivity() {
    override fun createInstanceIntent(): Intent? {
        return Intent(this, CreateInstanceActivity::class.java)
    }

    override fun mainActivityIntent(): Intent {
        return Intent(this, MainActivity::class.java)
    }

    override fun confirmDataIntent(): Intent {
        return Intent(this, ConfirmDataActivity::class.java)
    }

}

