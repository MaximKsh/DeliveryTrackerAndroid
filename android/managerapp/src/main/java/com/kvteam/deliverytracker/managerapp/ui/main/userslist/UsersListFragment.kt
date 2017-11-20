package com.kvteam.deliverytracker.managerapp.ui.main.userslist

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.models.UserModel
import com.kvteam.deliverytracker.core.roles.Role
import com.kvteam.deliverytracker.core.ui.AutoClearedValue
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_managers_list.*
import kotlinx.android.synthetic.main.toolbar.*
import javax.inject.Inject

// TODO: rename managersList xml to userslist
open class UsersListFragment : DeliveryTrackerFragment() {
    protected val layoutManagerKey = "layoutManager"
    protected val usersListKey = "usersList"
    lateinit var role: Role
    var isInEditMode: Boolean = false

    lateinit var mAddMenuItem: MenuItem
    lateinit var mRemoveMenuItem: MenuItem
    lateinit var mEditMenuItem: MenuItem

    @Inject
    lateinit var navigationController: NavigationController

    val userItemActions = object: UserItemActions {
        override fun onCallClick(user: UserModel) {
            if(user.phoneNumber != null
                    && (ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)) {
                val intent = Intent(Intent.ACTION_CALL)
                intent.data = Uri.parse("tel:${user.phoneNumber}")
                activity.startActivity(intent)
            }
        }

        override fun onChatClick(user: UserModel) {
            TODO("not implemented")
        }

        override fun onItemClick(user: UserModel) {
            TODO("not implemented")
        }

        override fun onSelectClick(userListModel: UserListModel) {
            activity.invalidateOptionsMenu()
            userListModel.isSelected = !userListModel.isSelected
        }
    }

    protected lateinit var adapter: AutoClearedValue<UsersListAdapter>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater?,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_managers_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.rvUsersList.layoutManager = LinearLayoutManager(
                this.activity.applicationContext,
                LinearLayoutManager.VERTICAL,
                false)

        this.adapter = AutoClearedValue(
                this,
                UsersListAdapter(userItemActions),
                {
                    // TODO how correctly clean it?
                    it?.userItemActions = null
                })
        this.rvUsersList.adapter = this.adapter.value

        savedInstanceState?.apply {
            if(rvUsersList?.layoutManager != null){
                adapter.value?.items?.clear()
                adapter.value?.items?.addAll(
                        savedInstanceState.getParcelableArray(usersListKey).map { it as UserListModel })
                rvUsersList.layoutManager.onRestoreInstanceState(
                        savedInstanceState.getParcelable(layoutManagerKey))
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.apply {
            if(rvUsersList?.layoutManager != null) {
                putParcelableArray(
                        usersListKey,
                        adapter.value?.items?.toTypedArray())
                putParcelable(
                        layoutManagerKey,
                        rvUsersList.layoutManager.onSaveInstanceState())
            }
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if(savedInstanceState == null) {
            return
        }


    }

    private fun startEditMode() {
        this.isInEditMode = true
        this.adapter.value?.items?.forEach { item -> item.isInEditMode = true }
        this.adapter.value?.notifyDataSetChanged()
    }

    private fun stopEditMode() {
        this.isInEditMode = false
        this.adapter.value?.items?.forEach { item -> item.isInEditMode = false }
        this.adapter.value?.notifyDataSetChanged()
    }

    private fun clearSelectedUsers() {
        this.adapter.value?.items?.forEach { item -> item.isSelected = false }
        this.adapter.value?.notifyDataSetChanged()
    }

    private fun setEditButtonVisible() {
        mAddMenuItem.isVisible = false
        mEditMenuItem.isVisible = true
        mRemoveMenuItem.isVisible = false
    }

    private fun setRemoveButtonVisible() {
        mAddMenuItem.isVisible = false
        mEditMenuItem.isVisible = false
        mRemoveMenuItem.isVisible = true
    }

    private fun setAddButtonVisible() {
        mAddMenuItem.isVisible = true
        mEditMenuItem.isVisible = false
        mRemoveMenuItem.isVisible = false
    }

    private fun setCancelButtonVisible(visibility: Boolean) {
        this.activity.toolbar_left_action.text =
                if (visibility) resources.getString(R.string.cancel)
                else EMPTY_STRING
    }

    // MENU CALLBACKS
    override fun onPrepareOptionsMenu(menu: Menu) {
        if (isInEditMode) {
            this.setCancelButtonVisible(true)
            if (this.adapter.value?.items!!.any { userListModel -> userListModel.isSelected }) {
                this.setRemoveButtonVisible()
            } else {
                this.setAddButtonVisible()
            }
        } else {
            this.setCancelButtonVisible(false)
            this.setEditButtonVisible()
        }
        super.onPrepareOptionsMenu(menu)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.setCancelButtonVisible(false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit -> {
                this.startEditMode()
                this.activity.invalidateOptionsMenu()
            }
            R.id.action_remove -> {
                val deleteUsersDialog = AlertDialog.Builder(activity)
                deleteUsersDialog.setMessage(R.string.delete_users_modal)
                        .setPositiveButton(getString(R.string.deleteButton), { _, _ ->
                            this.adapter.value?.items?.removeAll { userListModel -> userListModel.isSelected }
                            this.adapter.value?.notifyDataSetChanged()
                            this.stopEditMode()
                            this.setEditButtonVisible()
                            this.setCancelButtonVisible(false)
                            this.clearSelectedUsers()
                        })
                        .setNegativeButton(getString(R.string.cancel), { _, _ ->

                        })
                deleteUsersDialog.show()
            }
            R.id.action_add -> {
                navigationController.navigateToAddUser(this.role)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_managers_tab_menu, menu)

        this.mAddMenuItem = menu.findItem(R.id.action_add)
        this.mEditMenuItem = menu.findItem(R.id.action_edit)
        this.mRemoveMenuItem = menu.findItem(R.id.action_remove)

        this.activity.toolbar_left_action.setOnClickListener { _ ->
            this.setCancelButtonVisible(false)
            this.setEditButtonVisible()
            this.stopEditMode()
            this.clearSelectedUsers()
        }

        super.onCreateOptionsMenu(menu, inflater)
    }
}

