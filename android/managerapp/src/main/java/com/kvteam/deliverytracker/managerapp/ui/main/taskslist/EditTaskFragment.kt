package com.kvteam.deliverytracker.managerapp.ui.main.taskslist

import android.animation.ValueAnimator
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.DialogFragment
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import com.amulyakhare.textdrawable.TextDrawable
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.dataprovider.CacheException
import com.kvteam.deliverytracker.core.dataprovider.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.DataProviderGetMode
import com.kvteam.deliverytracker.core.dataprovider.NetworkException
import com.kvteam.deliverytracker.core.models.PaymentType
import com.kvteam.deliverytracker.core.models.Product
import com.kvteam.deliverytracker.core.models.TaskProduct
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.autocomplete.AutocompleteListAdapter
import com.kvteam.deliverytracker.core.ui.dropdownselect.DropdownSelect
import com.kvteam.deliverytracker.core.ui.dropdownselect.DropdownSelectItem
import com.kvteam.deliverytracker.core.ui.errorhandling.IErrorHandler
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController
import com.kvteam.deliverytracker.core.webservice.ITaskWebservice
import com.kvteam.deliverytracker.core.webservice.IViewWebservice
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_edit_task.*
import kotlinx.android.synthetic.main.fragment_edit_task.view.*
import kotlinx.android.synthetic.main.selected_performer_item.*
import kotlinx.android.synthetic.main.selected_performer_item.view.*
import kotlinx.android.synthetic.main.selected_product_item.view.*
import org.joda.time.DateTime
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

data class DeliveryReceiptAtItem(
        var name: String,
        var selectedDateTime: DateTime?
)

data class DeliveryDateTypeItem(
    var name: String,
    var selectedDateTime: DateTime?
)

data class DeliveryTimeTypeItem(
        var name: String,
        var fromTime: DateTime?,
        var toTime: DateTime?
)

data class PaymentTypeItem(
        var name: String,
        var paymentType: PaymentType? = null
)

data class PerformerTypeItem(
        var name: String,
        var performerId: UUID? = null
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

    private lateinit var dateTypesDropdownSelect: DropdownSelect<DeliveryDateTypeItem>

    private val selectedDeliveryDateTypeKey = "delivery_date_type"
    private var selectedDeliveryDateTypeIndex : Int?
        get() = arguments?.get(selectedDeliveryDateTypeKey) as Int?
        set(value) = arguments?.putInt(selectedDeliveryDateTypeKey, value!!)!!

    private val deliveryDateTypes = arrayListOf<DropdownSelectItem<DeliveryDateTypeItem>>(
            DropdownSelectItem(DeliveryDateTypeItem(DATE_TODAY, DateTime.now())),
            DropdownSelectItem(DeliveryDateTypeItem(DATE_TOMORROW, DateTime.now().plusDays(1))),
            DropdownSelectItem(DeliveryDateTypeItem(DATE_CUSTOM, null))
    )

    private fun setTaskDate (date: DateTime) = launchUI {
        val task = dp.taskInfos.getAsync(taskId, DataProviderGetMode.DIRTY).entry
        val dateReal = task.deliveryFrom?: DateTime.now()
        task.deliveryFrom = dateReal.withDate(date.toLocalDate())
        task.deliveryTo = dateReal.withDate(date.toLocalDate())
    }

    private fun deliveryDateTypeTextSelector (deliveryDateTypeItem: DeliveryDateTypeItem): String {
        return deliveryDateTypeItem.name
    }

    private fun onDeliveryDateSelect (index: Int, oldIndex: Int): Unit = launchUI{
        if ((oldIndex == index && index == deliveryDateTypes.size - 1) || deliveryDateTypes[index].data.selectedDateTime == null) {
            val datePickerFragment = DatePickerFragment()
            datePickerFragment.setOnDateSelectCallback({ date: DateTime -> updateCustomDateView(date, index) })
            val selectedDate = deliveryDateTypes[index].data.selectedDateTime
            datePickerFragment.setStartDate(selectedDate)
            datePickerFragment.show(activity!!.fragmentManager, "datePicker")
        }

        selectedDeliveryDateTypeIndex = index
        setTaskDate(deliveryDateTypes[index].data.selectedDateTime!!)
    }

    private fun updateCustomDateView (date: DateTime, index: Int) {
        val customDateItem = deliveryDateTypes[index].data
        customDateItem.name = date.toString("dd/MM")
        customDateItem.selectedDateTime = date
        setTaskDate(date)
        dateTypesDropdownSelect.refreshDropDownSelect()
    }

    // SECTION: DatePicker --END--

    // SECTION: TimePicker --START--

    private val TIME_MORNING = "09:00 - 12:00"
    private val TIME_AFTERNOON = "12:00 - 18:00"
    private val TIME_EVENING = "18:00 - 22:00"
    private val TIME_CUSTOM = "Exact time"

    private lateinit var timeTypesDropdownSelect: DropdownSelect<DeliveryTimeTypeItem>
    
    private val selectedDeliveryTimeTypeKey = "delivery_time_type"
    private var selectedDeliveryTimeTypeIndex : Int?
        get() = arguments?.get(selectedDeliveryTimeTypeKey) as Int?
        set(value) = arguments?.putInt(selectedDeliveryTimeTypeKey, value!!)!!

    private val deliveryTimeTypes = arrayListOf<DropdownSelectItem<DeliveryTimeTypeItem>>(
            DropdownSelectItem(
                    DeliveryTimeTypeItem(
                            TIME_MORNING,
                            DateTime.now().withTime(9, 0, 0, 0),
                            DateTime.now().withTime(11, 59, 59, 999))
            ),
            DropdownSelectItem(
                    DeliveryTimeTypeItem(
                            TIME_AFTERNOON,
                            DateTime.now().withTime(12, 0, 0, 0),
                            DateTime.now().withTime(17, 59, 59, 999))
            ),
            DropdownSelectItem(
                    DeliveryTimeTypeItem(
                            TIME_EVENING,
                            DateTime.now().withTime(18, 0, 0, 0),
                            DateTime.now().withTime(21, 59, 59, 999))
            ),
            DropdownSelectItem(
                    DeliveryTimeTypeItem(
                            TIME_CUSTOM,
                            null,
                            null)
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

    private fun updateCustomTimeView (date: DateTime, index: Int) {
        val customTimeItem = deliveryTimeTypes[index].data
        customTimeItem.name = date.toString("HH:mm")
        customTimeItem.fromTime = date
        customTimeItem.toTime = date
        setTaskTime(date, date)
        timeTypesDropdownSelect.refreshDropDownSelect()
    }

    private fun deliveryTimeTypeTextSelector (deliveryTimeTypeItem: DeliveryTimeTypeItem): String {
        return deliveryTimeTypeItem.name
    }

    private fun onDeliveryTimeSelect (index: Int, oldIndex: Int): Unit = launchUI{
        if ((oldIndex == index && index == deliveryTimeTypes.size - 1) || deliveryTimeTypes[index].data.fromTime == null) {
            val timePickerFragment = TimePickerFragment()
            timePickerFragment.setOnTimeSelectCallback({ date: DateTime -> updateCustomTimeView(date, index)})
            val selectedTime = deliveryTimeTypes[index].data.fromTime
            timePickerFragment.setStartTime(selectedTime)
            timePickerFragment.show(activity!!.fragmentManager, "timePicker")
        }

        selectedDeliveryTimeTypeIndex = index
        setTaskTime(deliveryTimeTypes[index].data.fromTime!!, deliveryTimeTypes[index].data.toTime!!)
    }

    // SECTION: TimePicker --END--

    // SECTION: ReceiptAt --START--

    private val RECEIPT_ASAP = "As soon as possible"
    private val RECEIPT_AUTO = "Auto"
    private val RECEIPT_CUSTOM= "Exact time"

    private val selectedReceiptDateKey = "delivery_receipt_at"
    private var selectedReceiptDateIndex : Int?
        get() = arguments?.get(selectedReceiptDateKey) as Int?
        set(value) = arguments?.putInt(selectedReceiptDateKey, value!!)!!

    private val receiptTypes = arrayListOf<DropdownSelectItem<DeliveryReceiptAtItem>>(
            DropdownSelectItem(DeliveryReceiptAtItem(RECEIPT_AUTO, DateTime.now())),
            DropdownSelectItem(DeliveryReceiptAtItem(RECEIPT_ASAP, DateTime.now())),
            DropdownSelectItem(DeliveryReceiptAtItem(RECEIPT_CUSTOM, DateTime.now()), false, true)
    )

    private fun setTaskReceiptAtDate (date: DateTime) = launchUI {
        val task = dp.taskInfos.getAsync(taskId, DataProviderGetMode.DIRTY).entry
        val dateReal = task.receipt?: DateTime.now()
        task.receipt = dateReal.withDate(date.toLocalDate())
    }

    private fun onReceiptAtSelect (index: Int, oldIndex: Int): Unit = launchUI{
        selectedReceiptDateIndex = index
        setTaskReceiptAtDate(receiptTypes[index].data.selectedDateTime!!)
    }

    private fun receiptTextSelector(receiptAtItem: DeliveryReceiptAtItem): String {
        return receiptAtItem.name
    }

    // SECTION: ReceiptAt --END--

    // SECTION: PaymentTypes --START--

    private val selectedPaymentTypeKey = "delivery_payment_type"
    private var selectedPaymentTypeIndex : Int?
        get() = arguments?.get(selectedPaymentTypeKey) as Int?
        set(value) = arguments?.putInt(selectedPaymentTypeKey, value!!)!!

    private val paymentTypes = mutableListOf<DropdownSelectItem<PaymentTypeItem>>(
            DropdownSelectItem(PaymentTypeItem("Add", null), true, false)
    )

    private fun setTaskPaymentType (paymentType: PaymentType) = launchUI {
        val task = dp.taskInfos.getAsync(taskId, DataProviderGetMode.DIRTY).entry
        task.paymentTypeId = paymentType.id
    }

    private fun onPaymentTypeSelect (index: Int, oldIndex: Int): Unit = launchUI{
        selectedPaymentTypeIndex = index
        if (paymentTypes[index].isLink) {
            navigationController.navigateToEditPaymentType()
        } else {
            setTaskPaymentType(paymentTypes[index].data.paymentType!!)
        }
    }

    private fun paymentTypeTextSelector (paymentTypeItem: PaymentTypeItem): String {
        return paymentTypeItem.name
    }

    // SECTION: PaymentTypes --END--

    // SECTION: Performer --START--

    private val PERFORMER_AUTO = "Auto"
    private val PERFORMER_UNASSIGNED = "Unassigned"
    private val PERFORMER_OUTSOURCE = "Delivery Couriers"
    private val PERFORMER_EXACT = "From staff"

    private val selectedPerformerTypeKey = "delivery_performer_type"
    private var selectedPerformerTypeIndex : Int?
        get() = arguments?.get(selectedPerformerTypeKey) as Int?
        set(value) = arguments?.putInt(selectedPerformerTypeKey, value!!)!!

    private val performersTypes = arrayListOf<DropdownSelectItem<PerformerTypeItem>>(
            DropdownSelectItem(PerformerTypeItem(PERFORMER_AUTO, null)),
            DropdownSelectItem(PerformerTypeItem(PERFORMER_OUTSOURCE, null), false, true),
            DropdownSelectItem(PerformerTypeItem(PERFORMER_UNASSIGNED, null)),
            DropdownSelectItem(PerformerTypeItem(PERFORMER_EXACT, null))
    )

    private fun setTaskPerformer (performerId: UUID?) = launchUI {
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

    private fun onPerformerTypeSelect (index: Int, oldIndex: Int): Unit = launchUI{
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
            1 -> {}
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
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun configureToolbar(toolbar: ToolbarController) {
        toolbar.disableDropDown()
        toolbar.setToolbarTitle("Task")
        toolbar.showBackButton()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) = launchUI {
        super.onActivityCreated(savedInstanceState)
        val position = savedInstanceState?.getInt("SCROLL_POSITION")
        if (position != null)
            svEditTask.scrollX = position

        val task = dp.taskInfos.getAsync(taskId, DataProviderGetMode.DIRTY).entry

        tvSelectProduct.setOnClickListener { _ ->
            navigationController.navigateToFilterProducts(taskId)
        }

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

        val paymentTypesData = dp.paymentTypesViews
                .getViewResultAsync("ReferenceViewGroup", "PaymentTypesView").viewResult

        if (paymentTypesData.isNotEmpty()) {
            paymentTypes.removeAt(0)
            for(id in paymentTypesData) {
                try{
                    val (e, _) = dp.paymentTypes.getAsync(id, DataProviderGetMode.FORCE_CACHE)
                    val item = PaymentTypeItem(e.name!!, e)
                    paymentTypes.add(DropdownSelectItem(item, false, false))
                } catch (e: CacheException) {}
            }
        }

        DropdownSelect<PaymentTypeItem>(
                "Payment type",
                paymentTypes,
                selectedPaymentTypeIndex,
                ::onPaymentTypeSelect,
                ::paymentTypeTextSelector,
                llPaymentTypesContainer,
                activity!!
        )
    }

    private fun updateProductView (view: View, taskProductInfo: TaskProduct?, container: View? = this.view) {
        if (taskProductInfo == null) {
            val anim = ValueAnimator.ofInt(view.height, 0)
            anim.addUpdateListener { valueAnimator ->
                val value = valueAnimator.animatedValue as Int
                val layoutParams = view.layoutParams
                layoutParams.height = value
                view.layoutParams = layoutParams
            }
            anim.duration = 100L
            anim.start()
        } else {
            val product = dp.products.get(taskProductInfo.productId as UUID, DataProviderGetMode.FORCE_CACHE).entry

            view.tvProductQuantity.text = taskProductInfo.quantity.toString()
            view.tvName.text = product.name
            view.tvCost.text = activity!!.resources.getString(com.kvteam.deliverytracker.core.R.string.Core_Product_Cost_Template, product.cost.toString())
            view.tvVendorCode.text = product.vendorCode.toString()
        }

        val task = dp.taskInfos.get(taskId, DataProviderGetMode.DIRTY).entry
        var newTotalCost = BigDecimal(0)
        task.taskProducts.forEach { taskProduct ->
            val product = dp.products.get(taskProduct.productId as UUID, DataProviderGetMode.FORCE_CACHE).entry
            newTotalCost = newTotalCost.add(product.cost!!.multiply(BigDecimal(taskProduct.quantity!!)))
        }
        container!!.tvTotalProductsPrice.text = activity!!.resources.getString(
                com.kvteam.deliverytracker.core.R.string.Core_Product_Cost_Template,
                newTotalCost.toString()
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_edit_task, container, false)

        val task = dp.taskInfos.get(taskId, DataProviderGetMode.DIRTY).entry

        task.taskProducts.forEach { taskProductInfo ->
            val productItemView = inflater.inflate(R.layout.selected_product_item, view.llSelectedProduct, false)

            updateProductView(productItemView, taskProductInfo, view)


            productItemView.ivIconIncreaseQuantity.setOnClickListener { _ ->
                taskProductInfo.quantity = taskProductInfo.quantity!! + 1
                updateProductView(productItemView, taskProductInfo)
            }

            productItemView.ivIconDecreaseQuantity.setOnClickListener { _ ->
                if (taskProductInfo.quantity!! > 1) {
                    taskProductInfo.quantity = taskProductInfo.quantity!! - 1
                    updateProductView(productItemView, taskProductInfo)
                }
            }

            productItemView.ivIconDelete.setOnClickListener { _ ->
                task.taskProducts.remove(taskProductInfo)
                updateProductView(productItemView, null)
            }

            productItemView.ivIconInfo.setOnClickListener { _ ->
                navigationController.navigateToProductDetails(taskProductInfo.productId as UUID)
            }

            view.llSelectedProduct.addView(productItemView)
        }

        dateTypesDropdownSelect = DropdownSelect<DeliveryDateTypeItem>(
                "Delivery date",
                deliveryDateTypes,
                selectedDeliveryDateTypeIndex,
                ::onDeliveryDateSelect,
                ::deliveryDateTypeTextSelector,
                view.llDeliveryDateContainer,
                activity!!
        )
        timeTypesDropdownSelect = DropdownSelect<DeliveryTimeTypeItem>(
                "Delivery interval",
                deliveryTimeTypes,
                selectedDeliveryTimeTypeIndex,
                ::onDeliveryTimeSelect,
                ::deliveryTimeTypeTextSelector,
                view.llDeliveryTimeContainer,
                activity!!
        )
        DropdownSelect<DeliveryReceiptAtItem>(
                "Receipt at",
                receiptTypes,
                selectedReceiptDateIndex,
                ::onReceiptAtSelect,
                ::receiptTextSelector,
                view.llReceiptAtContainer,
                activity!!
        )
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("SCROLL_POSITION", svEditTask.scrollY)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = launchUI ({
        when (item.itemId) {
            R.id.action_done -> {
                val (task, _) = dp.taskInfos.getAsync(taskId, DataProviderGetMode.DIRTY)
                task.taskNumber = etTaskNumber.text.toString()
                try {
                    dp.taskInfos.upsertAsync(task)
                } catch (e: NetworkException) {
                    eh.handle(e.result)
                }
                navigationController.closeCurrentFragment()
            }
        }
    }, {
        super.onOptionsItemSelected(item)
    })

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.done_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}

