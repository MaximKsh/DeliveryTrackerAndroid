package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.paymenttypes

import android.graphics.Color
import android.view.View
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.models.PaymentType
import com.kvteam.deliverytracker.core.ui.BaseListHeader
import com.kvteam.deliverytracker.core.ui.BaseListItem
import com.kvteam.deliverytracker.managerapp.R
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.utils.DrawableUtils
import kotlinx.android.synthetic.main.fragment_user_list_item.view.*

class PaymentTypeListItem(
        val paymentType: PaymentType,
        header: BaseListHeader?,
        private val lm: ILocalizationManager)
    : BaseListItem<PaymentType, PaymentTypeListItem.PaymentTypesListViewHolder>(paymentType, header) {

    override val key: String
        get() = paymentType.id!!.toString()

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>?) : PaymentTypesListViewHolder {
        return PaymentTypesListViewHolder(view, adapter);
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: PaymentTypesListViewHolder, position: Int, payloads: MutableList<Any>?) {
        holder.tvName.text = paymentType.name
    }

    open class PaymentTypesListViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>?) : BaseListItem.BaseListViewHolder(view, adapter) {
        override val layoutID: Int
            get() = R.layout.payment_type_list_item

        val tvName = view.tvName!!
    }
 }
