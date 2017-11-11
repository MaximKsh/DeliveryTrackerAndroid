package com.kvteam.deliverytracker.managerapp

import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject


class LoginActivity : DeliveryTrackerActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        this.tvForgotPassword.paintFlags = Paint.UNDERLINE_TEXT_FLAG
    }

    fun onAddCompanyClick(view: View) {
        val intent = Intent(this, AddCompanyActivity::class.java)
        startActivity(intent)
    }

    fun onLoginClick(view: View) {
        val intent = Intent(this, ApproveUserInfoActivity::class.java)
        startActivity(intent)
    }
}
