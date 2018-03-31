package com.kvteam.deliverytracker.core.ui.settings


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import com.amulyakhare.textdrawable.TextDrawable
import com.kvteam.deliverytracker.core.DeliveryTrackerApplication
import com.kvteam.deliverytracker.core.R
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.roles.Role
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController
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

    override fun configureToolbar(toolbar: ToolbarController) {
        super.configureToolbar(toolbar)
        toolbarController.setToolbarTitle("Profile")
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val user = session.user!!
        tvHeader.text = "${Role.getCaption(user.role, lm)} (${user.code})"
        tvSurname.text = user.surname
        tvName.text = user.name
        tvPatronymic.text = user.patronymic
        tvPhoneNumber.text = user.phoneNumber

        val surname = user.surname
        val name = user.name
        val materialAvatarDefault = TextDrawable.builder()
                .buildRound((name?.get(0)?.toString() ?: EMPTY_STRING) + (surname?.get(0)?.toString() ?: EMPTY_STRING), Color.LTGRAY)
        ivUserAvatar.setImageDrawable(materialAvatarDefault)

        bttnLogout.setOnClickListener { launchUI {
                session.logoutAsync()
                val loginActivity =
                        (activity?.application as DeliveryTrackerApplication).loginActivityType as Class<*>
                val intent = Intent(activity, loginActivity)
                startActivity(intent)
                activity?.finish()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit -> onEditSettingsClicked()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

}
