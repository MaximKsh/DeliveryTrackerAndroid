package com.kvteam.deliverytracker.managerapp.ui.main.userslist

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.dataprovider.DataProviderGetOrigin
import com.kvteam.deliverytracker.core.dataprovider.NetworkException
import com.kvteam.deliverytracker.core.models.Invitation
import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.roles.Role
import com.kvteam.deliverytracker.core.roles.toRole
import com.kvteam.deliverytracker.core.ui.BaseListFragment
import com.kvteam.deliverytracker.core.ui.BaseListHeader
import com.kvteam.deliverytracker.core.ui.IBaseListItemActions
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import eu.davidea.flexibleadapter.FlexibleAdapter
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Duration
import javax.inject.Inject

// TODO: rename managersList xml to userslist
open class UsersListFragment : BaseListFragment() {

    override var viewGroup = "UserViewGroup"

    override val tracer
            get() = navigationController.fragmentTracer

    override val defaultHeader by lazy { lm.getString(R.string.ServerMessage_Views_ManagersView) }

    @Inject
    lateinit var navigationController: NavigationController

    private val userActions = object: IBaseListItemActions<UserListItem> {
        override suspend fun onDelete(adapter: FlexibleAdapter<*>, itemList: MutableList<UserListItem>, item: UserListItem) {
            if(adapter !is UserListFlexibleAdapter) {
                return
            }
            try {
                dp.users.deleteAsync(item.user.id!!)
            } catch (e: NetworkException) {
                eh.handle(e.result)
                return
            }
            itemList.remove(item)
            adapter.updateDataSet(itemList, true)
        }

        override suspend fun onItemClicked(adapter: FlexibleAdapter<*>, itemList: MutableList<UserListItem>, item: UserListItem) {
        }
    }

    private val invitationActions = object: IBaseListItemActions<UserInvitationListItem> {
        override suspend fun onItemClicked(adapter: FlexibleAdapter<*>, itemList: MutableList<UserInvitationListItem>, item: UserInvitationListItem) {
        }

        override suspend fun onDelete(adapter: FlexibleAdapter<*>, itemList: MutableList<UserInvitationListItem>, item: UserInvitationListItem) {
            if(adapter !is UserInvitationListFlexibleAdapter) {
                return
            }
            try {
                dp.invitations.deleteAsync(item.invitation.id!!)
            } catch (e: NetworkException) {
                eh.handle(e.result)
                return
            }
            itemList.remove(item)
            adapter.updateDataSet(itemList, true)
        }
    }

    private var role: Role = Role.Manager

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
                .map { usr ->
                    val user = usr
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
        val adapter = mAdapter as? UserListFlexibleAdapter
        if (adapter != null) {
            adapter.updateDataSet(userList, animate)
        } else {
            mAdapter = UserListFlexibleAdapter(userList, userActions)
        }
    }

    override fun handleInvitations(invitations: List<Invitation>, animate: Boolean) {
        val headerThisWeek = BaseListHeader("This week")
        val headerPreviousWeek = BaseListHeader("Previous week")
        // TODO: fix this fate range
        val headerLongTimeAgo = BaseListHeader("Long time ago")

        val invitationList = invitations
                .sortedByDescending { a -> a.created }
                .map { invitation ->
                    val header: BaseListHeader
                    val dateValue = invitation.created!!
                    val duration = Duration(dateValue, DateTime.now(DateTimeZone.UTC))
                    when (duration.standardDays) {
                        in 0..7 -> {
                            header = headerThisWeek
                        }
                        in 8..14 -> {
                            header = headerPreviousWeek
                        }
                        else -> {
                            header = headerLongTimeAgo
                        }
                    }
                    UserInvitationListItem(invitation, header, lm)
                }.toMutableList()

        val adapter = mAdapter as? UserInvitationListFlexibleAdapter
        if (adapter != null) {
            adapter.updateDataSet(invitationList, animate)
        } else {
            mAdapter = UserInvitationListFlexibleAdapter(invitationList, invitationActions)
        }
    }

    override fun getViewFilterArguments(viewName: String, type: String?, groupIndex: Int, value: String): Map<String, Any>? {
        return mapOf("search" to value)
    }

    override fun configureFloatingActionButton(button: FloatingActionButton) {
        button.visibility = View.VISIBLE
        button.setImageResource(R.drawable.ic_person_add_black_24dp)
        button.setOnClickListener {
            navigationController.navigateToAddUser(this.role)
        }
        eh.handleNoInternetWarn(DataProviderGetOrigin.CACHE)
    }

    override fun refreshMenuItems() {
        if(toolbarController.isSearchEnabled) {
            setMenuMask(0)
        } else {
            setMenuMask(1)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mAdapter = UserListFlexibleAdapter(mutableListOf(),userActions)
        super.onActivityCreated(savedInstanceState)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> search()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}

