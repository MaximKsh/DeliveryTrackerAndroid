package com.kvteam.deliverytracker.managerapp.ui.main.taskslist

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.amulyakhare.textdrawable.TextDrawable
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.dataprovider.base.CacheException
import com.kvteam.deliverytracker.core.dataprovider.base.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.base.DataProviderGetMode
import com.kvteam.deliverytracker.core.models.Warehouse
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_task_receipt_at.*
import kotlinx.android.synthetic.main.fragment_task_receipt_at.view.*
import kotlinx.android.synthetic.main.selected_performer_item.view.*
import org.joda.time.DateTime
import java.util.*
import javax.inject.Inject



class TaskReceiptAtFragment : BaseTaskPageFragment() {
    @Inject
    lateinit var dp: DataProvider

    @Inject
    lateinit var navigationController: NavigationController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_task_receipt_at, container, false) as ViewGroup

        val task = dp.taskInfos.get(taskId, DataProviderGetMode.DIRTY).entry

        val strings = receiptTypes.map { it.name }
        view.spinnerReceiptAt.attachDataSource(strings)
        view.spinnerReceiptAt.addOnItemClickListener { adapterView, view, i, l ->
            setTaskReceiptAtDate(receiptTypes[i].selectedDateTime!!)
        }

        if (task.receipt == null) {
            setTaskReceiptAtDate(receiptTypes[0].selectedDateTime!!)
        }

        view.spinnerReceiptAt.setPadding(15, 10, 0, 10)

        view.spinnerPerformerType.attachDataSource(performersTypes)
        view.spinnerPerformerType.addOnItemClickListener { adapterView, view, i, l ->
            onPerformerTypeSelect(i)
        }

        if (task.performerId != null) {
            showSelectedPerformer(task.performerId as UUID, view)
        } else {
            hideSelectedPerformer(view)
        }

        view.spinnerPerformerType.setPadding(15, 10, 0, 10)

        view.spinnerWarehouse.setPadding(15, 10, 0, 10)

        return view
    }

    // SECTION: ReceiptAt --START--

    private val RECEIPT_ASAP = "As soon as possible"
    private val RECEIPT_AUTO = "Auto"
    private val RECEIPT_CUSTOM = "Exact time"


    private val receiptTypes = arrayListOf<DeliveryReceiptAtItem>(
            DeliveryReceiptAtItem(RECEIPT_AUTO, DateTime.now()),
            DeliveryReceiptAtItem(RECEIPT_ASAP, DateTime.now())
    )

    private fun setTaskReceiptAtDate(date: DateTime) = launchUI {
        val task = dp.taskInfos.getAsync(taskId, DataProviderGetMode.DIRTY).entry
        val dateReal = task.receipt ?: DateTime.now()
        task.receipt = dateReal.withDate(date.toLocalDate())
    }

    // SECTION: ReceiptAt --END--


    private val PERFORMER_AUTO = "Auto"
    private val PERFORMER_UNASSIGNED = "Unassigned"
    private val PERFORMER_OUTSOURCE = "Delivery Couriers"
    private val PERFORMER_EXACT = "From staff"

    private val performersTypes = arrayListOf<String>(
            PERFORMER_UNASSIGNED,
            PERFORMER_AUTO,
            PERFORMER_EXACT
    )

    private fun showSelectedPerformer(performerId: UUID, view: View = this.view!!) {
        val performer = dp.users.get(performerId, DataProviderGetMode.FORCE_CACHE).entry

        val materialAvatarDefault = TextDrawable.builder()
                .buildRound(performer.name!![0].toString() + performer.surname!![0].toString(), Color.LTGRAY)

        view.rlSelectedPerformer.ivUserAvatar.setImageDrawable(materialAvatarDefault)
        view.rlSelectedPerformer.tvPerformerName.text = performer.name
        view.rlSelectedPerformer.tvSurname.text = performer.surname
        view.rlSelectedPerformer.ivOnlineStatus.visibility = if (performer.online) View.VISIBLE else View.INVISIBLE
        view.rlSelectedPerformer.visibility = View.VISIBLE
    }

    private fun hideSelectedPerformer(view: View = this.view!!) {
        view.rlSelectedPerformer.visibility = View.GONE
    }

    private fun setTaskPerformer(performerId: UUID?) = launchUI {
        val task = dp.taskInfos.get(taskId, DataProviderGetMode.DIRTY).entry
        task.performerId = performerId
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

    override fun onActivityCreated(savedInstanceState: Bundle?) = launchUI {
        super.onActivityCreated(savedInstanceState)
        val warehousesIds = dp.warehousesViews
                .getViewResultAsync("ReferenceViewGroup", "WarehousesView").viewResult
        val task = dp.taskInfos.get(taskId, DataProviderGetMode.DIRTY).entry

        val strings = arrayListOf<String>()
        val warehouses = arrayListOf<Warehouse>()
        if (warehousesIds.isNotEmpty()) {
            for (id in warehousesIds) {
                try {
                    val (e, _) = dp.warehouses.get(id, DataProviderGetMode.FORCE_CACHE)
                    warehouses.add(e)
                    strings.add(e.name!!)
                } catch (e: CacheException) {
                }
            }
            spinnerWarehouse.attachDataSource(strings)
            spinnerWarehouse.addOnItemClickListener { adapterView, view, i, l ->
                setTaskWarehouse(warehouses[i])
            }

            if (task.warehouseId == null) {
                setTaskWarehouse(warehouses[0])
            }
            val selected = warehouses.indexOfFirst { it.id == task.warehouseId }
            spinnerWarehouse.selectedIndex = selected
        }
    }

    private fun setTaskWarehouse (warehouse: Warehouse) {
        val task = dp.taskInfos.get(taskId, DataProviderGetMode.DIRTY).entry
        task.warehouseId = warehouse.id
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }
}

