package com.kvteam.deliverytracker.managerapp.ui.main.userslist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kvteam.deliverytracker.core.roles.Role
import com.kvteam.deliverytracker.managerapp.R
import kotlinx.android.synthetic.main.fragment_manager_list_item.view.*

class UsersListAdapter(var userItemActions: UserItemActions?)
    : RecyclerView.Adapter<UsersListAdapter.ViewHolder>() {

    class ViewHolder(v: View): RecyclerView.ViewHolder(v) {
        val tvName = v.tvName!!
        val tvSurname = v.tvSurname!!
        val cbSelectUser = v.cbSelectUser!!
        val ivAdminStatusIcon = v.ivAdminStatusIcon!!
    }

    val items = mutableListOf<UserListModel>()

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int): UsersListAdapter.ViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(
                        R.layout.fragment_manager_list_item,
                        parent,
                        false)

        return ViewHolder(view)
    }

    fun toggleVisibility(holder: ViewHolder, userListModel: UserListModel) {
        if (userListModel.isInEditMode) {
            holder.cbSelectUser.visibility = View.VISIBLE
        } else {
            holder.cbSelectUser.visibility = View.GONE
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userListModel = this.items[position]
        holder.tvName.text = userListModel.user.name
        holder.tvSurname.text = userListModel.user.surname
        holder.cbSelectUser.isChecked = userListModel.isSelected

        holder.ivAdminStatusIcon.visibility = if (userListModel.user.role == Role.Creator.simpleName)
            View.VISIBLE
        else
            View.INVISIBLE

        this.toggleVisibility(holder, userListModel)

        holder.cbSelectUser.setOnClickListener{ this.userItemActions?.onSelectClick(userListModel) }

        // TODO: add animation
//        val autoTransaction = AutoTransition()
//        autoTransaction.duration = 100
//        TransitionManager.beginDelayedTransition(listI as ViewGroup, autoTransaction)
    }

    override fun getItemCount(): Int = items.size
}


