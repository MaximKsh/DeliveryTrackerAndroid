package com.kvteam.deliverytracker.core.ui.tasks


import com.kvteam.deliverytracker.core.models.TaskInfo
import com.kvteam.deliverytracker.core.ui.BaseListFlexibleAdapter
import com.kvteam.deliverytracker.core.ui.IBaseListItemActions

class TasksListFlexibleAdapter(tasksActions: IBaseListItemActions<TaskListItem>)
    : BaseListFlexibleAdapter<TaskInfo, TaskListItem, TaskListItem.TasksListViewHolder>(tasksActions)

