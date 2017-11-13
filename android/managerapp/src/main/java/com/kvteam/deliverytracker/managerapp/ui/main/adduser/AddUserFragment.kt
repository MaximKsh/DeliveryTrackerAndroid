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
import android.widget.*
import com.kvteam.deliverytracker.core.models.UserModel
import com.kvteam.deliverytracker.core.ui.AutoClearedValue
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment

import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.userslist.UserItemActions
import com.kvteam.deliverytracker.managerapp.ui.main.userslist.UserListModel
import com.kvteam.deliverytracker.managerapp.ui.main.userslist.UsersListAdapter
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_managers_list.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.fragment_add_user.*


// TODO: rename managersList xml to userslist
open class AddUserFragment : DeliveryTrackerFragment(), AdapterView.OnItemSelectedListener {
    private val layoutManagerKey = "layoutManager"
    private val usersListKey = "addingUser"
    private lateinit var savedUserData: UserModel
    private lateinit var mSubmitItem: MenuItem
    private lateinit var isFor: String

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        setHasOptionsMenu(true)
        isFor = arguments.getString("isFor")
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
                Toast.makeText(this.activity, "Приглашение успешно отправлено", Toast.LENGTH_LONG).show()
                fragmentManager.popBackStack()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_add_user_menu, menu)

        this.mSubmitItem = menu.findItem(R.id.action_finish)

        this.activity.toolbar_left_action.setOnClickListener { _ ->
            fragmentManager.popBackStack()
        }

        super.onCreateOptionsMenu(menu, inflater);
    }
}

