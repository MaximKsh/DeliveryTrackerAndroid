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
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController
import com.kvteam.deliverytracker.core.webservice.ITaskWebservice
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_base_task_details.*
import kotlinx.android.synthetic.main.task_product_view.view.*
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

        if(task.taskNumber?.isNotBlank() == true) {
            toolbarController.setToolbarTitle(task.taskNumber!!)
        } else {
            toolbarController.setToolbarTitle("Task details")
        }

        tvCreateDate.setText (task.created?.toString("dd/MM HH:mm") )
        tvReceiptDate.setText( task.receipt?.toString("dd/MM HH:mm") )
        if(task.deliveryFrom != null && task.deliveryTo != null) {
            ivDeliveryDate.setText ( task.deliveryFrom?.toString("dd/MM HH:mm") + " - " + task.deliveryTo?.toString("dd/MM HH:mm") )
        } else if( task.deliveryFrom != null ) {
            ivDeliveryDate.setText ( "After ${task.deliveryFrom?.toString("dd/MM HH:mm")}")

        } else if (task.deliveryTo != null) {
            ivDeliveryDate.setText ( "Before ${task.deliveryTo?.toString("dd/MM HH:mm")}")

        }

        if (task.performerId != null) {
            val user = dp.users.getAsync(task.performerId!!, DataProviderGetMode.FORCE_CACHE).entry
            val surname = user.surname
            val name = user.name
            val materialAvatarDefault = TextDrawable.builder()
                    .buildRound((name?.get(0)?.toString() ?: EMPTY_STRING) + (surname?.get(0)?.toString() ?: EMPTY_STRING), Color.LTGRAY)
            ivPerformerAvatar.setImageDrawable(materialAvatarDefault)
            ivPerformerName.text = "$surname $name"
        } else {
            val materialAvatarDefault = TextDrawable.builder()
                    .buildRound(EMPTY_STRING, Color.LTGRAY)
            ivPerformerAvatar.setImageDrawable(materialAvatarDefault)
            ivPerformerName.text = "No performer"
        }

        if(task.paymentTypeId != null) {
            val pt = dp.paymentTypes.getAsync(task.paymentTypeId!!, DataProviderGetMode.FORCE_CACHE).entry
            tvPaymentType.text = pt.name
        }

        if(task.clientId != null) {
            val client = dp.clients.getAsync(task.clientId!!, DataProviderGetMode.FORCE_CACHE).entry
            tvClientName.text = "${client.surname} ${client.name}"
            tvClientPhone.text = client.phoneNumber
            if(task.clientAddressId != null) {
                val addr = client.clientAddresses.firstOrNull { it.id == task.clientAddressId }
                if(addr != null) {
                    tvClientAddress.text = addr.rawAddress
                }
            }
        }

        for(tp in task.taskProducts) {
            val product = dp.products.getAsync(tp.productId!!, DataProviderGetMode.FORCE_CACHE)
            val layout = activity!!.layoutInflater.inflate(
                    R.layout.task_product_view,
                    llProducts,
                    false)
            layout.tvProductName.text = product.entry.name
            layout.tvQuantity.text = tp.quantity.toString()
            llProducts.addView(layout)
        }

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

        rlTaskInfo.setOnClickListener {
            collapseTransitions()
        }

        rlChangeState.setOnClickListener {
            animateTransitions()
        }
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