package com.kvteam.deliverytracker.core.ui.autocomplete

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.kvteam.deliverytracker.core.R
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.models.Client
import kotlinx.android.synthetic.main.autocomplete_client_item.view.*

class ClientsAutoCompleteAdapter(
        ctx: Context,
        getListFunc: (String) -> MutableList<Client>,
        mainLineCaptionFunc: (Client) -> String = { EMPTY_STRING }
): AutocompleteListAdapter<Client>(
        ctx,
        getListFunc,
        mainLineCaptionFunc
) {
    override val viewLayoutId: Int
        get() = R.layout.autocomplete_client_item

    override fun updateView(position: Int, item: Client, view: View, parent: ViewGroup?) {
       view.tvName.text = item.name
       view.tvSurname.text = item.surname
       view.tvPhoneNumber.text = item.phoneNumber
    }
}