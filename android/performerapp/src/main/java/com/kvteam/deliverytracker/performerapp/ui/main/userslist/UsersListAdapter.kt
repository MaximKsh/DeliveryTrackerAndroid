package com.kvteam.deliverytracker.performerapp.ui.main.userslist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.roles.toRole
import com.kvteam.deliverytracker.performerapp.R
import kotlinx.android.synthetic.main.fragment_users_item.view.*

class UsersListAdapter(
        var onCallClick: ((task: User) -> Unit)?,
        var getLocalizedString: ((id: Int) -> String)?): RecyclerView.Adapter<UsersListAdapter.ViewHolder>() {

    class ViewHolder(v: View): RecyclerView.ViewHolder(v) {
        val tvFullname = v.tvUserItemFullname!!
        val tvRole = v.tvUserItemRole!!
        val tvPhoneNumber = v.tvUserItemPhoneNumber!!
        val ivCall = v.ivCall!!
    }

    val items = mutableListOf<User>()

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int): UsersListAdapter.ViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(
                        R.layout.fragment_users_item,
                        parent,
                        false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = items[position]
        holder.tvFullname.text = String.format(
                getLocalizedString?.invoke(R.string.PerformerApp_UsersListFragment_FullnameFormat) ?: "\$s \$s",
                user.surname,
                user.name)
        val roleStringId = user.role?.toRole()?.localizationStringId
        holder.tvRole.text =
                if(roleStringId != null) getLocalizedString?.invoke(roleStringId) ?: EMPTY_STRING
                else EMPTY_STRING
        holder.tvPhoneNumber.text = user.phoneNumber
        holder.ivCall.setOnClickListener{ onCallClick?.invoke(user) }
    }

    override fun getItemCount(): Int = items.size
}

