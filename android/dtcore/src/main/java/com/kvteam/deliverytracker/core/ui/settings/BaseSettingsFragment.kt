package com.kvteam.deliverytracker.core.ui.settings


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kvteam.deliverytracker.core.DeliveryTrackerApplication
import com.kvteam.deliverytracker.core.R
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.materialDefaultAvatar
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_base_settings.*
import javax.inject.Inject


abstract class BaseSettingsFragment : DeliveryTrackerFragment() {
    private val scrollPosKey = "scrollPosition"

    @Inject
    lateinit var session: ISession

    @Inject
    lateinit var lm: ILocalizationManager

    protected abstract fun onEditSettingsClicked()

    protected abstract fun onChangePasswordClicked()

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
        toolbarController.setToolbarTitle(lm.getString(R.string.Core_ProfileTooltipHeader))
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val position = savedInstanceState?.getInt(scrollPosKey)
        if (position != null) {
            svMainScrollView.scrollY = position
        }

        val instance = session.instance
        val user = session.user!!
        tvSurnameNamePatronymic.text = context?.getString(
                R.string.Core_SurnameNamePatronymic,
                user.surname?.trim() ?: EMPTY_STRING,
                user.name?.trim() ?: EMPTY_STRING,
                user.patronymic?.trim() ?: EMPTY_STRING)

        if(instance != null) {
            tvInstance.text = instance.name
        }

        tvCode.text = user.code
        tvPhoneNumber.text = user.phoneNumber
        ivUserAvatar.setImageDrawable(materialDefaultAvatar(user))

        tvEditSettings.setOnClickListener { onEditSettingsClicked() }
        tvChangePassword.setOnClickListener { onChangePasswordClicked() }
        tvLogout.setOnClickListener { launchUI {
                session.logoutAsync()
                val loginActivity =
                        (activity?.application as DeliveryTrackerApplication).loginActivityType as Class<*>
                val intent = Intent(activity, loginActivity)
                startActivity(intent)
                activity?.finish()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(scrollPosKey, svMainScrollView.scrollY)
    }
}
