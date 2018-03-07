package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.clients

import com.kvteam.deliverytracker.core.models.Client
import com.kvteam.deliverytracker.core.ui.BaseListFlexibleAdapter
import com.kvteam.deliverytracker.core.ui.IBaseListItemActions

class ClientsListFlexibleAdapter(noHeaderItems: MutableList<ClientListItem>,
                                      referenceActions: IBaseListItemActions<ClientListItem>)
    : BaseListFlexibleAdapter<Client, ClientListItem, ClientListItem.ClientsListViewHolder>(noHeaderItems, referenceActions) {}
