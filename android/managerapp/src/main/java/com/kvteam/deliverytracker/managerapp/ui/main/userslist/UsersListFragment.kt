package com.kvteam.deliverytracker.managerapp.ui.main.userslist

import `in`.srain.cube.views.ptr.PtrDefaultHandler
import `in`.srain.cube.views.ptr.PtrFrameLayout
import `in`.srain.cube.views.ptr.PtrHandler
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.*
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.models.Invitation
import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.roles.Role
import com.kvteam.deliverytracker.core.roles.toRole
import com.kvteam.deliverytracker.core.ui.BaseListHeader
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.IBaseListItemActions
import com.kvteam.deliverytracker.core.webservice.IInvitationWebservice
import com.kvteam.deliverytracker.core.webservice.IUserWebservice
import com.kvteam.deliverytracker.core.webservice.IViewWebservice
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.common.dropdowntop.DropdownTop
import com.kvteam.deliverytracker.managerapp.ui.common.dropdowntop.DropdownTopItemInfo
import com.kvteam.deliverytracker.managerapp.ui.main.MainActivity
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.SelectableAdapter
import kotlinx.android.synthetic.main.fragment_user_list.*
import javax.inject.Inject

// TODO: rename managersList xml to userslist
open class UsersListFragment : DeliveryTrackerFragment() {

    @Inject
    lateinit var navigationController: NavigationController

    @Inject
    lateinit var viewWebservice: IViewWebservice

    @Inject
    lateinit var userWebservice: IUserWebservice

    @Inject
    lateinit var invitationWebservice: IInvitationWebservice

    @Inject
    lateinit var lm: ILocalizationManager

    private val dropDownTop: DropdownTop by lazy {
        (activity as MainActivity).dropDownTop
    }

    private val userActions = object: IBaseListItemActions<UserListItem> {
        override fun onDelete(adapter: FlexibleAdapter<*>, itemList: MutableList<UserListItem>, item: UserListItem) {
            if(adapter !is UserListFlexibleAdapter) {
                return
            }
            invokeAsync({
                userWebservice.delete(item.user.id!!)
            }, {
                if(it.success) {
                    itemList.remove(item)
                    adapter.updateDataSet(itemList, true)
                }
            })
        }

        override fun onItemClicked(adapter: FlexibleAdapter<*>, itemList: MutableList<UserListItem>, item: UserListItem) {
        }
    }

    private val invitationActions = object: IBaseListItemActions<UserInvitationListItem> {
        override fun onItemClicked(adapter: FlexibleAdapter<*>, itemList: MutableList<UserInvitationListItem>, item: UserInvitationListItem) {
        }

        override fun onDelete(adapter: FlexibleAdapter<*>, itemList: MutableList<UserInvitationListItem>, item: UserInvitationListItem) {
            if(adapter !is UserInvitationListFlexibleAdapter) {
                return
            }
            invokeAsync({
                invitationWebservice.delete(item.invitation.invitationCode!!)
            }, {
                if(it.success) {
                    itemList.remove(item)
                    adapter.updateDataSet(itemList, true)
                }
            })
        }
    }

    // TODO: выбирать добавляемую роль по сложной логике
    private var role: Role = Role.Manager

    private lateinit var mAdapter: FlexibleAdapter<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user_list, container, false)
    }

    private fun formatUsers(viewResult: List<Map<String, Any?>>): MutableList<UserListItem> {
        var letter: Char? = null
        var header = BaseListHeader(lm.getString(R.string.ServerMessage_Roles_CreatorRole))

        return viewResult
                .map { userMap ->
                    val user = User()
                    user.fromMap(userMap)
                    user
                }
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
    }

    private fun formatInvitations(viewResult: List<Map<String, Any?>>): MutableList<UserInvitationListItem> {
        var date: String? = null
        var header = BaseListHeader("A")

        return viewResult
                .map { invitationMap ->
                    val invitation = Invitation()
                    invitation.fromMap(invitationMap)
                    invitation
                }
                .sortedByDescending { a -> a.created }
                .map { invitation ->
                    val dateCaption = invitation.created?.toString("dd.MM.yyyy") ?: EMPTY_STRING
                    if (date == null || date != dateCaption) {
                        date = dateCaption
                        header = BaseListHeader(dateCaption)
                    }
                    UserInvitationListItem(invitation, header, lm)
                }.toMutableList()
    }

    private fun updateList(viewName: String,
                           type: String?,
                           groupIndex: Int,
                           afterUpdate: (() -> Unit) = {}) {
        invokeAsync({
            viewWebservice.getViewResult("UserViewGroup", viewName)
        }, { result ->
            if (result.success && groupIndex == dropDownTop.lastSelectedIndex.get()) {
                when (type) {
                    "User" -> {
                        val userList = formatUsers(result.entity?.viewResult!!)
                        val adapter = mAdapter as? UserListFlexibleAdapter
                        if (adapter != null) {
                            adapter.updateDataSet(userList, true)
                        } else {
                            mAdapter = UserListFlexibleAdapter(userList, userActions)
                            initAdapter()
                        }
                    }
                    "Invitation" -> {
                        val invitationList = formatInvitations(result.entity?.viewResult!!)
                        val adapter = mAdapter as? UserInvitationListFlexibleAdapter
                        if (adapter != null) {
                            adapter.updateDataSet(invitationList, true)
                        } else {
                            mAdapter = UserInvitationListFlexibleAdapter(invitationList, invitationActions)
                            initAdapter()
                        }
                    }
                }

                afterUpdate()
                dropDownTop.update()
            }
        })
    }

    private fun initAdapter() {
        mAdapter.setDisplayHeadersAtStartUp(true)
        //mAdapter.setStickyHeaders(true)
        mAdapter.mode = SelectableAdapter.Mode.SINGLE
        mAdapter.addListener(this)
        rvUsersList.adapter = mAdapter
    }

    private fun setCategories() {
        invokeAsync({
            viewWebservice.getDigest("UserViewGroup")
        }, { result ->
            if (result.success) {
                val digest = result.entity?.digest
                        ?.toList()
                        ?.sortedBy { it.second.order ?: Int.MAX_VALUE }!!

                val categoriesEnumeration = digest.map { category ->
                    DropdownTopItemInfo(
                            category.first,
                            category.second.entityType ?: EMPTY_STRING,
                            lm.getString(category.second.caption!!),
                            category.second.count!!.toInt(),
                            { index -> updateList(category.first, category.second.entityType, index)})
                }
                val categories = ArrayList(categoriesEnumeration)
                dropDownTop.updateDataSet(categories)

                updateList(digest[0].first, digest[0].second.entityType, 0)
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.rvUsersList.layoutManager = LinearLayoutManager(
                this.activity?.applicationContext,
                LinearLayoutManager.VERTICAL,
                false)

        ptrFrame.setPtrHandler(object : PtrHandler {
            override fun onRefreshBegin(frame: PtrFrameLayout?) {
                val index = dropDownTop.lastSelectedIndex.get()
                val selectedItem = dropDownTop.items[index]
                updateList(selectedItem.viewName, selectedItem.entityType, index, {ptrFrame.refreshComplete()})
            }

            override fun checkCanDoRefresh(frame: PtrFrameLayout?, content: View?, header: View?): Boolean {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header)
            }
        })

        // TODO: избавиться от лишней иницилизации
        mAdapter = UserListFlexibleAdapter(mutableListOf(),userActions)
        initAdapter()
        setCategories()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState == null) {
            return
        }
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

