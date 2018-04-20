package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.dataprovider.base.ActionNotSupportedException
import com.kvteam.deliverytracker.core.dataprovider.base.BaseDataComponent
import com.kvteam.deliverytracker.core.dataprovider.base.IViewDigestContainer
import com.kvteam.deliverytracker.core.models.Invitation
import com.kvteam.deliverytracker.core.webservice.IInvitationWebservice
import com.kvteam.deliverytracker.core.webservice.NetworkResult
import com.kvteam.deliverytracker.core.webservice.viewmodels.InvitationResponse
import java.util.*

class InvitationDataComponent (
        private val invitationWebservice: IInvitationWebservice,
        dataContainer: InvitationDataContainer,
        viewDigestContainer: IViewDigestContainer
) : BaseDataComponent<Invitation, InvitationResponse>(dataContainer, viewDigestContainer) {

    override suspend fun createRequestAsync(entity: Invitation): NetworkResult<InvitationResponse> {
        throw ActionNotSupportedException()
    }

    override suspend fun editRequestAsync(entity: Invitation): NetworkResult<InvitationResponse> {
        throw ActionNotSupportedException()
    }

    override suspend fun getRequestAsync(id: UUID): NetworkResult<InvitationResponse> {
        return invitationWebservice.getAsync(id)
    }

    override suspend fun deleteRequestAsync(id: UUID): NetworkResult<InvitationResponse> {
        return invitationWebservice.deleteAsync(id)
    }

    override fun transformRequestToEntry(result: NetworkResult<InvitationResponse>): Invitation {
        return result.entity?.invitation!!
    }

    override fun entryFactory(): Invitation = Invitation()

}