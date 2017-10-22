package com.kvteam.deliverytracker.core.models

data class ChangePasswordModel(var currentCredentials: CredentialsModel,
                               var newCredentials: CredentialsModel)