package com.kvteam.deliverytracker.performerapp.ui.main.performerslist

import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import com.kvteam.deliverytracker.core.models.UserModel
import com.kvteam.deliverytracker.core.ui.DataBoundListAdapter
import com.kvteam.deliverytracker.performerapp.R
import com.kvteam.deliverytracker.performerapp.databinding.FragmentPerformersItemBinding
import java.util.*

class PerformersListAdapter
    : DataBoundListAdapter<UserModel, FragmentPerformersItemBinding>() {

    override fun createBinding(parent: ViewGroup): FragmentPerformersItemBinding {
        return DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.fragment_performers_item,
                parent,
                false)
    }

    override fun bind(binding: FragmentPerformersItemBinding, item: UserModel) {
        binding.user = item
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

