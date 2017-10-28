package com.kvteam.deliverytracker.core.ui

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView

class DataBoundViewHolder<out T : ViewDataBinding>
constructor(val binding: T) : RecyclerView.ViewHolder(binding.root)
