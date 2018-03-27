package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.webservice.IUserWebservice
import com.kvteam.deliverytracker.core.webservice.NetworkResult
import com.kvteam.deliverytracker.core.webservice.viewmodels.UserResponse
import java.util.*

class UserDataComponent (
        private val userWebservice: IUserWebservice,
        dataContainer: UserDataContainer
) : BaseDataComponent<User, UserResponse> (dataContainer) {

    override suspend fun createRequestAsync(entity: User): NetworkResult<UserResponse> {
        throw ActionNotSupportedException()
    }

    override suspend fun editRequestAsync(entity: User): NetworkResult<UserResponse> {
        return userWebservice.editAsync(entity.id!!, entity)
    }

    override suspend fun getRequestAsync(id: UUID): NetworkResult<UserResponse> {
        return userWebservice.getAsync(id)
    }

    override suspend fun deleteRequestAsync(id: UUID): NetworkResult<UserResponse> {
        return userWebservice.deleteAsync(id)
    }

    override fun transformRequestToEntry(result: NetworkResult<UserResponse>): User {
        return result.entity?.user!!
    }

    override fun entryFactory(): User = User()
}