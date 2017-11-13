package com.kvteam.deliverytracker.managerapp.ui.main.userslist

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.kvteam.deliverytracker.core.instance.IInstanceManager
import com.kvteam.deliverytracker.core.models.UserModel
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import com.kvteam.deliverytracker.managerapp.ui.main.userslist.UsersListFragment
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.toolbar.*
import javax.inject.Inject

class ManagersListFragment: UsersListFragment() {
    @Inject
    lateinit var instanceManager: IInstanceManager

    var fakeUserList = mutableListOf<UserModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if(savedInstanceState != null) {
            return
        }

        this.activity.toolbar_title.text = resources.getString(R.string.managers)

        fakeUserList.add(UserModel(username = "1111", name = "Василий", surname = "Жуков", phoneNumber = "88005553535"))
        fakeUserList.add(UserModel(username = "2222", name = "Кирилл", surname = "Сонк", phoneNumber = "88005553535"))
        fakeUserList.add(UserModel(username = "3333", name = "Макс", surname = "Каширин", phoneNumber = "88005553535"))
        fakeUserList.add(UserModel(username = "4444", name = "Андрей", surname = "Борзов", phoneNumber = "88005553535"))
        fakeUserList.add(UserModel(username = "5555", name = "Петр", surname = "Первый", phoneNumber = "88005553535"))
        fakeUserList.add(UserModel(username = "6666", name = "Марина", surname = "Киска", phoneNumber = "88005553535"))
        fakeUserList.add(UserModel(username = "7777", name = "Артем", surname = "Лещев", phoneNumber = "88005553535"))
        fakeUserList.add(UserModel(username = "8888", name = "Максим", surname = "Лясковский", phoneNumber = "88005553535"))
        fakeUserList.add(UserModel(username = "9999", name = "Арен", surname = "Азибикян", phoneNumber = "88005553535"))
        fakeUserList.add(UserModel(username = "1010", name = "Константин", surname = "Мельников", phoneNumber = "88005553535"))

        val fakeUserListModel = fakeUserList.map { userModel ->
            UserListModel(false,false, userModel)
        }

        adapter.value?.items?.addAll(fakeUserListModel)
        adapter.value?.notifyDataSetChanged()

//        invokeAsync({
//            //instanceManager.getManagers()
//            var cnt = 0
//            listOf(
//                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
//                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
//                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
//                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
//                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
//                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
//                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
//                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
//                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
//                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
//                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
//                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
//                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
//                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
//                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
//                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
//                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
//                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
//                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
//                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
//                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
//                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
//                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
//                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
//                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
//                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
//                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
//                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
//                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
//                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
//                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
//                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
//                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
//                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
//                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
//                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
//                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
//                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
//                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
//                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
//                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
//                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() )
//            )
//        }, {
//            if(it != null) {
//                adapter.value?.items?.addAll(it)
//                adapter.value?.notifyDataSetChanged()
//            }
//        })
    }
}
