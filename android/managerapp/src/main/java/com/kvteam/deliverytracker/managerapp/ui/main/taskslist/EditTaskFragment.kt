package com.kvteam.deliverytracker.managerapp.ui.main.taskslist

import android.app.Dialog
import android.app.DialogFragment
import android.icu.util.Calendar
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.models.Product
import com.kvteam.deliverytracker.core.models.TaskInfo
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.autocomplete.AutocompleteListAdapter
import com.kvteam.deliverytracker.core.ui.errorhandling.IErrorHandler
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController
import com.kvteam.deliverytracker.core.webservice.ITaskWebservice
import com.kvteam.deliverytracker.core.webservice.IViewWebservice
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.R.layout.delivery_date_type_item
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.delivery_date_type_item.*
import kotlinx.android.synthetic.main.delivery_date_type_item.view.*
import kotlinx.android.synthetic.main.fragment_edit_task.*
import kotlinx.coroutines.experimental.runBlocking
import javax.inject.Inject
import android.widget.TimePicker
import android.text.format.DateFormat.is24HourFormat
import android.app.TimePickerDialog
import android.widget.DatePicker
import android.app.DatePickerDialog
import android.icu.text.DateFormat
import android.widget.TextView
import com.kvteam.deliverytracker.core.dataprovider.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.DataProviderGetMode
import kotlinx.android.synthetic.main.fragment_edit_task.view.*
import org.joda.time.DateTime
import java.util.*
import kotlin.collections.ArrayList

data class DeliveryDateTypeItem(
    var name: String,
    val handleFunction: (index: Int) -> Unit,
    var selectedDateTime: DateTime?
)

data class DeliveryTimeTypeItem(
        var name: String,
        val handleFunction: (index: Int) -> Unit,
        var fromTime: DateTime?,
        var toTime: DateTime?
)

class EditTaskFragment : DeliveryTrackerFragment(){
    @Inject
    lateinit var navigationController: NavigationController

    @Inject
    lateinit var taskWebservice: ITaskWebservice

    @Inject
    lateinit var viewWebservice: IViewWebservice

    @Inject
    lateinit var eh: IErrorHandler

    @Inject
    lateinit var lm: ILocalizationManager

    @Inject
    lateinit var dp: DataProvider

    private val taskIdKey = "task"
    private var taskId
        get() = arguments?.getSerializable(taskIdKey)!! as UUID
        set(value) = arguments?.putSerializable(taskIdKey, value)!!

    private val tryPrefetchKey = "tryPrefetch"
    private var tryPrefetch : Boolean
        get() = arguments?.getBoolean(tryPrefetchKey) ?: false
        set(value) = arguments?.putBoolean(tryPrefetchKey, value)!!

    fun setTask (id: UUID?) {
        this.taskId = id ?: UUID.randomUUID()
        this.tryPrefetch = id != null
    }

    // SECTION: DatePicker --START--

    class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {
        private lateinit var callback: (DateTime) -> Unit
        private lateinit var startingDate: DateTime

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val dialog = DatePickerDialog(activity, this, startingDate.year, startingDate.minusMonths(1).monthOfYear, startingDate.dayOfMonth)
            dialog.datePicker.minDate = DateTime.now().millis
            return dialog
        }

        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
          callback(DateTime(year, month + 1, day, 0, 0))
        }

        fun setStartDate (date: DateTime?) {
            startingDate = date?: DateTime.now()
        }

        fun setOnDateSelectCallback (cb: (DateTime) -> Unit) {
            callback = cb
        }
    }

    private val DATE_TODAY = "Today"
    private val DATE_TOMORROW = "Tomorrow"
    private val DATE_CUSTOM = "Custom date"

    private val selectedDeliveryDateTypeKey = "delivery_date_type"
    private var selectedDeliveryDateTypeIndex
        get() = arguments?.getInt(selectedDeliveryDateTypeKey)!!
        set(value) = arguments?.putInt(selectedDeliveryDateTypeKey, value)!!

    private val deliveryDateTypes = arrayListOf<DeliveryDateTypeItem>(
            DeliveryDateTypeItem(DATE_TODAY, ::onDeliveryDateSelect, DateTime.now()),
            DeliveryDateTypeItem(DATE_TOMORROW, ::onDeliveryDateSelect, DateTime.now().plusDays(1)),
            DeliveryDateTypeItem(DATE_CUSTOM, ::onDeliveryDateCustomSelect, null)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun configureToolbar(toolbar: ToolbarController) {
        toolbar.disableDropDown()
        toolbar.setToolbarTitle("Task")
        toolbar.showBackButton()
    }

    private fun refreshDeliveryTypesViewsList (view: View? = null) {
        val actualView = view ?: this.view
        actualView!!.llDeliveryDateTypes.removeAllViews()
        deliveryDateTypes.forEachIndexed { index, deliveryType ->
            val layout = activity!!.layoutInflater.inflate(delivery_date_type_item, actualView.llDeliveryDateTypes, false)
            layout.tvDeliveryDateTypeName.text = deliveryType.name
            layout.setOnClickListener { deliveryType.handleFunction(index) }
            if (index == selectedDeliveryDateTypeIndex)
                layout.ivDeliveryDateTypeSelectedIcon.visibility = View.VISIBLE
            actualView.llDeliveryDateTypes.addView(layout)
        }
    }

    private fun setTaskDate (date: DateTime) = launchUI {
        val task = dp.taskInfos.getAsync(taskId, DataProviderGetMode.DIRTY).entry
        val dateReal = task.deliveryFrom?: DateTime.now()
        task.deliveryFrom = dateReal.withDate(date.toLocalDate())
        task.deliveryTo = dateReal.withDate(date.toLocalDate())
    }

    private fun onDeliveryDateSelect (index: Int): Unit = launchUI{
        selectedDeliveryDateTypeIndex = index
        refreshDeliveryTypesViewsList()
        setTaskDate(deliveryDateTypes[index].selectedDateTime!!)
    }

    private fun updateCustomDateView (date: DateTime) {
        val customDateItem = deliveryDateTypes[selectedDeliveryDateTypeIndex] as DeliveryDateTypeItem
        customDateItem.name = date.toString("dd/MM")
        customDateItem.selectedDateTime = date
        setTaskDate(date)
        refreshDeliveryTypesViewsList()
    }

    private fun onDeliveryDateCustomSelect (index: Int): Unit = launchUI {
        if (selectedDeliveryDateTypeIndex == index || deliveryDateTypes[index].selectedDateTime == null) {
            selectedDeliveryDateTypeIndex = index
            val datePickerFragment = DatePickerFragment()
            datePickerFragment.setOnDateSelectCallback(::updateCustomDateView)
            val selectedDate = deliveryDateTypes[index].selectedDateTime
            datePickerFragment.setStartDate(selectedDate)
            datePickerFragment.show(activity!!.fragmentManager, "datePicker")
        }

        selectedDeliveryDateTypeIndex = index
        refreshDeliveryTypesViewsList()
    }

    // SECTION: DatePicker --END--

    // SECTION: TimePicker --START--

    private val TIME_MORNING = "09:00 - 12:00"
    private val TIME_AFTERNOON = "12:00 - 18:00"
    private val TIME_EVENING = "18:00 - 22:00"
    private val TIME_CUSTOM = "Exact time"

    private val selectedDeliveryTimeTypeKey = "delivery_time_type"
    private var selectedDeliveryTimeTypeIndex
        get() = arguments?.getInt(selectedDeliveryTimeTypeKey)!!
        set(value) = arguments?.putInt(selectedDeliveryTimeTypeKey, value)!!

    private val deliveryTimeTypes = arrayListOf<DeliveryTimeTypeItem>(
            DeliveryTimeTypeItem(
                    TIME_MORNING,
                    ::onDeliveryTimeSelect,
                    DateTime.now().withTime(9, 0, 0, 0),
                    DateTime.now().withTime(11, 59, 59, 999)
            ),
            DeliveryTimeTypeItem(
                    TIME_AFTERNOON,
                    ::onDeliveryTimeSelect,
                    DateTime.now().withTime(12, 0, 0, 0),
                    DateTime.now().withTime(17, 59, 59, 999)
            ),
            DeliveryTimeTypeItem(
                    TIME_EVENING,
                    ::onDeliveryTimeSelect,
                    DateTime.now().withTime(18, 0, 0, 0),
                    DateTime.now().withTime(21, 59, 59, 999)
            ),
            DeliveryTimeTypeItem(
                    TIME_CUSTOM,
                    ::onDeliveryCustomTimeSelect,
                    null,
                    null
            )
    )

    class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {
        private lateinit var callback: (DateTime) -> Unit
        private lateinit var startingTime: DateTime

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val dialog = TimePickerDialog(activity, this, startingTime.hourOfDay, startingTime.minuteOfHour, true)
            return dialog
        }

        fun setStartTime (date: DateTime?) {
            startingTime = date?: DateTime.now()
        }

        fun setOnTimeSelectCallback (cb: (DateTime) -> Unit) {
            callback = cb
        }

        override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
            callback(DateTime.now().withTime(hourOfDay, minute, 0, 0))
        }
    }

    private fun setTaskTime (fromTime: DateTime, toTime: DateTime) = launchUI {
        val task = dp.taskInfos.getAsync(taskId, DataProviderGetMode.DIRTY).entry
        val timeFromReal = task.deliveryFrom?: DateTime.now()
        val timeToReal = task.deliveryTo?: DateTime.now()
        task.deliveryFrom = timeFromReal.withTime(fromTime.toLocalTime())
        task.deliveryTo = timeToReal.withTime(toTime.toLocalTime())
    }

    private fun updateCustomTimeView (date: DateTime) {
        val customTimeItem = deliveryTimeTypes[selectedDeliveryTimeTypeIndex] as DeliveryTimeTypeItem
        customTimeItem.name = date.toString("hh:mm")
        customTimeItem.fromTime = date
        customTimeItem.toTime = date
        setTaskTime(date, date)
        refreshDeliveryTimeTypesViewsList()
    }

    private fun onDeliveryCustomTimeSelect (index: Int): Unit = launchUI {
        if (selectedDeliveryTimeTypeIndex == index || deliveryTimeTypes[index].fromTime == null) {
            selectedDeliveryTimeTypeIndex = index
            val timePickerFragment = TimePickerFragment()
            timePickerFragment.setOnTimeSelectCallback(::updateCustomTimeView)
            val selectedTime = deliveryTimeTypes[index].fromTime
            timePickerFragment.setStartTime(selectedTime)
            timePickerFragment.show(activity!!.fragmentManager, "timePicker")
        }

        selectedDeliveryTimeTypeIndex = index
        refreshDeliveryTimeTypesViewsList()
    }

    private fun onDeliveryTimeSelect (index: Int): Unit = launchUI{
        selectedDeliveryTimeTypeIndex = index
        refreshDeliveryTimeTypesViewsList()
        setTaskTime(deliveryTimeTypes[index].fromTime!!, deliveryTimeTypes[index].toTime!!)
    }

    private fun refreshDeliveryTimeTypesViewsList(view: View? = null) {
        val actualView = view ?: this.view
        actualView!!.llDeliveryTimeTypes.removeAllViews()
        deliveryTimeTypes.forEachIndexed { index, deliveryTimeType ->
            val layout = activity!!.layoutInflater.inflate(delivery_date_type_item, actualView.llDeliveryTimeTypes, false)
            layout.tvDeliveryDateTypeName.text = deliveryTimeType.name
            layout.setOnClickListener { deliveryTimeType.handleFunction(index) }
            if (index == selectedDeliveryTimeTypeIndex)
                layout.ivDeliveryDateTypeSelectedIcon.visibility = View.VISIBLE
            actualView.llDeliveryTimeTypes.addView(layout)
        }
    }

    // SECTION: TimePicker --END--

    override fun onActivityCreated(savedInstanceState: Bundle?) = launchUI {
        super.onActivityCreated(savedInstanceState)

//        val task = dp.taskInfos.getAsync(taskId, DataProviderGetMode.DIRTY).entry
//        selectedDeliveryDateTypeIndex = -1
//        selectedDeliveryTimeTypeIndex = -1

//        refreshDeliveryTypesViewsList()
//        refreshDeliveryTimeTypesViewsList()

        val autocomplete = acvProductAutocomplete.autoCompleteTextView
        autocomplete.setAutoCompleteDelay(200L)
        autocomplete.threshold = 2
        autocomplete.setAdapter(AutocompleteListAdapter(
                activity!!,
                {
                    runBlocking {
                        val networkResponse = viewWebservice.getViewResultAsync(
                                "ReferenceViewGroup",
                                "ProductsView",
                                mapOf("name" to it))
                        val viewResult = networkResponse.entity?.viewResult!!
                        val result = viewResult
                                .map { referenceMap ->
                                    val product = Product()
                                    product.fromMap(referenceMap)
                                    product
                                }.toMutableList()
                        result
                    }
                },
                { it.name!! }

        ))

        autocomplete.onItemClickListener = AdapterView.OnItemClickListener { av, _, pos, _ ->
            val item = av.getItemAtPosition(pos) as Product
            autocomplete.setText(item.name)
        }

        acvProductAutocomplete.listSelectionButton.setOnClickListener {
            navigationController.navigateToFilterProducts()
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_edit_task, container, false)
        selectedDeliveryDateTypeIndex = -1
        selectedDeliveryTimeTypeIndex = -1
        refreshDeliveryTypesViewsList(view)
        refreshDeliveryTimeTypesViewsList(view)
        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = launchUI ({
        when (item.itemId) {
            R.id.action_add -> {
                val task = TaskInfo()
                task.taskNumber = etTaskNumber.text.toString()
                val result = taskWebservice.createAsync(task)
                if(eh.handle(result)) {
                    return@launchUI
                }
                navigationController.closeCurrentFragment()
            }
        }
    }, {
        super.onOptionsItemSelected(item)
    })

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_edit_task_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}

