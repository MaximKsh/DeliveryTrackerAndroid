package com.kvteam.deliverytracker.performerapp.ui.main.taskslist

import com.kvteam.deliverytracker.core.models.TaskInfo
import com.kvteam.deliverytracker.core.ui.BaseListFlexibleAdapter
import com.kvteam.deliverytracker.core.ui.IBaseListItemActions

class TasksListFlexibleAdapter(noHeaderItems: MutableList<TaskListItem>,
                               tasksActions: IBaseListItemActions<TaskListItem>)
    : BaseListFlexibleAdapter<TaskInfo, TaskListItem, TaskListItem.TasksListViewHolder>(noHeaderItems, tasksActions) {}

