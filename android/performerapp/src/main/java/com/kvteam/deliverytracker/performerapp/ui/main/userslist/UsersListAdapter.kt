package com.kvteam.deliverytracker.performerapp.ui.main.userslist

import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import com.kvteam.deliverytracker.core.models.UserModel
import com.kvteam.deliverytracker.core.ui.DataBoundListAdapter
import com.kvteam.deliverytracker.performerapp.R
import com.kvteam.deliverytracker.performerapp.databinding.FragmentUsersItemBinding
import java.util.*

class UsersListAdapter(
        var onCallClicked: ((user: UserModel) -> Unit)? = null)
    : DataBoundListAdapter<UserModel, FragmentUsersItemBinding>() {

    override fun createBinding(parent: ViewGroup): FragmentUsersItemBinding {
        return DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.fragment_users_item,
                parent,
                false)
    }

    override fun bind(binding: FragmentUsersItemBinding, item: UserModel) {
        binding.user = item
        binding.onCallClicked = onCallClicked ?: {}
    }

    override fun areItemsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
        return Objects.equals(oldItem.username, newItem.username)
    }

    override fun areContentsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
        return oldItem.username == newItem.username
                && oldItem.surname === newItem.surname
                && oldItem.name === newItem.name
                && oldItem.phoneNumber === newItem.phoneNumber
    }
}