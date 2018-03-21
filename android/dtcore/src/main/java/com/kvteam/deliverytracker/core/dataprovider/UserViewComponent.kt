package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.webservice.IViewWebservice

class UserViewComponent (
        viewWebservice: IViewWebservice,
        dataContainer: UserDataContainer
) : BaseViewComponent<User>(viewWebservice, dataContainer) {
    override fun entryFactory(): User {
        return User()
    }
}