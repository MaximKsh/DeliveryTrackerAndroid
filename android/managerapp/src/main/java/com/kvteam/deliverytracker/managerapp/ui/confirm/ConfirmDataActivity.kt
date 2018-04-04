package com.kvteam.deliverytracker.managerapp.ui.confirm

import android.content.Intent
import com.kvteam.deliverytracker.core.ui.BaseConfirmDataActivity
import com.kvteam.deliverytracker.managerapp.ui.main.MainActivity

class ConfirmDataActivity : BaseConfirmDataActivity() {
    override fun mainActivityIntent(): Intent {
        return Intent(this, MainActivity::class.java)
    }

}