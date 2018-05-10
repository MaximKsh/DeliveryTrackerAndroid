package com.kvteam.deliverytracker.managerapp.ui.main.taskslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.common.ReferenceViewGroup
import com.kvteam.deliverytracker.core.common.WarehousesView
import com.kvteam.deliverytracker.core.dataprovider.base.DataProviderGetMode
import com.kvteam.deliverytracker.core.models.Warehouse
import com.kvteam.deliverytracker.core.ui.materialDefaultAvatar
import com.kvteam.deliverytracker.managerapp.R
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_task_receipt_at.*
import kotlinx.android.synthetic.main.fragment_task_receipt_at.view.*
import kotlinx.android.synthetic.main.selected_performer_item.view.*
import org.joda.time.DateTime
import java.util.*


class TaskReceiptAtFragment : BaseTaskPageFragment() {

    private val receiptAsap by lazy { lm.getString(R.string.ManagerApp_EditTaskFragment_ASAP) }
    private val receiptCustom by lazy { lm.getString(R.string.ManagerApp_EditTaskFragment_ExactTime) }
    private lateinit var receiptTypes: List<DeliveryReceiptAtItem>

    private val performerUnassigned by lazy { lm.getString(R.string.ManagerApp_EditTaskFragment_PerformerUnassigned) }
    private val performerAuto by lazy { lm.getString(R.string.ManagerApp_EditTaskFragment_PerformerAuto) }
    private val performerFromStaff by lazy { lm.getString(R.string.ManagerApp_EditTaskFragment_PerformerFromStaff) }

    private lateinit var performersTypes: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)

        receiptTypes = listOf(
                DeliveryReceiptAtItem(receiptAsap, null),
                DeliveryReceiptAtItem(receiptCustom, DateTime.now())
        )
        performersTypes = listOf(
                performerUnassigned,
                performerAuto,
                performerFromStaff
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_task_receipt_at, container, false) as ViewGroup

        val task = dp.taskInfos.get(taskId, DataProviderGetMode.DIRTY).entry

        val strings = receiptTypes.map { it.name }
        view.spinnerReceiptAt.attachDataSource(strings)
        view.spinnerReceiptAt.addOnItemClickListener { _, _, i, _ ->
            setTaskReceiptAtDate(receiptTypes[i].selectedDateTime!!)
        }

//        if (task.receipt == null) {
//            setTaskReceiptAtDate(receiptTypes[0].selectedDateTime!!)
//        }

        view.spinnerPerformerType.attachDataSource(performersTypes)
        view.spinnerPerformerType.addOnItemClickListener { _, _, i, _ ->
            onPerformerTypeSelect(i)
        }

        if (task.performerId != null) {
            showSelectedPerformer(task.performerId as UUID, view)
        } else {
            hideSelectedPerformer(view)
        }

        view.spinnerReceiptAt.setPadding(15, 10, 0, 10)
        view.spinnerPerformerType.setPadding(15, 10, 0, 10)
        view.spinnerWarehouse.setPadding(15, 10, 0, 10)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) = launchUI {
        super.onActivityCreated(savedInstanceState)
        val warehousesIds = dp.warehousesViews
                .getViewResultAsync(ReferenceViewGroup, WarehousesView, mode = DataProviderGetMode.PREFER_CACHE).viewResult
        val task = dp.taskInfos.get(taskId, DataProviderGetMode.DIRTY).entry

        val warehouses = loadWarehouses(dp)
        val strings = getWarehouseSpinnerList(warehouses)
        spinnerWarehouse.attachDataSource(strings)
        if (warehousesIds.isNotEmpty()) {
            spinnerWarehouse.addOnItemClickListener { _, _, i, _ ->
                setTaskWarehouse(warehouses[i])
            }
            val selected = warehouses.indexOfFirst { it.id == task.warehouseId }
            spinnerWarehouse.selectedIndex = selected
        }
    }

    private fun setTaskReceiptAtDate(date: DateTime) = launchUI {
        val task = dp.taskInfos.getAsync(taskId, DataProviderGetMode.DIRTY).entry
        val dateReal = task.receipt ?: DateTime.now()
        task.receipt = dateReal.withDate(date.toLocalDate())
    }


    private fun showSelectedPerformer(performerId: UUID, view: View = this.view!!) {
        val performer = dp.users.get(performerId, DataProviderGetMode.FORCE_CACHE).entry

        val materialAvatarDefault = materialDefaultAvatar(performer)
        view.rlSelectedPerformer.ivUserAvatar.setImageDrawable(materialAvatarDefault)
        view.rlSelectedPerformer.tvPerformerName.text = performer.name
        view.rlSelectedPerformer.tvSurname.text = performer.surname
        view.rlSelectedPerformer.ivOnlineStatus.visibility = if (performer.online) View.VISIBLE else View.INVISIBLE
        view.rlSelectedPerformer.visibility = View.VISIBLE
    }

    private fun onPerformerTypeSelect(index: Int): Unit = launchUI {
        when (index) {
            0 -> {
                setTaskPerformer(null)
            }
            1 -> {
                if (index == 0) {
                    val performersIds = dp.userViews.getViewResultAsync("UserViewGroup", "PerformersView").viewResult
                    if (performersIds.isEmpty()) {
                        Toast.makeText(activity, "No available performers", Toast.LENGTH_LONG).show()
                    } else {
                        setTaskPerformer(performersIds[0])
                    }
                }
            }
            2 -> {
                navigationController.navigateToFilterUsers(taskId)
            }
        }
    }

    private fun hideSelectedPerformer(view: View = this.view!!) {
        view.rlSelectedPerformer.visibility = View.GONE
    }

    private fun setTaskPerformer(performerId: UUID?) = launchUI {
        val task = dp.taskInfos.get(taskId, DataProviderGetMode.DIRTY).entry
        task.performerId = performerId
    }

    private fun setTaskWarehouse (warehouse: Warehouse) {
        val task = dp.taskInfos.get(taskId, DataProviderGetMode.DIRTY).entry
        task.warehouseId = warehouse.id
    }

    private fun getWarehouseSpinnerList(pt: List<Warehouse>) : List<String> {
        return if (pt.isNotEmpty()) {
            pt.map { it.name ?: EMPTY_STRING }
        } else {
            return listOf(lm.getString(R.string.ManagerApp_EditTaskFragment_NoAvailableWarehouse))
        }
    }
}

