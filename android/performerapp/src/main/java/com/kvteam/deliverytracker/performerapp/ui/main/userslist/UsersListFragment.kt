package com.kvteam.deliverytracker.performerapp.ui.main.userslist

import android.os.Bundle
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.roles.Role
import com.kvteam.deliverytracker.core.roles.toRole
import com.kvteam.deliverytracker.core.ui.BaseListFragment
import com.kvteam.deliverytracker.core.ui.BaseListHeader
import com.kvteam.deliverytracker.core.ui.IBaseListItemActions
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController
import com.kvteam.deliverytracker.performerapp.R
import com.kvteam.deliverytracker.performerapp.ui.main.NavigationController
import eu.davidea.flexibleadapter.FlexibleAdapter
import javax.inject.Inject

// TODO: rename managersList xml to userslist
open class UsersListFragment : BaseListFragment() {

    override var viewGroup = "UserViewGroup"

    override val tracer
            get() = navigationController.fragmentTracer

    override val defaultHeader: String
        get() = lm.getString(R.string.ServerMessage_Views_ManagersView)

    @Inject
    lateinit var navigationController: NavigationController


    private val userActions = object: IBaseListItemActions<UserListItem> {
        override suspend fun onDelete(adapter: FlexibleAdapter<*>, itemList: MutableList<UserListItem>, item: UserListItem) {
        }

        override suspend fun onItemClicked(adapter: FlexibleAdapter<*>, itemList: MutableList<UserListItem>, item: UserListItem) {
        }
    }


    override fun handleUsers(users: List<User>, animate: Boolean) {
        var letter: Char? = null
        var header = BaseListHeader(lm.getString(R.string.ServerMessage_Roles_CreatorRole))
        val emptyHeader = BaseListHeader(EMPTY_STRING)

        val userList = users
                .sortedWith (Comparator { u1, u2 ->
                    if(u1.role?.toRole() == Role.Creator) -1
                    else if(u2.role?.toRole() == Role.Creator) 1
                    else u1.surname?.compareTo(u2.surname ?: EMPTY_STRING) ?: 0
                })
                .map { user ->
                    if (user.role?.toRole() == Role.Creator) {
                        UserListItem(user, header)
                    } else {
                        if(user.surname.isNullOrBlank()) {
                            return@map UserListItem(user, emptyHeader)
                        }
                        if (letter == null || letter != user.surname!![0]) {
                            letter = user.surname!![0]
                            header = BaseListHeader(letter!!.toString())
                        }
                        UserListItem(user, header)
                    }
                }.toMutableList()
        updateDataSet(userList, { UserListFlexibleAdapter(userActions) }, animate)
    }

    override fun getViewFilterArguments(viewName: String, type: String?, groupIndex: Int, value: String): Map<String, Any>? {
        return mapOf("search" to value)
    }

    override fun configureToolbar(toolbar: ToolbarController) {
        toolbarController.enableDropdown()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mAdapter = UserListFlexibleAdapter(userActions)
        super.onActivityCreated(savedInstanceState)
        (mAdapter as UserListFlexibleAdapter).hideDeleteButton = true
    }
}

