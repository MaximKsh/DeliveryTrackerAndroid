package com.kvteam.deliverytracker.managerapp.usersRecycleView

import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.ScaleAnimation
import com.kvteam.deliverytracker.core.models.UserModel
import com.kvteam.deliverytracker.core.ui.AutoClearedValue
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerViewModelFactory
import com.kvteam.deliverytracker.managerapp.BR.UserListViewModel
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.databinding.FragmentManagersListBinding
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_managers_list.*
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

    val fakeUserList = mutableListOf<UserModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater?.inflate(
                R.layout.fragment_managers_list,
                container,
                false)
    }

    fun startEditMode() {
        this.userListViewModel.isInEditMode.set(true)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fakeUserList.add(UserModel(name = "Василий", surname = "Жуков", phoneNumber = "pidor"))
        fakeUserList.add(UserModel(name = "Кирилл", surname = "Сонк", phoneNumber = "pidor"))
        fakeUserList.add(UserModel(name = "Макс", surname = "Каширин", phoneNumber = "pidor"))
        fakeUserList.add(UserModel(name = "Андрей", surname = "Борзов", phoneNumber = "pidor"))
        fakeUserList.add(UserModel(name = "Петр", surname = "Первый", phoneNumber = "pidor"))
        fakeUserList.add(UserModel(name = "Марина", surname = "Киска", phoneNumber = "pidor"))
        fakeUserList.add(UserModel(name = "Артем", surname = "Лещев", phoneNumber = "pidor"))
        fakeUserList.add(UserModel(name = "Максим", surname = "Лясковский", phoneNumber = "pidor"))
        fakeUserList.add(UserModel(name = "Арен", surname = "Азибикян", phoneNumber = "pidor"))
        fakeUserList.add(UserModel(name = "Константин", surname = "Мельников", phoneNumber = "pidor"))

        this.userListViewModel = ViewModelProviders
                .of(this, vmFactory)
                .get(UsersListViewModel::class.java)

        val adapt = UsersListAdapter(userListViewModel)
        usersList.adapter = adapt

        adapt.replace(fakeUserList)
    }
}
