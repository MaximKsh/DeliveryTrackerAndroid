package com.kvteam.deliverytracker.performerapp.ui.confirm

import android.content.Intent
import com.kvteam.deliverytracker.core.ui.BaseConfirmDataActivity
import com.kvteam.deliverytracker.performerapp.ui.main.MainActivity

class ConfirmDataActivity : BaseConfirmDataActivity() {
    override fun mainActivityIntent(): Intent {
        return Intent(this, MainActivity::class.java)
    }
}