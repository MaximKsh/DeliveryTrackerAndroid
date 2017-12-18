package com.kvteam.deliverytracker.managerapp.ui.main.adduser

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.common.IErrorManager
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.instance.IInstanceManager
import com.kvteam.deliverytracker.core.models.UserModel
import com.kvteam.deliverytracker.core.roles.Role
import com.kvteam.deliverytracker.core.roles.toRole
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.ErrorDialog
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_add_user.*
import kotlinx.android.synthetic.main.toolbar.*
import javax.inject.Inject


class AddUserFragment : DeliveryTrackerFragment(), AdapterView.OnItemSelectedListener {
    @Inject
    lateinit var navigationController: NavigationController

    @Inject
    lateinit var instanceManager: IInstanceManager

    @Inject
    lateinit var errorManager: IErrorManager

    @Inject
    lateinit var lm: ILocalizationManager

    private val userRoleKey = "userRoleKey"
    private val userNameKey = "userNameKey"
    private val userSurnameKey = "userSurnameKey"
    private val userPhoneKey = "userPhoneKey"
    private val inviteCodeKey = "inviteCodeKey"

    private val selectedRoleMapper = mapOf(
            0 to Role.Manager,
            1 to Role.Performer
    )

    private lateinit var mSubmitItem: MenuItem
    private lateinit var role: Role

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater?,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_add_user, container, false)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if(outState == null) {
            return
        }
        outState.putString(
                userRoleKey, this.role.simpleName
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val adapter = ArrayAdapter.createFromResource(this.activity,
                R.array.roles_array, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sUserRole.adapter = adapter
        this.activity.toolbar_title.text = resources.getString(R.string.ManagerApp_Toolbar_AddUser)

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

    // Role selection callbacks
    override fun onItemSelected(parent: AdapterView<*>, view: View,
                       pos: Int, id: Long) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
    }

    // MENU CALLBACKS
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (tvInvitationCodeInfo.visibility == View.VISIBLE) {
            navigationController.closeCurrentFragment()
            return super.onOptionsItemSelected(item)
        }

        item.title = lm.getString(R.string.ManagerApp_Toolbar_Finish)
        val selectedRole = selectedRoleMapper[sUserRole.selectedItemId.toInt()]
        val inviteFunction = if (selectedRole == Role.Performer)
            instanceManager::invitePerformer
        else
            instanceManager::inviteManager

        when (item.itemId) {
            R.id.action_finish -> {
                invokeAsync({
                    inviteFunction(UserModel(
                            name = etNameField.text.toString(),
                            surname = etSurnameField.text.toString(),
                            phoneNumber = etPhoneNumberField.text.toString()
                    ))
                }, {
                   if (it.success) {
                       val view =  this@AddUserFragment.activity.currentFocus
                       if (view != null) {
                           val imm = this@AddUserFragment.activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                           imm.hideSoftInputFromWindow(view.windowToken, 0)
                       }
                       this@AddUserFragment.tvInvitationCodeInfo.visibility = View.VISIBLE
                       this@AddUserFragment.tvInvitationCode.text = it.entity?.invitationCode
                   } else {
                       val dialog = ErrorDialog(this@AddUserFragment.context)
                       if(it.errorChainId != null) {
                           dialog.addChain(errorManager.getAndRemove(it.errorChainId!!)!!)
                       }
                       dialog.show()
                   }
                })
//                navigationController.closeCurrentFragment()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_add_user_menu, menu)

        mSubmitItem = menu.findItem(R.id.action_finish)

        activity.toolbar_left_action.setOnClickListener { _ ->
            navigationController.closeCurrentFragment()
        }

        if(tvInvitationCodeInfo.visibility == View.VISIBLE) {
            mSubmitItem.title = lm.getString(R.string.ManagerApp_Toolbar_Finish)
        }

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

