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
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.webservice.IViewWebservice
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.common.dropdowntop.DropdownTop
import com.kvteam.deliverytracker.managerapp.ui.common.dropdowntop.DropdownTopItemInfo
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.SelectableAdapter
import kotlinx.android.synthetic.main.fragment_user_list.*
import javax.inject.Inject

// TODO: rename managersList xml to userslist
open class UsersListFragment : DeliveryTrackerFragment(), FlexibleAdapter.OnItemClickListener {

    @Inject
    lateinit var navigationController: NavigationController

    @Inject
    lateinit var viewWebservice: IViewWebservice

    @Inject
    lateinit var lm: ILocalizationManager

    // TODO: выбирать добавляемую роль по сложной логике
    private var role: Role = Role.Manager

    private lateinit var dropDownTop: DropdownTop

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

    override fun onItemClick(position: Int): Boolean {
        Log.i("POSITION", position.toString())
        return false
    }

    private fun formatUsers(viewResult: List<Map<String, Any?>>): MutableList<UserListItem> {
        var letter: Char? = null
        var header = SubgroupListHeader("A")

        return viewResult
                .map { userMap ->
                    val user = User()
                    user.fromMap(userMap)
                    user
                }
                .sortedBy { a -> a.surname }
                .map { user ->
                    if (letter == null || letter != user.surname!![0]) {
                        letter = user.surname!![0]
                        header = SubgroupListHeader(letter!!.toString())
                    }
                    UserListItem(user, header)
                }.toMutableList()
    }

    private fun formatInvitations(viewResult: List<Map<String, Any?>>): MutableList<UserInvitationListItem> {
        var role: String? = null
        var header = SubgroupListHeader("A")

        return viewResult
                .map { invitationMap ->
                    val invitation = Invitation()
                    invitation.fromMap(invitationMap)
                    invitation
                }
                .sortedBy { a -> lm.getString(a.role!!.toRole()!!.localizationStringId) }
                .map { invitation ->
                    val roleCaption = lm.getString(invitation.role!!.toRole()!!.localizationStringId)
                    if (role == null || role != roleCaption) {
                        role = roleCaption
                        header = SubgroupListHeader(roleCaption)
                    }
                    UserInvitationListItem(invitation, header)
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
                            adapter.updateDataSet(userList)
                        } else {
                            mAdapter = UserListFlexibleAdapter(userList)
                            initAdapter()
                        }
                    }
                    "Invitation" -> {
                        val invitationList = formatInvitations(result.entity?.viewResult!!)
                        val adapter = mAdapter as? UserInvitationListFlexibleAdapter
                        if (adapter != null) {
                            adapter.updateDataSet(invitationList)
                        } else {
                            mAdapter = UserInvitationListFlexibleAdapter(invitationList)
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
                dropDownTop = DropdownTop(categories, activity!!)

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

        mAdapter = UserListFlexibleAdapter(mutableListOf())
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

