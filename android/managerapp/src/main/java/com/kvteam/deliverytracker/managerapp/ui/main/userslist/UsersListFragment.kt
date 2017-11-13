package com.kvteam.deliverytracker.managerapp.ui.main.userslist

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.kvteam.deliverytracker.core.models.UserModel
import com.kvteam.deliverytracker.core.ui.AutoClearedValue
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.adduser.AddUserFragment
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_manager_list_item.*
import kotlinx.android.synthetic.main.fragment_managers_list.*
import kotlinx.android.synthetic.main.toolbar.*

interface UserItemActions {
    fun onCallClick(user: UserModel)
    fun onChatClick(user: UserModel)
    fun onSelectClick(userListModel: UserListModel)
    fun onItemClick(user: UserModel)
}

// TODO: rename managersList xml to userslist
open class UsersListFragment : DeliveryTrackerFragment() {
    protected val layoutManagerKey = "layoutManager"
    protected val usersListKey = "usersList"
    var isInEditMode: Boolean = false

    lateinit var mAddMenuItem: MenuItem
    lateinit var mRemoveMenuItem: MenuItem
    lateinit var mEditMenuItem: MenuItem

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
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if(outState == null) {
            return
        }
        outState.putParcelableArray(
                usersListKey,
                this.adapter.value?.items?.toTypedArray())
        outState.putParcelable(
                layoutManagerKey,
                this.rvUsersList.layoutManager.onSaveInstanceState())
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if(savedInstanceState == null) {
            return
        }

        this.adapter.value?.items?.clear()
        this.adapter.value?.items?.addAll(
                savedInstanceState.getParcelableArray(usersListKey).map { it as UserListModel })
        this.rvUsersList.layoutManager.onRestoreInstanceState(
                savedInstanceState.getParcelable(layoutManagerKey))
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
        mAddMenuItem.setVisible(false)
        mEditMenuItem.setVisible(true)
        mRemoveMenuItem.setVisible(false)
    }

    private fun setRemoveButtonVisible() {
        mAddMenuItem.setVisible(false)
        mEditMenuItem.setVisible(false)
        mRemoveMenuItem.setVisible(true)
    }

    private fun setAddButtonVisible() {
        mAddMenuItem.setVisible(true)
        mEditMenuItem.setVisible(false)
        mRemoveMenuItem.setVisible(false)
    }

    private fun setCancelButtonVisible(visibility: Boolean) {
        this.activity.toolbar_left_action.text = if (visibility) resources.getString(R.string.cancel) else ""
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit -> {
                this.startEditMode()
                this.activity.invalidateOptionsMenu()
            }
            R.id.action_remove -> {
                val deleteUsersDialog = AlertDialog.Builder(activity)
                deleteUsersDialog.setMessage(R.string.delete_users_modal)
                        .setPositiveButton("Удалить", { dialogInterface, id ->
                            this.adapter.value?.items?.removeAll { userListModel -> userListModel.isSelected }
                            this.adapter.value?.notifyDataSetChanged()
                            this.stopEditMode()
                            this.setEditButtonVisible()
                            this.setCancelButtonVisible(false)
                            this.clearSelectedUsers()
                        })
                        .setNegativeButton("Отменить", { dialogInterface, id ->

                        })
                deleteUsersDialog.show()
            }
            R.id.action_add -> {
                val addUserFragment = AddUserFragment()
                val data = Bundle()
                data.putString("isFor", "managers")
                addUserFragment.arguments = data
                val transaction = activity.supportFragmentManager.beginTransaction()
                transaction.replace(R.id.mainContainer, addUserFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun startAddUserFragment() {

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

        super.onCreateOptionsMenu(menu, inflater);
    }
}
