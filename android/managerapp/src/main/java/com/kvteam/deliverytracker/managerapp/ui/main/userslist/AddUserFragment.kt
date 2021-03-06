package com.kvteam.deliverytracker.managerapp.ui.main.userslist

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.dataprovider.base.DataProvider
import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.roles.Role
import com.kvteam.deliverytracker.core.roles.toRole
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.errorhandling.IErrorHandler
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController
import com.kvteam.deliverytracker.core.webservice.IInvitationWebservice
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_add_user.*
import javax.inject.Inject


class AddUserFragment : DeliveryTrackerFragment() {
    @Inject
    lateinit var navigationController: NavigationController

    @Inject
    lateinit var invitationWebservice: IInvitationWebservice

    @Inject
    lateinit var dp: DataProvider

    @Inject
    lateinit var lm: ILocalizationManager

    @Inject
    lateinit var eh: IErrorHandler

    @Inject
    lateinit var session: ISession

    private val userRoleKey = "userRoleKey"
    private val userNameKey = "userNameKey"
    private val userSurnameKey = "userSurnameKey"
    private val userPhoneKey = "userPhoneKey"
    private val inviteCodeKey = "inviteCodeKey"

    private val selectedRoleMapperCreator = mapOf(
            0 to Role.Manager,
            1 to Role.Performer
    )


    private val selectedRoleMapperManager = mapOf(
            0 to Role.Performer
    )

    private lateinit var role: Role

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_user, container, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(
                userRoleKey, this.role.roleName
        )
        outState.putString(
                userNameKey, etNameField.text.toString())
        outState.putString(
                userSurnameKey, etSurnameField.text.toString())
        outState.putString(
                userPhoneKey, etPhoneNumberField.text.toString())
        val invitationCode = tvInvitationCode.text.toString()
        if(invitationCode != EMPTY_STRING) {
            outState.putString(
                    inviteCodeKey, tvInvitationCode.text.toString())
        }
    }

    override fun configureToolbar(toolbar: ToolbarController) {
        toolbar.disableDropDown()
        toolbar.disableSearchMode()
        toolbar.setToolbarTitle(resources.getString(R.string.ManagerApp_Toolbar_AddUser))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val array = if (session.user?.role?.toRole() == Role.Creator) {
            R.array.roles_for_creator
        } else {
            R.array.roles_for_manager
        }

        val adapter = ArrayAdapter.createFromResource(this.activity, array, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        dtActivity.softKeyboard.openSoftKeyboard()
        etNameField.requestFocus()

        sUserRole.adapter = adapter

        if (savedInstanceState != null) {
            role = savedInstanceState.getString(userRoleKey).toRole()!!
            etNameField.setText(savedInstanceState.getString(userNameKey, EMPTY_STRING))
            etSurnameField.setText(savedInstanceState.getString(userSurnameKey, EMPTY_STRING))
            etPhoneNumberField.setText(savedInstanceState.getString(userPhoneKey, EMPTY_STRING))
            val invitationCode = savedInstanceState.getString(inviteCodeKey, EMPTY_STRING)
            if(invitationCode != EMPTY_STRING) {
                tvInvitationCodeInfo.visibility = View.VISIBLE
                tvInvitationCode.text = invitationCode
            }
        }
        sUserRole.setSelection(adapter.getPosition(getString(this.role.localizationStringId)))
    }
    // MENU CALLBACKS
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (tvInvitationCodeInfo.visibility == View.VISIBLE) {
            menu.getItem(0).isVisible = false
            menu.getItem(1).isVisible = true
        } else {
            menu.getItem(0).isVisible = true
            menu.getItem(1).isVisible = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = launchUI ({
        val selectedRole = if (session.user?.role?.toRole() == Role.Creator) {
            selectedRoleMapperCreator[sUserRole.selectedItemId.toInt()]
        } else {
            selectedRoleMapperManager[sUserRole.selectedItemId.toInt()]
        }

        when (item.itemId) {
            R.id.action_send -> {
                val user = User()
                user.name = etNameField.text.toString()
                user.surname = etSurnameField.text.toString()
                user.phoneNumber = etPhoneNumberField.text.toString()
                user.role = selectedRole?.id
                val result = invitationWebservice.createAsync(user)
                if(eh.handle(result)) {
                    return@launchUI
                }
                dp.invitationView.invalidate()
                dtActivity.softKeyboard.closeSoftKeyboard()
                this@AddUserFragment.tvInvitationCodeInfo.visibility = View.VISIBLE
                this@AddUserFragment.tvInvitationCode.text = result.entity?.invitation?.invitationCode

                activity?.invalidateOptionsMenu()
            }
            R.id.action_done -> {
                navigationController.closeCurrentFragment()
            }
        }
    }, {
        super.onOptionsItemSelected(item)
    })

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_user_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    companion object {
        fun create(role: Role): AddUserFragment {
            val fragment = AddUserFragment()
            fragment.role = role
            return fragment
        }
    }
}

