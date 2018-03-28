package com.kvteam.deliverytracker.managerapp.ui.main.userslist

import android.os.Bundle
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.dataprovider.CacheException
import com.kvteam.deliverytracker.core.dataprovider.DataProviderGetMode
import com.kvteam.deliverytracker.core.dataprovider.DataProviderGetOrigin
import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.roles.Role
import com.kvteam.deliverytracker.core.roles.toRole
import com.kvteam.deliverytracker.core.ui.BaseListFragment
import com.kvteam.deliverytracker.core.ui.BaseListHeader
import com.kvteam.deliverytracker.core.ui.IBaseListItemActions
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import eu.davidea.flexibleadapter.FlexibleAdapter
import java.util.*
import javax.inject.Inject

open class FilterUsersFragment : BaseListFragment() {
    @Inject
    lateinit var navigationController: NavigationController

    override val tracer
        get() = navigationController.fragmentTracer

    override val viewGroup: String = "UserViewGroup"

    private val taskIdKey = "task_id_key"
    private var taskId
        get() = arguments?.getSerializable(taskIdKey)!! as UUID
        set(value) = arguments?.putSerializable(taskIdKey, value)!!

    fun setEditTaskId (taskId: UUID) {
        this.taskId = taskId
    }

    private val userActions = object : IBaseListItemActions<UserListItem> {
        override suspend fun onDelete(adapter: FlexibleAdapter<*>, itemList: MutableList<UserListItem>, item: UserListItem) {}

        override suspend fun onItemClicked(adapter: FlexibleAdapter<*>, itemList: MutableList<UserListItem>, item: UserListItem) {
            val task = dp.taskInfos.getAsync(taskId, DataProviderGetMode.DIRTY).entry
            task.performerId = item.user.id
            navigationController.closeCurrentFragment()
        }
    }

    override fun handleUsers(users: List<User>, animate: Boolean) {
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

        (mAdapter as UserListFlexibleAdapter).updateDataSet(userList, animate)
    }

    override fun onDestroy() {
        super.onDestroy()
        toolbarController.disableSearchMode()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) = launchUI {
        super.onActivityCreated(savedInstanceState)
        dropdownTop.lastSelectedIndex.set(1)
        mAdapter = UserListFlexibleAdapter(mutableListOf(), userActions)
        (mAdapter as UserListFlexibleAdapter).hideDeleteButton = true

        val (result, origin) = dp.userViews.getViewResultAsync(
                viewGroup,
                "PerformersView",
                null,
                DataProviderGetMode.FORCE_WEB)
        val entities = mutableListOf<User>()
        for(id in result) {
            try{
                val (e, _) = dp.users.getAsync(id, DataProviderGetMode.FORCE_CACHE)
                entities.add(e)
            } catch (e: CacheException) {}
        }

        handleUsers(entities, origin == DataProviderGetOrigin.WEB)

        toolbarController.enableSearchMode({text ->
            val (result, origin) = dp.userViews.getViewResultAsync(
                    viewGroup,
                    "PerformersView",
                    mapOf(
                            "search" to text
                    ),
                    DataProviderGetMode.FORCE_WEB)

            val entities = mutableListOf<User>()
            for(id in result) {
                try{
                    val (e, _) = dp.users.getAsync(id, DataProviderGetMode.FORCE_CACHE)
                    entities.add(e)
                } catch (e: CacheException) {}
            }
            handleUsers(entities, true)

        })
        super.onActivityCreated(savedInstanceState)
    }
}