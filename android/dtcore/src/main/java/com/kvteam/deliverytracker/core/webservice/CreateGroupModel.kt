package com.kvteam.deliverytracker.core.webservice

/**
* Created by maxim on 14.10.17.
*/

data class CreateGroupModel(val groupName: String,
                            val creatorDisplayableName: String,
                            val creatorPassword: String)