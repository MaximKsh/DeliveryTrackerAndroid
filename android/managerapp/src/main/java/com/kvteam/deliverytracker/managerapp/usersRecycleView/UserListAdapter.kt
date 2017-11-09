package com.kvteam.deliverytracker.managerapp.usersRecycleView

import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.ScaleAnimation
import com.kvteam.deliverytracker.core.models.UserModel
import com.kvteam.deliverytracker.core.ui.DataBoundListAdapter
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.databinding.FragmentManagerListItemBinding
import java.util.*
import android.databinding.ViewDataBinding
import android.databinding.OnRebindCallback
import android.support.transition.AutoTransition
import android.support.transition.TransitionManager


class UsersListAdapter(private val userListViewModel: UsersListViewModel)
    : DataBoundListAdapter<UserModel, FragmentManagerListItemBinding>() {

    override fun createBinding(parent: ViewGroup): FragmentManagerListItemBinding {
        return DataBindingUtil.inflate<FragmentManagerListItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.fragment_manager_list_item,
                parent,
                false)
    }

    override fun bind(binding: FragmentManagerListItemBinding, item: UserModel) {
        binding.user = item
        binding.userListViewModel = this.userListViewModel

        binding.addOnRebindCallback(object: OnRebindCallback<FragmentManagerListItemBinding>() {
            override fun onPreBind(binding: FragmentManagerListItemBinding?): Boolean {
                val autoTransaction = AutoTransition()
                autoTransaction.duration = 100
                TransitionManager.beginDelayedTransition(binding!!.root as ViewGroup, autoTransaction)
                return super.onPreBind(binding)
            }
        })
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
