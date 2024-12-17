package com.stytch.sdk.ui.b2b.usecases

import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.network.models.B2BSearchMemberResponseData
import com.stytch.sdk.b2b.searchManager.SearchManager
import com.stytch.sdk.ui.b2b.PerformRequest

internal class UseSearchMember(
    private val request: PerformRequest<B2BSearchMemberResponseData>,
) {
    suspend operator fun invoke(
        emailAddress: String,
        organizationId: String,
    ) = request {
        StytchB2BClient.searchManager.searchMember(
            SearchManager.SearchMemberParameters(
                emailAddress = emailAddress,
                organizationId = organizationId,
            ),
        )
    }
}
