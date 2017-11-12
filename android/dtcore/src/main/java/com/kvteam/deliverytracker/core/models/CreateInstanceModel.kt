package com.kvteam.deliverytracker.core.models

data class CreateInstanceModel(
        var instance: InstanceModel,
        var credentials: CredentialsModel,
        var creator: UserModel)