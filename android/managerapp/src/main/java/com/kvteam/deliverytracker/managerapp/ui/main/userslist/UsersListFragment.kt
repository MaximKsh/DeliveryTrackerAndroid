package com.kvteam.deliverytracker.managerapp.ui.main.userslist

import `in`.srain.cube.views.ptr.PtrDefaultHandler
import `in`.srain.cube.views.ptr.PtrFrameLayout
import `in`.srain.cube.views.ptr.PtrHandler
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.roles.Role
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.dropdowntop.DropdownTopItemInfo
import com.kvteam.deliverytracker.managerapp.ui.dropdowntop.DropdownTop
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_user_list.*
import kotlinx.android.synthetic.main.toolbar.*
import javax.inject.Inject
import android.view.ViewGroup
import kotlin.collections.ArrayList
import android.util.Log
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.models.Invitation
import com.kvteam.deliverytracker.core.roles.toRole
import com.kvteam.deliverytracker.core.webservice.IViewWebservice
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.SelectableAdapter
import eu.davidea.flexibleadapter.helpers.ActionModeHelper

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
    var isInEditMode: Boolean = false

    private lateinit var dropDownTop: DropdownTop

    lateinit var mAddMenuItem: MenuItem
    lateinit var mRemoveMenuItem: MenuItem
    lateinit var mEditMenuItem: MenuItem

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

    private fun updateList(viewName: String, type: String?, groupIndex: Int) {
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
                            rvUsersList.adapter = mAdapter
                        }
                    }
                    "Invitation" -> {
                        val invitationList = formatInvitations(result.entity?.viewResult!!)
                        val adapter = mAdapter as? UserInvitationListFlexibleAdapter
                        if (adapter != null) {
                            adapter.updateDataSet(invitationList)
                        } else {
                            mAdapter = UserInvitationListFlexibleAdapter(invitationList)
                            rvUsersList.adapter = mAdapter
                        }
                    }
                }
                dropDownTop.update()
            }
        })
    }

    private fun setCategories() {
        invokeAsync({
            viewWebservice.getDigest("UserViewGroup")
        }, { result ->
            if (result.success) {
                val digest = result.entity?.digest?.toList()!!

                val categoriesEnumeration = digest.map { category ->

                    DropdownTopItemInfo(lm.getString(category.second.caption!!), category.second.count!!.toInt(), { index ->
                        updateList(category.first, category.second.entityType, index)
                    })
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

        mAdapter = UserListFlexibleAdapter(mutableListOf())

        mAdapter.mode = SelectableAdapter.Mode.SINGLE

        mAdapter.addListener(this)

        ptrFrame.setPtrHandler(object : PtrHandler {
            override fun onRefreshBegin(frame: PtrFrameLayout?) {
                frame?.postDelayed(Runnable { ptrFrame.refreshComplete() }, 1800)
            }

            override fun checkCanDoRefresh(frame: PtrFrameLayout?, content: View?, header: View?): Boolean {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        })

        mAdapter.setDisplayHeadersAtStartUp(true)
        mAdapter.setStickyHeaders(true)

        rvUsersList.adapter = mAdapter

        setCategories()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState == null) {
            return
        }
    }

    private fun startEditMode() {
        this.isInEditMode = true
//        this.adapter.value?.items?.forEach { item -> item.isInEditMode = true }
//        this.adapter.value?.notifyDataSetChanged()
    }

    private fun stopEditMode() {
        this.isInEditMode = false
//        this.adapter.value?.items?.forEach { item -> item.isInEditMode = false }
//        this.adapter.value?.notifyDataSetChanged()
    }

    private fun clearSelectedUsers() {
//        this.adapter.value?.items?.forEach { item -> item.isSelected = false }
//        this.adapter.value?.notifyDataSetChanged()
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
        this.activity?.toolbar_left_action?.text =
                if (visibility) resources.getString(R.string.ManagerApp_Cancel)
                else EMPTY_STRING
    }

    // MENU CALLBACKS
    override fun onPrepareOptionsMenu(menu: Menu) {
//        if (isInEditMode) {
//            this.setCancelButtonVisible(true)
//            if (this.adapter.value?.items!!.any { userListModel -> userListModel.isSelected }) {
//                this.setRemoveButtonVisible()
//            } else {
//                this.setAddButtonVisible()
//            }
//        } else {
//            this.setCancelButtonVisible(false)
//            this.setEditButtonVisible()
//        }
        super.onPrepareOptionsMenu(menu)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.setCancelButtonVisible(false)
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
        inflater.inflate(R.menu.toolbar_managers_tab_menu, menu)

        this.mAddMenuItem = menu.findItem(R.id.action_add)

        this.activity?.toolbar_left_action?.setOnClickListener { _ ->
            this.setCancelButtonVisible(false)
            this.setEditButtonVisible()
            this.stopEditMode()
            this.clearSelectedUsers()
        }

        super.onCreateOptionsMenu(menu, inflater)
    }
}

