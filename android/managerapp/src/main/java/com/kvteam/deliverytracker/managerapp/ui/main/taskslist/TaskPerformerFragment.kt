package com.kvteam.deliverytracker.managerapp.ui.main.taskslist

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.amulyakhare.textdrawable.TextDrawable
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.dataprovider.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.DataProviderGetMode
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.dropdownselect.DropdownSelect
import com.kvteam.deliverytracker.core.ui.dropdownselect.DropdownSelectItem
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_task_performer.view.*
import kotlinx.android.synthetic.main.selected_performer_item.*
import kotlinx.android.synthetic.main.selected_performer_item.view.*
import java.util.*
import javax.inject.Inject

data class PerformerTypeItem(
        var name: String,
        var performerId: UUID? = null
)

class TaskPerformerFragment : PageFragment() {
    @Inject
    lateinit var navigationController: NavigationController

    @Inject
    lateinit var dp: DataProvider

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_task_performer, container, false) as ViewGroup
        DropdownSelect<PerformerTypeItem>(
                "Performer type",
                performersTypes,
                selectedPerformerTypeIndex,
                ::onPerformerTypeSelect,
                ::performerTextSelector,
                view.llPerformerSelectionContainer,
                activity!!
        )
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) = launchUI {
        super.onActivityCreated(savedInstanceState)
        val task = dp.taskInfos.getAsync(taskId, DataProviderGetMode.DIRTY).entry

        if (task.performerId != null) {
            val performer = dp.users.getAsync(task.performerId as UUID, DataProviderGetMode.FORCE_CACHE).entry

            val materialAvatarDefault = TextDrawable.builder()
                    .buildRound(performer.name!![0].toString() + performer.surname!![0].toString(), Color.LTGRAY)

            rlSelectedPerformer.ivUserAvatar.setImageDrawable(materialAvatarDefault)
            rlSelectedPerformer.tvPerformerName.text = performer.name
            rlSelectedPerformer.tvSurname.text = performer.surname
            rlSelectedPerformer.ivOnlineStatus.visibility = if (performer.online) View.VISIBLE else View.INVISIBLE
            rlSelectedPerformer.visibility = View.VISIBLE
        } else {
            rlSelectedPerformer.visibility = View.GONE
        }
    }

    // SECTION: Performer --START--

    private val PERFORMER_AUTO = "Auto"
    private val PERFORMER_UNASSIGNED = "Unassigned"
    private val PERFORMER_OUTSOURCE = "Delivery Couriers"
    private val PERFORMER_EXACT = "From staff"

    private val selectedPerformerTypeKey = "delivery_performer_type"
    private var selectedPerformerTypeIndex: Int?
        get() = arguments?.get(selectedPerformerTypeKey) as Int?
        set(value) = arguments?.putInt(selectedPerformerTypeKey, value!!)!!

    private val performersTypes = arrayListOf<DropdownSelectItem<PerformerTypeItem>>(
            DropdownSelectItem(PerformerTypeItem(PERFORMER_AUTO, null)),
            DropdownSelectItem(PerformerTypeItem(PERFORMER_OUTSOURCE, null), false, true),
            DropdownSelectItem(PerformerTypeItem(PERFORMER_UNASSIGNED, null)),
            DropdownSelectItem(PerformerTypeItem(PERFORMER_EXACT, null))
    )

    private fun setTaskPerformer(performerId: UUID?) = launchUI {
        if (performerId != null) {
            val performer = dp.users.getAsync(performerId, DataProviderGetMode.FORCE_CACHE).entry

            val materialAvatarDefault = TextDrawable.builder()
                    .buildRound(performer.name!![0].toString() + performer.surname!![0].toString(), Color.LTGRAY)

            rlSelectedPerformer.ivUserAvatar.setImageDrawable(materialAvatarDefault)
            rlSelectedPerformer.tvPerformerName.text = performer.name
            rlSelectedPerformer.tvSurname.text = performer.surname
            rlSelectedPerformer.ivOnlineStatus.visibility = if (performer.online) View.VISIBLE else View.INVISIBLE
            rlSelectedPerformer.visibility = View.VISIBLE
        } else {
            rlSelectedPerformer.visibility = View.GONE
        }

        val task = dp.taskInfos.getAsync(taskId, DataProviderGetMode.DIRTY).entry
        task.performerId = performerId
    }

    private fun onPerformerTypeSelect(index: Int, oldIndex: Int): Unit = launchUI {
        selectedPerformerTypeIndex = index
        when (index) {
            0 -> {
                if (index == 0) {
                    val performersIds = dp.userViews.getViewResultAsync("UserViewGroup", "PerformersView").viewResult
                    if (performersIds.isEmpty()) {
                        Toast.makeText(activity, "No available performers", Toast.LENGTH_LONG).show()
                    } else {
                        performersTypes[index].data.performerId = performersIds[0]
                        setTaskPerformer(performersTypes[index].data.performerId!!)
                    }
                }
            }
            1 -> {
            }
            2 -> {
                setTaskPerformer(null)
            }
            3 -> {
                navigationController.navigateToFilterUsers(taskId)
            }
        }
    }

    private fun performerTextSelector(performerTypeItem: PerformerTypeItem): String {
        return performerTypeItem.name
    }

    // SECTION: Performer --END-


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }
}

