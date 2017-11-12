package com.kvteam.deliverytracker.performerapp.ui.main.userslist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kvteam.deliverytracker.core.models.UserModel
import com.kvteam.deliverytracker.performerapp.R
import kotlinx.android.synthetic.main.fragment_users_item.view.*

class UsersListAdapter(
        var onCallClick: ((task: UserModel) -> Unit)?): RecyclerView.Adapter<UsersListAdapter.ViewHolder>() {

    class ViewHolder(v: View): RecyclerView.ViewHolder(v) {
        val tvSurname = v.tvUserItemSurname!!
        val tvPhoneNumber = v.tvUserItemPhoneNumber!!
        val ivCall = v.ivCall!!
    }

    val items = mutableListOf<UserModel>()

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
        holder.tvSurname.text = user.surname
        holder.tvPhoneNumber.text = user.phoneNumber
        holder.ivCall.setOnClickListener{ onCallClick?.invoke(user) }
    }

    override fun getItemCount(): Int = items.size
}

