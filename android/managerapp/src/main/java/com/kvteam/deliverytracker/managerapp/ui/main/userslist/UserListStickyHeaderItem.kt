package com.kvteam.deliverytracker.managerapp.ui.main.userslist

import android.view.View
import com.kvteam.deliverytracker.managerapp.R
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractHeaderItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import kotlinx.android.synthetic.main.user_list_sticky_header_item.view.*

class UserListAlphabeticHeader(
        private val letter: String
) : AbstractHeaderItem<UserListAlphabeticHeader.UserListHeaderViewHolder>() {
    override fun getLayoutRes(): Int {
        return R.layout.user_list_sticky_header_item
    }

    override fun hashCode(): Int {
        return letter.hashCode()
    }

    override fun createViewHolder(view: View?, adapter: FlexibleAdapter<out IFlexible<*>>?): UserListHeaderViewHolder {
        return UserListHeaderViewHolder(view, adapter)
    }

    override fun equals(other: Any?): Boolean {
        if (other is UserListAlphabeticHeader) {
            return letter.equals(other.letter)
        }
        return false
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: UserListHeaderViewHolder?, position: Int, payloads: MutableList<Any>?) {
        holder?.tvLetter?.text = letter
    }

    class UserListHeaderViewHolder(val view: View?, val adapter: FlexibleAdapter<out IFlexible<*>>?) : FlexibleViewHolder(view, adapter, true) {
        val tvLetter = view?.tvLetter
    }
}
