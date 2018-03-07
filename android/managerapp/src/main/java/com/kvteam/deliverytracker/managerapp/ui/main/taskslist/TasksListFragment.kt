package com.kvteam.deliverytracker.managerapp.ui.main.taskslist


import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.ui.AutoClearedValue
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.core.ui.dropdowntop.DropdownTop
import com.kvteam.deliverytracker.core.ui.dropdowntop.DropdownTopItemInfo
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_tasks_list.*
import kotlinx.android.synthetic.main.toolbar.*
import javax.inject.Inject


open class TasksListFragment : DeliveryTrackerFragment() {
    protected val layoutManagerKey = "layoutManager"
    protected val tasksListKey = "tasksListKey"

    @Inject
    lateinit var lm: ILocalizationManager

    @Inject
    lateinit var navigationController: NavigationController

    private lateinit var addTaskMenuItem: MenuItem

    protected lateinit var adapter: AutoClearedValue<TasksListAdapter>

    protected var ignoreSavedState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        return inflater.inflate(
                R.layout.fragment_tasks_list,
                container,
                false)
    }

    private fun showMine(index: Int) {

    }

    private fun showAll(index: Int) {

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        rvTasksList.layoutManager = LinearLayoutManager(
                activity?.applicationContext,
                LinearLayoutManager.VERTICAL,
                false)
        rvTasksList.addItemDecoration(
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        val categories = arrayListOf<DropdownTopItemInfo>(
                //DropdownTopItemInfo("Mine", 2, ::showMine),
                //DropdownTopItemInfo("All", 12, ::showAll)
        )

        DropdownTop(categories, activity!!)

        adapter = AutoClearedValue(
                this,
                TasksListAdapter(this::onTaskClicked, lm),
                {
                    it?.onTaskClick = null
                    it?.lm = null
                })
        rvTasksList.adapter = adapter.value

        /*savedInstanceState?.apply {
            val adapter = adapter.value
            val layoutManager = rvTasksList?.layoutManager
            if(adapter != null
                    && layoutManager != null) {
                if(containsKey(tasksListKey)
                        && containsKey(layoutManagerKey)) {
                    val savedTasks = getParcelableArray(tasksListKey).map { it as TaskModel }
                    adapter.items.clear()
                    adapter.items.addAll(savedTasks)
                    layoutManager.onRestoreInstanceState(getParcelable(layoutManagerKey))
                } else {
                    ignoreSavedState = true
                }
            }
        }*/
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.apply {
            val adapter = adapter.value
            val layoutManager = rvTasksList?.layoutManager
            /*if(adapter != null
                    && layoutManager != null) {
                putParcelableArray(
                        tasksListKey,
                        adapter.items.toTypedArray())
                putParcelable(
                        layoutManagerKey,
                        layoutManager.onSaveInstanceState())
            }*/
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_addTask -> {
                navigationController.navigateToAddTask()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_tasks_tab_menu, menu)

        this.addTaskMenuItem = menu.findItem(R.id.action_addTask)
        this.addTaskMenuItem.isVisible = true
        this.activity?.toolbar_left_action?.setOnClickListener { _ ->

        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun onTaskClicked(task: Any) {
        /*val id = task.id
        if(id != null) {
            navigationController.navigateToTask(id)
        }*/
    }

}
