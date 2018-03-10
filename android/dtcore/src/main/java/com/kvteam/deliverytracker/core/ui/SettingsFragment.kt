package com.kvteam.deliverytracker.core.ui


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kvteam.deliverytracker.core.DeliveryTrackerApplication

import com.kvteam.deliverytracker.core.R
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.roles.toRole
import com.kvteam.deliverytracker.core.session.ISession
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_settings.*
import javax.inject.Inject


class SettingsFragment : DeliveryTrackerFragment() {

    @Inject
    lateinit var session: ISession

    @Inject
    lateinit var lm: ILocalizationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        tvCode.text = session.code
        tvSurname.text = session.surname
        tvName.text = session.name
        tvPatronymic.text = session.patronymic
        tvPhoneNumber.text = session.phoneNumber
        tvRole.text = lm.getString(session.role?.toRole()?.localizationStringName ?: "")


        bttnLogout.setOnClickListener {
            session.logout()
            val intent = Intent(
                    activity,
                    (context?.applicationContext as DeliveryTrackerApplication).loginActivityType as Class<*>)
            startActivity(intent)
            activity?.finish()
        }
    }

}
