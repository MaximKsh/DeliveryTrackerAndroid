package com.kvteam.deliverytracker.managerapp.ui.main.addtask

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kvteam.deliverytracker.core.models.UserModel
import com.kvteam.deliverytracker.managerapp.R
import kotlinx.android.synthetic.main.fragment_select_performers_item.view.*


class SelectPerformerAdapter(
        var onClick: ((task: UserModel) -> Unit)?)
    : RecyclerView.Adapter<SelectPerformerAdapter.ViewHolder>() {

    class ViewHolder(v: View): RecyclerView.ViewHolder(v) {
        val tvFullname = v.tvAvailablePerformerFullname!!
        val llSelectPerformer = v.llSelectPerformerLayout!!
    }

    val items = mutableListOf<UserModel>()

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int): SelectPerformerAdapter.ViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(
                        R.layout.fragment_select_performers_item,
                        parent,
                        false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = items[position]
        holder.tvFullname.text = user.surname
        holder.llSelectPerformer.setOnClickListener{ onClick?.invoke(user) }
    }

    override fun getItemCount(): Int = items.size
}
