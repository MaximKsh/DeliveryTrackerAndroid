package com.kvteam.deliverytracker.managerapp.ui.main.adduser

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.instance.IInstanceManager
import com.kvteam.deliverytracker.core.models.UserModel
import com.kvteam.deliverytracker.core.roles.Role
import com.kvteam.deliverytracker.core.ui.AutoClearedValue
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment

import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import com.kvteam.deliverytracker.managerapp.ui.main.userslist.UserItemActions
import com.kvteam.deliverytracker.managerapp.ui.main.userslist.UserListModel
import com.kvteam.deliverytracker.managerapp.ui.main.userslist.UsersListAdapter
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_managers_list.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.fragment_add_user.*
import javax.inject.Inject


// TODO: rename managersList xml to userslist
open class AddUserFragment : DeliveryTrackerFragment(), AdapterView.OnItemSelectedListener {
    @Inject
    lateinit var navigationController: NavigationController

    @Inject
    lateinit var instanceManager: IInstanceManager

    private val layoutManagerKey = "layoutManager"
    private val usersListKey = "addingUser"
    private lateinit var savedUserData: UserModel
    private lateinit var mSubmitItem: MenuItem
    private lateinit var role: Role

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        setHasOptionsMenu(true)
        this.activity.toolbar_title.text = resources.getString(R.string.add_user)
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
        outState.putParcelable(
                usersListKey,
                this.savedUserData)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val adapter = ArrayAdapter.createFromResource(this.activity,
                R.array.roles_array, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sUserRole.adapter = adapter
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if(savedInstanceState == null) {
            return
        }
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
        when (item.itemId) {
            R.id.action_finish -> {
                invokeAsync({
                    instanceManager.inviteManager(UserModel(
                            name = etNameField.text.toString(),
                            surname = etSurnameField.text.toString(),
                            phoneNumber = etPhoneNumberField.text.toString()
                    ))
                }, {
                   if (it != null) {
                       val view =  this@AddUserFragment.activity.currentFocus
                       if (view != null) {
                           val imm = this@AddUserFragment.activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                           imm.hideSoftInputFromWindow(view.windowToken, 0)
                       }
                       this@AddUserFragment.tvInvitationCodeInfo.visibility = View.VISIBLE
                       this@AddUserFragment.tvInvitationCode.text = it.invitationCode
                   }
                })
//                navigationController.closeCurrentFragment()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_add_user_menu, menu)

        this.mSubmitItem = menu.findItem(R.id.action_finish)

        this.activity.toolbar_left_action.setOnClickListener { _ ->
            navigationController.closeCurrentFragment()
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    companion object {
        fun create(role: Role): AddUserFragment {
            val fragment = AddUserFragment()
            fragment.role = role
            return fragment
        }
    }
}

