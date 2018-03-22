package com.kvteam.deliverytracker.managerapp.ui.main.userslist

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.dataprovider.NetworkException
import com.kvteam.deliverytracker.core.models.Invitation
import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.roles.Role
import com.kvteam.deliverytracker.core.roles.toRole
import com.kvteam.deliverytracker.core.ui.BaseListFragment
import com.kvteam.deliverytracker.core.ui.BaseListHeader
import com.kvteam.deliverytracker.core.ui.IBaseListItemActions
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import eu.davidea.flexibleadapter.FlexibleAdapter
import javax.inject.Inject

// TODO: rename managersList xml to userslist
open class UsersListFragment : BaseListFragment() {

    override var viewGroup = "UserViewGroup"

    override val tracer = navigationController.fragmentTracer

    @Inject
    lateinit var navigationController: NavigationController


    private val INVITE_USER_MENU_ITEM = 1
    private val SHOW_ON_MAP_MENU_ITEM = INVITE_USER_MENU_ITEM shl 1

    private val IVITATIONS_MENU_MASK = 0
    private val MANAGERS_MENU_MASK = INVITE_USER_MENU_ITEM
    private val PERFORMERS_MENU_MASK = INVITE_USER_MENU_ITEM and SHOW_ON_MAP_MENU_ITEM

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

    override fun handleUsers(users: List<User>) {
        var letter: Char? = null
        var header = BaseListHeader(lm.getString(R.string.ServerMessage_Roles_CreatorRole))

        val userList = users
                .sortedWith (Comparator<User> { u1, u2 ->
                    if(u1.role?.toRole() == Role.Creator) -1
                    else if(u2.role?.toRole() == Role.Creator) 1
                    else u1.surname?.compareTo(u2.surname ?: EMPTY_STRING) ?: 0
                })
                .map { user ->
                    if (user.role?.toRole() == Role.Creator) {
                        UserListItem(user, header)
                    } else {
                        if (letter == null || letter != user.surname!![0]) {
                            letter = user.surname!![0]
                            header = BaseListHeader(letter!!.toString())
                        }
                        UserListItem(user, header)
                    }
                }.toMutableList()
        val adapter = mAdapter as? UserListFlexibleAdapter
        setMenuMask(MANAGERS_MENU_MASK)
        if (adapter != null) {
            adapter.updateDataSet(userList, true)
        } else {
            mAdapter = UserListFlexibleAdapter(userList, userActions)
            initAdapter()
        }
    }

    override fun handleInvitations(invitations: List<Invitation>) {
        var date: String? = null
        var header = BaseListHeader("A")

        val invitationList = invitations
                .sortedByDescending { a -> a.created }
                .map { invitation ->
                    val dateCaption = invitation.created?.toString("dd.MM.yyyy") ?: EMPTY_STRING
                    if (date == null || date != dateCaption) {
                        date = dateCaption
                        header = BaseListHeader(dateCaption)
                    }
                    UserInvitationListItem(invitation, header, lm)
                }.toMutableList()
        val adapter = mAdapter as? UserInvitationListFlexibleAdapter
        setMenuMask(IVITATIONS_MENU_MASK)
        if (adapter != null) {
            adapter.updateDataSet(invitationList, true)
        } else {
            mAdapter = UserInvitationListFlexibleAdapter(invitationList, invitationActions)
            initAdapter()
        }
    }

    override fun getViewFilterArguments(viewName: String, type: String?, groupIndex: Int, value: String): Map<String, Any>? {
        return mapOf("search" to value)
    }

    override fun configureToolbar(toolbar: ToolbarController) {
        toolbarController.enableDropdown()
        useSearchInToolbar(toolbar)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mAdapter = UserListFlexibleAdapter(mutableListOf(),userActions)
        super.onActivityCreated(savedInstanceState)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_show_on_map -> {

            }
            R.id.action_add -> {
                navigationController.navigateToAddUser(this.role)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_user_list_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}

