package com.kvteam.deliverytracker.core.ui.tasks

import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amulyakhare.textdrawable.TextDrawable
import com.kvteam.deliverytracker.core.R
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.dataprovider.CacheException
import com.kvteam.deliverytracker.core.dataprovider.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.DataProviderGetMode
import com.kvteam.deliverytracker.core.dataprovider.NetworkException
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.errorhandling.IErrorHandler
import com.kvteam.deliverytracker.core.ui.materialDefaultAvatar
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController
import com.kvteam.deliverytracker.core.webservice.ITaskWebservice
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_base_task_details.*
import kotlinx.android.synthetic.main.selected_product_item.view.*
import kotlinx.android.synthetic.main.task_product_view.view.*
import org.joda.time.DateTime
import java.util.*
import javax.inject.Inject

abstract class BaseTaskDetailsFragment : DeliveryTrackerFragment() {
    @Inject
    lateinit var taskWebservice: ITaskWebservice

    @Inject
    lateinit var eh: IErrorHandler

    @Inject
    lateinit var lm: ILocalizationManager

    @Inject
    lateinit var dp: DataProvider

    private val density by lazy { activity?.resources?.displayMetrics?.density!! }
    private val toggleIcon by lazy { ContextCompat.getDrawable(activity!!, R.drawable.ic_expand_more_black_24dp) }
    private val toggleIconResized by lazy {
        BitmapDrawable(
                activity!!.resources,
                Bitmap.createScaledBitmap(
                        (toggleIcon as BitmapDrawable).bitmap,
                        Math.round(30 * density),
                        Math.round(30 * density),
                        true)
        )
    }
    private val rotatedToggleIcon by lazy {
        BitmapDrawable(activity!!.resources, rotateBitmap(toggleIconResized.bitmap, 180f))
    }

    private val taskIdKey = "taskIdKey"
    protected var taskId
        get() = arguments?.getSerializable(taskIdKey)!! as UUID
        set(value) = arguments?.putSerializable(taskIdKey, value)!!


    private val transitionsCountKey = "transitionsCountKey"
    private var transitionsCount
        get() = arguments?.getInt(transitionsCountKey)!!
        set(value) = arguments?.putInt(transitionsCountKey, value)!!

    fun setTask (id: UUID) {
        this.taskId= id
    }

    protected abstract fun closeFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun configureToolbar(toolbar: ToolbarController) {
        super.configureToolbar(toolbar)

        val taskResult = try {
            dp.taskInfos.get(taskId, DataProviderGetMode.FORCE_CACHE)
        } catch (e: CacheException) {
            return
        }
        val task = taskResult.entry
        toolbar.setToolbarTitle(task.taskNumber ?: EMPTY_STRING)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) = launchUI {
        super.onActivityCreated(savedInstanceState)

        val taskResult = try {
            dp.taskInfos.getAsync(taskId, DataProviderGetMode.FORCE_WEB)
        } catch (e: NetworkException) {
            eh.handle(e.result)
            return@launchUI
        }
        val task = taskResult.entry

        tvTaskStatus.text = lm.getString(task.taskStateCaption!!)
        tvTaskNumber.text = task.taskNumber

        if (task.authorId != null) {
            val author = dp.users.get(task.authorId as UUID, DataProviderGetMode.FORCE_CACHE).entry
            tvAuthorName.text = "by ${author.name}"
            tvAuthorSurname.text = author.surname
        }

        if (task.performerId != null) {
            val user = dp.users.get(task.performerId!!, DataProviderGetMode.FORCE_CACHE).entry
            val materialAvatarDefault = materialDefaultAvatar(user)
            ivPerformerImage.setImageDrawable(materialAvatarDefault)
            tvPerformerName.text = user.name
            tvPerformerSurname.text = user.surname
        } else {
            val materialAvatarDefault = materialDefaultAvatar(null)
            ivPerformerImage.setImageDrawable(materialAvatarDefault)
        }

        if (task.receipt != null) {
            tvTaskReceiptAt.text = (task.receipt as DateTime).toString("dd/MM HH:mm")
        }

        if (task.warehouseId != null) {
            val warehouse = dp.warehouses.get(task.warehouseId as UUID, DataProviderGetMode.FORCE_CACHE).entry
            tvWarehouseName.text = warehouse.name
        }

        if (task.comment != null) {
            tvTaskDescription.text = task.comment
        }

        if (task.deliveryFrom != null) {
            tvDeliveryDate.text = "${(task.deliveryFrom as DateTime).toString("dd.MM")}, " +
                    "${(task.deliveryFrom as DateTime).toString("HH:mm")}-" +
                    "${(task.deliveryTo as DateTime).toString("HH:mm")}"
        }

        if (task.clientId != null) {
            val client = dp.clients.get(task.clientId as UUID, DataProviderGetMode.FORCE_CACHE).entry
            if (task.clientAddressId != null) {
                tvClientAddress.text = client.clientAddresses.first{ it.id == task.clientAddressId }.rawAddress
            }
            if (client.name != null) {
                tvClientName.text = client.name
            }
            if (client.surname != null) {
                tvClientSurname.text = client.surname
            }
            if (client.phoneNumber != null) {
                tvClientPhoneNumber.text = client.phoneNumber
            }
        }

        if (task.taskProducts.size > 0) {
            tvNoProducts.visibility = View.GONE
            task.taskProducts.forEach { productInfo ->
                val product = dp.products.get(productInfo.productId!!, DataProviderGetMode.FORCE_CACHE).entry
                val inflatedProductView = layoutInflater.inflate(R.layout.selected_product_item, llProductsContainer, false)
                inflatedProductView.tvProductQuantity.text = productInfo.quantity.toString()
                inflatedProductView.tvName.text = product.name
                inflatedProductView.tvCost.text = activity!!.resources.getString(com.kvteam.deliverytracker.core.R.string.Core_Product_Cost_Template, product.cost.toString())
                inflatedProductView.tvVendorCode.text = product.vendorCode.toString()
                llProductsContainer.addView(inflatedProductView)
            }

            rlTotalCost.visibility = View.VISIBLE
            tvTotalCost.text = activity!!.resources.getString(
                    com.kvteam.deliverytracker.core.R.string.Core_Product_Cost_Template,
                    task.cost.toString())
        }

        tvCreateDate.text = "${task.created!!.toString("dd.MM")}, ${task.created!!.toString("HH:mm")}"

        toolbarController.setToolbarTitle("Task details")

        llTransitionButtons.removeAllViews()
        transitionsCount = task.taskStateTransitions.size
        for (transition in task.taskStateTransitions) {
            val view = SwitchStateView(this@BaseTaskDetailsFragment.context!!)
            view.button.text = lm.getString( transition.buttonCaption ?: EMPTY_STRING )
            view.button.setOnClickListener { onChangeStateClick(transition.id!!) }
            llTransitionButtons.addView(view)
        }

        tvTaskState.text = lm.getString(task.taskStateCaption!!)
        ivTaskStateExpandIcon.setImageDrawable(toggleIconResized)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_base_task_details, container, false)
    }

    private fun onChangeStateClick(transitionId: UUID) = launchUI {
        val transitResult = taskWebservice.changeStateAsync(taskId, transitionId)
        dp.taskInfoViews.invalidate()
        if(eh.handle(transitResult)) {
            return@launchUI
        }
        closeFragment()
    }

    private fun collapseTransitions() {
        if(llTransitionButtons.visibility == View.VISIBLE) {
            animateTransitions()
        }
    }

    private fun expandTransitions() {
        if(llTransitionButtons.visibility == View.GONE) {
            animateTransitions()
        }
    }

    private fun animateTransitions() {
        val expanding = llTransitionButtons.visibility == View.GONE
        val animator = if(expanding) {
            ValueAnimator.ofFloat(0.0f, 1.0f)
        } else {
            ValueAnimator.ofFloat(1.0f, 0.0f)
        }
        val minSize = 0
        val maxSize = transitionsCount * activity?.resources?.getDimension(R.dimen.state_transition_item_height)!!

        animator.addUpdateListener {
            val value = animator.animatedValue as Float
            val layoutParams = llTransitionButtons.layoutParams
            layoutParams.height = (minSize + maxSize * value).toInt()
            llTransitionButtons.layoutParams = layoutParams

            if(value == 0.0f) {
                if(expanding) {
                    llTransitionButtons.visibility = View.VISIBLE
                    ivTaskStateExpandIcon.setImageDrawable(rotatedToggleIcon)
                } else {
                    llTransitionButtons.visibility = View.GONE
                    ivTaskStateExpandIcon.setImageDrawable(toggleIconResized)
                }
            }
        }
        animator.duration = 300L
        animator.start()
    }

    private fun rotateBitmap (source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }
}