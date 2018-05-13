package com.kvteam.deliverytracker.managerapp.ui.main.taskslist

import android.os.Bundle
import com.kvteam.deliverytracker.core.common.AuxTaskViewGroup
import com.kvteam.deliverytracker.core.common.TaskInfoType
import com.kvteam.deliverytracker.core.common.UserTasksView
import com.kvteam.deliverytracker.core.models.TaskInfo
import com.kvteam.deliverytracker.core.roles.Role
import com.kvteam.deliverytracker.core.ui.BaseFilterFragment
import com.kvteam.deliverytracker.core.ui.BaseListHeader
import com.kvteam.deliverytracker.core.ui.IBaseListItemActions
import com.kvteam.deliverytracker.core.ui.tasks.TaskListItem
import com.kvteam.deliverytracker.core.ui.tasks.TasksListFlexibleAdapter
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import eu.davidea.flexibleadapter.FlexibleAdapter
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Duration
import java.util.*
import javax.inject.Inject

class UserTasksListFragment : BaseFilterFragment() {
    private val tasksActions = object : IBaseListItemActions<TaskListItem> {
        override suspend fun onDelete(adapter: FlexibleAdapter<*>, itemList: MutableList<TaskListItem>, item: TaskListItem) {}

        override suspend fun onItemClicked(adapter: FlexibleAdapter<*>, itemList: MutableList<TaskListItem>, item: TaskListItem) {
            val id = item.task.id
            if(id != null) {
                navigationController.navigateToTaskDetails(id)
            }
        }
    }

    @Inject
    lateinit var navigationController: NavigationController

    override val viewGroup: String = AuxTaskViewGroup
    override val viewName: String = UserTasksView
    override val type: String = TaskInfoType

    private val userIdKey = "userId"
    private var userId
        get() = arguments?.getSerializable(userIdKey)!! as UUID
        set(value) = arguments?.putSerializable(userIdKey, value)!!

    private val userRoleKey = "userRole"
    private var userRole
        get() = arguments?.getSerializable(userRoleKey)!! as Role
        set(value) = arguments?.putSerializable(userRoleKey, value)!!

    fun setUser(id: UUID, role: Role) {
        this.userId = id
        this.userRole = role
    }

    override fun configureToolbar(toolbar: ToolbarController) {
    }

    override val tracer
        get() = navigationController.fragmentTracer

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mAdapter = TasksListFlexibleAdapter(tasksActions)
        (mAdapter as TasksListFlexibleAdapter).hideDeleteButton = true
        super.onActivityCreated(savedInstanceState)
    }

    override fun handleTasks(tasks: List<TaskInfo>, animate: Boolean) {
        val headerThisWeek = BaseListHeader(lm.getString(R.string.Core_ThisWeek))
        val headerPreviousWeek = BaseListHeader(lm.getString(R.string.Core_PreviousWeek))
        val headerLongTimeAgo = BaseListHeader(lm.getString(R.string.Core_LongTimeAgo))

        val list = tasks
                .sortedByDescending { a -> a.created }
                .map { task ->
                    val header: BaseListHeader
                    val dateValue = task.created!!
                    val duration = Duration(dateValue, DateTime.now(DateTimeZone.UTC))
                    header = when (duration.standardDays) {
                        in 0..7 -> {
                            headerThisWeek
                        }
                        in 8..14 -> {
                            headerPreviousWeek
                        }
                        else -> {
                            headerLongTimeAgo
                        }
                    }
                    TaskListItem(task, header, lm, dp, context!!)
                }.toMutableList()
        updateDataSet(list, { TasksListFlexibleAdapter(tasksActions) }, animate)
    }

    override fun getInitialArguments(): Map<String, Any>? {
        return if (this.userRole == Role.Performer) {
            mapOf("performer_id" to this.userId)
        } else {
            mapOf("author_id" to this.userId)
        }
    }

}
