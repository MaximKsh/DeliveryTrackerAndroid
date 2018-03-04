package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist

import android.graphics.Color
import android.view.View
import com.amulyakhare.textdrawable.TextDrawable
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.models.PaymentType
import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.roles.Role
import com.kvteam.deliverytracker.core.ui.BaseListHeader
import com.kvteam.deliverytracker.core.ui.BaseListItem
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.userslist.UserListItem
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.utils.DrawableUtils
import kotlinx.android.synthetic.main.fragment_user_list_item.view.*

class ReferenceListItem(
        val reference: PaymentType,
        header: BaseListHeader,
        private val lm: ILocalizationManager)
    : BaseListItem<PaymentType, ReferenceListItem.ReferenceListViewHolder>(reference, header) {

    override val key: String
        get() = reference.id!!.toString()

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>?) : ReferenceListItem.ReferenceListViewHolder {
        return ReferenceListItem.ReferenceListViewHolder(view, adapter);
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: ReferenceListViewHolder, position: Int, payloads: MutableList<Any>?) {
        val context = holder.itemView.context

        val drawable = DrawableUtils.getSelectableBackgroundCompat(
                Color.WHITE, Color.parseColor("#dddddd"),
                DrawableUtils.getColorControlHighlight(context))

        DrawableUtils.setBackgroundCompat(holder.itemView, drawable)

        holder.tvName.text = reference.name
    }

    open class ReferenceListViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>?) : BaseListItem.BaseListViewHolder(view, adapter) {
        override val layoutID: Int
            get() = R.layout.fragment_reference_list_item

        val tvName = view.tvName!!
    }
 }
