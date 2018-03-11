package com.kvteam.deliverytracker.core.ui.settings


import android.content.Intent
import android.os.Bundle
import android.view.*
import com.kvteam.deliverytracker.core.DeliveryTrackerApplication

import com.kvteam.deliverytracker.core.R
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.roles.Role
import com.kvteam.deliverytracker.core.roles.toRole
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_base_settings.*
import javax.inject.Inject


abstract class BaseSettingsFragment : DeliveryTrackerFragment() {

    @Inject
    lateinit var session: ISession

    @Inject
    lateinit var lm: ILocalizationManager

    protected abstract fun onEditSettingsClicked()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_base_settings, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        tvCode.text = session.code
        tvSurname.text = session.surname
        tvName.text = session.name
        tvPatronymic.text = session.patronymic
        tvPhoneNumber.text = session.phoneNumber
        tvRole.text = Role.getCaption(session.role, lm)

        bttnLogout.setOnClickListener {
            session.logout()
            val loginActivity =
                    (context?.applicationContext as DeliveryTrackerApplication).loginActivityType as Class<*>
            val intent = Intent(activity, loginActivity)
            startActivity(intent)
            activity?.finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit_settings -> onEditSettingsClicked()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_base_settings_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

}
