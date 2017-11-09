package com.kvteam.deliverytracker.managerapp.usersRecycleView

import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.view.animation.ScaleAnimation
import com.kvteam.deliverytracker.core.models.UserModel
import com.kvteam.deliverytracker.core.ui.AutoClearedValue
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerViewModelFactory
import com.kvteam.deliverytracker.managerapp.BR.UserListViewModel
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.databinding.FragmentManagersListBinding
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_manager_list_item.*
import kotlinx.android.synthetic.main.fragment_managers_list.*
import kotlinx.android.synthetic.main.toolbar.*
import javax.inject.Inject


data class MockUserData(
    val name: String,
    val surname: String,
    val avatarUrl: String
)

class ManagersListFragment : DeliveryTrackerFragment() {
    @Inject
    lateinit var vmFactory: DeliveryTrackerViewModelFactory

    lateinit var userListViewModel: UsersListViewModel

    lateinit var mAddMenuItem: MenuItem
    lateinit var mRemoveMenuItem: MenuItem
    lateinit var mEditMenuItem: MenuItem

    val fakeUserList = mutableListOf<UserModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater?.inflate(
                R.layout.fragment_managers_list,
                container,
                false)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val isInEditMode = this.userListViewModel.isInEditMode.get()
        if (isInEditMode) {
            if (this.userListViewModel.selectedUsersList.size > 0) {
                this.activity.toolbar_left_action.text = resources.getString(R.string.cancel)
                mAddMenuItem.setVisible(false)
                mEditMenuItem.setVisible(false)
                mRemoveMenuItem.setVisible(true)
            } else {
                this.activity.toolbar_left_action.text = resources.getString(R.string.cancel)
                mEditMenuItem.setVisible(false)
                mAddMenuItem.setVisible(true)
            }
        } else if (!isInEditMode) {
            this.activity.toolbar_left_action.text = ""
            mAddMenuItem.setVisible(false)
            mEditMenuItem.setVisible(true)
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
                this.stopEditMode()
                this.activity.invalidateOptionsMenu()
            }
            R.id.action_add -> {

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
            this.activity.toolbar_left_action.text = ""
            mAddMenuItem.setVisible(false)
            mEditMenuItem.setVisible(true)
            this.clearSelectedUsers()
            this.stopEditMode()
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    fun startEditMode() {
        this.userListViewModel.isInEditMode.set(true)
    }

    fun stopEditMode() {
        this.userListViewModel.isInEditMode.set(false)
    }

    fun clearSelectedUsers() {
        this.userListViewModel.selectedUsersList.clear()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fakeUserList.add(UserModel(username = "1111", name = "Василий", surname = "Жуков", phoneNumber = "pidor"))
        fakeUserList.add(UserModel(username = "2222", name = "Кирилл", surname = "Сонк", phoneNumber = "pidor"))
        fakeUserList.add(UserModel(username = "3333", name = "Макс", surname = "Каширин", phoneNumber = "pidor"))
        fakeUserList.add(UserModel(username = "4444", name = "Андрей", surname = "Борзов", phoneNumber = "pidor"))
        fakeUserList.add(UserModel(username = "5555", name = "Петр", surname = "Первый", phoneNumber = "pidor"))
        fakeUserList.add(UserModel(username = "6666", name = "Марина", surname = "Киска", phoneNumber = "pidor"))
        fakeUserList.add(UserModel(username = "7777", name = "Артем", surname = "Лещев", phoneNumber = "pidor"))
        fakeUserList.add(UserModel(username = "8888", name = "Максим", surname = "Лясковский", phoneNumber = "pidor"))
        fakeUserList.add(UserModel(username = "9999", name = "Арен", surname = "Азибикян", phoneNumber = "pidor"))
        fakeUserList.add(UserModel(username = "1010", name = "Константин", surname = "Мельников", phoneNumber = "pidor"))

        this.activity.toolbar_title.text = resources.getString(R.string.managers)

        this.userListViewModel = ViewModelProviders
                .of(this, vmFactory)
                .get(UsersListViewModel::class.java)

        val self = this
        val onListChangedCallback = object: ObservableList.OnListChangedCallback<ObservableArrayList<UserModel>>() {
            override fun onItemRangeChanged(sender: ObservableArrayList<UserModel>?, positionStart: Int, itemCount: Int) {
                self.activity.invalidateOptionsMenu()
            }

            override fun onItemRangeRemoved(sender: ObservableArrayList<UserModel>?, positionStart: Int, itemCount: Int) {
                self.activity.invalidateOptionsMenu()
            }

            override fun onItemRangeInserted(sender: ObservableArrayList<UserModel>?, positionStart: Int, itemCount: Int) {
                self.activity.invalidateOptionsMenu()
            }

            override fun onItemRangeMoved(sender: ObservableArrayList<UserModel>?, fromPosition: Int, toPosition: Int, itemCount: Int) {
                self.activity.invalidateOptionsMenu()
            }

            override fun onChanged(sender: ObservableArrayList<UserModel>?) {
                self.activity.invalidateOptionsMenu()
            }
        }

        this.userListViewModel.selectedUsersList.addOnListChangedCallback(onListChangedCallback)

        val adapt = UsersListAdapter(userListViewModel)
        usersList.adapter = adapt

        adapt.replace(fakeUserList)
    }
}
