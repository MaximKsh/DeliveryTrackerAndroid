package com.kvteam.deliverytracker.managerapp.ui.main.taskdetails

import android.location.Geocoder
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.models.Geoposition
import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.managerapp.R
import kotlinx.android.synthetic.main.fragment_select_performers_item.view.*
import java.io.IOException


class SelectPerformerAdapter(
        var geocoder: Geocoder?,
        var lm: ILocalizationManager?,
        var onClick: ((task: User) -> Unit)?)
    : RecyclerView.Adapter<SelectPerformerAdapter.ViewHolder>() {

    class ViewHolder(v: View): RecyclerView.ViewHolder(v) {
        val tvSurname = v.tvAvailablePerformerSurname!!
        val tvName = v.tvAvailablePerformerName!!
        val tvPhone = v.tvAvailablePerformerPhoneNumber!!
        val tvAddress = v.tvAvailablePerformerAddress!!
        val llSelectPerformer = v.llSelectPerformerLayout!!
    }

    val items = mutableListOf<User>()

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
        holder.tvSurname.text = user.surname
        holder.tvName.text = user.name
        holder.tvPhone.text = user.phoneNumber
        holder.tvAddress.text = decodeLocation(user.position)
        holder.llSelectPerformer.setOnClickListener{ onClick?.invoke(user) }
    }

    override fun getItemCount(): Int = items.size


    private fun decodeLocation(pos: Geoposition?) : String {
        if(pos == null) {
            return lm?.getString(R.string.ManagerApp_SelectPerformersFragment_EmptyAddress) ?: EMPTY_STRING
        }

        try {
            val addresses = geocoder?.getFromLocation(pos.latitude, pos.longitude, 1)
            if(addresses?.isNotEmpty() == true) {
                val address = addresses.first()
                var addressString = EMPTY_STRING
                for(i in address.maxAddressLineIndex downTo 0) {
                    addressString += "${address.getAddressLine(i)} "
                }
                return addressString
            }
        } catch(e: IOException) {
            Log.e("Geocoder", e.message, e)
        }

        return lm?.getString(R.string.ManagerApp_SelectPerformersFragment_EmptyAddress) ?: EMPTY_STRING
    }
}
