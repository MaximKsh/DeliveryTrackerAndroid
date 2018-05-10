package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.common.DifferenceResult
import com.kvteam.deliverytracker.core.dataprovider.base.ActionNotSupportedException
import com.kvteam.deliverytracker.core.dataprovider.base.BaseDataComponent
import com.kvteam.deliverytracker.core.dataprovider.base.IViewDigestContainer
import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.webservice.IUserWebservice
import com.kvteam.deliverytracker.core.webservice.NetworkResult
import com.kvteam.deliverytracker.core.webservice.viewmodels.UserResponse
import java.util.*

class UserDataComponent (
        private val userWebservice: IUserWebservice,
        dataContainer: UserDataContainer,
        viewDigestContainer: IViewDigestContainer
) : BaseDataComponent<User, UserResponse>(dataContainer, viewDigestContainer) {

    override suspend fun createRequestAsync(entity: User): NetworkResult<UserResponse> {
        throw ActionNotSupportedException()
    }

    override suspend fun editRequestAsync(diff: DifferenceResult<User>): NetworkResult<UserResponse>? {
        val entity = diff.difference
        return if (diff.hasDifferentFields) {
            userWebservice.editAsync(entity.id!!, entity)
        } else {
            null
        }
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